package com.bettypower.util.urlManagmentForLogos;

import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 19/02/18.
 */

public class ImageUrlUnpacker implements Unpacker {

    private static String responseFromNetwork;
    private static int num = 1; //current number of image
    private static int max;     //max number of image possibles


    @Override
    public String getResponce() {
        return responseFromNetwork;
    }

    @Override
    public void setResponce(String responseFromNetwork) {
        this.responseFromNetwork = responseFromNetwork;
    }

    public int getImageNum(){
        return num;
    }

    public void setImageNum(int num){
        this.num = num;
    }

    public int getMax(){
        return max;
    }

    /**
     * this class is able to make a pircing of the response to find out the first url witch contains the
     * first image in yahoo image
     * the url I'm searching for is contained into a string like this:
     * ...<\>img src='...IMAGE URL...' ...
     * my http is: https://tse2.mm.bing.net/th?id=OIP.qnErHRm9_ZRCmpR1gMwq-gB2Es&pid=15.1&H=160 &W=62&P=0' alt='' style='width:60px;height:153.6px;
     *NOTE: this class load the image number "num" so you need to call setImageNum(int num) before calling this method
     * @return the url of the first image on yahoo, it return "error" if no url was found
     */
    @Override
    public String getMyString() {
        String response = responseFromNetwork;
        StringTokenizer token = new StringTokenizer(response);
        String first_word = "";//the previus of the correct string
        String second_word = "";    //the correct string
        String imgURL = "https://";
        boolean foundURL = false;
        int current = 0; //this counter is used to switch the image
        while (token.hasMoreTokens() && foundURL == false) {
            first_word = token.nextToken();
            if (first_word.endsWith("img")) {  //...<\>img
                second_word = token.nextToken();
                if (second_word.startsWith("src")) {   //src=
                    //by here starting to extract http url
                    current++;
                    if (current == num) {


                        char[] charArray = second_word.toCharArray();
                        charArray = giveURLFromTheBeginning(charArray);
                        imgURL = imgURL + String.valueOf(charArray);
                        String restPartOfURL = token.nextToken();
                        //somewhere in the url there are some space, them are there just for html sintax,
                        // to obtain the real url them must be replaced with "%20"
                        while (!restPartOfURL.startsWith("/>")) {
                            imgURL = imgURL + "%20" + restPartOfURL;
                            restPartOfURL = token.nextToken();
                            if(!token.hasMoreTokens()){
                                return "error";
                            }
                        }
                        char[] finalCharArray = imgURL.toCharArray();
                        finalCharArray = remuveLastCharacterOfURL(finalCharArray);
                        imgURL = String.valueOf(finalCharArray);

                        foundURL = true;

                    }
                }
            }
        }
        if (foundURL == false) {
            return "error";
        }
        return imgURL;
    }

    /**
     * this class recognize the first part of url without https://
     * @param charArray
     * @return
     */
    private char[] giveURLFromTheBeginning(char[] charArray) {
        char[] urlFromTheBeginning = new char[charArray.length];
        for(int currentCaracter = 0;currentCaracter<charArray.length;currentCaracter++){
            //jump after https://
            if(charArray[currentCaracter]=='h'&&charArray[currentCaracter+1]=='t'&&charArray[currentCaracter+2]=='t'
                    &&charArray[currentCaracter+3]=='p' &&charArray[currentCaracter+4]=='s'&&charArray[currentCaracter+5]==':'
                    &&charArray[currentCaracter+6]=='/'&&charArray[currentCaracter+7]=='/'){
                currentCaracter = currentCaracter+8;
                int i = 0;
                urlFromTheBeginning = new char[charArray.length-currentCaracter];
                while(currentCaracter<charArray.length){
                    urlFromTheBeginning[i] = charArray[currentCaracter];
                    currentCaracter++;
                    i++;
                }

            }
        }
        return urlFromTheBeginning;
    }

    /**
     * in the html page, the url is contains inside '...', you need to remuve the last '
     * @param charArray
     * @return
     */
    private char[] remuveLastCharacterOfURL(char[] charArray){
        char[] urlWithoutLastCharacter = new char[charArray.length-1];
        for(int current = 0; current<charArray.length-1;current++){
            urlWithoutLastCharacter[current] = charArray[current];
        }
        return urlWithoutLastCharacter;
    }

    /**
     * class uses to know how many images are there in the responce of yahoo image
     * @param
     * @return
     */
    public int countImageOnUrl(){
        String response = responseFromNetwork;
        StringTokenizer token = new StringTokenizer(response);
        String first_word = "";//the previus of the correct string
        String second_word = "";    //the correct string
        int count = 0;
        while (token.hasMoreTokens()) {
            first_word = token.nextToken();
            if (first_word.endsWith("img")) {  //...<\>img
                second_word = token.nextToken();
                if (second_word.startsWith("src='https:")) {   //src=
                    count++;
                }
            }
        }
        max = count;
        return count;
    }
}

