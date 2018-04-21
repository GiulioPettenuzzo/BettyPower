package com.bettypower.betMatchFinder;

import android.content.Context;
import android.util.Log;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.betMatchFinder.listeners.CompleteElaborationListener;
import com.bettypower.betMatchFinder.listeners.RealTimeElaborationListener;
import com.bettypower.betMatchFinder.listeners.StaticElaborationListener;
import com.bettypower.betMatchFinder.threads.RealTimeThread;
import com.bettypower.betMatchFinder.threads.StaticThread;
import com.bettypower.entities.Bet;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.SingleBet;
import com.renard.ocr.TextFairyApplication;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class Resolver {

    private TextFairyApplication application;
    private CompleteElaborationListener completeElaborationListener;

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;  //all the palimpsest from bookmaker
    private ArrayList<MatchToFind> allMatchFoundByRealTimeOCR;
    private ArrayList<OddsToFind> allQuoteFoundByRealTimeOCR;
    private ArrayList<PalimpsestMatch> allMatchFoundByStaticOCR;
    private Map<String,String> bookmakerAndMoney;

    private String realTimeResponse;
    private String staticResponse;
    private RealTimeThread realTimeThread;
    private StaticThread staticThread;
    private boolean realTimeExecutionFinish = false;
    private boolean staticExecutionFinish = false;
    private Finder finderForStatic;

    private long init;

    /**
     * this constructor also sort the palimpsest match array list in order by the palimpsest number
     * @param allPalimpsestMatch all palimpsest match from server
     * @param context in order to get application context fields
     */
    public Resolver(ArrayList<PalimpsestMatch> allPalimpsestMatch,Context context){
        this.allPalimpsestMatch = allPalimpsestMatch;
        application = (TextFairyApplication) context;
        if(allPalimpsestMatch!=null)
            finderForStatic = new Finder(allPalimpsestMatch);
    }


    /* **********************************************************************************************
     *********************************   PUBLIC METHODS   ******************************************
     **********************************************************************************************/

    /**
     * set response from google ocr
     * @param realTimeResponse response from google ocr
     */
    public void setRealTimeResponse(String realTimeResponse){
        this.realTimeResponse = realTimeResponse;
        checkExeguible();
    }

    /**
     * set response from renard ocr
     * @param staticResponse response from renard ocr
     */
    public void setNormalResponse(String staticResponse){
        init = System.currentTimeMillis();
        this.staticResponse = staticResponse;
        checkExeguible();
    }
    /**
     * set the listener created in order to know when all the elaboration are done
     * @param completeElaborationListener listener in order to understand when the bet is load
     */
    public void setCompleteElaborationListener(CompleteElaborationListener completeElaborationListener){
        this.completeElaborationListener = completeElaborationListener;
    }

    /* **********************************************************************************************
     *********************************   PRIVATE METHODS   ******************************************
     **********************************************************************************************/

    /**
     * called when one of the two thread finish, this method is used in order to know when every
     * thread execution  are terminated
     */
    private void notifyThreads(){
        if(!realTimeExecutionFinish && realTimeResponse!=null){
            if(realTimeThread == null){
                executeRealTimeResponse();
            }
        }
        if(!staticExecutionFinish && staticResponse!=null && realTimeExecutionFinish){
            if(staticThread == null){
                executeStaticResponse();
            }
        }
        if(staticExecutionFinish && realTimeExecutionFinish){
            notifyThreadsFinish();
        }

    }

    private void notifyThreadsFinish(){
        Bet bet = new SingleBet(allMatchFoundByStaticOCR);
        if(bookmakerAndMoney.get("bookmaker")!=null){
            bet.setBookMaker(bookmakerAndMoney.get("bookmaker"));
        }
        if(bookmakerAndMoney.get("vincita")!=null){
            String vincita = bookmakerAndMoney.get("vincita");
            vincita = vincita.replace(",",".");
            bet.setVincita(vincita);
        }
        if(bookmakerAndMoney.get("puntata")!=null){
            String puntata = bookmakerAndMoney.get("puntata");
            puntata = puntata.replace(",",".");
            bet.setPuntata(puntata);
        }
        Log.i("TEMPO DI ESECUZIONE",String.valueOf(System.currentTimeMillis()-init));
        completeElaborationListener.onElaborationComplete(bet);
    }

    /**
     * the apposite variable is changing when the match are loaded
     */
    private void checkExeguible(){
        if(allPalimpsestMatch == null){
            application.setAllMatchLoadListener(new TextFairyApplication.AllMatchLoadListener() {
                @Override
                public void onMatchLoaded(ArrayList<PalimpsestMatch> allMatch) {
                    allPalimpsestMatch = allMatch;
                    finderForStatic = new Finder(allPalimpsestMatch);
                    notifyThreads();
                }
            });
        }
        else{
            notifyThreads();
        }
    }

    /**
     * start thread from google ocr and wait the response in a listener
     */
    private void executeRealTimeResponse(){
        realTimeThread = new RealTimeThread(allPalimpsestMatch,realTimeResponse);
        Log.i("REAL TIME RESPONSE",realTimeResponse);
        realTimeThread.start();
        realTimeThread.setRealTimeElaborationListener(new RealTimeElaborationListener() {
            @Override
            public void onElaborationCompleted(ArrayList<MatchToFind> allMatchFound, ArrayList<OddsToFind> quote, Map<String,String> bookAndMoney) {
                realTimeExecutionFinish = true;
                allMatchFoundByRealTimeOCR = allMatchFound;
                allQuoteFoundByRealTimeOCR = quote;
                bookmakerAndMoney = bookAndMoney;
                notifyThreads();
            }
        });
    }

    /**
     * start thread from renard ocr and wait the response in a listener
     */
    private void executeStaticResponse(){
        staticThread = new StaticThread(allPalimpsestMatch,staticResponse,allMatchFoundByRealTimeOCR,allQuoteFoundByRealTimeOCR,finderForStatic,bookmakerAndMoney);
        Log.i("STATIC RESPONSE",staticResponse);
        staticThread.start();
        staticThread.setStaticElaborationListener(new StaticElaborationListener() {
            @Override
            public void onElaborationCompleted(ArrayList<PalimpsestMatch> allMatchFound,Map<String,String> bookMakerAndMoney) {
                allMatchFoundByStaticOCR = allMatchFound;
                bookmakerAndMoney = bookMakerAndMoney;
                staticExecutionFinish = true;
                notifyThreads();
            }
        });
    }

}
