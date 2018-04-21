package com.bettypower.betMatchFinder.listeners;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public interface StaticElaborationListener {
    void onElaborationCompleted(ArrayList<PalimpsestMatch> allMatchFound, Map<String,String> bookMakerAndMoney);
}
