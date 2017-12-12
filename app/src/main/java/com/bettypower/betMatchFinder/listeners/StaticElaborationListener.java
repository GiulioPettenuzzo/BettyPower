package com.bettypower.betMatchFinder.listeners;

import com.bettypower.betMatchFinder.entities.MatchToFind;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public interface StaticElaborationListener {
    void onElaborationCompleted(ArrayList<MatchToFind> allMatchFound);
}
