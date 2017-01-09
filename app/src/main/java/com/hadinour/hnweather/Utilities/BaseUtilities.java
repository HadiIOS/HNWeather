package com.hadinour.hnweather.Utilities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaCodec;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * Created by hadinour on 12/26/16.
 */

public class BaseUtilities {
    private static BaseUtilities _sharedInstance;
    private BaseUtilities() {

    }

    final static String LAT_LONG_PATTERN = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$\n";

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

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void setBackgroundOfView(View view, Drawable drawable, Context context) {
        Bitmap bitmap = BaseUtilities.getUtilities().drawableToBitmap(drawable);
        int dimension = Math.max(bitmap.getWidth(), bitmap.getHeight());

        Bitmap extractThumbnail = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
        Drawable finalDrawble = new BitmapDrawable(context.getResources(), extractThumbnail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(finalDrawble);

        } else  {
            view.setBackgroundDrawable(finalDrawble);
        }
    }

    public void setBackgroundColor(View view, int color, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(context.getResources().getColor(color, context.getTheme()));
        } else {
            view.setBackgroundColor(context.getResources().getColor(color));
        }

    }

    public Boolean validate(double lat, double lon){
        return (lat >= -90 && lat <= 90) && (lon >= -180 && lon <= 180);
    }

    private Bitmap scaleImage(Bitmap bitmap, Context context) {

        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(250, context);

        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return scaledBitmap;
    }

    private int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

}