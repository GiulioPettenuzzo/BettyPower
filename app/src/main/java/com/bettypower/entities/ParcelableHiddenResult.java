package com.bettypower.entities;

/**
 * Created by giuliopettenuzzo on 28/06/17.
 */

public class ParcelableHiddenResult implements HiddenResult {

    private String playerName;
    private String time;
    private String action;
    private int actionTeam;
    private String  result;

    public ParcelableHiddenResult(String playerName,String time,String action,int actionTeam){
        this.playerName = playerName;
        this.time = time;
        this.action = action;
        this.actionTeam = actionTeam;
    }
    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String actionName) {
        this.action = actionName;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public void setTime(String time) {
    this.time = time;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int getActionTeam() {
        return actionTeam;
    }

    @Override
    public void setActionTeam(int numberIdentifier) {
        this.actionTeam = numberIdentifier;
    }
}
