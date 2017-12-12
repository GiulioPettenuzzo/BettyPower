package com.bettypower.betMatchFinder;

import android.util.Log;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class RealTimePredictor {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private RealTimeUpdater realTimeUpdater;
    private String response;
    private ArrayList<String> lastWordsNames;

    //result
    private ArrayList<MatchToFind> elementsFound;
    private ArrayList<String> oddsElementFound;


    public RealTimePredictor(ArrayList<PalimpsestMatch> allPalimpsestMatch, String response){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.response = response;
        realTimeUpdater = new RealTimeUpdater(allPalimpsestMatch);
        lastWordsNames = new ArrayList<>();
    }

    public void elaborateResponsetToFindMatch(){
        StringTokenizer token = new StringTokenizer(response);
        Finder finder = new Finder(allPalimpsestMatch);
        Map<String,Object> allElementFound;
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            finder.setWord(word);
            allElementFound = finder.getWordKinds();
            if(!allElementFound.isEmpty()) {
                allElementFound = realNameFilter(allElementFound);
                realTimeUpdater.updateNewElement(allElementFound);
            }
            for (String currentKey:allElementFound.keySet()
                    ) {
                Log.i("realTime + " + currentKey, allElementFound.get(currentKey).toString());

            }
        }

        elementsFound = realTimeUpdater.getMainListOfMatches();
        Assembler assembler = new Assembler(elementsFound);
        elementsFound = assembler.getAllMatchReassembled();
        int i = 0;
        for (MatchToFind currentMatch:elementsFound
             ) {
            Log.i("","");
            Log.i("NEW ","MATCH " + i);
            i++;
            Log.i("number match",String.valueOf(currentMatch.getPalimpsestMatch().size()));
            if(currentMatch.getPalimpsest()!=null)
                Log.i("PLIMPSEST",""+currentMatch.getPalimpsest());
            if(currentMatch.getHomeName()!=null)
                Log.i("HOME NAME",""+currentMatch.getHomeName());
            if(currentMatch.getAwayName()!=null)
                Log.i("AWAY NAME",""+currentMatch.getAwayName());
            if(currentMatch.getUnknownTeamName()!=null)
                Log.i("UNKNOWN NAME",""+currentMatch.getUnknownTeamName());
            if(currentMatch.getDate()!=null)
                Log.i("DATE",""+currentMatch.getDate());
            if(currentMatch.getHour()!=null)
                Log.i("HOUR",""+currentMatch.getHour());
            if(currentMatch.getBet()!=null)
                Log.i("BET",""+currentMatch.getBet());
            if(currentMatch.getBetKind()!=null)
                Log.i("BET KIND",""+currentMatch.getBetKind());
            if(currentMatch.getOdds()!=null)
                Log.i("QUOTE",""+currentMatch.getOdds());
        }
        oddsElementFound = realTimeUpdater.getMainListOfOdds();
    }

    /*
     * in order to delete the smaller names that are found before the biggest names
     */
    private Map<String,Object> realNameFilter(Map<String,Object> map){
        boolean isNameTeamFoundNow = false;
        if(map.containsKey(Finder.NAME)){
            String nameFound = map.get(Finder.NAME).toString();
            map.remove(Finder.NAME);
            for(int i = lastWordsNames.size()-1;i>=0;i--){
                if(nameFound.contains(lastWordsNames.get(i))){
                     lastWordsNames.remove(i);
                }
            }
            lastWordsNames.add(nameFound);
            isNameTeamFoundNow = true;
            Log.i("realTime + " ,nameFound);
        }
        if(map.containsKey(Finder.NAME_TWO)){
            String nameFound = map.get(Finder.NAME_TWO).toString();
            map.remove(Finder.NAME_TWO);
            for (String currentName:lastWordsNames
                    ) {
                if(nameFound.contains(currentName)){
                    lastWordsNames.remove(currentName);
                }
            }
            lastWordsNames.add(nameFound);
            isNameTeamFoundNow = true;
            Log.i("realTime + " ,nameFound);
        }
        if(!isNameTeamFoundNow){
            if(lastWordsNames.size()!=0){
                if(lastWordsNames.size() == 1){
                    String nameOne = lastWordsNames.get(0);
                    map.put(Finder.NAME, nameOne);
                    lastWordsNames.clear();
                }
                else if(lastWordsNames.size() == 2){
                    String nameOne = lastWordsNames.get(0);
                    map.put(Finder.NAME, nameOne);

                    String nameTwo = lastWordsNames.get(1);
                    map.put(Finder.NAME_TWO, nameTwo);
                    lastWordsNames.clear();
                }
                else{
                    Log.i("ERRORE","LASTWORDNAMES.SIZE() > 2");
                    lastWordsNames.clear();
                }
            }
        }
        return map;
    }

    public ArrayList<MatchToFind> getAllMatchToFind(){
        return elementsFound;
    }

    public ArrayList<String> getAllQuoteTofind(){
        return oddsElementFound;
    }
}
