package com.hadinour.hnweather.Service.PlacePhotos;

import com.google.android.gms.common.api.GoogleApiClient;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

/**
 * Created by hadinour on 12/27/16.
 */

public abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

    private int mHeight;

    private int mWidth;

    private GoogleApiClient mGoogleApiClient;
    private Activity mActivity;

    public PhotoTask(int width, int height, GoogleApiClient googleApiClient, Activity activity) {
        mHeight = height;
        mWidth = width;
        mGoogleApiClient = googleApiClient;
        mActivity = activity;
    }
    /**
     * Loads the first photo for a place id from the Geo Data API.
     * The place id must be the first (and only) parameter.
     */


    @Override
    protected AttributedPhoto doInBackground(String... params) {
        if (params.length != 1) {
            return null;
        }
        final String placeId = params[0];
        AttributedPhoto attributedPhoto = null;

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                // Get the first bitmap and its attributions.
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                CharSequence attribution = photo.getAttributions();
                // Load a scaled bitmap for this photo.
                Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                        .getBitmap();

                attributedPhoto = new AttributedPhoto(attribution, image);
            }
            // Release the PlacePhotoMetadataBuffer.
            photoMetadataBuffer.release();
        }
        return attributedPhoto;
    }


    /**
     * Holder for an image and its attribution.
     */
    public class AttributedPhoto {

        public final CharSequence attribution;

        public final Bitmap bitmap;

        public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
            this.attribution = attribution;
            this.bitmap = bitmap;
        }
    }
}
