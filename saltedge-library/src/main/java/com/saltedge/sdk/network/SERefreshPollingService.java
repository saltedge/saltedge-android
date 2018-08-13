/*
Copyright © 2018 Salt Edge. https://saltedge.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.saltedge.sdk.network;

import java.util.Timer;
import java.util.TimerTask;

class SERefreshPollingService  {

    private final static long POLLING_TIMEOUT = 5000L;
    private Timer timer;
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            pollingAction();
        }
    };

    public void start() {
        try {
            timer = new Timer();
            timer.schedule(task, 0, POLLING_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        timer = null;
    }

    public boolean isRunning() {
        return timer != null;
    }

    void pollingAction() {

    }
}
