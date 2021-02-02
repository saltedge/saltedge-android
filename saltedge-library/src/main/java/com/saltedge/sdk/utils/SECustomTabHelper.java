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
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.browser.customtabs.CustomTabsService;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Custom Tab Helper
 */
public class SECustomTabHelper {

    private static final String STABLE_PACKAGE = "com.android.chrome";
    private static final String BETA_PACKAGE = "com.chrome.beta";
    private static final String DEV_PACKAGE = "com.chrome.dev";
    private static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
    private static final String[] chromePackages = new String[] { STABLE_PACKAGE, BETA_PACKAGE, DEV_PACKAGE, LOCAL_PACKAGE };
    private static final String[] nonChromePackages = new String[] { "com.android.browser", "com.mi.globalbrowser", "org.mozilla.firefox", "org.mozilla.fennec", "com.opera.browser", "com.opera.mini.native", "com.microsoft.emmx", "com.yandex.browser" };
    private static final String[] browserPackages = ArrayUtils.concat(chromePackages, nonChromePackages);
    private static final List<String> chromePackagesList = Arrays.asList(chromePackages);
    private static final List<String> browserPackagesList = Arrays.asList(browserPackages);

    public static String getNonBrowserActivity(Context context, String url) {
        PackageManager pm = context.getPackageManager();
        int queryFlag = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            queryFlag = PackageManager.MATCH_ALL;
        }
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        List<ResolveInfo> resolvedActivities = pm.queryIntentActivities(activityIntent, queryFlag);

        for (ResolveInfo info : resolvedActivities) {
            String packageName = getInfoPackageName(info);
            if (packageName != null && !browserPackagesList.contains(packageName)) {
                return packageName;
            }
        }
        return null;
    }

    /**
     * Goes through all apps that handle VIEW intents and have a warm-up service. Picks
     * the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name.
     *
     * This is <strong>not</strong> threadsafe.
     *
     * @param context {@link Context} to use for accessing {@link PackageManager}.
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    public static String getCustomTabActivity(Context context, String url) {
        PackageManager pm = context.getPackageManager();
        if (pm ==null) return null;

        // Get default VIEW intent handler.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        String defaultViewHandlerPackageName = getInfoPackageName(pm.resolveActivity(activityIntent, 0));

        // Get all apps that can handle VIEW intents.
        int queryFlag = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            queryFlag = PackageManager.MATCH_ALL;
        }
        List<ResolveInfo> resolvedActivities = pm.queryIntentActivities(activityIntent, queryFlag);

        List<String> resolvedPackageNames = new ArrayList<>();
        for (ResolveInfo info : resolvedActivities) {
            String activityPackageName = getInfoPackageName(info);
            if (activityPackageName != null) resolvedPackageNames.add(activityPackageName);
        }

        Intent serviceIntent = new Intent();
        serviceIntent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);

        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (String packageName : resolvedPackageNames) {
            serviceIntent.setPackage(packageName);
            ResolveInfo serviceInfo = pm.resolveService(serviceIntent, queryFlag);
            String servicePackageName = getInfoPackageName(serviceInfo);
            if (servicePackageName != null) packagesSupportingCustomTabs.add(servicePackageName);
        }

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        if (packagesSupportingCustomTabs.isEmpty()) {
            if (!resolvedPackageNames.isEmpty()) {
                if (resolvedPackageNames.contains(defaultViewHandlerPackageName)) return defaultViewHandlerPackageName;
                else if (supportsChrome(resolvedPackageNames)) return getSupportedChromeHandler(resolvedPackageNames);
            }
        }
        else if (packagesSupportingCustomTabs.size() == 1) return packagesSupportingCustomTabs.get(0);
        else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                && !hasSpecializedHandlerIntents(context, activityIntent)
                && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            return defaultViewHandlerPackageName;
        }
        else if (packagesSupportingCustomTabs.contains(STABLE_PACKAGE)) return STABLE_PACKAGE;
        else if (packagesSupportingCustomTabs.contains(BETA_PACKAGE)) return BETA_PACKAGE;
        else if (packagesSupportingCustomTabs.contains(DEV_PACKAGE)) return DEV_PACKAGE;
        else if (packagesSupportingCustomTabs.contains(LOCAL_PACKAGE)) return LOCAL_PACKAGE;

        return null;
    }

    private static String getInfoPackageName(ResolveInfo info) {
        if (info == null) return null;
        ActivityInfo activityInfo = info.activityInfo;
        return (activityInfo == null) ? null : activityInfo.packageName;
    }

    private static Boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter != null
                        && filter.countDataAuthorities() > 0
                        && filter.countDataPaths() > 0
                        && resolveInfo.activityInfo != null
                ) return true;
            }
        } catch (RuntimeException e) {
        }
        return false;
    }

    private static Boolean supportsChrome(List<String> names) {
        for (String packageName : names) {
            if (chromePackagesList.contains(packageName)) return true;
        }
        return false;
    }

    private static String getSupportedChromeHandler(List<String> names) {
        for (String packageName : names) {
            if (chromePackagesList.contains(packageName)) return packageName;
        }
        return null;
    }
}
