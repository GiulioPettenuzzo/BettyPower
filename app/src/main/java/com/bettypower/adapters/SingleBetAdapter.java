package com.bettypower.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bettypower.HiddenResultLayout;
import com.bettypower.adapters.helpers.CorrectionToUserWatcher;
import com.bettypower.adapters.helpers.ExpandCollapseClickExecutor;
import com.bettypower.betChecker.BetChecker;
import com.bettypower.betChecker.MatchChecker;
import com.bettypower.dialog.DeleteMatchDialog;
import com.bettypower.dialog.SetBetDialog;
import com.bettypower.entities.Bet;
import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.ParcelableHiddenResult;
import com.bettypower.util.Helper;
import com.bettypower.util.touchHelper.ItemTouchHelperAdapter;
import com.renard.ocr.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * this is the adapter of single bet
 * Created by giuliopettenuzzo on 21/06/17.
 */

public class SingleBetAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private Bet singleBet;
    private ArrayList<PalimpsestMatch> isSelected = new ArrayList<>();
    private boolean editMode = false;
    private boolean editModeConfirm = false;
    private static final int ELEVATION = 0;
    private Helper helper;
    private MatchChecker matchChecer = new MatchChecker();
    private BetChecker betChecker = new BetChecker();
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    //normal constructor.......
    public SingleBetAdapter(Context context, Bet singleBet){
        SingleBetAdapter.context = context;
        helper = new Helper(context);
        this.singleBet = singleBet;
    }
    //constructor for instance state
    public SingleBetAdapter(Context context, Bet singleBet, ArrayList<PalimpsestMatch> allSelectedMatch){
        SingleBetAdapter.context = context;
        helper = new Helper(context);
        this.singleBet = singleBet;
        isSelected = allSelectedMatch;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View viewOfRecycle = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_bet,parent,false);
                return  new ViewHolder(viewOfRecycle);
            case 1:
                View lastViewOfRecycle = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_bet_last_item,parent,false);
                return new LastItemViewHolder(lastViewOfRecycle);
        }
        return null;
    }

    @Override
    public void onViewRecycled(final RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        switch (holder.getItemViewType()) {
            case 0:
                SingleBetAdapter.ViewHolder viewHolder = (SingleBetAdapter.ViewHolder) holder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                     viewHolder.itemView.setElevation(ELEVATION);
                }
                viewHolder.awayLogo.setImageBitmap(null);
                viewHolder.homeLogo.setImageBitmap(null);
                viewHolder.hiddenResult.removeAllViews();
                break;
            case 1:
                SingleBetAdapter.LastItemViewHolder lastItemViewHolder = (SingleBetAdapter.LastItemViewHolder) holder;
                break;
        }
    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * <p>
     * <p>This can be used as a reasonable signal that the view is about to be seen
     * by the user. If the adapter previously freed any resources in
     * {@link #onViewDetachedFromWindow(RecyclerView.ViewHolder) onViewDetachedFromWindow}
     * those resources should be restored here.</p>
     *
     * @param holder Holder of the view being attached
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        switch (holder.getItemViewType()) {
            case 0:
                SingleBetAdapter.ViewHolder viewHolder = (SingleBetAdapter.ViewHolder) holder;
                if(!editMode) {
                    if (viewHolder.isItemSelected) {
                        viewHolder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24px));
                    } else {
                        viewHolder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24px));
                    }
                }
                break;
            case 1:
                break;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                SingleBetAdapter.ViewHolder viewHolder = (SingleBetAdapter.ViewHolder) holder;
                viewHolder.setItemView(singleBet.getArrayMatch().get(position));
                break;
            case 1:
                SingleBetAdapter.LastItemViewHolder lastItemViewHolder = (SingleBetAdapter.LastItemViewHolder) holder;
                lastItemViewHolder.setItemView();
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
        if(position == singleBet.getArrayMatch().size()){
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
                case ParcelableHiddenResult.ACTION_GOAL:
                    HiddenResultLayout hiddenResultLayout = new HiddenResultLayout(context,event);
                    viewHolder.hiddenResult.addView(hiddenResultLayout);
                    hiddenResultLayout.setGoalHiddenResult();
                    break;
                case ParcelableHiddenResult.ACTION_REDCARD:
                    HiddenResultLayout hiddenResultLayoutRed = new HiddenResultLayout(context,event);
                    viewHolder.hiddenResult.addView(hiddenResultLayoutRed);
                    hiddenResultLayoutRed.setRedCardHiddenResult();
                    break;
                case ParcelableHiddenResult.ACTION_YELLOWCARD:
                    HiddenResultLayout hiddenResultLayoutYellow = new HiddenResultLayout(context,event);
                    viewHolder.hiddenResult.addView(hiddenResultLayoutYellow);
                    hiddenResultLayoutYellow.setYellowCardHiddenResult();
                    break;
                case ParcelableHiddenResult.NO_MATCH_FOUND:
                    HiddenResultLayout hiddenResultLayoutNoMatchFound = new HiddenResultLayout(context,event);
                    viewHolder.hiddenResult.addView(hiddenResultLayoutNoMatchFound);
                    hiddenResultLayoutNoMatchFound.setNoMatchFound();
                    break;
            }

        }
    }

    public ArrayList<PalimpsestMatch> getSelectedMatch(){
        return isSelected;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return singleBet.getArrayMatch().size() +1;
    }

    /**
     * Called when user swiped an item of the list in order to delate it
     *
     * @param position the position of swiped element in the list
     */
    @Override
    public void onItemSwiped(final int position) {
        final DeleteMatchDialog deleteMatchDialog = new DeleteMatchDialog(context, singleBet.getArrayMatch().get(position));
        Log.i("position" , String.valueOf(position));
        deleteMatchDialog.setDeleteMatchDialogListener(new DeleteMatchDialog.DeleteMatchDialogListener() {
            @Override
            public void onDeleteMatchConfirm() {
                deleteMatchDialog.dismiss();
                singleBet.getArrayMatch().remove(position);
                notifyItemChanged(singleBet.getArrayMatch().size()+1);
                notifyItemRemoved(position);
            }

            @Override
            public void onDeleteMatchNotConfirm() {
                deleteMatchDialog.dismiss();
                notifyItemChanged(position);
            }
        });
        deleteMatchDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                deleteMatchDialog.dismiss();
                notifyItemChanged(position);
            }
        });
        deleteMatchDialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View itemView;
        public View lineSeparator;
        public ImageView dropDownImageView;
        public LinearLayout hiddenResult;
        public boolean isItemSelected = false;

        TextView homeTeam;
        TextView awayTeam;
        TextView homeScores;
        TextView awayScores;
        TextView time;
        TextView betKind;
        TextView bet;
        TextView quote;
        CheckBox fissaCheckBox;
        TextView fissaTextView;
        ImageView homeLogo;
        ImageView awayLogo;
        ConstraintLayout normalResult;
        PalimpsestMatch match;

        private ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
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
            fissaCheckBox = (CheckBox) itemView.findViewById(R.id.fissa_check_box);
            fissaTextView = (TextView) itemView.findViewById(R.id.fissa_text_view);
            lineSeparator = itemView.findViewById(R.id.separator_line);
            ConstraintSet set = new ConstraintSet();
            set.clone(normalResult);
            set.constrainWidth(homeTeam.getId(),ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
            set.constrainWidth(awayTeam.getId(),ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);

            set.applyTo(normalResult);

            itemView.setOnClickListener(this);
            fissaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        match.setFissa(true);
                    }
                    else{
                        match.setFissa(false);
                    }
                }
            });
            fissaCheckBox.setVisibility(View.GONE);
            fissaTextView.setVisibility(View.GONE);
        }


        private void setItemView(PalimpsestMatch mmatch){
            this.match = mmatch;
            homeTeam.setText(match.getHomeTeam().getName());
            awayTeam.setText(match.getAwayTeam().getName());
            if(isTimeOut(match.getDate(),match.getHour()) && match.getHomeResult().equalsIgnoreCase("-") && match.getAwayResult().equalsIgnoreCase("-") && (match.getResultTime()==null || !match.getResultTime().equalsIgnoreCase("posticipata"))){
                HiddenResult hiddenResult = new ParcelableHiddenResult("","",ParcelableHiddenResult.NO_MATCH_FOUND,ParcelableHiddenResult.ERROR_TEAM);
                ArrayList<HiddenResult> allHiddenResult = new ArrayList<>();
                allHiddenResult.add(hiddenResult);
                match.setAllHiddenResult(allHiddenResult);
            }
            homeScores.setText(match.getHomeResult());
            awayScores.setText(match.getAwayResult());

            if(match.getResultTime()==null || singleBet.areMatchesFinished()){
                time.setText(match.getTime());
            }else{
                if(!match.getResultTime().contains(":"))
                    time.setText(match.getResultTime());
                else
                    time.setText(match.getTime());
            }
            bet.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
            if(helper.isMatchFinish(match) && match.getBet()!=null){
                if(!matchChecer.isMatchWin(match)){
                    bet.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                }
            }
            bet.setText(match.getBet());
            betKind.setText(match.getBetKind());
            quote.setText(match.getQuote());
            Picasso.with(context).load("file:"+context.getDir("logo_images", Context.MODE_PRIVATE).getPath()+"/"+match.getHomeTeam().getName() + ".png").placeholder(context.getResources().getDrawable(R.drawable.ic_shield_with_castle_inside)).error(context.getResources().getDrawable(R.drawable.ic_shield_with_castle_inside)).into(homeLogo);
            Picasso.with(context).load("file:"+context.getDir("logo_images", Context.MODE_PRIVATE).getPath()+"/"+match.getAwayTeam().getName() + ".png").placeholder(context.getResources().getDrawable(R.drawable.ic_eagle_shield)).error(context.getResources().getDrawable(R.drawable.ic_eagle_shield)).into(awayLogo);
            if(editMode){
                lineSeparator.setVisibility(View.GONE);
                dropDownImageView.setVisibility(View.VISIBLE);
                dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.edit_black));
                hiddenResult.setVisibility(View.INVISIBLE);
                hiddenResult.removeAllViews();
                hiddenResult.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                itemView.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.setElevation(0);
                }
                isSelected.clear();
                if(singleBet.isSistema()){
                    fissaCheckBox.setVisibility(View.VISIBLE);
                    fissaTextView.setVisibility(View.GONE);
                    if(match.isFissa()){
                        fissaCheckBox.setChecked(true);
                    }
                    else{
                        fissaCheckBox.setChecked(false);
                    }
                }else{
                    fissaCheckBox.setVisibility(View.GONE);
                }
            }else{
                dropDownImageView.setVisibility(View.VISIBLE);
                hiddenResult.setVisibility(View.VISIBLE);
                hiddenResult.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                fissaCheckBox.setVisibility(View.GONE);
                if(match.isFissa()){
                    fissaTextView.setVisibility(View.VISIBLE);
                    fissaCheckBox.setChecked(true);
                }
                else{
                    fissaCheckBox.setChecked(false);
                    fissaTextView.setVisibility(View.GONE);
                }

                if(isSelected.lastIndexOf(match)>=0){
                    lineSeparator.setVisibility(View.VISIBLE);
                    dropDownImageView.setVisibility(View.VISIBLE);
                    addLayouts(match.getAllHiddenResult(), this);
                    isItemSelected = true;
                    dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24px));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        itemView.setElevation(50);
                    }
                }
                else{
                    lineSeparator.setVisibility(View.GONE);
                    hiddenResult.removeAllViews();
                    isItemSelected = false;
                    if( match.getAllHiddenResult()!=null && match.getAllHiddenResult().size()>0){
                        dropDownImageView.setVisibility(View.VISIBLE);
                        dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24px));
                    }
                    else {
                        dropDownImageView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }

        private boolean isClicking = false; //used for disable the expandCollapseExecutor when he is already operating
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if(!editMode && !isClicking) {
                ExpandCollapseClickExecutor expandCollapseClickExecutor = new ExpandCollapseClickExecutor(context, ViewHolder.this, match, isSelected);
                expandCollapseClickExecutor.setCickStat(new ExpandCollapseClickExecutor.ClickStat() {
                    @Override
                    public void onClickStart() {
                        isClicking = true;
                        expandCollapseClickMode.onClickStart();
                    }

                    @Override
                    public void onClickEnds() {
                        isClicking = false;
                        expandCollapseClickMode.onClickEnds();
                    }
                });
                expandCollapseClickExecutor.onClick();
            }
            if(editMode) {
                SetBetDialog selectBetDialog = new SetBetDialog(context, match);
                selectBetDialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = selectBetDialog.getWindow();
                if (window != null) {
                    lp.copyFrom(window.getAttributes());
                }
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                if (window != null) {
                    window.setAttributes(lp);
                }
                selectBetDialog.setFinishEdittingDialogListener(new SetBetDialog.FinishEdittingDialogListener() {
                    @Override
                    public void onEdittingDialogFinish(PalimpsestMatch editMatch) {
                        match.setBetKind(editMatch.getBetKind());
                        match.setBet(editMatch.getBet());
                        match.setQuote(editMatch.getQuote());
                        notifyItemChanged(singleBet.getArrayMatch().lastIndexOf(match));
                    }
                });
            }
        }
    }

    public class LastItemViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        TextView matchNumberTextView;
        EditText vincitaEditText;
        TextView puntataTextView;
        TextView vincitaTextView;
        EditText puntataEditText;
        EditText errorsEditText;
        CheckBox sistemaCheckBox;
        TextView errorsTextView;
        TextView numberMatchWinTextView;
        TextView numberMatchLostTextView;
        TextView numberMatchNotFinished;
        TextView bookMakerTextView;

        private LastItemViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemView.setElevation(30);
            }
            matchNumberTextView = (TextView) itemView.findViewById(R.id.match_number);
            vincitaEditText = (EditText) itemView.findViewById(R.id.vincita_edit_text);
            puntataTextView = (TextView) itemView.findViewById(R.id.puntata);
            puntataEditText = (EditText) itemView.findViewById(R.id.puntata_edit_text);
            vincitaTextView = (TextView) itemView.findViewById(R.id.vincita);
            errorsEditText = (EditText) itemView.findViewById(R.id.errors_edit_text);
            sistemaCheckBox = (CheckBox) itemView.findViewById(R.id.sistema_checkbox);
            errorsTextView = (TextView) itemView.findViewById(R.id.errors_text_view);
            numberMatchWinTextView = (TextView) itemView.findViewById(R.id.number_match_win);
            numberMatchLostTextView = (TextView) itemView.findViewById(R.id.number_match_lost);
            numberMatchNotFinished = (TextView) itemView.findViewById(R.id.match_not_finished);
            bookMakerTextView = (TextView) itemView.findViewById(R.id.book_maker_text_view);
            setTextEdit();

        }

        private void setItemView(){
            String numberAvveniments = context.getResources().getString(R.string.number_avveniments) +" "+ String.valueOf(singleBet.getArrayMatch().size());
            matchNumberTextView.setText(numberAvveniments);
            if(singleBet.getBookMaker()!=null)
                bookMakerTextView.setText(singleBet.getBookMaker());
            else
                bookMakerTextView.setVisibility(View.INVISIBLE);
            if(editMode){
                itemView.setBackgroundColor(context.getResources().getColor(R.color.toolbar_background_light));
                vincitaTextView.setVisibility(View.VISIBLE);
                vincitaEditText.setVisibility(View.VISIBLE);
                puntataEditText.setVisibility(View.VISIBLE);
                puntataTextView.setVisibility(View.VISIBLE);
                puntataEditText.setVisibility(View.VISIBLE);
                errorsEditText.setVisibility(View.VISIBLE);
                errorsTextView.setVisibility(View.VISIBLE);
                sistemaCheckBox.setOnCheckedChangeListener(null);
                if(singleBet.isSistema())
                    sistemaCheckBox.setChecked(true);
                else
                    sistemaCheckBox.setChecked(false);
                sistemaCheckBox.setOnCheckedChangeListener(new CheckedChangeListener());
                sistemaCheckBox.setVisibility(View.VISIBLE);
                errorsTextView.setText(context.getResources().getString(R.string.sistema_errori));
                vincitaTextView.setText(context.getResources().getString(R.string.totale_vincita));
                puntataTextView.setText(context.getResources().getString(R.string.totale_importo_scommesso));
                errorsEditText.setText(singleBet.getErrors());
                vincitaEditText.setText(singleBet.getVincita());
                puntataEditText.setText(singleBet.getPuntata());
            }
            else{
                itemView.setBackgroundColor(context.getResources().getColor(R.color.toolbar_lighter));
                if(editModeConfirm){
                    editModeConfirm = false;
                    if(!errorsEditText.getText().toString().equals("")) {
                        String errorText = errorsEditText.getText().toString();
                        if(Integer.parseInt(errorText)>=singleBet.getArrayMatch().size())
                            errorText = String.valueOf(singleBet.getArrayMatch().size()-1);
                        String tmp = context.getResources().getString(R.string.sistema_errori) + " " + errorText;
                        errorsTextView.setText(tmp);
                        singleBet.setErrors(errorText);
                    }else{
                        errorsTextView.setVisibility(View.GONE);
                        singleBet.setErrors(null);
                    }
                    if(!puntataEditText.getText().toString().equals("")) {
                        String tmp = context.getResources().getString(R.string.totale_importo_scommesso) + " " + fixEuroValue(puntataEditText.getText().toString()) + "€";
                        puntataTextView.setText(tmp);
                        singleBet.setPuntata(fixEuroValue(puntataEditText.getText().toString()));
                    }else{
                        puntataTextView.setVisibility(View.GONE);
                        singleBet.setPuntata(null);
                    }
                    if(!vincitaEditText.getText().toString().equals("")) {
                        String tmp = context.getResources().getString(R.string.totale_vincita) + " " + fixEuroValue(vincitaEditText.getText().toString()) + "€";
                        vincitaTextView.setText(tmp);
                        singleBet.setVincita(fixEuroValue(vincitaEditText.getText().toString()));
                    }else{
                        vincitaTextView.setVisibility(View.GONE);
                        singleBet.setVincita(null);
                    }
                }
                else{
                    if(singleBet.getPuntata()!=null) {
                        String tmp = context.getResources().getString(R.string.totale_importo_scommesso) + " " + singleBet.getPuntata() + "€";
                        puntataTextView.setText(tmp);
                        puntataEditText.setText(singleBet.getPuntata());
                    }
                    if(singleBet.getVincita()!=null) {
                        String tmp = context.getResources().getString(R.string.totale_vincita) + " " + singleBet.getVincita()+ "€";
                        vincitaTextView.setText(tmp);
                        vincitaEditText.setText(singleBet.getVincita());
                    }
                    if(singleBet.getErrors()!=null) {
                        String tmp = context.getResources().getString(R.string.sistema_errori) + " " + singleBet.getErrors();
                        errorsTextView.setText(tmp);
                        errorsEditText.setText(singleBet.getErrors());
                    }
                }
                if(!betChecker.isBetWin(singleBet,context)){
                    itemView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
                }
                String matchWon = context.getResources().getString(R.string.event_win)+" "+String.valueOf(betChecker.getWinNumber(singleBet));
                String matchLost = context.getResources().getString(R.string.event_lost)+" "+String.valueOf(betChecker.getErrorNumber(singleBet));
                numberMatchWinTextView.setText(matchWon);
                numberMatchLostTextView.setText(matchLost);
                vincitaEditText.setVisibility(View.GONE);
                puntataEditText.setVisibility(View.GONE);
                errorsEditText.setVisibility(View.GONE);
                if(singleBet.getErrors()==null || Integer.parseInt(singleBet.getErrors())==0 || singleBet.getErrors().equals(""))
                    errorsTextView.setVisibility(View.GONE);
                else
                    errorsTextView.setVisibility(View.VISIBLE);
                if(singleBet.getVincita()==null || singleBet.getVincita().isEmpty())
                    vincitaTextView.setVisibility(View.GONE);
                else
                    vincitaTextView.setVisibility(View.VISIBLE);
                if(singleBet.getPuntata()==null || singleBet.getPuntata().isEmpty())
                    puntataTextView.setVisibility(View.GONE);
                else
                    puntataTextView.setVisibility(View.VISIBLE);
                sistemaCheckBox.setVisibility(View.GONE);
            }
            int matchRemained = helper.getNumberMatchNotFinished(singleBet.getArrayMatch());
            String matchRemain = context.getResources().getString(R.string.eventi_rimanenti)+" "+String.valueOf(matchRemained);
            numberMatchNotFinished.setText(matchRemain);
            if(matchRemained==0){
                singleBet.setAreMatchFinished(true);
            }else{
                singleBet.setAreMatchFinished(false);
            }
        }

        private void setTextEdit(){
            vincitaEditText.addTextChangedListener(new CorrectionToUserWatcher(vincitaEditText));
            puntataEditText.addTextChangedListener(new CorrectionToUserWatcher(puntataEditText));

            vincitaEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT){
                        vincitaEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(50)});
                        if(!puntataEditText.getText().toString().equals("")) {
                            vincitaEditText.setText(fixEuroValue(vincitaEditText.getText().toString()));
                            singleBet.setVincita(vincitaEditText.getText().toString());
                        }
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(vincitaEditText.getWindowToken(), 0);
                        }
                        vincitaEditText.clearFocus();

                        return true;
                    }else{
                        return false;
                    }
                }
            });

            vincitaEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus && !vincitaEditText.getText().toString().equals("")) {
                        vincitaEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                        vincitaEditText.setText(fixEuroValue(vincitaEditText.getText().toString()));
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(vincitaEditText.getWindowToken(), 0);
                        }
                        singleBet.setVincita(vincitaEditText.getText().toString());
                        vincitaEditText.clearFocus();
                    }

                }
            });

            puntataEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT){
                        puntataEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(50)});
                        if(!puntataEditText.getText().toString().equals("")) {
                            puntataEditText.setText(fixEuroValue(puntataEditText.getText().toString()));
                            singleBet.setPuntata(puntataEditText.getText().toString());
                        }
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(puntataEditText.getWindowToken(), 0);
                        }
                        puntataEditText.clearFocus();
                        return true;
                    }else{
                        return false;
                    }
                }
            });

            puntataEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus && !puntataEditText.getText().toString().equals("")) {
                        puntataEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                        puntataEditText.setText(fixEuroValue(puntataEditText.getText().toString()));
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(puntataEditText.getWindowToken(), 0);
                        }
                        singleBet.setPuntata(puntataEditText.getText().toString());
                        puntataEditText.clearFocus();
                    }
                }
            });

            errorsEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        if(!errorsEditText.getText().toString().equals("") && Integer.parseInt(errorsEditText.getText().toString())>singleBet.getArrayMatch().size()){
                            errorsEditText.setText(String.valueOf(singleBet.getArrayMatch().size()-1));
                        }
                        if(!errorsEditText.getText().toString().equals("")){
                            singleBet.setErrors(errorsEditText.getText().toString());
                        }
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(errorsEditText.getWindowToken(), 0);
                        }
                        errorsEditText.clearFocus();
                        return true;
                    }
                    else{
                        return false;

                    }
                }
            });

            errorsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus && !errorsEditText.getText().toString().equals("")) {
                        if (Integer.parseInt(errorsEditText.getText().toString()) > singleBet.getArrayMatch().size()) {
                            errorsEditText.setText(String.valueOf(singleBet.getArrayMatch().size() - 1));
                        }
                        if(Integer.parseInt(errorsEditText.getText().toString()) <= 0){
                            errorsEditText.setText("");
                        }
                        singleBet.setErrors(errorsEditText.getText().toString());
                    }
                }
            });
        }

        private String fixEuroValue(String euro){
            if(euro.contains(".")){
                if(euro.lastIndexOf(".")==euro.length()-1){
                    euro = euro + "00";
                }
                else if(euro.lastIndexOf(".")==euro.length()-2){
                    euro = euro+"0";
                }
            }else{
                euro = euro+".00";
            }

            return euro;
        }

        private class CheckedChangeListener implements CheckBox.OnCheckedChangeListener{
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    singleBet.setSistema(true);
                    notifyDataSetChanged();
                } else {
                    singleBet.setSistema(false);
                    for (PalimpsestMatch currentMatch : singleBet.getArrayMatch()
                            ) {
                        currentMatch.setFissa(false);
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }

    public void setBet(Bet singleBet){
        this.singleBet = singleBet;
        notifyDataSetChanged();
    }

    private boolean isTimeOut(String date,String hour) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
        try {
            Date hourMin = sdf.parse(date +"/"+ context.getResources().getString(R.string.current_year) + " " + hour);
            hourMin = addMinutesToDate(hourMin);
            if(System.currentTimeMillis() > hourMin.getTime()){
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
        return false;
    }

    private static Date addMinutesToDate(Date beforeTime){
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        long curTimeInMs = beforeTime.getTime();
        return new Date(curTimeInMs + (100 * ONE_MINUTE_IN_MILLIS));
    }

    public void setEditMode(boolean editMode){
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    public void setEditModeConfirm(){
        editModeConfirm = true;
    }

    private ExpandCollapseClickExecutor.ClickStat expandCollapseClickMode;

    public void setExpandCollapseClickMode(ExpandCollapseClickExecutor.ClickStat expandCollapseClickMode){
        this.expandCollapseClickMode = expandCollapseClickMode;
    }
}