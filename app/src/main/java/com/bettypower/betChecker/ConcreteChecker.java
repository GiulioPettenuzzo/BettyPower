package com.bettypower.betChecker;

/**
 * Created by giuliopettenuzzo on 18/03/18.
 * Return always true if win, false if lost
 */

public class ConcreteChecker {

    private int homeResult;
    private int awayResult;

    public ConcreteChecker(int homeResult, int awayResult){
        this.homeResult = homeResult;
        this.awayResult = awayResult;
    }

    public boolean uno(){
        return homeResult > awayResult;
    }

    public boolean due(){
        return awayResult > homeResult;
    }

    public boolean x(){
        return homeResult == awayResult;
    }

    public boolean unoX(){
        return homeResult >= awayResult;
    }

    public boolean dueX(){
        return homeResult <= awayResult;
    }

    public boolean unoDue(){
        return homeResult != awayResult;
    }

    public boolean goal(){
        return homeResult > 0 && awayResult > 0;
    }

    public boolean noGoal(){
        return homeResult == 0 || awayResult == 0;
    }

    public boolean under05(){
        return (homeResult + awayResult) == 0;
    }

    public boolean over05(){
        return (homeResult + awayResult) > 0;
    }

    public boolean under15(){
        return (homeResult + awayResult) < 2;
    }

    public boolean over15(){
        return (homeResult + awayResult) >= 2;
    }

    public boolean under25(){
        return (homeResult + awayResult) < 3;
    }

    public boolean over25(){
        return (homeResult + awayResult) >= 3;
    }

    public boolean under35(){
        return (homeResult + awayResult) < 4;
    }

    public boolean over35(){
        return (homeResult + awayResult) >= 4;
    }

    public boolean under45(){
        return (homeResult + awayResult) < 5;
    }

    public boolean over45(){
        return (homeResult + awayResult) >= 5;
    }

    public boolean under55(){
        return (homeResult + awayResult) < 6;
    }

    public boolean over55(){
        return (homeResult + awayResult) >= 6;
    }

    public boolean under65(){
        return (homeResult + awayResult) < 7;
    }

    public boolean over65(){
        return (homeResult + awayResult) >= 7;
    }

    public boolean under75(){
        return (homeResult + awayResult) < 8;
    }

    public boolean over75(){
        return (homeResult + awayResult) >= 8;
    }

    public boolean handicap101() {
        awayResult = awayResult + 1;
        return uno();
    }

    public boolean handicapX01(){
        awayResult = awayResult + 1;
        return x();
    }

    public boolean handicap201(){
        awayResult = awayResult + 1;
        return due();
    }

    public boolean handicap110(){
        homeResult = homeResult + 1;
        return uno();
    }

    public boolean handicapX10(){
        homeResult = homeResult + 1;
        return x();
    }

    public boolean handicap210(){
        homeResult = homeResult + 1;
        return due();
    }

    public boolean handicap102() {
        awayResult = awayResult + 2;
        return uno();
    }

    public boolean handicapX02(){
        awayResult = awayResult + 2;
        return x();
    }

    public boolean handicap202(){
        awayResult = awayResult + 2;
        return due();
    }

    public boolean handicap120(){
        homeResult = homeResult + 2;
        return uno();
    }

    public boolean handicapX20(){
        homeResult = homeResult + 2;
        return x();
    }

    public boolean handicap220(){
        homeResult = homeResult + 2;
        return due();
    }

}
