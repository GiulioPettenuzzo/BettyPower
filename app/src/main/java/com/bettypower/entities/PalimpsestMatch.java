package com.bettypower.entities;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 01/08/17.
 * all the stuff that characterize the matches
 */

public interface PalimpsestMatch extends Parcelable{

    String getPalimpsest();
    void setPalimpsest(String palimpsest);

    String getEventNumber();
    void setEventNumber(String eventNumber);

    String getCompletePalimpsest();
    void setCompletePalimpsest(String completePalimpsest);

    Team getHomeTeam();
    void setHomeTeam(Team homeTeam);

    Team getAwayTeam();
    void setAwayTeam(Team awayTeam);

    String getTime();
    String getDate();
    String getHour();
    void setTime(String time);

    void setBet(String bet);
    String getBet();

    void setBetKind(String betKind);
    String getBetKind();

    void setQuote(String quote);
    String getQuote();

    Map<String,String> getAllOdds();
    void setAllOdds(Map<String,String> allOdds);

    String getHomeResult();
    void setHomeResult(String result);

    String getAwayResult();
    void setAwayResult(String result);

    void setFissa(boolean fissa);
    boolean isFissa();

    ArrayList<HiddenResult> getAllHiddenResult();
    void setAllHiddenResult(ArrayList<HiddenResult> allHiddenResult);

    boolean compareTo(PalimpsestMatch match);

    boolean compareToByName(PalimpsestMatch palimpsestMatch);

}
