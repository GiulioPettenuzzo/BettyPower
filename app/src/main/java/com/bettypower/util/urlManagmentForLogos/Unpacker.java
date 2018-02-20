package com.bettypower.util.urlManagmentForLogos;

/**
 * Created by giuliopettenuzzo on 19/02/18.
 */
/**
 * This Interface is thinking to Unpacked the wole html responce I recive
 * Created by giuliopettenuzzo on 09/05/17.
 */

public interface Unpacker {
    /**
     *
     * @return the origin html responce given from constructor altought the setResponce method
     */
    public String getResponce();

    /**
     *set the html responce
     */
    public void setResponce(String responseFromNetwork);

    /**
     *this is the most important method that give the streeng I'm tring to find'out in the response
     * @return
     */
    public String getMyString();
}
