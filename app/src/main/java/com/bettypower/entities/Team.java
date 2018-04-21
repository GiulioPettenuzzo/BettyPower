package com.bettypower.entities;

import android.graphics.Bitmap;
import android.os.Parcelable;

/**
 * Created by giuliopettenuzzo on 21/06/17.
 * Describe a Team, a Team is rapresented only by the name
 */

public interface Team extends Parcelable{
    /**
     *
     * @return the name of the team
     */
    String getName();

    void setName(String name);

}