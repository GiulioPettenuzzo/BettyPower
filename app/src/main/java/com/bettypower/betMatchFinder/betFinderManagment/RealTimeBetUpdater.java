package com.bettypower.betMatchFinder.betFinderManagment;

import android.util.Log;

import com.bettypower.betMatchFinder.entities.ConcreteOddsToFind;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.betMatchFinder.labelSet.BetLabelSet;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/*
  TEST
  I/realTime + date: 31/03
I/realTime + hour: 12:30
I/BET KIND DEBUG =: 1x2
I/realTime + betkind: 1x2
I/realTime + date: 31/03
I/realTime + betkind: goal/nogoal
I/realTime + date: 31/03
I/realTime + betkind: goal/nogoal
I/realTime + date: 31/03
I/realTime + hour: 15:00
I/realTime + bet: 2
I/realTime + quote: 2,32
I/realTime + quote: 2,25
I/realTime + hour: 02:25
I/NewDocumentActivity: onTakePhotoActivityResult
I/NewDocumentActivity: registerImageLoaderReceiver com.renard.ocr.documents.creation.NewDocumentActivity$1@c040296
I/ImageLoadAsyncTask: onPreExecute
I/NewDocumentActivity: onReceive com.renard.ocr.documents.viewing.grid.DocumentGridActivity@1f7efc4
                       showLoadingImageProgressDialog
I/realTime + betkind: 1x2
I/realTime + date: 31/03
I/realTime + hour: 15:00
I/realTime + betkind: 1x2
I/realTime + quote: 1,50
I/realTime + hour: 01:50
I/realTime + quote: 1,90
I/realTime + quote: 2,00
I/realTime + hour: 02:00
I/realTime + quote: 2,49
I/ExifInterface_JNI: Raw image not detected
I/realTime + quote: 52,17
I/realTime + bet: 1
I/BET KIND ===: 1x2
I/---------: ----------
I/BET KIND ===: goal/nogoal
I/---------: ----------
I/BET KIND ===: goal/nogoal
I/---------: ----------
I/BET ===: 2
I/---------: ----------
I/BET KIND ===: 1x2
I/---------: ----------
I/BET ===: 1
I/BET KIND ===: 1x2
I/---------: ----------
             ----------
I/QUOTE ===: 2,32
I/---------: ----------
I/QUOTE ===: 2,25
I/---------: ----------
I/QUOTE ===: 1,50
I/---------: ----------
I/QUOTE ===: 1,90
I/---------: ----------
I/QUOTE ===: 2,00
I/---------: ----------
I/QUOTE ===: 2,49
I/---------: ----------
I/QUOTE ===: 52,17
I/---------: ----------
 */

public class RealTimeBetUpdater {
    private ArrayList<OddsToFind> mainListOfOdds = new ArrayList<>();
    private ArrayList<String> mainListOfQuote = new ArrayList<>();
    private TreeMap<String, ArrayList<String>> hashBetAndBetKind;

    public RealTimeBetUpdater(){
        OddsToFind oddsToFind = new ConcreteOddsToFind();
        hashBetAndBetKind = new BetLabelSet().hashBetAndBetKind();
        mainListOfOdds.add(oddsToFind);
    }

    //TODO potrei fare un controllo anche sull'indice
    public ArrayList<MatchToFind> updateNewBet(String bet, ArrayList<MatchToFind> matchToFind){
        if(mainListOfOdds.get(mainListOfOdds.size()-1).getBet()==null){
            if(isBetValidate(bet)){
                matchToFind.get(matchToFind.size()-1).setBet(mainListOfOdds.get(mainListOfOdds.size()-1).getBet());
                matchToFind.get(matchToFind.size()-1).setBetKind(mainListOfOdds.get(mainListOfOdds.size()-1).getBetKind());
            }
        }else{
            OddsToFind oddsToFind = new ConcreteOddsToFind();
            oddsToFind.setBet(bet);
            mainListOfOdds.add(oddsToFind);
        }
        return matchToFind;
    }

    public ArrayList<MatchToFind> updateNewBetKind(String betKind,ArrayList<MatchToFind> matchToFind){
        if(mainListOfOdds.get(mainListOfOdds.size()-1).getBetKind()==null){
            if(isBetKindValidate(betKind)){
                matchToFind.get(matchToFind.size()-1).setBet(mainListOfOdds.get(mainListOfOdds.size()-1).getBet());
                matchToFind.get(matchToFind.size()-1).setBetKind(mainListOfOdds.get(mainListOfOdds.size()-1).getBetKind());
            }
        }
        else{
            OddsToFind oddsToFind = new ConcreteOddsToFind();
            oddsToFind.setBetKind(betKind);
            mainListOfOdds.add(oddsToFind);
        }
        return matchToFind;
    }

