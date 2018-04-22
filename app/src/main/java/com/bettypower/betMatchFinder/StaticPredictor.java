package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.entities.PalimpsestMatch;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class StaticPredictor {

    private String response;
    private StaticUpdater staticUpdater;
    private ArrayList<String> lastWordsNames;
    private Finder finder;

    private ArrayList<MatchToFind> allMatchesFound;
    private ArrayList<String> allQuoteFound = new ArrayList<>();

    public StaticPredictor(ArrayList<PalimpsestMatch> allPalimpsestMatch, String response, ArrayList<MatchToFind> allMatchFoundByRealTimeOCR, ArrayList<OddsToFind> allQuoteFoundByRealTimeOCR, Finder finder,Map<String,String> bookMakerAndMoney){
        this.response = response;
        this.finder = finder;
        staticUpdater = new StaticUpdater(allPalimpsestMatch,allMatchFoundByRealTimeOCR,allQuoteFoundByRealTimeOCR,bookMakerAndMoney);
        lastWordsNames = new ArrayList<>();

    }

    public void getAllElementFound(){
        StringTokenizer token = new StringTokenizer(response);
        Map<String,Object> allElementFound;
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            finder.setWord(word);
            allElementFound = finder.getWordKinds();
            if(!allElementFound.isEmpty()){
                allElementFound = realNameFilter(allElementFound);
                staticUpdater.updateNewElement(allElementFound);
            }
        }

        allMatchesFound = staticUpdater.getAllMatchFound();
        ArrayList<MatchToFind> remained = staticUpdater.getAllMatchRemainedFromRealTimeOCR();
        allMatchesFound.addAll(remained);
        Assembler assembler = new Assembler(allMatchesFound);
        allMatchesFound = assembler.getAllMatchReassembled();
        allQuoteFound = staticUpdater.getAllQuote();
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
                    lastWordsNames.clear();
                }
            }
        }
        return map;
    }


    public ArrayList<MatchToFind> getAllMatchesFound(){
        return allMatchesFound;
    }

    public ArrayList<String> getAllQuoteFound() {
        return allQuoteFound;
    }
}
