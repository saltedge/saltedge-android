/*
Copyright © 2019 Salt Edge. https://saltedge.com

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
package com.saltedge.sdk.sample;

import android.app.Application;

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.sample.utils.PreferenceRepository;

public class SampleApplication extends Application {

    private final static boolean ENABLE_LOGGING = false;
    private final static String CLIENT_APP_ID = "";
    private final static String CLIENT_APP_SECRET = "";
    private final static String RETURN_TO_URL = "saltedge://saltedge.com/connect";

    @Override
    public void onCreate() {
        super.onCreate();
        SaltEdgeSDK.getInstance().init(
                this,
                CLIENT_APP_ID,
                CLIENT_APP_SECRET,
                RETURN_TO_URL,
                ENABLE_LOGGING
        );//Init Base Salt Edge API

        PreferenceRepository.init(this);
    }
}
