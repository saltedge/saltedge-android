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
package com.saltedge.sdk.sample.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.saltedge.sdk.sample.R;

public class UITools {

    public static AlertDialog showAlertDialog(Context context, String message) {
        return showAlertDialog(context, message, null);
    }

    public static AlertDialog showAlertDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.warning)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener);
        return adb.show();
    }

    public static void destroyAlertDialog(AlertDialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static ProgressDialog refreshProgressDialog(Context context, ProgressDialog dialog, int messageResId) {
        return refreshProgressDialog(context, dialog, context.getString(messageResId));
    }

    public static ProgressDialog refreshProgressDialog(Context context, ProgressDialog dialog, String message) {
        destroyProgressDialog(dialog);
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.show();
        return dialog;
    }

    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.show();
        return dialog;
    }

    public static void destroyProgressDialog(ProgressDialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static void hideProgress(ProgressDialog dialog) {
        dialog.hide();
    }

    public static void showShortToast(Context context, @StringRes Integer messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, @StringRes Integer messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
