package com.bettypower.entities;

/**
 * Created by giuliopettenuzzo on 13/10/17.
 */

public class SingleBet implements Bet {

    private String bet;
    private String quote;

    public SingleBet(String bet, String quote){
        this.bet = bet;
        this.quote = quote;
    }
    @Override
    public String getBet() {
        return bet;
    }

    @Override
    public void setBet(String bet) {
        this.bet = bet;
    }

    @Override
    public String getQuote() {
        return quote;
    }

    @Override
    public void setQuote(String bet) {
        this.bet = bet;
    }
}
