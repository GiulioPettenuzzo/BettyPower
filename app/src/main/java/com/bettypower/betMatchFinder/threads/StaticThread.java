package com.bettypower.betMatchFinder.threads;

import android.util.Log;

import com.bettypower.betMatchFinder.Finder;
import com.bettypower.betMatchFinder.StaticPredictor;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.betMatchFinder.listeners.StaticElaborationListener;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class StaticThread extends Thread {

    private String staticResponse;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private StaticElaborationListener staticElaborationListener;
    private ArrayList<MatchToFind> allMatchFoundByRealTimeOCR;
    private ArrayList<OddsToFind> allQuoteFoundByRealTimeOCR;
    private Map<String,String> bookmakerAndMoney;
    private Finder finder;

    public StaticThread(ArrayList<PalimpsestMatch> allPalimpsestMatch, String realTimeResponse, ArrayList<MatchToFind> allMatchFoundByRealTimeOCR, ArrayList<OddsToFind> allQuoteFoundByRealTimeOCR, Finder finder, Map<String,String> bookmakerAndMoney){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.staticResponse = realTimeResponse;
        this.allMatchFoundByRealTimeOCR = allMatchFoundByRealTimeOCR;
        this.allQuoteFoundByRealTimeOCR = allQuoteFoundByRealTimeOCR;
        this.bookmakerAndMoney = bookmakerAndMoney;
        this.finder = finder;

    }
    @Override
    public void run() {
        super.run();
        long init = System.currentTimeMillis();
        StaticPredictor predictor = new StaticPredictor(allPalimpsestMatch,staticResponse,allMatchFoundByRealTimeOCR,allQuoteFoundByRealTimeOCR,finder,bookmakerAndMoney);
        predictor.getAllElementFound(); //elaboration
        Log.i("STATIC PREDICTOR",String.valueOf(System.currentTimeMillis()-init));
        for (MatchToFind current:predictor.getAllMatchesFound()
             ) {
            if(current.getBet()!=null){
                Log.i("bet====",current.getBet());
            }
        }
        ArrayList<PalimpsestMatch> allPalimpsestFound = new ArrayList<>();
        for (MatchToFind currentMatchToFind:predictor.getAllMatchesFound()
             ) {
            if(currentMatchToFind.getPalimpsestMatch().size()==1){
                if(!isPalimpsestPresent(currentMatchToFind.getPalimpsestMatch().get(0).getPalimpsest(),allPalimpsestFound)){
                    PalimpsestMatch palimpsestMatch = currentMatchToFind.getPalimpsestMatch().get(0);
                    /*if(currentMatchToFind.getBet()!=null){
                        palimpsestMatch.setBet(currentMatchToFind.getBet());
                    }
                    if(currentMatchToFind.getBetKind()!=null){
                        palimpsestMatch.setBetKind(currentMatchToFind.getBetKind());
                    }*/
                    allPalimpsestFound.add(palimpsestMatch);
                }
            }
        }
        ArrayList<String> allQuoteFound = predictor.getAllQuoteFound();

       /* bookmakerAndMoney = predictor.getBookmakerAndMoney();
        for(int i = 0;i<allQuoteFound.size();i++){
            try {
                allPalimpsestFound.get(i).setQuote(allQuoteFound.get(i));
                Log.i("SIZE OF ALLMATCH = ",String.valueOf(allPalimpsestFound.size()));
            }catch (IndexOutOfBoundsException e){
                if(bookmakerAndMoney.get("puntata")==null){
                    bookmakerAndMoney.put("puntata",allQuoteFound.get(i));
                }
            }
        }*/
        try{
            if(bookmakerAndMoney.get("puntata")==null){
                bookmakerAndMoney.put("puntata",allQuoteFound.get(allPalimpsestFound.size()));
            }
        }catch(IndexOutOfBoundsException e){

        }
        staticElaborationListener.onElaborationCompleted(allPalimpsestFound,bookmakerAndMoney);
    }

    private boolean isPalimpsestPresent(String palimpsest,ArrayList<PalimpsestMatch> allPalimpsestMatch){
        for (PalimpsestMatch currentPalimpsest:allPalimpsestMatch
             ) {
            if(palimpsest.equalsIgnoreCase(currentPalimpsest.getPalimpsest())){
                return true;
            }
        }
        return false;
    }


    public void setStaticElaborationListener(StaticElaborationListener staticElaborationListener){
        this.staticElaborationListener = staticElaborationListener;
    }

}
