/*
Copyright © 2021 Salt Edge. https://saltedge.com

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
package com.saltedge.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

/**
 * Salt Edge Connect utilities
 */
public class SEConnectHelper {

    public static int CONNECT_REQUEST_CODE = 10117;
    private static Integer toolbarColor = null;

    /**
     * @param toolbarColor Custom Tab toolbar custom color.
     */
    public static void setupUrlHandler(Integer toolbarColor) {
        SEConnectHelper.toolbarColor = toolbarColor;
    }

    /**
     * Opens the URL on a Custom Tab if possible. Otherwise invoke fallback function.
     *
     * @param url The Url String to be opened.
     * @param context The host activity context.
     */
    public static void openExternalApp(String url, Context context) {
        String appPackage = SECustomTabHelper.getNonBrowserActivity(context, url);
        String tabPackageName = SECustomTabHelper.getCustomTabActivity(context, url);

        if (appPackage != null) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else if (tabPackageName != null) {
            try {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setShowTitle(true);
                builder.setUrlBarHidingEnabled(true);
                if (toolbarColor != null) builder.setToolbarColor(toolbarColor);
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setPackage(tabPackageName);
                customTabsIntent.launchUrl(context, Uri.parse(url));
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        } else {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    public static Boolean isSaltEdgeUrl(String targetUrl) {
        return targetUrl != null && (targetUrl.startsWith("https://www.saltedge.com") || targetUrl.startsWith("https://bucket.banksalt.com"));
    }
}
