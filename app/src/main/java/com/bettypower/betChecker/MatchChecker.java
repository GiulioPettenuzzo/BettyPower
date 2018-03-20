package com.bettypower.betChecker;

/**
 * Created by giuliopettenuzzo on 18/03/18.
 * Class used to decide if a bet is win or loose
 */

public class MatchChecker {

    private int homeResult;
    private int awayResult;
    private String bet;
    private ConcreteChecker concreteChecker;

    public boolean isMatchWin(String homeResult, String awayResult, String bet){
        this.homeResult = Integer.parseInt(homeResult);
        this.awayResult = Integer.parseInt(awayResult);
        this.bet = bet;
        this.concreteChecker = new ConcreteChecker(this.homeResult,this.awayResult);
        return checkMatch();
    }

    private boolean checkMatch(){
        //1X2
        if(bet.equalsIgnoreCase("1")){
            return concreteChecker.uno();
        }
        if(bet.equalsIgnoreCase("X")){
            return concreteChecker.x();
        }
        if(bet.equalsIgnoreCase("2")){
            return concreteChecker.due();
        }

        //doppia chance
        if(bet.equalsIgnoreCase("1X")){
            return concreteChecker.unoX();
        }
        if(bet.equalsIgnoreCase("12")){
            return concreteChecker.unoDue();
        }
        if(bet.equalsIgnoreCase("X2")){
            return concreteChecker.dueX();
        }

        //goal/nogoal
        if(bet.equalsIgnoreCase("Goal")){
            return concreteChecker.goal();
        }
        if(bet.equalsIgnoreCase("no goal")){
            return concreteChecker.noGoal();
        }

        //Under/Over
        if(bet.equalsIgnoreCase("Under 0,5")){
            return concreteChecker.under05();
        }
        if(bet.equalsIgnoreCase("Over 0,5")){
            return concreteChecker.over05();
        }
        if(bet.equalsIgnoreCase("Under 1,5")){
            return concreteChecker.under15();
        }
        if(bet.equalsIgnoreCase("Over 1,5")){
            return concreteChecker.over15();
        }
        if(bet.equalsIgnoreCase("Under 2,5")){
            return concreteChecker.under25();
        }
        if(bet.equalsIgnoreCase("Over 2,5")){
            return concreteChecker.over25();
        }
        if(bet.equalsIgnoreCase("Under 3,5")){
            return concreteChecker.under35();
        }
        if(bet.equalsIgnoreCase("Over 3,5")){
            return concreteChecker.over35();
        }
        if(bet.equalsIgnoreCase("Under 4,5")){
            return concreteChecker.under45();
        }
        if(bet.equalsIgnoreCase("Over 4,5")){
            return concreteChecker.over45();
        }
        if(bet.equalsIgnoreCase("Under 5,5")){
            return concreteChecker.under55();
        }
        if(bet.equalsIgnoreCase("Over 5,5")){
            return concreteChecker.over55();
        }
        if(bet.equalsIgnoreCase("Under 6,5")){
            return concreteChecker.under65();
        }
        if(bet.equalsIgnoreCase("Over 6,5")){
            return concreteChecker.over65();
        }
        if(bet.equalsIgnoreCase("Under 7,5")){
            return concreteChecker.under75();
        }
        if(bet.equalsIgnoreCase("Over 7,5")){
            return concreteChecker.over75();
        }

        //Handicap
        if(bet.equalsIgnoreCase("1 (0:1)")){
            return concreteChecker.handicap101();
        }
        if(bet.equalsIgnoreCase("X (0:1)")){
            return concreteChecker.handicapX01();
        }
        if(bet.equalsIgnoreCase("2 (0:1)")){
            return concreteChecker.handicap201();
        }
        if(bet.equalsIgnoreCase("1 (1:0)")){
            return concreteChecker.handicap110();
        }
        if(bet.equalsIgnoreCase("X (1:0)")){
            return concreteChecker.handicapX10();
        }
        if(bet.equalsIgnoreCase("2 (1:0)")){
            return concreteChecker.handicap210();
        }
        if(bet.equalsIgnoreCase("1 (0:2)")){
            return concreteChecker.handicap102();
        }
        if(bet.equalsIgnoreCase("X (0:2)")){
            return concreteChecker.handicapX02();
        }
        if(bet.equalsIgnoreCase("2 (0:2)")){
            return concreteChecker.handicap202();
        }
        if(bet.equalsIgnoreCase("1 (2:0)")){
            return concreteChecker.handicap120();
        }
        if(bet.equalsIgnoreCase("X (2:0)")){
            return concreteChecker.handicapX20();
        }
        if(bet.equalsIgnoreCase("2 (2:0)")){
            return concreteChecker.handicap220();
        }

        return false;
    }


}
