package com.bettypower.betMatchFinder.entities;

import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 22/10/17.
 */

public interface MatchToFind {

    void setPalimpsestMatch(ArrayList<PalimpsestMatch> palimpsestMatch);
    ArrayList<PalimpsestMatch> getPalimpsestMatch();

    void setPalimpsest(String palimpsest);
    String getPalimpsest();

    void setHomeTeamName(String name);
    String getHomeName();

    void setAwayTeamName(String name);
    String getAwayName();

    void setUnknownTeamName(String name);
    String getUnknownTeamName();

    void setDate(String date);
    String getDate();

    void setHour(String hour);
    String getHour();

    String getBet();
    void setBet(String bet);

    String getOdds();
    void setOdds(String odds);

    String getBetKind();
    void setBetKind(String betKind);
}
