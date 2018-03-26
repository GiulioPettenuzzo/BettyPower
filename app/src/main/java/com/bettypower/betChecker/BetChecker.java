package com.bettypower.betChecker;

import com.bettypower.entities.Bet;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 18/03/18.
 */

public class BetChecker {

    private Bet bet;
    private MatchChecker matchChecker = new MatchChecker();

    public boolean isBetWin(Bet bet){
        this.bet = bet;
        return checkBet();
    }

    public int getErrorNumber(Bet bet){
        int nummberOfError = 0;
        for (PalimpsestMatch currentPalimpsest:bet.getArrayMatch()
                ) {
            if(currentPalimpsest.getBet()!=null && !matchChecker.isMatchWin(currentPalimpsest.getHomeResult(),currentPalimpsest.getAwayResult(),currentPalimpsest.getBet(),currentPalimpsest.getBetKind(),currentPalimpsest.getResultTime())){
                nummberOfError++;
            }
        }
        return  nummberOfError;
    }

    public int getWinNumber(Bet bet){
        int winNumber = 0;
        for (PalimpsestMatch currentPalimpsest:bet.getArrayMatch()
                ) {
            if(currentPalimpsest.getBet()!=null && matchChecker.isMatchWin(currentPalimpsest.getHomeResult(),currentPalimpsest.getAwayResult(),currentPalimpsest.getBet(),currentPalimpsest.getBetKind(),currentPalimpsest.getResultTime())){
                winNumber++;
            }
        }
        return winNumber;
    }

    private boolean checkBet(){
        int nummberOfError = 0;
        for (PalimpsestMatch currentPalimpsest:bet.getArrayMatch()
             ) {
            if(currentPalimpsest.getBet()!=null && !matchChecker.isMatchWin(currentPalimpsest.getHomeResult(),currentPalimpsest.getAwayResult(),currentPalimpsest.getBet(),currentPalimpsest.getBetKind(),currentPalimpsest.getResultTime()) && !currentPalimpsest.isFissa()){
                nummberOfError++;
            }
            else if(currentPalimpsest.getBet()!=null && !matchChecker.isMatchWin(currentPalimpsest.getHomeResult(),currentPalimpsest.getAwayResult(),currentPalimpsest.getBet(),currentPalimpsest.getBetKind(),currentPalimpsest.getResultTime()) && currentPalimpsest.isFissa()){
                return false;
            }
        }
        if(bet.getErrors()!=null) {
            if (Integer.parseInt(bet.getErrors()) != 0) {
                nummberOfError = nummberOfError - Integer.parseInt(bet.getErrors());
            }
        }

        return nummberOfError <= 0;
    }




}
