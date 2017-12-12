package com.bettypower.matchFinder;

import com.bettypower.entities.PalimpsestMatch;

/**
 * Created by giuliopettenuzzo on 14/08/17.
 */

public interface LikelyHoodMatch {

    void setPalimpsestMatch(PalimpsestMatch palimpsestMatch);
    PalimpsestMatch getPalimpsestMatch();

    void setLikelyHood(int likelyHood);
    int getLikelyHood();

    boolean compareTo(PalimpsestMatch palimpsestMatch);

    boolean compareToByName(PalimpsestMatch palimpsestMatch);
}
