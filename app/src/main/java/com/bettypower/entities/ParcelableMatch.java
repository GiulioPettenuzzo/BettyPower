package com.bettypower.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 21/06/17.
 */

public class ParcelableMatch implements Match {

    private Team homeTeam;
    private Team awayTeam;
    private String time;
    private String date;
    private String hour;
    private String bet;
    private String quote;
    private String betKind;
    private String resultHomeTeam;
    private String resultAwayTeam;
    private boolean fissa;
    private ArrayList<HiddenResult> allHiddenResult = new ArrayList<>();


    public ParcelableMatch(Team homeTeam, Team awayTeam){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    @Override
    public Team getHomeTeam() {
        return homeTeam;
    }

    @Override
    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    @Override
    public Team getAwayTeam() {
        return awayTeam;
    }

    @Override
    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public String getHour() {
        return hour;
    }

    @Override
    public void setTime(String time) {
        StringTokenizer token = new StringTokenizer(time,"/");
        this.time = time;
        date = token.nextToken();
        hour = token.nextToken();
    }

    @Override
    public String getHomeResult() {
        return resultHomeTeam;
    }

    @Override
    public void setBet(String bet) {
        this.bet = bet;
    }

    @Override
    public String getBet() {
        return bet;
    }

    @Override
    public void setBetKind(String betKind) {
        this.betKind = betKind;
    }

    @Override
    public String getBetKind() {
        return betKind;
    }

    @Override
    public void setQuote(String quote) {
        this.quote = quote;
    }

    @Override
    public String getQuote() {
        return quote;
    }

    @Override
    public void setHomeResult(String result) {
        this.resultHomeTeam = result;
    }

    @Override
    public String getAwayResult() {
        return resultAwayTeam;
    }

    @Override
    public void setAwayResult(String result) {
        this.resultAwayTeam = result;
    }

    @Override
    public void setFissa(boolean fissa) {
        this.fissa = fissa;
    }

    @Override
    public boolean isFissa() {
        return fissa;
    }

    public ArrayList<HiddenResult> getAllHiddenResult(){
        return allHiddenResult;
    }

    public void setAllHiddenResult(ArrayList<HiddenResult> allHiddenResult){
        this.allHiddenResult = allHiddenResult;
    }

    public int getNumberOfHiddenResult(){
        return allHiddenResult.size();
    }

    @Override
    public boolean compareTo(Match match) {
        if(this.getHomeTeam().getName().compareTo(match.getHomeTeam().getName()) ==0 &&
                this.getAwayTeam().getName().compareTo(match.getAwayTeam().getName())==0){
            return true;
        } else{
            return false;
        }
    }

    /**
     * method of Parcelable
     * @return
     */

    public static final Parcelable.Creator<ParcelableMatch> CREATOR = new Parcelable.Creator<ParcelableMatch>(){

        @Override
        public ParcelableMatch createFromParcel(Parcel source) {
            return new ParcelableMatch(source);
        }

        @Override
        public ParcelableMatch[] newArray(int size) {
            return new ParcelableMatch[size];
        }
    };

    public ParcelableMatch(Parcel source){
        this(new ParcelableTeam(source.readString()),new ParcelableTeam(source.readString()));
        resultHomeTeam = source.readString();
        resultAwayTeam = source.readString();
        time = source.readString();
        bet = source.readString();
        betKind = source.readString();
        quote = source.readString();
        fissa = (Boolean) source.readValue(getClass().getClassLoader());
        allHiddenResult = source.readArrayList(ParcelableHiddenResult.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(homeTeam.getName());
        dest.writeString(awayTeam.getName());
        dest.writeString(resultHomeTeam);
        dest.writeString(resultAwayTeam);
        dest.writeString(time);
        dest.writeString(bet);
        dest.writeString(betKind);
        dest.writeString(quote);
        dest.writeValue(fissa);
        dest.writeList(allHiddenResult);
    }
}
