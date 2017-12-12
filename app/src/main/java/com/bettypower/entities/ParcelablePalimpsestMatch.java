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
    private String bookMaker;
    private String bet;
    private String betKind;
    private String finalQuote;
    private Map<String,String> allOdds;

    public ParcelablePalimpsestMatch(Team homeTeam, Team awayTeam, String palimpsest, String eventNUmber, String matchTime){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.palimpsest = palimpsest;
        this.eventNumber = eventNUmber;
        this.matchTime = matchTime;
        StringTokenizer token = new StringTokenizer(matchTime);
        matchDate = token.nextToken();
        matchHour = token.nextToken();
        bookMaker = ""; //TODO
    }

    public ParcelablePalimpsestMatch(Team homeTeam, Team awayTeam){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }


    public ParcelablePalimpsestMatch(Parcel source){
        this(new ParcelableTeam(source.readString()),new ParcelableTeam(source.readString()),source.readString(),source.readString(),source.readString());
        this.bookMaker = source.readString();
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
        return null;
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

    }

    @Override
    public String getBetKind() {
        return null;
    }

    @Override
    public void setQuote(String quote) {

    }

    @Override
    public String getQuote() {
        return finalQuote;
    }

    @Override
    public void setHomeResult(String result) {

    }

    @Override
    public String getAwayResult() {
        return null;
    }

    @Override
    public void setAwayResult(String result) {

    }

    @Override
    public ArrayList<HiddenResult> getAllHiddenResult() {
        return null;
    }

    @Override
    public void setAllHiddenResult(ArrayList<HiddenResult> allHiddenResult) {

    }

    @Override
    public int getNumberOfHiddenResult() {
        return 0;
    }

    @Override
    public boolean compareTo(Match match) {
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

    @Override
    public String getBookMaker() {
        return bookMaker;
    }

    @Override
    public void setBookMaker(String bookMaker) {
        this.bookMaker = bookMaker;
    }

    public boolean compareToByName(PalimpsestMatch paramPalimpsestMatch){
        StringTokenizer homeToken = new StringTokenizer(getHomeTeam().getName());
        String homeTeam = "";
        while(homeToken.hasMoreTokens()){
            String word = homeToken.nextToken();
            if(word.length()>2){
                homeTeam = homeTeam + " " + word;
            }
        }
        StringTokenizer awayToken = new StringTokenizer(getAwayTeam().getName());
        String awayTeam = "";
        while(awayToken.hasMoreTokens()){
            String word = awayToken.nextToken();
            if(word.length()>2){
                awayTeam = awayTeam + " " + word;
            }
        }
        StringTokenizer paramHomeToken = new StringTokenizer(paramPalimpsestMatch.getHomeTeam().getName());
        String paramHomeTeam = "";
        while(paramHomeToken.hasMoreTokens()){
            String word = paramHomeToken.nextToken();
            if(word.length()>2){
                paramHomeTeam = paramHomeTeam + " " + word;
            }
        }
        StringTokenizer paramAwayToken = new StringTokenizer(paramPalimpsestMatch.getAwayTeam().getName());
        String paramAwayTeam = "";
        while(paramAwayToken.hasMoreTokens()){
            String word = paramAwayToken.nextToken();
            if(word.length()>2){
                paramAwayTeam = paramAwayTeam + " " + word;
            }
        }
        if(getTime().equalsIgnoreCase(paramPalimpsestMatch.getTime())){
            if(homeTeam.equalsIgnoreCase(paramHomeTeam) || awayTeam.equalsIgnoreCase(paramAwayTeam)) {
                return true;
            }else{
                return false;
            }
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


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(homeTeam.getName());
        dest.writeString(awayTeam.getName());
        dest.writeString(palimpsest);
        dest.writeString(eventNumber);
        dest.writeString(matchTime);
        dest.writeString(bookMaker);
    }
}
