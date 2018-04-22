package com.bettypower.entities;

import android.os.Parcel;

/**
 * Created by giuliopettenuzzo on 21/06/17.
 */

public class ParcelableTeam implements Team {

    private String name;

    public ParcelableTeam(String name){
        this.name = name;
    }

    ParcelableTeam(){
    super();
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

    private ParcelableTeam(Parcel source){
        this(source.readString());
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
