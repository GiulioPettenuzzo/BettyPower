package com.bettypower.betChecker;

import android.content.Context;

import com.bettypower.entities.Bet;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.util.Helper;
/**
 * Created by giuliopettenuzzo on 18/03/18.
 */

public class BetChecker {

    private Bet bet;
    private MatchChecker matchChecker = new MatchChecker();
    private Helper helper;

    public boolean isBetWin(Bet bet,Context context){
        this.bet = bet;
        helper = new Helper(context);
        return checkBet();
    }

    public int getErrorNumber(Bet bet){
        int nummberOfError = 0;
        for (PalimpsestMatch currentPalimpsest:bet.getArrayMatch()
                ) {
            if(currentPalimpsest.getBet()!=null && !matchChecker.isMatchWin(currentPalimpsest)){
                nummberOfError++;
            }
        }
        return  nummberOfError;
    }

    public int getWinNumber(Bet bet){
        int winNumber = 0;
        for (PalimpsestMatch currentPalimpsest:bet.getArrayMatch()
                ) {
            if(currentPalimpsest.getBet()!=null && matchChecker.isMatchWin(currentPalimpsest) && helper.isMatchFinish(currentPalimpsest)){
                winNumber++;
            }
        }
        return winNumber;
    }

    private boolean checkBet(){
        int nummberOfError = 0;
        for (PalimpsestMatch currentPalimpsest:bet.getArrayMatch()
             ) {
            if(currentPalimpsest.getBet()!=null && !matchChecker.isMatchWin(currentPalimpsest)){
                nummberOfError++;
            }
            if(currentPalimpsest.getBet()!=null && !matchChecker.isMatchWin(currentPalimpsest) && currentPalimpsest.isFissa()){
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
