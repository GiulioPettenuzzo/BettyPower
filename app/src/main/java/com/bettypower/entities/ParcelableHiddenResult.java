package com.bettypower.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * red card, yellow card and gal informations of a match
 * Created by giuliopettenuzzo on 28/06/17.
 */

public class ParcelableHiddenResult implements HiddenResult {

    private String playerName;
    private String time;
    private String action;
    private int actionTeam;
    private String  result;

    public static final String ACTION_GOAL = "Goal";
    public static final String ACTION_YELLOWCARD = "Yellowcard";
    public static final String ACTION_REDCARD = "Redcard";
    public static final String NO_MATCH_FOUND = "no_match_found";
    public static final int HOME_TEAM = 0;
    public static final int AWAY_TEAM = 1;
    public static final int ERROR_TEAM = -1;

    public ParcelableHiddenResult(String playerName,String time,String action,int actionTeam){
        this.playerName = playerName;
        this.time = time;
        this.action = action;
        this.actionTeam = actionTeam;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String actionName) {
        this.action = actionName;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public void setTime(String time) {
    this.time = time;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int getActionTeam() {
        return actionTeam;
    }

    @Override
    public void setActionTeam(int numberIdentifier) {
        this.actionTeam = numberIdentifier;
    }

    /**
     * methods of Parcelable
     */

    private ParcelableHiddenResult(Parcel source) {
        this(source.readString(),source.readString(),source.readString(),source.readInt());
        result = source.readString();
    }

    public static final Parcelable.Creator<ParcelableHiddenResult> CREATOR = new Parcelable.Creator<ParcelableHiddenResult>(){

        @Override
        public ParcelableHiddenResult createFromParcel(Parcel source) {
            return new ParcelableHiddenResult(source);
        }

        @Override
        public ParcelableHiddenResult[] newArray(int size) {
            return new ParcelableHiddenResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playerName);
        dest.writeString(time);
        dest.writeString(action);
        dest.writeInt(actionTeam);
        dest.writeString(result);
    }
}
