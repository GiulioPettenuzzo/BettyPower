package com.bettypower.betChecker;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableHiddenResult;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 18/03/18.
 * Class used to decide if a bet is win or loose
 */

public class MatchChecker {

    private int homeResult;
    private int awayResult;
    private String betKind;
    private String bet;
    private ConcreteChecker concreteChecker;

    private int firstTimeHomeResult = -1;
    private int firstTimeAwayResult = -1;

    private int secondTimeHomeResult = -1;
    private int secondTimeAwayResult = -1;

    public boolean isMatchWin(PalimpsestMatch palimpsestMatch){
        if(palimpsestMatch.getHomeResult().equals("-") || palimpsestMatch.getAwayResult().equals("-")){
            return true;
        }
        this.homeResult = Integer.parseInt(palimpsestMatch.getHomeResult());
        this.awayResult = Integer.parseInt(palimpsestMatch.getAwayResult());
        this.bet = palimpsestMatch.getBet();
        this.betKind = palimpsestMatch.getBetKind();
        getResultsPerTime(palimpsestMatch.getAllHiddenResult());
        this.concreteChecker = new ConcreteChecker(this.homeResult,this.awayResult,this.firstTimeHomeResult,this.firstTimeAwayResult,this.secondTimeHomeResult,this.secondTimeAwayResult);
        return checkMatch();
    }

    private void getResultsPerTime(ArrayList<HiddenResult> allHiddenResult){
        if(allHiddenResult==null || allHiddenResult.size()==0){
            firstTimeHomeResult = -1;
            firstTimeAwayResult = -1;
            secondTimeHomeResult = -1;
            secondTimeAwayResult = -1;
        }
        else{
            String firstTimeResult = "";
            String secondTimeResult = "";
            for (HiddenResult currentHiddenResult:allHiddenResult
                 ) {
                if(currentHiddenResult.getAction().equalsIgnoreCase(ParcelableHiddenResult.ACTION_GOAL)&& Integer.parseInt(currentHiddenResult.getTime().substring(0,currentHiddenResult.getTime().length()-1))<=45 ){
                    firstTimeResult = currentHiddenResult.getResult();
                }
                if(currentHiddenResult.getAction().equalsIgnoreCase(ParcelableHiddenResult.ACTION_GOAL)&&Integer.parseInt(currentHiddenResult.getTime().substring(0,currentHiddenResult.getTime().length()-1))>45){
                    secondTimeResult = currentHiddenResult.getResult();
                }
            }
            //firstTimeResult = firstTimeResult.replace(":"," ");
            if(!firstTimeResult.isEmpty()) {
                StringTokenizer firstToken = new StringTokenizer(firstTimeResult, ":");
                firstTimeHomeResult = Integer.parseInt(firstToken.nextToken().substring(1));
                String away = firstToken.nextToken();
                firstTimeAwayResult = Integer.parseInt(away.substring(0, away.length()-1));
            }
            else{
                firstTimeHomeResult = 0;
                firstTimeAwayResult = 0;
            }

           // secondTimeResult = secondTimeResult.replace(":"," ");
            if(!secondTimeResult.isEmpty()) {
                StringTokenizer secondToken = new StringTokenizer(secondTimeResult, ":");
                secondTimeHomeResult = Integer.parseInt(secondToken.nextToken().substring(1));
                String away = secondToken.nextToken();
                secondTimeAwayResult = Integer.parseInt(away.substring(0,away.length()-1));
            }
            else{
                secondTimeHomeResult = 0;
                secondTimeAwayResult = 0;
            }
        }
    }

    private boolean noHiddenResult(){
        return firstTimeHomeResult < 0 && firstTimeAwayResult < 0 && secondTimeHomeResult < 0 && secondTimeAwayResult < 0;
    }

