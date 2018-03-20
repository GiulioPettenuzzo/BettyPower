package com.bettypower.adapters;

import android.app.Activity;
import android.content.Context;
import android.icu.text.RelativeDateTimeFormatter;
import android.icu.text.SimpleDateFormat;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bettypower.entities.PalimpsestMatch;
import com.renard.ocr.R;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.stream.Stream;


/**
 * Created by giuliopettenuzzo on 28/01/18.
 * Adapter for autoCompleteTextView used for show all the match in the database
 */

public class DropDownAutoCompleteTextViewAdapter<M extends Parcelable> extends BaseAdapter implements Filterable {

    private ArrayList<PalimpsestMatch> itemsAll;
    private ArrayList<PalimpsestMatch> suggestions = new ArrayList<>();
    private PalimpsestMatch lastMatchSelected;
    private SelectItemListener selectItemListener;
    private Activity activity;
    private Filter filter = new CustomFilter();

    public DropDownAutoCompleteTextViewAdapter(Activity activity, ArrayList<PalimpsestMatch> itemsAll) {
        this.itemsAll = itemsAll;
        this.activity = activity;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return suggestions.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return suggestions.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.item_match_autocomplete,parent, false);
            }
            viewHolder = new ViewHolder();
            viewHolder.homeTeam = (TextView) convertView.findViewById(R.id.home_team_item_autocomplete);
            viewHolder.awayTeam = (TextView) convertView.findViewById(R.id.away_team_autocomplete);
            viewHolder.date = (TextView) convertView.findViewById(R.id.match_date_item_autocomplete);
            convertView.setTag(viewHolder);
        }
        else{

            viewHolder = (ViewHolder) convertView.getTag();
        }
        PalimpsestMatch match = suggestions.get(position);
        if (match != null) {
            viewHolder.homeTeam.setText(match.getHomeTeam().getName());
            viewHolder.awayTeam.setText(match.getAwayTeam().getName());
            viewHolder.date.setText(match.getDate());
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private ArrayList<PalimpsestMatch> setSuggestion(CharSequence constraint) {
        ArrayList<PalimpsestMatch> matchSuggestedWithStartWith = new ArrayList<>();
        ArrayList<PalimpsestMatch> matchSuggestedWithEquals = new ArrayList<>();
        ArrayList<PalimpsestMatch> matchSuggestedWithContains = new ArrayList<>();

        ArrayList<PalimpsestMatch> matchSuggestedEqualsEquals = new ArrayList<>();
        ArrayList<PalimpsestMatch> matchSuggestedEqualsStartWith = new ArrayList<>();
        ArrayList<PalimpsestMatch> matchSuggestedEqualsContains = new ArrayList<>();
        ArrayList<PalimpsestMatch> matchSuggestedContainsEquals = new ArrayList<>();
        ArrayList<PalimpsestMatch> matchSuggestedContainsStartWith = new ArrayList<>();
        ArrayList<PalimpsestMatch> matchSuggestedContainsContains = new ArrayList<>();
        boolean isSeparatorPresent = false;

        for (PalimpsestMatch customer : itemsAll) {
            if (isHomeAwaySeparatorPresent(constraint.toString())) {
                isSeparatorPresent = true;
                String homeConstraint = constraint.subSequence(0, constraint.toString().indexOf("-") - 1).toString();
                String awayConstraint = constraint.subSequence(constraint.toString().indexOf("-") + 2, constraint.length()).toString();
                if(customer.getAwayTeam().getName().toLowerCase().equals(awayConstraint.toLowerCase()) && (customer.getHomeTeam().getName().equals(homeConstraint))){
                    matchSuggestedEqualsEquals.add(customer);
                } else if(customer.getAwayTeam().getName().toLowerCase().equals(awayConstraint.toLowerCase())&& (customer.getHomeTeam().getName().startsWith(homeConstraint) || homeConstraint.isEmpty() || homeConstraint == null)){
                    matchSuggestedEqualsStartWith.add(customer);
                } else if (customer.getAwayTeam().getName().toLowerCase().equals(awayConstraint.toLowerCase()) && (customer.getHomeTeam().getName().contains(homeConstraint) || homeConstraint.isEmpty() || homeConstraint == null)) {
                    matchSuggestedEqualsContains.add(customer);
                } else if (customer.getAwayTeam().getName().toLowerCase().contains(awayConstraint.toLowerCase()) && (customer.getHomeTeam().getName().equals(homeConstraint) || homeConstraint.isEmpty() || homeConstraint == null)) {
                    matchSuggestedContainsEquals.add(customer);
                } else if (customer.getAwayTeam().getName().toLowerCase().contains(awayConstraint.toLowerCase()) && (customer.getHomeTeam().getName().startsWith(homeConstraint) || homeConstraint.isEmpty() || homeConstraint == null)) {
                    matchSuggestedContainsStartWith.add(customer);
                } else if (customer.getAwayTeam().getName().toLowerCase().contains(awayConstraint.toLowerCase()) && (customer.getHomeTeam().getName().contains(homeConstraint) || homeConstraint.isEmpty() || homeConstraint == null)) {
                    matchSuggestedContainsContains.add(customer);
                }
            } else {
                isSeparatorPresent = false;
                if(customer.getHomeTeam().getName().toLowerCase().equals(constraint.toString().toLowerCase())){
                    matchSuggestedWithEquals.add(customer);
                } else if (customer.getHomeTeam().getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                    matchSuggestedWithStartWith.add(customer);
                } else if (customer.getHomeTeam().getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    matchSuggestedWithContains.add(customer);
                }
            }
        }

        ArrayList<PalimpsestMatch> matchSuggested = new ArrayList<>();

        if(!isSeparatorPresent) {
            //ONLY HOME MATCH
            matchSuggested.addAll(matchSuggestedWithEquals);
            matchSuggested.addAll(matchSuggestedWithStartWith);
            matchSuggested.addAll(matchSuggestedWithContains);
        }

        else{
            //HOME AND AWAY MATCH
            matchSuggested.addAll(matchSuggestedEqualsEquals);
            matchSuggested.addAll(matchSuggestedEqualsStartWith);
            matchSuggested.addAll(matchSuggestedEqualsContains);
            matchSuggested.addAll(matchSuggestedContainsEquals);
            matchSuggested.addAll(matchSuggestedContainsStartWith);
            matchSuggested.addAll(matchSuggestedContainsContains);
        }
        return matchSuggested;
    }


    private boolean isHomeAwaySeparatorPresent(String constraint) {
        StringTokenizer token = new StringTokenizer(constraint);
        while (token.hasMoreTokens()) {
            String word = token.nextToken();
            if (word.equals("-")) {
                return true;
            }
        }
        return false;
    }

    public PalimpsestMatch getLastMatchSelected() {
        return lastMatchSelected;
    }

    private class CustomFilter extends Filter {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((PalimpsestMatch) (resultValue)).getHomeTeam().getName() + " - " + ((PalimpsestMatch) (resultValue)).getAwayTeam().getName();
            lastMatchSelected = (PalimpsestMatch) resultValue;
            return str;
        }

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            final FilterResults filterResults = new FilterResults();
            final ArrayList<PalimpsestMatch> suggested = setSuggestion(constraint);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    selectItemListener.onMatchNotSelected();
                    if (constraint != null) {
                        suggestions.clear();
                        suggestions = suggested;
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                    }
                }
            });
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public static class ViewHolder {
        public TextView homeTeam;
        public TextView awayTeam;
        public TextView date;
    }

    public interface SelectItemListener {
        void onMatchNotSelected();
    }

    public void setSelectedItemListener(SelectItemListener selectedItemListener) {
        this.selectItemListener = selectedItemListener;
    }


}
