package com.bettypower.betMatchFinder.entities;

import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;

/**
 * this class is created in order to process better the information taken from the ocr result
 * Created by giuliopettenuzzo on 23/10/17.
 */

public class ConcreteMatchToFind implements MatchToFind {

    private ArrayList<PalimpsestMatch> palimpsestMatchFound;
    private String palimpsest;
    private String homeName;
    private String awayName;
    private String unknownTeamName;
    private String date;
    private String hour;
    private String betKind;
    private String bet;
    private String odds;

    @Override
    public void setPalimpsestMatch(ArrayList<PalimpsestMatch> palimpsestMatchFound) {
        this.palimpsestMatchFound = palimpsestMatchFound;
    }

    @Override
    public ArrayList<PalimpsestMatch> getPalimpsestMatch() {
        return palimpsestMatchFound;
    }

    @Override
    public void setPalimpsest(String palimpsest) {
        this.palimpsest = palimpsest;
    }

    @Override
    public String getPalimpsest() {
        return palimpsest;
    }

    @Override
    public void setHomeTeamName(String homeName) {
        this.homeName = homeName;
    }

    @Override
    public String getHomeName() {
        return homeName;
    }

    @Override
    public void setAwayTeamName(String awayName) {
        this.awayName = awayName;
    }

    @Override
    public String getAwayName() {
        return awayName;
    }

    @Override
    public void setUnknownTeamName(String name) {
        unknownTeamName = name;
    }

    @Override
    public String getUnknownTeamName() {
        return unknownTeamName;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setHour(String hour) {
        this.hour = hour;
    }

    @Override
    public String getHour() {
        return hour;
    }

    @Override
    public String getBet() {
        return bet;
    }

    @Override
    public void setBet(String bet) {
        this.bet = bet;
    }

    @Override
    public String getOdds() {
        return odds;
    }

    @Override
    public void setOdds(String odds) {
        this.odds = odds;
    }

    @Override
    public String getBetKind() {
        return betKind;
    }

    @Override
    public void setBetKind(String betKind) {
        this.betKind = betKind;
    }
}