    private boolean checkMatch(){
        //1X2
        if(betKind.equalsIgnoreCase("1X2")) {
            if (bet.equalsIgnoreCase("1")) {
                return concreteChecker.uno();
            }
            if (bet.equalsIgnoreCase("X")) {
                return concreteChecker.x();
            }
            if (bet.equalsIgnoreCase("2")) {
                return concreteChecker.due();
            }
        }

        //doppia chance
        if(betKind.equalsIgnoreCase("Doppia Chance")) {
            if (bet.equalsIgnoreCase("1X")) {
                return concreteChecker.unoX();
            }
            if (bet.equalsIgnoreCase("12")) {
                return concreteChecker.unoDue();
            }
            if (bet.equalsIgnoreCase("X2")) {
                return concreteChecker.dueX();
            }
        }

        //goal/nogoal
        if(betKind.equalsIgnoreCase("Goal / NoGoal")) {
            if (bet.equalsIgnoreCase("Goal")) {
                return concreteChecker.goal();
            }
            if (bet.equalsIgnoreCase("no goal")) {
                return concreteChecker.noGoal();
            }
        }

        //Under/Over
        if(betKind.equalsIgnoreCase("Under / Over")) {
            if (bet.equalsIgnoreCase("Under 0,5")) {
                return concreteChecker.under05();
            }
            if (bet.equalsIgnoreCase("Over 0,5")) {
                return concreteChecker.over05();
            }
            if (bet.equalsIgnoreCase("Under 1,5")) {
                return concreteChecker.under15();
            }
            if (bet.equalsIgnoreCase("Over 1,5")) {
                return concreteChecker.over15();
            }
            if (bet.equalsIgnoreCase("Under 2,5")) {
                return concreteChecker.under25();
            }
            if (bet.equalsIgnoreCase("Over 2,5")) {
                return concreteChecker.over25();
            }
            if (bet.equalsIgnoreCase("Under 3,5")) {
                return concreteChecker.under35();
            }
            if (bet.equalsIgnoreCase("Over 3,5")) {
                return concreteChecker.over35();
            }
            if (bet.equalsIgnoreCase("Under 4,5")) {
                return concreteChecker.under45();
            }
            if (bet.equalsIgnoreCase("Over 4,5")) {
                return concreteChecker.over45();
            }
            if (bet.equalsIgnoreCase("Under 5,5")) {
                return concreteChecker.under55();
            }
            if (bet.equalsIgnoreCase("Over 5,5")) {
                return concreteChecker.over55();
            }
            if (bet.equalsIgnoreCase("Under 6,5")) {
                return concreteChecker.under65();
            }
            if (bet.equalsIgnoreCase("Over 6,5")) {
                return concreteChecker.over65();
            }
            if (bet.equalsIgnoreCase("Under 7,5")) {
                return concreteChecker.under75();
            }
            if (bet.equalsIgnoreCase("Over 7,5")) {
                return concreteChecker.over75();
            }
        }

        //Handicap
        if(betKind.equalsIgnoreCase("Handicap")) {
            if (bet.equalsIgnoreCase("1 (0:1)")) {
                return concreteChecker.handicap101();
            }
            if (bet.equalsIgnoreCase("X (0:1)")) {
                return concreteChecker.handicapX01();
            }
            if (bet.equalsIgnoreCase("2 (0:1)")) {
                return concreteChecker.handicap201();
            }
            if (bet.equalsIgnoreCase("1 (1:0)")) {
                return concreteChecker.handicap110();
            }
            if (bet.equalsIgnoreCase("X (1:0)")) {
                return concreteChecker.handicapX10();
            }
            if (bet.equalsIgnoreCase("2 (1:0)")) {
                return concreteChecker.handicap210();
            }
            if (bet.equalsIgnoreCase("1 (0:2)")) {
                return concreteChecker.handicap102();
            }
            if (bet.equalsIgnoreCase("X (0:2)")) {
                return concreteChecker.handicapX02();
            }
            if (bet.equalsIgnoreCase("2 (0:2)")) {
                return concreteChecker.handicap202();
            }
            if (bet.equalsIgnoreCase("1 (2:0)")) {
                return concreteChecker.handicap120();
            }
            if (bet.equalsIgnoreCase("X (2:0)")) {
                return concreteChecker.handicapX20();
            }
            if (bet.equalsIgnoreCase("2 (2:0)")) {
                return concreteChecker.handicap220();
            }
        }

        //pari - dispari
        if(betKind.equalsIgnoreCase("Pari / Dispari")) {
            if (bet.equalsIgnoreCase("Dispari")) {
                return concreteChecker.dispari();
            }
            if (bet.equalsIgnoreCase("Pari")) {
                return concreteChecker.pari();
            }
            if (bet.equalsIgnoreCase("Dispari 1T") && !noHiddenResult()){
                return concreteChecker.dispari1time();
            }
            if (bet.equalsIgnoreCase("Pari 1T") && !noHiddenResult()){
                return concreteChecker.pari1time();
            }
        }

        if(betKind.equalsIgnoreCase("Primo Tempo")&&!noHiddenResult()){
            if(bet.equalsIgnoreCase("1")){
                return concreteChecker.uno1tempo();
            }
            if(bet.equalsIgnoreCase("X")){
                return concreteChecker.x1tempo();
            }
            if(bet.equalsIgnoreCase("2")){
                return concreteChecker.due1tempo();
            }
        }

        if(betKind.equalsIgnoreCase("Primo Tempo / Secondo Tempo")&&!noHiddenResult()){
            if(bet.equalsIgnoreCase("1 / 1")){
                return concreteChecker.unoUno();
            }
            if(bet.equalsIgnoreCase("1 / X")){
                return concreteChecker.unoAndX ();
            }
            if(bet.equalsIgnoreCase("1 / 2")){
                return concreteChecker.unoAndDue();
            }
            if(bet.equalsIgnoreCase("X / 1")){
                return concreteChecker.xUno();
            }
            if(bet.equalsIgnoreCase("X / X")){
                return concreteChecker.xX();
            }
            if(bet.equalsIgnoreCase("X / 2")){
                return concreteChecker.xDue();
            }
            if(bet.equalsIgnoreCase("2 / 1")){
                return concreteChecker.dueUno();
            }
            if(bet.equalsIgnoreCase("2 / X")){
                return concreteChecker.dueandX();
            }
            if(bet.equalsIgnoreCase("2 / 2")){
                return concreteChecker.dueDue();
            }
        }

        //risultato finale
        if(betKind.equalsIgnoreCase("Risultato Finale")) {
            if (bet.equalsIgnoreCase("1-0")) {
                return concreteChecker.final10();
            }
            if (bet.equalsIgnoreCase("2-0")) {
                return concreteChecker.final20();
            }
            if (bet.equalsIgnoreCase("2-1")) {
                return concreteChecker.final21();
            }
            if (bet.equalsIgnoreCase("3-0")) {
                return concreteChecker.final30();
            }
            if (bet.equalsIgnoreCase("3-1")) {
                return concreteChecker.final31();
            }
            if (bet.equalsIgnoreCase("3-2")) {
                return concreteChecker.final32();
            }
            if (bet.equalsIgnoreCase("4-0")) {
                return concreteChecker.final40();
            }
            if (bet.equalsIgnoreCase("4-1")) {
                return concreteChecker.final41();
            }
            if (bet.equalsIgnoreCase("4-2")) {
                return concreteChecker.final42();
            }
            if (bet.equalsIgnoreCase("4-3")) {
                return concreteChecker.final43();
            }
            if (bet.equalsIgnoreCase("0-0")) {
                return concreteChecker.final00();
            }
            if (bet.equalsIgnoreCase("1-1")) {
                return concreteChecker.final11();
            }
            if (bet.equalsIgnoreCase("2-2")) {
                return concreteChecker.final22();
            }
            if (bet.equalsIgnoreCase("3-3")) {
                return concreteChecker.final33();
            }
            if (bet.equalsIgnoreCase("4-4")) {
                return concreteChecker.final44();
            }
            if (bet.equalsIgnoreCase("0-1")) {
                return concreteChecker.final01();
            }
            if (bet.equalsIgnoreCase("0-2")) {
                return concreteChecker.final02();
            }
            if (bet.equalsIgnoreCase("1-2")) {
                return concreteChecker.final12();
            }
            if (bet.equalsIgnoreCase("0-3")) {
                return concreteChecker.final03();
            }
            if (bet.equalsIgnoreCase("1-3")) {
                return concreteChecker.final13();
            }
            if (bet.equalsIgnoreCase("2-3")) {
                return concreteChecker.final23();
            }
            if (bet.equalsIgnoreCase("0-4")) {
                return concreteChecker.final04();
            }
            if (bet.equalsIgnoreCase("1-4")) {
                return concreteChecker.final14();
            }
            if (bet.equalsIgnoreCase("2-4")) {
                return concreteChecker.final24();
            }
            if (bet.equalsIgnoreCase("3-4")) {
                return concreteChecker.final34();
            }
        }

        //goal totali

        if(betKind.equalsIgnoreCase("Gol Totali")) {
            if (bet.equalsIgnoreCase("0")) {
                return concreteChecker.tot0();
            }
            if (bet.equalsIgnoreCase("1")) {
                return concreteChecker.tot1();
            }
            if (bet.equalsIgnoreCase("2")) {
                return concreteChecker.tot2();
            }
            if (bet.equalsIgnoreCase("3")) {
                return concreteChecker.tot3();
            }
            if (bet.equalsIgnoreCase("4")) {
                return concreteChecker.tot4();
            }
            if (bet.equalsIgnoreCase("5")) {
                return concreteChecker.tot5();
            }
            if (bet.equalsIgnoreCase("5+")) {
                return concreteChecker.tot5plus();
            }
            if (bet.equalsIgnoreCase("6")) {
                return concreteChecker.tot6();
            }
            if (bet.equalsIgnoreCase("7")) {
                return concreteChecker.tot7();
            }
            if (bet.equalsIgnoreCase("7+")) {
                return concreteChecker.tot7plus();
            }
        }

        //segna goal squadra in casa

        if(betKind.equalsIgnoreCase("Segna Squadra In Casa")) {
            if (bet.equalsIgnoreCase("si")) {
                return concreteChecker.segnaSquadraCasaSI();
            }
            if (bet.equalsIgnoreCase("no")) {
                return concreteChecker.segnaSquadraCasaNO();
            }
        }

        if(betKind.equalsIgnoreCase("Segna squadra fuori casa")) {
            if (bet.equalsIgnoreCase("si")) {
                return concreteChecker.segaSquadraOspiteSI();
            }
            if (bet.equalsIgnoreCase("no")) {
                return concreteChecker.segnaSquadraOspiteNO();
            }
        }

        if(betKind.equalsIgnoreCase("Under / Over primo tempo") && !noHiddenResult()){
            if(bet.equalsIgnoreCase("Under 0,5")){
                return concreteChecker.under05primo();
            }
            if(bet.equalsIgnoreCase("Over 0,5")){
                return concreteChecker.over05primo();
            }
            if(bet.equalsIgnoreCase("Under 1,5")){
                return concreteChecker.under15primo();
            }
            if(bet.equalsIgnoreCase("Over 1,5")){
                return concreteChecker.over15primo();
            }
            if(bet.equalsIgnoreCase("Under 2,5")){
                return concreteChecker.under25primo();
            }
            if(bet.equalsIgnoreCase("Over 2,5")){
                return concreteChecker.over25primo();
            }
            if(bet.equalsIgnoreCase("Under 3,5")){
                return concreteChecker.under35primo();
            }
            if(bet.equalsIgnoreCase("Over 3,5")){
                return concreteChecker.over35primo();
            }
            if(bet.equalsIgnoreCase("Under 4,5")){
                return concreteChecker.under45primo();
            }
            if(bet.equalsIgnoreCase("Over 4,5")){
                return concreteChecker.over45primo();
            }
            if(bet.equalsIgnoreCase("Under 5,5")){
                return concreteChecker.under55primo();
            }
            if(bet.equalsIgnoreCase("Over 5,5")){
                return concreteChecker.over55primo();
            }

        }

        if(betKind.equalsIgnoreCase("Under / Over secondo tempo") && !noHiddenResult()){
            if(bet.equalsIgnoreCase("Under 0,5")){
                return concreteChecker.under05secondo();
            }
            if(bet.equalsIgnoreCase("Over 0,5")){
                return concreteChecker.over05secondo();
            }
            if(bet.equalsIgnoreCase("Under 1,5")){
                return concreteChecker.under15secondo();
            }
            if(bet.equalsIgnoreCase("Over 1,5")){
                return concreteChecker.over15secondo();
            }
            if(bet.equalsIgnoreCase("Under 2,5")){
                return concreteChecker.under25secondo();
            }
            if(bet.equalsIgnoreCase("Over 2,5")){
                return concreteChecker.over25secondo();
            }
            if(bet.equalsIgnoreCase("Under 3,5")){
                return concreteChecker.under35secondo();
            }
            if(bet.equalsIgnoreCase("Over 3,5")){
                return concreteChecker.over35secondo();
            }
            if(bet.equalsIgnoreCase("Under 4,5")){
                return concreteChecker.under45secondo();
            }
            if(bet.equalsIgnoreCase("Over 4,5")){
                return concreteChecker.over45secondo();
            }
            if(bet.equalsIgnoreCase("Under 5,5")){
                return concreteChecker.under55secondo();
            }
            if(bet.equalsIgnoreCase("Over 5,5")){
                return concreteChecker.over55secondo();
            }
        }

        if(betKind.equalsIgnoreCase("Goal totali squadra in casa")){
            if(bet.equalsIgnoreCase("Under 1,5")){
                return concreteChecker.casaUnder15();
            }
            if(bet.equalsIgnoreCase("Over 1,5")){
                return concreteChecker.casaOver15();
            }
            if(bet.equalsIgnoreCase("Under 2,5")){
                return concreteChecker.casaUnder25();
            }
            if(bet.equalsIgnoreCase("Over 2,5")){
                return concreteChecker.casaOver25();
            }
            if(bet.equalsIgnoreCase("Under 3,5")){
                return concreteChecker.casaUnder35();
            }
            if(bet.equalsIgnoreCase("Over 3,5")){
                return concreteChecker.casaOver35();
            }
            if(bet.equalsIgnoreCase("Under 4,5")){
                return concreteChecker.casaUnder45();
            }
            if(bet.equalsIgnoreCase("Over 4,5")){
                return concreteChecker.casaOver45();
            }
            if(bet.equalsIgnoreCase("Under 5,5")){
                return concreteChecker.casaUnder55();
            }
            if(bet.equalsIgnoreCase("Over 5,5")){
                return concreteChecker.casaOver55();
            }
        }

        if(betKind.equalsIgnoreCase("Goal totali squadra fuori casa")){
            if(bet.equalsIgnoreCase("Under 1,5")){
                return concreteChecker.ospiteUnder15();
            }
            if(bet.equalsIgnoreCase("Over 1,5")){
                return concreteChecker.ospiteOver15();
            }
            if(bet.equalsIgnoreCase("Under 2,5")){
                return concreteChecker.ospiteUnder25();
            }
            if(bet.equalsIgnoreCase("Over 2,5")){
                return concreteChecker.ospiteOver25();
            }
            if(bet.equalsIgnoreCase("Under 3,5")){
                return concreteChecker.ospiteUnder35();
            }
            if(bet.equalsIgnoreCase("Over 3,5")){
                return concreteChecker.ospiteOver35();
            }
            if(bet.equalsIgnoreCase("Under 4,5")){
                return concreteChecker.ospiteUnder45();
            }
            if(bet.equalsIgnoreCase("Over 4,5")){
                return concreteChecker.ospiteOver45();
            }
            if(bet.equalsIgnoreCase("Under 5,5")){
                return concreteChecker.ospiteUnder55();
            }
            if(bet.equalsIgnoreCase("Over 5,5")){
                return concreteChecker.ospiteOver55();
            }
        }

        if(betKind.equalsIgnoreCase("Squadra in casa vittoria a zero")) {
            if (bet.equalsIgnoreCase("si")) {
                return concreteChecker.casaVittoriaZeroSI();
            }
            if (bet.equalsIgnoreCase("no")) {
                return concreteChecker.casaVittoriaZeroNO();
            }
        }

        if(betKind.equalsIgnoreCase("Squadra fuori casa vittoria a zero")) {
            if (bet.equalsIgnoreCase("si")) {
                return concreteChecker.ospiteVittoriaZeroSI();
            }
            if (bet.equalsIgnoreCase("no")) {
                return concreteChecker.ospiteVittoriaZeroNO();
            }
        }

        if(betKind.equalsIgnoreCase("Goal / NoGoal primo tempo") && !noHiddenResult()){
            if(bet.equalsIgnoreCase("Gol")){
                return concreteChecker.goalPrimo();
            }
            if(bet.equalsIgnoreCase("NoGol")){
                return concreteChecker.noGoalPrimo();
            }
        }

        if(betKind.equalsIgnoreCase("Goal / NoGoal secondo tempo") && !noHiddenResult()){
            if(bet.equalsIgnoreCase("Gol")){
                return concreteChecker.goalSecondo();
            }
            if(bet.equalsIgnoreCase("NoGol")){
                return concreteChecker.noGoalSecondo();
            }
        }

        if(betKind.equalsIgnoreCase("Tempo con piu gol") && !noHiddenResult()){
            if(bet.equalsIgnoreCase("Primo Tempo")){
                return concreteChecker.primo();
            }
            if(bet.equalsIgnoreCase("Secondo Tempo")){
                return concreteChecker.secondo();
            }
            if(bet.equalsIgnoreCase("Pari")){
                return concreteChecker.entrambiPari();
            }

        }

        if(betKind.equalsIgnoreCase("Esito finale + Under / Over")){
            if(bet.equalsIgnoreCase("1 & Under 1.5")){
                return concreteChecker.unoAndUnder15();
            }
            if(bet.equalsIgnoreCase("1 & Over 1.5")){
                return concreteChecker.unoAndOver15();
            }
            if(bet.equalsIgnoreCase("X & Under 1.5")){
                return concreteChecker.xAndUnder15();
            }
            if(bet.equalsIgnoreCase("X & Over 1.5")){
                return concreteChecker.xAndOver15();
            }
            if(bet.equalsIgnoreCase("2 & Under 1.5")){
                return concreteChecker.dueAndUnder15();
            }
            if(bet.equalsIgnoreCase("2 & Over 1.5")){
                return concreteChecker.dueAndOver15();
            }
            if(bet.equalsIgnoreCase("1 & Under 2.5")){
                return concreteChecker.unoAndUnder25();
            }
            if(bet.equalsIgnoreCase("1 & Over 2.5")){
                return concreteChecker.unoAndOver25();
            }
            if(bet.equalsIgnoreCase("X & Under 2.5")){
                return concreteChecker.xAndUnder25();
            }
            if(bet.equalsIgnoreCase("X & Over 2.5")){
                return concreteChecker.xAndOver25();
            }
            if(bet.equalsIgnoreCase("2 & Under 2.5")){
                return concreteChecker.dueAndUnder25();
            }
            if(bet.equalsIgnoreCase("2 & Over 2.5")){
                return concreteChecker.dueAndOver25();
            }
            if(bet.equalsIgnoreCase("1 & Under 3.5")){
                return concreteChecker.unoAndUnder35();
            }
            if(bet.equalsIgnoreCase("1 & Over 3.5")){
                return concreteChecker.unoAndOver35();
            }
            if(bet.equalsIgnoreCase("X & Under 3.5")){
                return concreteChecker.xAndUnder35();
            }
            if(bet.equalsIgnoreCase("X & Over 3.5")){
                return concreteChecker.xAndOver35();
            }
            if(bet.equalsIgnoreCase("2 & Under 3.5")){
                return concreteChecker.dueAndUnder35();
            }
            if(bet.equalsIgnoreCase("2 & Over 3.5")){
                return concreteChecker.dueAndOver35();
            }
            if(bet.equalsIgnoreCase("1 & Under 4.5")){
                return concreteChecker.unoAndUnder45();
            }
            if(bet.equalsIgnoreCase("1 & Over 4.5")){
                return concreteChecker.unoAndOver45();
            }
            if(bet.equalsIgnoreCase("X & Under 4.5")){
                return concreteChecker.xAndUnder45();
            }
            if(bet.equalsIgnoreCase("X & Over 4.5")){
                return concreteChecker.xAndOver45();
            }
            if(bet.equalsIgnoreCase("2 & Under 4.5")){
                return concreteChecker.dueAndUnder45();
            }
            if(bet.equalsIgnoreCase("2 & Over 4.5")){
                return concreteChecker.dueAndOver45();
            }
        }

        if(betKind.equalsIgnoreCase("Esito finale + Gol / NoGol")){
            if(bet.equalsIgnoreCase("1 & Gol")){
                return concreteChecker.unoAndGoal();
            }
            if(bet.equalsIgnoreCase("1 & NoGol")){
                return concreteChecker.unoAndNoGoal();
            }
            if(bet.equalsIgnoreCase("X & Gol")){
                return concreteChecker.xAndGoal();
            }
            if(bet.equalsIgnoreCase("X & NoGol")){
                return concreteChecker.xAndNoGoal();
            }
            if(bet.equalsIgnoreCase("2 & Gol")){
                return concreteChecker.dueAndGoal();
            }
            if(bet.equalsIgnoreCase("2 & NoGol")){
                return concreteChecker.dueAndNoGoal();
            }
        }

        if(betKind.equalsIgnoreCase("Doppia Chance + Gol / NoGol")){
            if(bet.equalsIgnoreCase("1X & Gol")){
                return concreteChecker.unoXAndGoal();
            }
            if(bet.equalsIgnoreCase("1X & NoGol")){
                return concreteChecker.unoXAndNoGoal();
            }
            if(bet.equalsIgnoreCase("X2 & Gol")){
                return concreteChecker.xDueAndGoal();
            }
            if(bet.equalsIgnoreCase("X2 & NoGol")){
                return concreteChecker.xDueAndNoGoal();
            }
            if(bet.equalsIgnoreCase("12 & Gol")){
                return concreteChecker.unoDueAndGoal();
            }
            if(bet.equalsIgnoreCase("12 & NoGol")){
                return concreteChecker.unoDueAndNoGoal();
            }
        }

        if(betKind.equalsIgnoreCase("Doppia chance + Under / Over")){
            if(bet.equalsIgnoreCase("1X & Under 1.5")){
                return concreteChecker.unoXAndUnder15();
            }
            if(bet.equalsIgnoreCase("1X & Over 1.5")){
                return concreteChecker.unoXAndOver15();
            }
            if(bet.equalsIgnoreCase("X2 & Under 1.5")){
                return concreteChecker.xDueAndUnder15();
            }
            if(bet.equalsIgnoreCase("X2 & Over 1.5")){
                return concreteChecker.xDueAndOver15();
            }
            if(bet.equalsIgnoreCase("12 & Under 1.5")){
                return concreteChecker.unoDueAndUnder15();
            }
            if(bet.equalsIgnoreCase("12 & Over 1.5")){
                return concreteChecker.unoDueAndOver15();
            }
            if(bet.equalsIgnoreCase("1X & Under 2.5")){
                return concreteChecker.unoXAndUnder25();
            }
            if(bet.equalsIgnoreCase("1X & Over 2.5")){
                return concreteChecker.unoXAndOver25();
            }
            if(bet.equalsIgnoreCase("X2 & Under 2.5")){
                return concreteChecker.xDueAndUnder25();
            }
            if(bet.equalsIgnoreCase("X2 & Over 2.5")){
                return concreteChecker.xDueAndOver25();
            }
            if(bet.equalsIgnoreCase("12 & Under 2.5")){
                return concreteChecker.unoDueAndUnder25();
            }
            if(bet.equalsIgnoreCase("12 & Over 2.5")){
                return concreteChecker.unoDueAndOver25();
            }
            if(bet.equalsIgnoreCase("1X & Under 3.5")){
                return concreteChecker.unoXAndUnder35();
            }
            if(bet.equalsIgnoreCase("1X & Over 3.5")){
                return concreteChecker.unoXAndOver35();
            }
            if(bet.equalsIgnoreCase("X2 & Under 3.5")){
                return concreteChecker.xDueAndUnder35();
            }
            if(bet.equalsIgnoreCase("X2 & Over 3.5")){
                return concreteChecker.xDueAndOver35();
            }
            if(bet.equalsIgnoreCase("12 & Under 3.5")){
                return concreteChecker.unoDueAndUnder35();
            }
            if(bet.equalsIgnoreCase("12 & Over 3.5")){
                return concreteChecker.unoDueAndOver35();
            }
            if(bet.equalsIgnoreCase("1X & Under 4.5")){
                return concreteChecker.unoXAndUnder45();
            }
            if(bet.equalsIgnoreCase("1X & Over 4.5")){
                return concreteChecker.unoXAndOver45();
            }
            if(bet.equalsIgnoreCase("X2 & Under 4.5")){
                return concreteChecker.xDueAndUnder45();
            }
            if(bet.equalsIgnoreCase("X2 & Over 4.5")){
                return concreteChecker.xDueAndOver45();
            }
            if(bet.equalsIgnoreCase("12 & Under 4.5")){
                return concreteChecker.unoDueAndUnder45();
            }
            if(bet.equalsIgnoreCase("12 & Over 4.5")){
                return concreteChecker.unoDueAndOver45();
            }
        }

        if(betKind.equalsIgnoreCase("Gol / NoGol + Under / Over")){
            if(bet.equalsIgnoreCase("Gol & Over 2,5")){
                return concreteChecker.goalAndOver25();
            }
            if(bet.equalsIgnoreCase("Gol & Under 2,5")){
                return concreteChecker.goalAndUnder25();
            }
            if(bet.equalsIgnoreCase("NoGol & Over 2,5")){
                return concreteChecker.noGoalAndOver25();
            }
            if(bet.equalsIgnoreCase("NoGol & Under 2,5")){
                return concreteChecker.noGoalAndUnder25();
            }
        }

        if(betKind.equalsIgnoreCase("Multi Gol")){
            if(bet.equalsIgnoreCase("1-2 Gol")){
                return concreteChecker.goal12();
            }
            if(bet.equalsIgnoreCase("1-3 Gol")){
                return concreteChecker.goal13();
            }
            if(bet.equalsIgnoreCase("1-4 Gol")){
                return concreteChecker.goal14();
            }
            if(bet.equalsIgnoreCase("1-5 Gol")){
                return concreteChecker.goal15();
            }
            if(bet.equalsIgnoreCase("1-6 Gol")){
                return concreteChecker.goal16();
            }
            if(bet.equalsIgnoreCase("2-3 Gol")){
                return concreteChecker.goal23();
            }
            if(bet.equalsIgnoreCase("2-4 Gol")){
                return concreteChecker.goal24();
            }
            if(bet.equalsIgnoreCase("2-5 Gol")){
                return concreteChecker.goal25();
            }
            if(bet.equalsIgnoreCase("2-6 Gol")){
                return concreteChecker.goal26();
            }
            if(bet.equalsIgnoreCase("3-4 Gol")){
                return concreteChecker.goal34();
            }
            if(bet.equalsIgnoreCase("3-5 Gol")){
                return concreteChecker.goal35();
            }
            if(bet.equalsIgnoreCase("3-6 Gol")){
                return concreteChecker.goal36();
            }
            if(bet.equalsIgnoreCase("4-5 Gol")){
                return concreteChecker.goal45();
            }
            if(bet.equalsIgnoreCase("4-6 Gol")){
                return concreteChecker.goal46();
            }
            if(bet.equalsIgnoreCase("5-6 Gol")){
                return concreteChecker.goal56();
            }
            if(bet.equalsIgnoreCase("7+ Gol")){
                return concreteChecker.goal7plus();
            }


        }

        if(betKind.equalsIgnoreCase("Multi Gol 1/2") && !noHiddenResult()){
            if(bet.equalsIgnoreCase("1-2 Gol Primo Tempo")){
                return concreteChecker.goal12primo();
            }
            if(bet.equalsIgnoreCase("1-3 Gol Primo Tempo")){
                return concreteChecker.goal13primo();
            }
            if(bet.equalsIgnoreCase("2-3 Gol Primo Tempo")){
                return concreteChecker.goal23primo();
            }
            if(bet.equalsIgnoreCase("1-2 Gol Secondo Tempo")){
                return concreteChecker.goal12secondo();
            }
            if(bet.equalsIgnoreCase("1-3 Gol Secondo Tempo")){
                return concreteChecker.goal13secondo();
            }
            if(bet.equalsIgnoreCase("2-3 Gol Secondo Tempo")){
                return concreteChecker.goal23secondo();
            }
        }

        if(betKind.equalsIgnoreCase("Multi Gol C/T")){
            if(bet.equalsIgnoreCase("1-2 Gol Casa")){
                return concreteChecker.goal12casa();
            }
            if(bet.equalsIgnoreCase("1-3 Gol Casa")){
                return concreteChecker.goal13casa();
            }
            if(bet.equalsIgnoreCase("1-4 Gol Casa")){
                return concreteChecker.goal14casa();
            }
            if(bet.equalsIgnoreCase("1-5 Gol Casa")){
                return concreteChecker.goal15casa();
            }
            if(bet.equalsIgnoreCase("2-3 Gol Casa")){
                return concreteChecker.goal23casa();
            }
            if(bet.equalsIgnoreCase("2-4 Gol Casa")){
                return concreteChecker.goal24casa();
            }
            if(bet.equalsIgnoreCase("2-5 Gol Casa")){
                return concreteChecker.goal25casa();
            }

            if(bet.equalsIgnoreCase("1-2 Gol Trasferta")){
                return concreteChecker.goal12trasferta();
            }
            if(bet.equalsIgnoreCase("1-3 Gol Trasferta")){
                return concreteChecker.goal13trasferta();
            }
            if(bet.equalsIgnoreCase("1-4 Gol Trasferta")){
                return concreteChecker.goal14trasferta();
            }
            if(bet.equalsIgnoreCase("1-5 Gol Trasferta")){
                return concreteChecker.goal15trasferta();
            }
            if(bet.equalsIgnoreCase("2-3 Gol Trasferta")){
                return concreteChecker.goal23trasferta();
            }
            if(bet.equalsIgnoreCase("2-4 Gol Trasferta")){
                return concreteChecker.goal24trasferta();
            }
            if(bet.equalsIgnoreCase("2-5 Gol Trasferta")){
                return concreteChecker.goal25trasferta();
            }

            if(betKind.equalsIgnoreCase("Squadra in casa vince entrambi i tempi") && !noHiddenResult()){
                if(bet.equalsIgnoreCase("Si")){
                    return concreteChecker.vinceCasaSi();
                }
                if(bet.equalsIgnoreCase("No")){
                    return concreteChecker.vinceCasaNo();
                }
            }

            if(betKind.equalsIgnoreCase("Squadra ospite vince entrambi i tempi") && !noHiddenResult()){
                if(bet.equalsIgnoreCase("Si")){
                    return concreteChecker.vinceOspiteSi();
                }
                if(bet.equalsIgnoreCase("No")){
                    return concreteChecker.vinceOspiteNo();
                }
            }

            if(betKind.equalsIgnoreCase("Squadra in casa segna entrambi i tempi") && !noHiddenResult()){
                if(bet.equalsIgnoreCase("Si")){
                    return concreteChecker.casaSi();
                }
                if(bet.equalsIgnoreCase("No")){
                    return concreteChecker.casaNo();
                }
            }

            if(betKind.equalsIgnoreCase("Squadra ospite segna entrambi i tempi") && !noHiddenResult()){
                if(bet.equalsIgnoreCase("Si")){
                    return concreteChecker.ospiteSi();
                }
                if(bet.equalsIgnoreCase("No")){
                    return concreteChecker.ospiteNo();
                }
            }

            if(betKind.equalsIgnoreCase("Doppia Chance primo tempo") && !noHiddenResult()){
                if(bet.equalsIgnoreCase("1X")){
                    return concreteChecker.unoXprimo();
                }
                if(bet.equalsIgnoreCase("12")){
                    return concreteChecker.unoDueprimo();
                }
                if(bet.equalsIgnoreCase("X2")){
                    return concreteChecker.dueXprimo();
                }
            }

            if(betKind.equalsIgnoreCase("Doppia Chance secondo tempo") && !noHiddenResult()){
                if(bet.equalsIgnoreCase("1X")){
                    return concreteChecker.unoXsecondo();
                }
                if(bet.equalsIgnoreCase("12")){
                    return concreteChecker.unoDuesecondo();
                }
                if(bet.equalsIgnoreCase("X2")){
                    return concreteChecker.dueXsecondo();
                }
            }

            if(betKind.equalsIgnoreCase("Rimborso in caso di pareggio")){
                if(bet.equalsIgnoreCase("1")){
                    return concreteChecker.unoX();
                }
                if(bet.equalsIgnoreCase("2")){
                    return concreteChecker.dueX();
                }
            }
        }


        return false;
    }


}
