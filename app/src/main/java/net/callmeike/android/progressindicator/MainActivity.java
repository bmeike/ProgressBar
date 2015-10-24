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
package net.callmeike.android.progressindicator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import net.callmeike.android.widget.progressbar.ProgressBar;


/**
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 * @version $Revision: $
 */
public class MainActivity extends AppCompatActivity {
    private static class ProgressHandler extends Handler {
        private int progress;
        private ProgressBar indicator;

        public ProgressHandler(ProgressBar indicator) {
            this.indicator = indicator;
        }

        public void start() {
            progress = 0;
            sendEmptyMessage(1);
        }

        public void stop() { progress = -1; }

        @Override
        public void handleMessage(Message msg) {
            progress++;

            if (progress < 0) { return; }
            else if (progress > 130) { progress = 0; }
            else if (progress > 115) { indicator.setProgress(0); }
            else if (progress < 100) {
                indicator.setProgress(progress / 100.0F);
            }

            sendEmptyMessageDelayed(1, 50);
        }
    }


    private ProgressBar indicator;
    private ProgressHandler hdlr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        indicator = (ProgressBar) findViewById(R.id.indicator);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hdlr = new ProgressHandler(indicator);
        hdlr.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hdlr.stop();
        hdlr = null;
    }
}
