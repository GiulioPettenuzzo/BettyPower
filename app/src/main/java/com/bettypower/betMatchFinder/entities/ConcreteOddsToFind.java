package com.bettypower.betMatchFinder.entities;

/**
 * Created by giuliopettenuzzo on 23/10/17.
 */

public class ConcreteOddsToFind implements OddsToFind {

    private String bet;
    private String odds;
    private String betKind;

    @Override
    public String getBet() {
        return bet;
    }

    @Override
    public void setBet(String bet) {
        this.bet = bet;
    }

    @Override
    public String getOdds() {
        return odds;
    }

    @Override
    public void setOdds(String odds) {
        this.odds = odds;
    }

    @Override
    public String getBetKind() {
        return betKind;
    }

    @Override
    public void setBetKind(String betKind) {
        this.betKind = betKind;
    }
}
