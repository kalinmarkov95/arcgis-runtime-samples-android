package com.esri.arcgisruntime.sample.displaymap.location;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.esri.arcgisruntime.mapping.view.LocationDisplay;

import java.io.Serializable;

public class DataWrapper implements Parcelable {

    private LocationDisplay locationDisplay;

    private Context applicationContext;

    public DataWrapper(LocationDisplay locationDisplay, Context applicationContext) {

        this.locationDisplay = locationDisplay;
        this.applicationContext = applicationContext;
    }

    public LocationDisplay getLocationDisplay() {

        return locationDisplay;
    }

    public Context getApplicationContext() {

        return applicationContext;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
