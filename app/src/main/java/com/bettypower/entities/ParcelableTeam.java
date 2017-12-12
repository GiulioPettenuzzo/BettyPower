package com.bettypower.entities;

import android.graphics.Bitmap;
import android.os.Parcel;

/**
 * Created by giuliopettenuzzo on 21/06/17.
 */

public class ParcelableTeam implements Team {

    String name;
    Bitmap bitmap;

    public ParcelableTeam(String name){
        this.name = name;
    }

    public static final Creator<ParcelableTeam> CREATOR = new Creator<ParcelableTeam>(){

        @Override
        public ParcelableTeam createFromParcel(Parcel source) {
            return new ParcelableTeam(source);
        }

        @Override
        public ParcelableTeam[] newArray(int size) {
            return new ParcelableTeam[size];
        }
    };

    public ParcelableTeam(Parcel source){
        this(source.readString());
        bitmap = Bitmap.CREATOR.createFromParcel(source);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setBitmapLogo(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Bitmap getBitmapLogo() {
        return bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        bitmap.writeToParcel(dest,0);
    }
}
