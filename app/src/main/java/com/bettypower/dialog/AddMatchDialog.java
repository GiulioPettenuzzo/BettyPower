package com.bettypower.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bettypower.SingleBetActivity;
import com.bettypower.adapters.DropDownAutoCompleteTextViewAdapter;
import com.bettypower.entities.Match;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.entities.ParcelablePalimpsestMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.threads.LoadImageThread;
import com.renard.ocr.R;
import com.renard.ocr.TextFairyApplication;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 27/01/18.
 */

public class AddMatchDialog extends Dialog {

    private Context context;
    private CustomTextView autoCompleteTextView;
    private ArrayList<PalimpsestMatch> allMatches;
    private ArrayList<PalimpsestMatch> allMatchInBet;
    private PalimpsestMatch selectedPalimpsestMatch;
    private Button confirm;
    private Button delete;
    private ImageView homeImageView;
    private ImageView awayImageView;
    private FinishEditDialog finishEditDialog;
    private TextView matchDateTextView;
    private TextFairyApplication application;
    private DropDownAutoCompleteTextViewAdapter dropDownAutoCompleteTextViewAdapter;
    private Activity activity;

    private static final String IMAGE_URL = "http://www.fishtagram.it/loghi.html";


    public AddMatchDialog(@NonNull final Context context,ArrayList<PalimpsestMatch> allMatchesInBet,Activity activity) {
        super(context);
        this.context = context;
        this.allMatchInBet = allMatchesInBet;
        application = (TextFairyApplication) context.getApplicationContext();
        setContentView(R.layout.dialog_add_new_match);
        autoCompleteTextView = (CustomTextView) findViewById(R.id.autoCompleteTextView);
        confirm = (Button) findViewById(R.id.add_dialog_confirm);
        delete = (Button) findViewById(R.id.add_dialog_cancel);
        matchDateTextView = (TextView) findViewById(R.id.match_time_text_view);
        homeImageView = (ImageView) findViewById(R.id.home_team_add_logo);
        awayImageView = (ImageView) findViewById(R.id.away_team_add_logo);
        autoCompleteTextView.setHint("squadra in casa - squadra ospite");
        autoCompleteTextView.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        this.activity = activity;

        matchDateTextView.setVisibility(View.INVISIBLE);
        this.allMatches = application.getAllPalimpsestMatch();
        if(allMatches!=null){
            getMatchesAndSetAdapter();
        }else{
            Toast toast = Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT);
            toast.show();
            application.setAllMatchLoadListener(new TextFairyApplication.AllMatchLoadListener() {
                @Override
                public void onMatchLoaded(ArrayList<PalimpsestMatch> allPalimpsestMatch) {
                    allMatches = allPalimpsestMatch;
                    getMatchesAndSetAdapter();
                }
            });
        }

