package com.bettypower.betMatchFinder.listeners;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public interface RealTimeElaborationListener {
    void onElaborationCompleted(ArrayList<MatchToFind> matchToFind, ArrayList<OddsToFind> quote, Map<String,String> bookMakerAndMoney);
}
