package com.bettypower.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bettypower.adapters.DropDownAutoCompleteTextViewAdapter;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.threads.LoadLogoThread;
import com.renard.betty.R;
import com.renard.betty.TextFairyApplication;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by giuliopettenuzzo on 27/01/18.
 * Custom class to add a match
 */

public class AddMatchDialog extends Dialog {

    private Context context;
    private CustomTextView autoCompleteTextView;
    private ArrayList<PalimpsestMatch> allMatches;
    private PalimpsestMatch selectedPalimpsestMatch;
    private Button confirm;
    private ImageView homeImageView;
    private ImageView awayImageView;
    private FinishEditDialog finishEditDialog;
    private TextView matchDateTextView;
    private DropDownAutoCompleteTextViewAdapter dropDownAutoCompleteTextViewAdapter;
    private Activity activity;

    public AddMatchDialog(@NonNull final Context context, final Activity activity) {
        super(context);
        setContentView(R.layout.dialog_add_new_match);
        this.context = context;
        this.activity = activity;

        autoCompleteTextView = (CustomTextView) findViewById(R.id.autoCompleteTextView);
        confirm = (Button) findViewById(R.id.add_dialog_confirm);
        matchDateTextView = (TextView) findViewById(R.id.match_time_text_view);
        homeImageView = (ImageView) findViewById(R.id.home_team_add_logo);
        awayImageView = (ImageView) findViewById(R.id.away_team_add_logo);
        autoCompleteTextView.setHint(context.getResources().getString(R.string.autocomplete_textview_hint));
        autoCompleteTextView.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        TextFairyApplication application = (TextFairyApplication) context.getApplicationContext();
        Button delete = (Button) findViewById(R.id.add_dialog_cancel);


        matchDateTextView.setVisibility(View.INVISIBLE);
        this.allMatches = application.getAllPalimpsestMatch();
        if(allMatches!=null && allMatches.size()!=0){
            getMatchesAndSetAdapter();
        }else{
            Toast toast = Toast.makeText(context, R.string.match_not_loaded, Toast.LENGTH_LONG);
            toast.show();
            application.setAllMatchLoadListener(new TextFairyApplication.AllMatchLoadListener() {
                @Override
                public void onMatchLoaded(final ArrayList<PalimpsestMatch> allPalimpsestMatch) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            allMatches = allPalimpsestMatch;
                            getMatchesAndSetAdapter();
                            Toast toast = Toast.makeText(context, R.string.match_loaded, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }
            });
        }

        confirm.setTextColor(context.getResources().getColor(R.color.grey6));
        confirm.setOnClickListener(null);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //non no perchè ho messo queste righe
               // TextFairyApplication application = (TextFairyApplication) context.getApplicationContext();
               // allMatches = application.getAllPalimpsestMatch();
                dismiss();
            }
        });
    }


    private void getMatchesAndSetAdapter(){
        Collections.sort(allMatches, new Comparator<PalimpsestMatch>() {
            @Override
            public int compare(PalimpsestMatch o1, PalimpsestMatch o2) {
                String[] parts = o1.getDate().split("/", 2);
                String dateOne = parts[1]+parts[0];
                String[] partsTwo = o2.getDate().split("/", 2);
                String dateTwo = partsTwo[1]+partsTwo[0];
                return dateTwo.compareTo(dateOne);
            }
        });
        dropDownAutoCompleteTextViewAdapter = new DropDownAutoCompleteTextViewAdapter<>(activity,allMatches);
        dropDownAutoCompleteTextViewAdapter.setSelectedItemListener(new DropDownAutoCompleteTextViewAdapter.SelectItemListener() {
            @Override
            public void onMatchNotSelected() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                confirm.setTextColor(context.getResources().getColor(R.color.green_for_dialog));
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishEditDialog.onAddNewItem(dropDownAutoCompleteTextViewAdapter.getLastMatchSelected());
                    }
                });
                matchDateTextView.setVisibility(View.VISIBLE);
                matchDateTextView.setText(selectedPalimpsestMatch.getTime());
                //loadAllLogosExecutor(selectedPalimpsestMatch);
                loadMatchLogos(selectedPalimpsestMatch);
            }
        });
    }

    public void setOnAddNewItem(FinishEditDialog finishEditDialog){
        this.finishEditDialog = finishEditDialog;
    }

    private boolean backPressed = false;
    /**
     * Called when the dialog has detected the user's press of the back
     * key.  The default implementation simply cancels the dialog (only if
     * it is cancelable), but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        if(autoCompleteTextView.isPopupShowing() && !backPressed) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(
                        activity.getCurrentFocus().getWindowToken(), 0);
            }
            backPressed = true;
        }
        else if(autoCompleteTextView.isPopupShowing() && backPressed){
            autoCompleteTextView.dismissDropDown();
            backPressed = false;
        }
    }

    public interface FinishEditDialog{
        void onAddNewItem(PalimpsestMatch newMatch);
    }

    private void loadMatchLogos(final PalimpsestMatch match){
        Picasso.with(context).load("file:"+context.getDir("logo_images", Context.MODE_PRIVATE).getPath()+"/"+match.getHomeTeam().getName() + ".png").placeholder(context.getResources().getDrawable(R.drawable.ic_shield_with_castle_inside)).into(homeImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                LoadLogoThread homeThread = new LoadLogoThread(match.getHomeTeam().getName(), new LoadLogoThread.LoadLogoListener() {
                    @Override
                    public void onLoadLogoFinish(Bitmap bitmap) {
                        homeImageView.setImageBitmap(bitmap);
                    }
                },context);
                homeThread.start();
            }
        });
        Picasso.with(context).load("file:"+context.getDir("logo_images", Context.MODE_PRIVATE).getPath()+"/"+match.getAwayTeam().getName() + ".png").placeholder(context.getResources().getDrawable(R.drawable.ic_eagle_shield)).into(awayImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                LoadLogoThread awayThread = new LoadLogoThread(match.getAwayTeam().getName(), new LoadLogoThread.LoadLogoListener() {
                    @Override
                    public void onLoadLogoFinish(Bitmap bitmap) {
                        awayImageView.setImageBitmap(bitmap);
                    }
                },context);
                awayThread.start();
            }
        });
    }
}
