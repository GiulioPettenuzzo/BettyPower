package com.bettypower.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bettypower.HiddenResultLayout;
import com.bettypower.SingleBetActivity;
import com.bettypower.betMatchFinder.labelSet.BetLabelSet;
import com.bettypower.dialog.SetBetDialog;
import com.bettypower.entities.ParcelableMatch;
import com.bettypower.entities.ParcelableTeam;
import com.bettypower.entities.Team;
import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.Match;

import java.util.ArrayList;

import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.Match;
import com.bettypower.util.HashMatchUtil;
import com.renard.ocr.R;
import com.renard.ocr.documents.viewing.DocumentContentProvider;

/**
 * Created by giuliopettenuzzo on 21/06/17.
 */

public class SingleBetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** FOR DECORATION
     * RecyclerView.ItemDecoration itemDecoration = new
     DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
     recyclerView.addItemDecoration(itemDecoration);
     */
    private ArrayList<Match> allMatches = new ArrayList<>();
    private ArrayList<Match> isSelected = new ArrayList<>();
    float elevation = 0;
    private Uri uri;

    private static final String ACTION_GOAL = "Goal";
    private static final String ACTION_YELLOWCARD = "Yellowcard";
    private static final String ACTION_REDCARD = "Redcard";

    OutsideClicklistener outsideClicklistener;
    static Context context;

    //normal constructor
    public SingleBetAdapter(Context context, ArrayList<Match> allMatches){
        this.context = context;
        this.allMatches = allMatches;
    }
    //constructor for instance state
    public SingleBetAdapter(Context context, ArrayList<Match> allMatches, ArrayList<Match> allSelectedMatch){
        this.context = context;
        this.allMatches = allMatches;
        isSelected = allSelectedMatch;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View viewOfRecycle = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_bet,parent,false);
                ViewHolder viewHolder = new ViewHolder(viewOfRecycle);
                return viewHolder;
            case 1:
                View lastViewOfRecycle = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_bet_last_item,parent,false);
                LastItemViewHolder lastViewHolder = new LastItemViewHolder(lastViewOfRecycle);
                return lastViewHolder;
        }
        return null;
    }

    @Override
    public void onViewRecycled(final RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        SingleBetAdapter.ViewHolder viewHolder = (SingleBetAdapter.ViewHolder) holder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewHolder.itemView.setElevation(elevation);
        }
        viewHolder.hiddenResult.removeAllViews();
        Log.i("sto", "reciclando");
    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * <p>
     * <p>This can be used as a reasonable signal that the view is about to be seen
     * by the user. If the adapter previously freed any resources in
     * {@link #onViewDetachedFromWindow(RecyclerView.ViewHolder) onViewDetachedFromWindow}
     * those resources should be restored here.</p>
     *
     *
     * @param holder Holder of the view being attached
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(holder.getItemViewType() == 0) {
            SingleBetAdapter.ViewHolder viewHolder = (SingleBetAdapter.ViewHolder) holder;
            if (viewHolder.isSelected) {
                viewHolder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24px));
            } else {
                viewHolder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24px));
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //holder.hiddenResult.removeAllViews();
        switch (holder.getItemViewType()) {
            case 0:
                final SingleBetAdapter.ViewHolder viewHolder = (SingleBetAdapter.ViewHolder) holder;
                final Match match = allMatches.get(position);
                Team homeTeam = match.getHomeTeam();
                viewHolder.homeTeam.setText(homeTeam.getName());
                Team awayTeam = match.getAwayTeam();
                viewHolder.awayTeam.setText(awayTeam.getName());
                viewHolder.homeScores.setText(match.getHomeResult());
                viewHolder.awayScores.setText(match.getAwayResult());
                viewHolder.time.setText(match.getTime());
                viewHolder.bet.setText(match.getBet());
                viewHolder.betKind.setText(match.getBetKind());
                viewHolder.quote.setText(match.getQuote());
                Log.i("sto", "bindando + " + String.valueOf(position));
                //set the correct logo image
                if(match.getHomeTeam().getBitmapLogo()!=null) {
                    viewHolder.homeLogo.setImageBitmap(match.getHomeTeam().getBitmapLogo());
                } else{
                    viewHolder.homeLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_shield_with_castle_inside));
                }
                if(match.getAwayTeam().getBitmapLogo()!=null) {
                    viewHolder.awayLogo.setImageBitmap(match.getAwayTeam().getBitmapLogo());
                } else{
                    viewHolder.awayLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eagle_shield));
                }
                //set the expand collapse view
                if(isSelected.lastIndexOf(match)>=0){
                    addLayouts(match.getAllHiddenResult(), viewHolder);
                    viewHolder.isSelected = true;
                    viewHolder.dropDownImageView.setVisibility(View.VISIBLE);
                    viewHolder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24px));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        viewHolder.itemView.setElevation(50);
                    }
                }
                else{
                    viewHolder.hiddenResult.removeAllViews();
                    viewHolder.isSelected = false;
                    if( match.getAllHiddenResult()!=null && match.getAllHiddenResult().size()>0){
                        viewHolder.dropDownImageView.setVisibility(View.VISIBLE);
                        viewHolder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24px));
                    }
                    else {
                        viewHolder.dropDownImageView.setVisibility(View.INVISIBLE);
                    }
                }

                outsideClicklistener.onItemClicked(context,viewHolder,match,isSelected);
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        SetBetDialog selectBetDialog = new SetBetDialog(context,match);
                        selectBetDialog.show();
                        selectBetDialog.setFinishEdittingDialogListener(new SetBetDialog.FinishEdittingDialogListener() {
                            @Override
                            public void onEdittingDialogFinish(Match editMatch) {
                                viewHolder.bet.setText(editMatch.getBet());
                                viewHolder.betKind.setText(editMatch.getBetKind());
                                viewHolder.quote.setText(editMatch.getQuote());
                                match.setBetKind(editMatch.getBetKind());
                                match.setBet(editMatch.getBet());
                                match.setQuote(editMatch.getQuote());
                              //  allMatches.remove(allMatches.size()-1);
                                HashMatchUtil utils = new HashMatchUtil();
                                String stringArrayMatch = utils.fromArrayToString(allMatches);
                                ContentValues values = new ContentValues();
                                values.put(DocumentContentProvider.Columns.OCR_TEXT, stringArrayMatch);
                                context.getContentResolver().update(uri,values,null,null);
                            }
                        });
                        return false;
                    }
                });
                break;
            case 1:
                break;
        }

    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     * <p>
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        if(position == allMatches.size()){
            return 1;
        }
        else {
            return 0;
        }
    }

    public static void addLayouts(ArrayList<HiddenResult> events, ViewHolder viewHolder) {
        viewHolder.hiddenResult.removeAllViews();
        for (HiddenResult event : events) {
            switch (event.getAction()){
                case ACTION_GOAL:
                    HiddenResultLayout hiddenResultLayout = new HiddenResultLayout(context,event);
                    viewHolder.hiddenResult.addView(hiddenResultLayout);
                    hiddenResultLayout.setGoalHiddenResult();
                    break;
                case ACTION_REDCARD:
                    HiddenResultLayout hiddenResultLayoutRed = new HiddenResultLayout(context,event);
                    viewHolder.hiddenResult.addView(hiddenResultLayoutRed);
                    hiddenResultLayoutRed.setRedCardHiddenResult();
                    break;
                case ACTION_YELLOWCARD:
                    HiddenResultLayout hiddenResultLayoutYellow = new HiddenResultLayout(context,event);
                    viewHolder.hiddenResult.addView(hiddenResultLayoutYellow);
                    hiddenResultLayoutYellow.setYellowCardHiddenResult();
                    break;
            }

        }
    }

    public ArrayList<Match> getSelectedMatch(){
        return isSelected;
    }

    public void setAllMatches(ArrayList<Match> allMatches){
        this.allMatches = allMatches;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return allMatches.size() +1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        TextView homeTeam;
        TextView awayTeam;
        TextView homeScores;
        TextView awayScores;
        TextView time;
        TextView betKind;
        TextView bet;
        TextView quote;
        public ImageView dropDownImageView;
        public ImageView homeLogo;
        public ImageView awayLogo;

        public LinearLayout hiddenResult;
        ConstraintLayout normalResult;

        public boolean isSelected = false;


        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            //this.itemView.setBackgroundColor(Color.WHITE);
            normalResult = (ConstraintLayout) itemView.findViewById(R.id.middle_screen);
            hiddenResult = (LinearLayout) itemView.findViewById(R.id.hidden_result);
            hiddenResult.setOrientation(LinearLayout.VERTICAL);
            homeTeam = (TextView) itemView.findViewById(R.id.home_team);
            awayTeam = (TextView) itemView.findViewById(R.id.away_team);
            homeScores = (TextView) itemView.findViewById(R.id.home_scores);
            awayScores = (TextView) itemView.findViewById(R.id.away_scores);
            time = (TextView) itemView.findViewById(R.id.match_time);
            dropDownImageView = (ImageView) itemView.findViewById(R.id.drop_down_item_view);
            homeLogo = (ImageView) itemView.findViewById(R.id.home_logo);
            awayLogo = (ImageView) itemView.findViewById(R.id.away_logo);
            betKind = (TextView) itemView.findViewById(R.id.bet_kind);
            bet = (TextView) itemView.findViewById(R.id.bet);
            quote = (TextView) itemView.findViewById(R.id.quote);

            ConstraintSet set = new ConstraintSet();
            set.clone(normalResult);
           // set.constrainWidth(betKind.getId(),ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
            set.constrainWidth(homeTeam.getId(),ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
            set.constrainWidth(awayTeam.getId(),ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
            set.applyTo(normalResult);

        }
    }

    public class LastItemViewHolder extends RecyclerView.ViewHolder{

        public View itemView;

        public LastItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemView.setElevation(30);
            }
        }
    }

    public void setUri(Uri uri){
        this.uri = uri;
    }

    public interface OutsideClicklistener{
        void onItemClicked(Context context, SingleBetAdapter.ViewHolder holder, Match match, ArrayList<Match> isSelected);
    }
    public void setOutsideClickListener(OutsideClicklistener outsideClickListener){
        this.outsideClicklistener = outsideClickListener;
    }
}
