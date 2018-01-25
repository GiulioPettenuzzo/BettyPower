package com.bettypower.entities;

import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 21/06/17.
 */

public interface Match extends Parcelable {

     Team getHomeTeam();

     void setHomeTeam(Team homeTeam);

     Team getAwayTeam();

     void setAwayTeam(Team awayTeam);

     String getTime();

     String getDate();

     String getHour();

     void setTime(String time);

     String getHomeResult();

     void setBet(String bet);

     String getBet();

     void setBetKind(String betKind);

     String getBetKind();

     void setQuote(String quote);

     String getQuote();

     void setHomeResult(String result);

     String getAwayResult();

     void setAwayResult(String result);

     void setFissa(boolean fissa);

     boolean isFissa();

     ArrayList<HiddenResult> getAllHiddenResult();

     void setAllHiddenResult(ArrayList<HiddenResult> allHiddenResult);

     int getNumberOfHiddenResult();

     boolean compareTo(Match match);
    }


