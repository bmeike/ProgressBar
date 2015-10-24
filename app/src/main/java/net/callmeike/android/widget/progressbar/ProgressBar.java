/* $Id: $
   Copyright 2012, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package net.callmeike.android.widget.progressbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import net.callmeike.android.progressindicator.R;


/**
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 * @version $Revision: $
 */
public class ProgressBar extends View {
    private static final String TAG = "PROG";

    private static final float COMPLETE = 0.98F;

    private static final int SHADOW_COLOR = Color.parseColor("#33000000");
    private static final float SHADOW_BLUR = 3.0F;
    private static final float SHADOW_DX = -8.0F;
    private static final float SHADOW_DY = 2.0F;


    private final Paint paint = new Paint();

    private int wireRadius = 4;
    private int wireColor = Color.BLACK;

    private int ballRadius = 16;
    private int ballStartColor = Color.BLUE;
    private int ballFinishColor = Color.GREEN;

    private float x, y;
    private float x1, y1, x2, y2;
    private float xSpan;
    private float ctrX, ctrY;
    private float radius2;

    private float progress;
    private int ballColor;

    public ProgressBar(Context context) {
        super(context);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttrs(context.getTheme(), attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyle) {
        super(context, attrs, defStyleAttr, defStyle);
        parseAttrs(context.getTheme(), attrs, defStyle);
        init();
    }

    public int getWireRadius() { return wireRadius; }
    public void setWireRadius(int wireRadius) {
        this.wireRadius = wireRadius;
        init();
        invalidate();
    }

    public int getWireColor() { return wireColor; }
    public void setWireColor(int wireColor) {
        this.wireColor = wireColor;
        invalidate();
    }

    public int getBallRadius() { return ballRadius; }
    public void setBallRadius(int ballRadius) {
        this.ballRadius = ballRadius;
        invalidate();
    }

    public int getBallStartColor() { return ballStartColor; }
    public void setBallStartColor(int ballStartColor) {
        this.ballStartColor = ballStartColor;
        invalidate();
    }

    public int getBallFinishColor() { return ballFinishColor; }
    public void setBallFinishColor(int ballFinishColor) {
        this.ballFinishColor = ballFinishColor;
        invalidate();
    }

    public float getProgress() { return progress; }
    public void setProgress(float progress) {
        if ((progress < 0.0F) || (progress > 1.0F)) {
            throw new IllegalArgumentException("0.0 <= progress <= 1.0: " + progress);
        }

        this.progress = progress;

        ballColor = (progress < COMPLETE) ? ballStartColor : ballFinishColor;

        x = ballRadius + (xSpan * progress);
        float dx = ctrX - x;
        y = (float) Math.sqrt(radius2 - (dx * dx)) + ctrY;

        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!changed) { return; }

        x1 = ballRadius;
        y1 = ballRadius;

        x2 = (right - left) - ballRadius;
        y2 = ballRadius;

        xSpan = x2 - x1;

        float x3 = x2 / 2;
        float y3 = (bottom - top) - ballRadius;

        float p1Sq = (x1 * x1) + (y1 * y1);
        float p2Sq = (x2 * x2) + (y2 * y2);
        float p3Sq = (x3 * x3) + (y3 * y3);

        float x13dif = x1 - x3;
        float x21dif = x2 - x1;
        float x23dif = x2 - x3;
        float x32dif = x3 - x2;

        float y12dif = y1 - y2;
        float y23dif = y2 - y3;
        float y31dif = y3 - y1;

        float div = 2 * ((x1 * y23dif) - (y1 * x23dif) + (x2 * y3) - (x3 * y2));

        ctrX = ((p1Sq * y23dif) + (p2Sq * y31dif) + (p3Sq * y12dif)) / div;
        ctrY = ((p1Sq * x32dif) + (p2Sq * x13dif) + (p3Sq * x21dif)) / div;

        float dx = ctrX - x1;
        float dy = ctrY - y1;
        radius2 = (dx * dx) + (dy * dy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(wireColor);
        canvas.drawLine(x1, y1, x, y, paint);
        canvas.drawLine(x2, y2, x, y, paint);

        paint.setColor(ballColor);
        canvas.drawCircle(x, y, ballRadius, paint);
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setShadowLayer(SHADOW_BLUR, SHADOW_DX, SHADOW_DY, SHADOW_COLOR);

        paint.setStrokeWidth(wireRadius * 2);
    }

    private void parseAttrs(Resources.Theme theme, AttributeSet attrs, int defStyle) {
        TypedArray atts = theme.obtainStyledAttributes(attrs, R.styleable.progress_indicator, defStyle, 0);
        try {
            wireRadius
                = atts.getDimensionPixelSize(R.styleable.progress_indicator_progress_wire_radius, wireRadius);
            wireColor
                = atts.getColor(R.styleable.progress_indicator_progress_wire_color, wireColor);

            ballRadius
                = atts.getDimensionPixelSize(R.styleable.progress_indicator_progress_ball_radius, ballRadius);
            ballStartColor
                = atts.getColor(R.styleable.progress_indicator_progress_ball_start_color, ballStartColor);
            ballFinishColor
                = atts.getColor(R.styleable.progress_indicator_progress_ball_finish_color, ballFinishColor);
        }
        catch (UnsupportedOperationException | Resources.NotFoundException e) {
            Log.w(TAG, "Failed parsing attributes", e);
        }
    }
}