    public ArrayList<MatchToFind> updateNewQuote(String quote,ArrayList<MatchToFind> matchToFind){
        mainListOfQuote.add(quote); //TODO validator
        return matchToFind;
    }

    /*
     * se ritorna true aggiorno e betKind in mainlist of matches altrimenti non fa nulla. aggiorna solo main list of quote
     * deve controllare che i valori siano coerenti
     */
    private boolean isBetValidate(String bet){
        String betKind = mainListOfOdds.get(mainListOfOdds.size()-1).getBetKind();
        if(betKind!=null){
            for (String currentBet:hashBetAndBetKind.get(betKind)
                 ) {
                if(currentBet.equalsIgnoreCase(bet)){
                    mainListOfOdds.get(mainListOfOdds.size()-1).setBet(bet);
                    return true;
                }
            }
        }
        //confronto anche con l'ultimo trovato!
        try {
            String lastBetKind = mainListOfOdds.get(mainListOfOdds.size() - 2).getBetKind();
            String lastBet = mainListOfOdds.get(mainListOfOdds.size() - 2).getBet();
            if (lastBet==null && lastBetKind!=null){
                for (String currentBet:hashBetAndBetKind.get(lastBetKind)
                        ) {
                    if(currentBet.equalsIgnoreCase(bet)){
                        //inserisce nel penultimo
                        mainListOfOdds.get(mainListOfOdds.size() - 2).setBet(bet);
                        //e rimuove l'ultimo non può essere che l'ocr legga prima uno e dopo l'altra
                        mainListOfOdds.remove(mainListOfOdds.size()-1);
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            if(betKind==null) {
                mainListOfOdds.get(mainListOfOdds.size() - 1).setBet(bet);
            }else{
                OddsToFind oddsToFind = new ConcreteOddsToFind();
                oddsToFind.setBet(bet);
                mainListOfOdds.add(oddsToFind);
            }
            return  false;
        }
        if(betKind==null) {
            mainListOfOdds.get(mainListOfOdds.size() - 1).setBet(bet);
        }else{
            OddsToFind oddsToFind = new ConcreteOddsToFind();
            oddsToFind.setBet(bet);
            mainListOfOdds.add(oddsToFind);
        }

        return false;
    }

    /*
     * se ritorna true aggiorno bet e betkind su mainListofMatches altrimenti aggiorna solo list of odds
     */
    private boolean isBetKindValidate(String betKind){
        String bet = mainListOfOdds.get(mainListOfOdds.size()-1).getBet();
        Log.i("BET KIND DEBUG = ",betKind);
        if(bet!=null){
            for (String currentBet:hashBetAndBetKind.get(betKind)
                 ) {
                if(currentBet.equalsIgnoreCase(bet)){
                    mainListOfOdds.get(mainListOfOdds.size()-1).setBetKind(betKind);
                    return true;
                }
            }
        }
        try {
            String lastBetKind = mainListOfOdds.get(mainListOfOdds.size() - 2).getBetKind();
            String lastBet = mainListOfOdds.get(mainListOfOdds.size() - 2).getBet();
            if (lastBetKind == null && lastBet != null) {
                for(String currentBet:hashBetAndBetKind.get(betKind)){
                    if(currentBet.equalsIgnoreCase(lastBet)){
                        mainListOfOdds.get(mainListOfOdds.size()-1).setBetKind(betKind);
                        mainListOfOdds.remove(mainListOfOdds.size()-1);
                        return true;
                    }
                }
            }
        }
        catch (Exception e){
            if(bet==null) {
                mainListOfOdds.get(mainListOfOdds.size() - 1).setBetKind(betKind);
            }
            else{
                OddsToFind oddsToFind = new ConcreteOddsToFind();
                oddsToFind.setBetKind(betKind);
                mainListOfOdds.add(oddsToFind);
            }
            return false;
        }
        if(bet==null) {
            mainListOfOdds.get(mainListOfOdds.size() - 1).setBetKind(betKind);
        }
        else{
            OddsToFind oddsToFind = new ConcreteOddsToFind();
            oddsToFind.setBetKind(betKind);
            mainListOfOdds.add(oddsToFind);
        }
        return true;
    }

    /*
     * TODO devo ancora pensarla bene
     */
    private  boolean isQuoteValidate(){
        return true;
    }

    public ArrayList<OddsToFind> getMainListOfOdds(){
        for(int i = 0;i<mainListOfQuote.size();i++){
            try{
                mainListOfOdds.get(i).setOdds(mainListOfQuote.get(i));
            }
            catch(Exception e){
                OddsToFind oddsToFind = new ConcreteOddsToFind();
                oddsToFind.setOdds(mainListOfQuote.get(i));
                mainListOfOdds.add(oddsToFind);
            }
        }
        return mainListOfOdds;
    }
}
