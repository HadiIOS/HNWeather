package com.hadinour.hnweather.Utilities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by hadinour on 12/26/16.
 */

public class BaseUtilities {
    private static BaseUtilities _sharedInstance;
    private BaseUtilities() {

    }

    public synchronized static BaseUtilities getUtilities() {
        if (_sharedInstance == null)
            _sharedInstance = new BaseUtilities();
        return _sharedInstance;
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!checkPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isDeviceFineLocationGranted(Context context) {
        return checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public Point getDeviceSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public void popAlertView(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
    }

    public void popErrorDialog(Context context, String message) {
        this.popAlertView(context, "Error", message);
    }

    public void popAlertDialog(Context context, String message) {
        this.popAlertView(context, "Alert", message);
    }

    public void showToast(Context context, int resID) {
        this.showToast(context, resID, Toast.LENGTH_SHORT);
    }

    public void showToast(Context context, int resID, int duration) {
        Toast.makeText(context, resID, duration).show();
    }
}