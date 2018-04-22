package com.bettypower.unpacker;

import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.Team;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 24/06/17.
 * generic interface to save the response and making the pircing
 */

public interface Unpacker {

    /**
     * basic method to recive the whole teams found in web
     */
    public ArrayList<Team> getAllTeams();
    /**
     * basic method to recive the whole matches found in web
     */
    public ArrayList<PalimpsestMatch> getAllMatches();

}
