package com.bettypower.betMatchFinder.entities;

/**
 * Created by giuliopettenuzzo on 22/10/17.
 */

public interface OddsToFind {
    String getBet();
    void setBet(String bet);

    String getOdds();
    void setOdds(String odds);

    String getBetKind();
    void setBetKind(String betKind);
}
