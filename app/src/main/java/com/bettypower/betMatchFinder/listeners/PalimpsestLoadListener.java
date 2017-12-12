package com.bettypower.betMatchFinder.listeners;

import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;

/**
 * Listener used in order to know when all the palimpsest matches are loaded in memory and are ready to be use
 * Created by giuliopettenuzzo on 21/10/17.
 */

public interface PalimpsestLoadListener {
    /**
     * called when all the palimpsestMatch are ready
     */
    void onPalimpsestLoadCompleted(ArrayList<PalimpsestMatch> allPalimpsestMatches);
}
