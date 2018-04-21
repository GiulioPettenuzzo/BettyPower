package com.bettypower.betMatchFinder.betFinderManagment;

import android.support.v7.util.AsyncListUtil;

import com.bettypower.betMatchFinder.entities.ConcreteOddsToFind;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.betMatchFinder.labelSet.BetLabelSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

public class StaticBetUpdater {
    private ArrayList<String> allQuoteFoundByRealTimeOCR = new ArrayList<>();
    private ArrayList<OddsToFind> allOddsFoundByRealTimeOCR = new ArrayList<>();
    private ArrayList<String> allQuoteFoundByStaticOCR = new ArrayList<>();
    private TreeMap<String, ArrayList<String>> hashBetAndBetKind;


    public StaticBetUpdater(ArrayList<OddsToFind> allOddsFoundByRealTimeOCR){
        this.allOddsFoundByRealTimeOCR = allOddsFoundByRealTimeOCR;
        for (OddsToFind currentOdds:allOddsFoundByRealTimeOCR
             ) {
            if(currentOdds.getOdds()!=null){
                allQuoteFoundByRealTimeOCR.add(currentOdds.getOdds());
            }
        }
        hashBetAndBetKind = new BetLabelSet().hashBetAndBetKind();
    }

    //TODO o la trova nella stessa posizione oppure validifica

    public ArrayList<MatchToFind> updateNewBet(String bet, ArrayList<MatchToFind> matchToFind){
        int index = isBetValidate(bet);
        if(index>=0){
            if(allOddsFoundByRealTimeOCR.get(index).getBet()!=null)
                matchToFind.get(matchToFind.size()-1).setBet(allOddsFoundByRealTimeOCR.get(index).getBet());
            if(allOddsFoundByRealTimeOCR.get(index).getBetKind()!=null)
                matchToFind.get(matchToFind.size()-1).setBetKind(allOddsFoundByRealTimeOCR.get(index).getBetKind());
            if( matchToFind.get(matchToFind.size()-1).getBet()!=null && matchToFind.get(matchToFind.size()-1).getBetKind()==null){
                Set<String> allBetKind = hashBetAndBetKind.keySet();
                ArrayList<String> allBetKindString = new ArrayList<>(allBetKind);
                Collections.sort(allBetKindString);

                for (String currentBetKind:allBetKindString
                     ) {
                    for (String currentBet:hashBetAndBetKind.get(currentBetKind)
                         ) {
                        if(matchToFind.get(matchToFind.size()-1).getBet().equalsIgnoreCase(currentBet)){
                            matchToFind.get(matchToFind.size()-1).setBetKind(currentBetKind);
                        }
                    }
                }
            }
            for(int i = 0;i<=index;i++){
                allOddsFoundByRealTimeOCR.remove(0);
            }
        }
        else{
            OddsToFind oddsToFind = new ConcreteOddsToFind();
            oddsToFind.setBet(bet);
            allOddsFoundByRealTimeOCR.add(oddsToFind);
        }
        return matchToFind;
    }

    public ArrayList<MatchToFind> updateNewBetKind(String betKind,ArrayList<MatchToFind> matchToFind){
        int index = isBetKindValidate(betKind);
        if(index>0){
            if(allOddsFoundByRealTimeOCR.get(index).getBet()!=null)
                matchToFind.get(matchToFind.size()-1).setBet(allOddsFoundByRealTimeOCR.get(index).getBet());
            if(allOddsFoundByRealTimeOCR.get(index).getBetKind()!=null)
                matchToFind.get(matchToFind.size()-1).setBetKind(allOddsFoundByRealTimeOCR.get(index).getBetKind());
            for(int i = 0;i<=index;i++){
                allOddsFoundByRealTimeOCR.remove(0);
            }
        }
        else{
            OddsToFind oddsToFind = new ConcreteOddsToFind();
            oddsToFind.setBetKind(betKind);
            allOddsFoundByRealTimeOCR.add(oddsToFind);
        }
        return matchToFind;
    }

    private int isBetValidate(String bet){
        for (OddsToFind currentOdds:allOddsFoundByRealTimeOCR
             ) {
            if(currentOdds.getBet()!=null && currentOdds.getBetKind()!=null && currentOdds.getBet().equalsIgnoreCase(bet)){
                return -1; //ritorna falso perchè in questo caso il match è già stato inserito,
            }
            else if(currentOdds.getBet()!=null && currentOdds.getBetKind()==null && currentOdds.getBet().equalsIgnoreCase(bet)){
                return allOddsFoundByRealTimeOCR.indexOf(currentOdds);
            }
            else if(currentOdds.getBet()==null && currentOdds.getBetKind()!=null){
                for (String currentBet:hashBetAndBetKind.get(currentOdds.getBetKind())
                     ) {
                    if(bet.equalsIgnoreCase(currentBet)){
                        currentOdds.setBet(bet);
                        return allOddsFoundByRealTimeOCR.indexOf(currentOdds);
                    }
                }
            }
        }
        return -1;
    }

    private int isBetKindValidate(String betKind){
        for (OddsToFind currentOdds:allOddsFoundByRealTimeOCR
                ) {
            if(currentOdds.getBet()!=null && currentOdds.getBetKind()!=null && currentOdds.getBetKind().equalsIgnoreCase(betKind)){
                return -1; //ritorna falso perchè in questo caso il match è già stato inserito,
            }
            else if(currentOdds.getBet()==null && currentOdds.getBetKind()!=null && currentOdds.getBetKind().equalsIgnoreCase(betKind)){
                return allOddsFoundByRealTimeOCR.indexOf(currentOdds);
            }
            else if(currentOdds.getBet()!=null && currentOdds.getBetKind()==null){
                for (String currentBet:hashBetAndBetKind.get(betKind)
                        ) {
                    if(currentBet.equalsIgnoreCase(currentOdds.getBet())){
                        currentOdds.setBet(currentBet);
                        return allOddsFoundByRealTimeOCR.indexOf(currentOdds);
                    }
                }

            }
        }
        return -1;
    }

    public ArrayList<MatchToFind> updateNewQuote(String quote,ArrayList<MatchToFind> matchToFind){
        boolean found = false;
        for(int i = 0;i<allQuoteFoundByRealTimeOCR.size();i++){
            if(quote.equalsIgnoreCase(allQuoteFoundByRealTimeOCR.get(i))){
                for(int j = 0;j<=i;j++){
                    allQuoteFoundByStaticOCR.add(allQuoteFoundByRealTimeOCR.remove(0));
                    found = true;
                }
            }
        }
        if(!found){
            allQuoteFoundByStaticOCR.add(quote);
        }
        return matchToFind;
    }

    public ArrayList<String> getAllQuoteFoundByStaticOCR() {
        allQuoteFoundByStaticOCR.addAll(allQuoteFoundByRealTimeOCR);
        return allQuoteFoundByStaticOCR;
    }
}