        confirm.setTextColor(context.getResources().getColor(R.color.grey6));
        confirm.setOnClickListener(null);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextFairyApplication application = (TextFairyApplication) context.getApplicationContext();
                allMatches = application.getAllPalimpsestMatch();
                dismiss();
            }
        });
    }

    private void getMatchesAndSetAdapter(){
        ArrayList<PalimpsestMatch> nuova = new ArrayList<>();
        for (PalimpsestMatch currentPalimpsestMatch:allMatches
                ) {
           if(!isMatchInList(allMatchInBet,currentPalimpsestMatch)){
               nuova.add(currentPalimpsestMatch);
           }
        }

        dropDownAutoCompleteTextViewAdapter = new DropDownAutoCompleteTextViewAdapter<>(context, R.layout.item_match_autocomplete ,nuova);
        dropDownAutoCompleteTextViewAdapter.setSelectedItemListener(new DropDownAutoCompleteTextViewAdapter.SelectItemListener() {
            @Override
            public void onMatchNotSelected() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("NOT SELECTED","NOT SELECTED");
                        confirm.setTextColor(context.getResources().getColor(R.color.grey6));
                        matchDateTextView.setVisibility(View.INVISIBLE);
                        selectedPalimpsestMatch = null;
                        confirm.setOnClickListener(null);
                        homeImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eagle_shield));
                        awayImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_shield_with_castle_inside));
                    }
                });
            }
        });
        autoCompleteTextView.setAdapter(dropDownAutoCompleteTextViewAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (PalimpsestMatch currentPalimpsestMatch:allMatches
                     ) {
                    String completeName = currentPalimpsestMatch.getHomeTeam().getName() + " - " + currentPalimpsestMatch.getAwayTeam().getName();
                    if(autoCompleteTextView.getText().toString().equals(completeName)){
                        selectedPalimpsestMatch = currentPalimpsestMatch;
                        break;
                    }
                }
                Log.i("SELECTED ITEM",selectedPalimpsestMatch.getHomeTeam().getName());
                confirm.setTextColor(context.getResources().getColor(R.color.green_for_dialog));
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishEditDialog.onAddNewItem(dropDownAutoCompleteTextViewAdapter.getLastMatchSelected());
                    }
                });
                matchDateTextView.setVisibility(View.VISIBLE);
                matchDateTextView.setText(selectedPalimpsestMatch.getTime());
                loadAllLogosExecutor(selectedPalimpsestMatch);
            }
        });
    }

    private boolean isMatchInList(ArrayList<PalimpsestMatch> allPalimpsestMatches, PalimpsestMatch match){
        if(match.getHomeTeam().getName().startsWith(" ")){
            match.getHomeTeam().setName(match.getHomeTeam().getName().substring(1));
        }
        if(match.getAwayTeam().getName().startsWith(" ")){
            match.getAwayTeam().setName(match.getAwayTeam().getName().substring(1));
        }
        for (PalimpsestMatch currentMatchInBet:allPalimpsestMatches
                ) {
            if(currentMatchInBet.getHomeTeam().getName().startsWith(" ")){
                currentMatchInBet.getHomeTeam().setName(currentMatchInBet.getHomeTeam().getName().substring(1));
            }
            if(currentMatchInBet.getAwayTeam().getName().startsWith(" ")){
                currentMatchInBet.getAwayTeam().setName(currentMatchInBet.getAwayTeam().getName().substring(1));
            }
            if(currentMatchInBet.getHomeTeam().getName().equalsIgnoreCase(match.getHomeTeam().getName()) &&
                    currentMatchInBet.getAwayTeam().getName().equalsIgnoreCase(match.getAwayTeam().getName()) &&
                    currentMatchInBet.getTime().equalsIgnoreCase(match.getTime())){
                return true;
            }
        }
        return false;
    }

    public void setOnAddNewItem(FinishEditDialog finishEditDialog){
        this.finishEditDialog = finishEditDialog;
    }

    /**
     * Called when the dialog has detected the user's press of the back
     * key.  The default implementation simply cancels the dialog (only if
     * it is cancelable), but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        if(autoCompleteTextView.isFocused()) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }else {
            super.onBackPressed();
        }
    }

    public interface FinishEditDialog{
        void onAddNewItem(PalimpsestMatch newMatch);
    }

    public void loadAllLogosExecutor(PalimpsestMatch match){
        final ArrayList<PalimpsestMatch> allMatch = new ArrayList<>();
        allMatch.add(match);
        RequestQueue imageQueue = Volley.newRequestQueue(context);
        StringRequest imageRequest = new StringRequest(Request.Method.GET, IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LoadImageThread loadImageThread = new
                                LoadImageThread(response,allMatch,new ArrayList<String>(),context);
                        loadImageThread.setOnLoadLogoListener(new LoadImageThread.LoadLogoListener() {
                            @Override
                            public void onLoadLogoFinish(final PalimpsestMatch match) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Picasso.with(context).load("file:"+context.getDir("logo_images", Context.MODE_PRIVATE).getPath()+"/"+match.getHomeTeam().getName() + ".png").error(context.getResources().getDrawable(R.drawable.ic_eagle_shield)).into(homeImageView);
                                        Picasso.with(context).load("file:"+context.getDir("logo_images", Context.MODE_PRIVATE).getPath()+"/"+match.getAwayTeam().getName() + ".png").error(context.getResources().getDrawable(R.drawable.ic_shield_with_castle_inside)).into(awayImageView);
                                    }
                                });
                            }

                            @Override
                            public void onAllLogosUploaded() {
                            }
                        });
                        loadImageThread.start();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errore", error.toString());
            }
        });
        imageRequest.setShouldCache(false);
        imageQueue.add(imageRequest);
    }
}
