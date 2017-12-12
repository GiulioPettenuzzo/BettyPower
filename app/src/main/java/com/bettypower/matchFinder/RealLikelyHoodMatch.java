package com.bettypower.matchFinder;

import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 14/08/17.
 */

public class RealLikelyHoodMatch implements LikelyHoodMatch {

    private PalimpsestMatch palimpsestMatch;
    private int likelyHood;

    public RealLikelyHoodMatch(PalimpsestMatch palimpsestMatch,int allText){
        this.palimpsestMatch = palimpsestMatch;
        this.likelyHood = allText;
    }

    public RealLikelyHoodMatch(PalimpsestMatch palimpsestMatch){
        this.palimpsestMatch = palimpsestMatch;
        this.likelyHood = -1;
    }

    @Override
    public void setPalimpsestMatch(PalimpsestMatch palimpsestMatch) {
        this.palimpsestMatch = palimpsestMatch;
    }

    @Override
    public PalimpsestMatch getPalimpsestMatch() {
        return palimpsestMatch;
    }

    @Override
    public void setLikelyHood(int likelyHood) {
        this.likelyHood = likelyHood;
    }

    @Override
    public int getLikelyHood() {
        return likelyHood;
    }

    @Override
    public boolean compareTo(PalimpsestMatch paramPalimpsestMatch) {
        String paramPalimpsest = paramPalimpsestMatch.getPalimpsest()+paramPalimpsestMatch.getEventNumber();
        String thisPalimpsest = palimpsestMatch.getPalimpsest()+palimpsestMatch.getEventNumber();
        if(paramPalimpsest.equalsIgnoreCase(thisPalimpsest)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * in order to compare two match not from the palimpsest but from the name
     * @param paramPalimpsestMatch
     * @return
     */
    public boolean compareToByName(PalimpsestMatch paramPalimpsestMatch){
        StringTokenizer homeToken = new StringTokenizer(palimpsestMatch.getHomeTeam().getName());
        String homeTeam = "";
        while(homeToken.hasMoreTokens()){
            String word = homeToken.nextToken();
            if(word.length()>2){
                homeTeam = homeTeam + " " + word;
            }
        }
        StringTokenizer awayToken = new StringTokenizer(palimpsestMatch.getAwayTeam().getName());
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
        if(palimpsestMatch.getTime().equalsIgnoreCase(paramPalimpsestMatch.getTime())){
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
}
