package com.bettypower.listeners;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.bettypower.adapters.SingleBetAdapter;
import com.bettypower.entities.Match;
import com.renard.ocr.R;


import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 11/07/17.
 */

public class ExpandCollapseClickListener implements View.OnClickListener {
    SingleBetAdapter.ViewHolder holder;
    Match match;
    ArrayList<Match> isSelected;
    //private static int clickCounter = 0;
    private int originalHeight = 0;
    private float elevation = 0;
    private int hidenResultHeight = 0;
    private ValueAnimator dropDownAnimation;
    Context context;

    private ClickStat clickStat;


    private final static int DURATION = 300;


    public ExpandCollapseClickListener(Context context, SingleBetAdapter.ViewHolder holder, Match match, ArrayList<Match> isSelected){
        this.holder = holder;
        this.match = match;
        this.isSelected = isSelected;
        this.context = context;
    }
    public void setCickStat(ClickStat clickStat){
        this.clickStat = clickStat;
    }

    @Override
    public void onClick(View v) {
        if (match.getAllHiddenResult()!= null&&match.getAllHiddenResult().size()>0&&isSelected.lastIndexOf(match) < 0) {
            clickStat.onClickStart();
            holder.isItemSelected = true;
            holder.lineSeparator.setVisibility(View.VISIBLE);

            SingleBetAdapter.addLayouts(match.getAllHiddenResult(), holder);

            originalHeight = holder.itemView.getHeight();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = holder.itemView.getElevation();
                holder.itemView.setElevation(50);
            }
            holder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24px));
            Thread x = new Thread(new Runnable() {
                @Override
                public void run() {
                    RotateAnimation rotate = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    holder.dropDownImageView.setAnimation(rotate);
                    rotate.setDuration(DURATION);
                    rotate.setFillEnabled(true);
                    rotate.start();
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24px));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });
            x.start();
            holder.hiddenResult.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                holder.hiddenResult.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } else {
                                holder.hiddenResult.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }

                            holder.hiddenResult.measure(0,0);
                            hidenResultHeight = holder.hiddenResult.getMeasuredHeight();

                            dropDownAnimation = ValueAnimator.ofInt(originalHeight, originalHeight + hidenResultHeight); // These values in this method can be changed to expand however much you like
                            dropDownAnimation.setDuration(DURATION);
                            dropDownAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            dropDownAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    Integer value = null;
                                    value = (Integer) animation.getAnimatedValue();
                                    holder.itemView.getLayoutParams().height = value;
                                    holder.hiddenResult.setVisibility(View.VISIBLE);
                                    if(holder.itemView.getLayoutParams().height>=originalHeight+hidenResultHeight) {
                                        //holder.itemView.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                                        holder.itemView.setMinimumHeight(value);
                                        holder.itemView.requestLayout();
                                        clickStat.onClickEnds();
                                    }
                                }
                            });
                            holder.itemView.clearAnimation();

                            dropDownAnimation.start();

                        }

                    });
            isSelected.add(match);

        } else if (isSelected.lastIndexOf(match) >= 0) {
            clickStat.onClickStart();
            holder.isItemSelected = false;
            holder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24px));
            Thread x = new Thread(new Runnable() {
                @Override
                public void run() {
                    RotateAnimation rotate = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    holder.dropDownImageView.setAnimation(rotate);
                    rotate.setDuration(DURATION);
                    rotate.setFillEnabled(true);
                    rotate.start();
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.dropDownImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24px));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });
            x.start();

            hidenResultHeight = holder.hiddenResult.getHeight();
            originalHeight = holder.itemView.getHeight()-hidenResultHeight;


            dropDownAnimation = ValueAnimator.ofInt(originalHeight + hidenResultHeight, originalHeight);
            dropDownAnimation.setDuration(300);
            dropDownAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            dropDownAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = null;
                    value = (Integer) animation.getAnimatedValue();
                    holder.itemView.getLayoutParams().height = value;
                    Log.i("rate", String.valueOf(value));
                    holder.hiddenResult.removeAllViews();
                    if(holder.itemView.getLayoutParams().height==originalHeight) {
                        holder.itemView.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                        holder.lineSeparator.setVisibility(View.GONE);
                        clickStat.onClickEnds();
                    }
                }
            });
            holder.itemView.clearAnimation();
            dropDownAnimation.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.itemView.setElevation(elevation);
            }
            hidenResultHeight = 0;
            isSelected.remove(match);
        }
    }



    public interface ClickStat{
        void onClickStart();
        void onClickEnds();
    }
}