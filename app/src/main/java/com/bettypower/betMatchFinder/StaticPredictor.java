package com.bettypower.betMatchFinder;

import android.util.Log;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.betMatchFinder.labelSet.BetLabelSet;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    private BetLabelSet betLabelSet = new BetLabelSet();
    private ArrayList<String> allQuoteFound = new ArrayList<>();
    private Map<String,String> bookmakerAndMoney;

    public StaticPredictor(ArrayList<PalimpsestMatch> allPalimpsestMatch, String response, ArrayList<MatchToFind> allMatchFoundByRealTimeOCR, ArrayList<OddsToFind> allQuoteFoundByRealTimeOCR, Finder finder,Map<String,String> bookMakerAndMoney){
        this.response = response;
        this.finder = finder;
        staticUpdater = new StaticUpdater(allPalimpsestMatch,allMatchFoundByRealTimeOCR,allQuoteFoundByRealTimeOCR,bookMakerAndMoney);
        lastWordsNames = new ArrayList<>();

    }

    public void getAllElementFound(){
        StringTokenizer token = new StringTokenizer(response);
        Map<String,Object> allElementFound;
        long init = System.currentTimeMillis();
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            finder.setWord(word);
            allElementFound = finder.getWordKinds();
            if(!allElementFound.isEmpty()){
                allElementFound = realNameFilter(allElementFound);
                staticUpdater.updateNewElement(allElementFound);
            }
            for (String currentKey:allElementFound.keySet()
                    ) {
                Log.i("static + " + currentKey,allElementFound.get(currentKey).toString());
            }
        }

        Log.i("TEMPO FINDER",String.valueOf(System.currentTimeMillis()-init));

        allMatchesFound = staticUpdater.getAllMatchFound();
        ArrayList<MatchToFind> remained = staticUpdater.getAllMatchRemainedFromRealTimeOCR();
        bookmakerAndMoney = staticUpdater.getBookmakerAndMoney();
        allMatchesFound.addAll(remained);
        Assembler assembler = new Assembler(allMatchesFound);
        allMatchesFound = assembler.getAllMatchReassembled();
        allQuoteFound = staticUpdater.getAllQuote();
        int i = 0;
        for (MatchToFind currentMatch:allMatchesFound
                ) {
            Log.i("","");
            Log.i("NEW ","MATCH " + i);
            i++;
            Log.i("number match",String.valueOf(currentMatch.getPalimpsestMatch().size()));
          /*  if((currentMatch.getHomeName()!=null && currentMatch.getHomeName().equals("SYDNEY")) || (currentMatch.getUnknownTeamName()!=null && currentMatch.getUnknownTeamName().equals("SYDNEY"))){
                String x = "";
            }*/
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
            if(currentMatch.getBet()!=null) {
                //TODO quando hai finito ci aggiungi sta roba ma prima deve funzionare bene senza

              /*  BetLabelSet betLabelSet = new BetLabelSet();
                if(currentMatch.getBetKind()==null) {
                    Map<String, ArrayList<String>> allBetKind = betLabelSet.hashBetAndBetKind();
                    for (String currentBetKind : allBetKind.keySet()
                            ) {
                        for (String currentbet : allBetKind.get(currentBetKind)
                                ) {
                            if (currentMatch.getBet().equalsIgnoreCase(currentbet)) {
                                currentMatch.setBetKind(currentBetKind);
                            }
                        }
                    }
                }*/
                Log.i("BET", "" + currentMatch.getBet());
            }
            if(currentMatch.getBetKind()!=null)
                //TODO  quando hai finito ci aggiungi sta roba ma prima deve funzionare bene senza
              /*  if(currentMatch.getBet()==null){
                    currentMatch.setBet(betLabelSet.hashBetAndBetKind().get(currentMatch.getBetKind()).get(0));
                }*/
                Log.i("BET KIND",""+currentMatch.getBetKind());
            if(currentMatch.getOdds()!=null)
                Log.i("QUOTE",""+currentMatch.getOdds());
        }
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


    public ArrayList<MatchToFind> getAllMatchesFound(){
        return allMatchesFound;
    }

    public Map<String, String> getBookmakerAndMoney() {
        return bookmakerAndMoney;
    }

    public ArrayList<String> getAllQuoteFound() {
        return allQuoteFound;
    }
}
