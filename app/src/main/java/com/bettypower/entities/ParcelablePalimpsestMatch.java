package com.bettypower.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 01/08/17.
 */
//TODO non ho fatto i test dei parcelable
public class ParcelablePalimpsestMatch implements PalimpsestMatch {

    private Team homeTeam;
    private Team awayTeam;
    private String palimpsest;
    private String matchTime;
    private String matchDate;
    private String matchHour;
    private String eventNumber;
    private String completePalimpsest;
    private String bet;
    private String betKind;
    private String finalQuote;
    private Map<String,String> allOdds;
    private boolean fissa;
    private String resultHomeTeam;
    private String resultAwayTeam;
    private ArrayList<HiddenResult> allHiddenResult = new ArrayList<>();

    public ParcelablePalimpsestMatch(Team homeTeam, Team awayTeam, String palimpsest, String eventNUmber, String matchTime){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.palimpsest = palimpsest;
        this.eventNumber = eventNUmber;
        this.matchTime = matchTime;
        StringTokenizer token = new StringTokenizer(matchTime);
        matchDate = token.nextToken();
        matchHour = token.nextToken();
    }

    public ParcelablePalimpsestMatch(Team homeTeam, Team awayTeam){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public ParcelablePalimpsestMatch() {
        super();
    }


    @Override
    public Team getHomeTeam() {
        return homeTeam;
    }

    @Override
    public void setHomeTeam(Team team) {
        this.homeTeam = team;
    }

    @Override
    public Team getAwayTeam() {
        return awayTeam;
    }

    @Override
    public void setAwayTeam(Team team) {
        this.awayTeam = team;
    }

    @Override
    public String getPalimpsest() {
        return palimpsest;
    }

    @Override
    public void setPalimpsest(String palimpsest) {
        this.palimpsest = palimpsest;
    }

    @Override
    public String getEventNumber() {
        return eventNumber;
    }

    @Override
    public void setEventNumber(String eventNumber) {
        this.eventNumber = eventNumber;
    }

    @Override
    public String getTime() {
        return matchTime;
    }

    @Override
    public String getDate() {
        return matchDate;
    }

    @Override
    public String getHour() {
        return matchHour;
    }

    @Override
    public void setTime(String time) {
        this.matchTime = time;
        StringTokenizer token = new StringTokenizer(matchTime);
        matchDate = token.nextToken();
        matchHour = token.nextToken();

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
        this.finalQuote = quote;
    }

    @Override
    public String getQuote() {
        return finalQuote;
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

    @Override
    public ArrayList<HiddenResult> getAllHiddenResult() {
        return allHiddenResult;
    }

    @Override
    public void setAllHiddenResult(ArrayList<HiddenResult> allHiddenResult) {
        this.allHiddenResult = allHiddenResult;
    }

    @Override
    public boolean compareTo(PalimpsestMatch match) {
        if(completePalimpsest.equals(match.getCompletePalimpsest())){
            return true;
        }
        return false;
    }

    @Override
    public String getCompletePalimpsest() {
        completePalimpsest = palimpsest + eventNumber;
        return completePalimpsest;
    }

    @Override
    public void setCompletePalimpsest(String completePalimpsest) {
        this.completePalimpsest = completePalimpsest;
    }

    @Override
    public Map<String,String> getAllOdds() {
        return allOdds;
    }

    @Override
    public void setAllOdds(Map<String,String> allOdds) {
        this.allOdds = allOdds;
    }

    public boolean compareToByName(PalimpsestMatch paramPalimpsestMatch){
        StringTokenizer homeToken = new StringTokenizer(getHomeTeam().getName());
        StringBuilder homeTeam = new StringBuilder();
        while(homeToken.hasMoreTokens()){
            String word = homeToken.nextToken();
            if(word.length()>2){
                homeTeam.append(" ").append(word);
            }
        }
        StringTokenizer awayToken = new StringTokenizer(getAwayTeam().getName());
        StringBuilder awayTeam = new StringBuilder();
        while(awayToken.hasMoreTokens()){
            String word = awayToken.nextToken();
            if(word.length()>2){
                awayTeam.append(" ").append(word);
            }
        }
        StringTokenizer paramHomeToken = new StringTokenizer(paramPalimpsestMatch.getHomeTeam().getName());
        StringBuilder paramHomeTeam = new StringBuilder();
        while(paramHomeToken.hasMoreTokens()){
            String word = paramHomeToken.nextToken();
            if(word.length()>2){
                paramHomeTeam.append(" ").append(word);
            }
        }
        StringTokenizer paramAwayToken = new StringTokenizer(paramPalimpsestMatch.getAwayTeam().getName());
        StringBuilder paramAwayTeam = new StringBuilder();
        while(paramAwayToken.hasMoreTokens()){
            String word = paramAwayToken.nextToken();
            if(word.length()>2){
                paramAwayTeam.append(" ").append(word);
            }
        }
        if(getTime().equalsIgnoreCase(paramPalimpsestMatch.getTime())){
            return homeTeam.toString().equalsIgnoreCase(paramHomeTeam.toString()) || awayTeam.toString().equalsIgnoreCase(paramAwayTeam.toString());
        }
        else{
            return false;
        }
    }

    public static final Parcelable.Creator<ParcelablePalimpsestMatch> CREATOR = new Parcelable.Creator<ParcelablePalimpsestMatch>() {

        @Override
        public ParcelablePalimpsestMatch createFromParcel(Parcel source) {
            return new ParcelablePalimpsestMatch(source);
        }
        @Override
        public ParcelablePalimpsestMatch[] newArray(int size) {
            return new ParcelablePalimpsestMatch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public ParcelablePalimpsestMatch(Parcel source){
        this(new ParcelableTeam(source.readString()),new ParcelableTeam(source.readString()),source.readString(),source.readString(),source.readString());
        this.bet = source.readString();
        this.betKind = source.readString();
        this.finalQuote = source.readString();
        resultHomeTeam = source.readString();
        resultAwayTeam = source.readString();
        fissa = (Boolean)source.readValue(getClass().getClassLoader());
        allHiddenResult = source.readArrayList(ParcelableHiddenResult.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(homeTeam.getName());
        dest.writeString(awayTeam.getName());
        dest.writeString(palimpsest);
        dest.writeString(eventNumber);
        dest.writeString(matchTime);
        dest.writeString(bet);
        dest.writeString(betKind);
        dest.writeString(finalQuote);
        dest.writeString(resultHomeTeam);
        dest.writeString(resultAwayTeam);
        dest.writeValue(fissa);
        dest.writeList(allHiddenResult);

    }
}
