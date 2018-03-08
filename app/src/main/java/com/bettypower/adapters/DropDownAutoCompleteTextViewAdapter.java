package com.bettypower.adapters;

import android.app.Activity;
import android.content.Context;
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


import java.util.ArrayList;
import java.util.StringTokenizer;


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

    public void setAllMatches(ArrayList<PalimpsestMatch> itemsAll) {
        this.itemsAll = itemsAll;
        notifyDataSetChanged();
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
        ArrayList<PalimpsestMatch> matchSuggested = new ArrayList<>();
        ArrayList<PalimpsestMatch> matchSuggestedWithContains = new ArrayList<>();
        for (PalimpsestMatch currentCustomer : itemsAll) {
            PalimpsestMatch customer = currentCustomer;
            if (isHomeAwaySeparatorPresent(constraint.toString())) {
                String homeConstraint = constraint.subSequence(0, constraint.toString().indexOf("-") - 1).toString();
                String awayConstraint = constraint.subSequence(constraint.toString().indexOf("-") + 2, constraint.length()).toString();
                if (customer.getAwayTeam().getName().toLowerCase().startsWith(awayConstraint.toLowerCase()) && (customer.getHomeTeam().getName().contains(homeConstraint) || homeConstraint.isEmpty() || homeConstraint == null)) {
                    matchSuggested.add(currentCustomer);
                } else if (customer.getAwayTeam().getName().toLowerCase().contains(awayConstraint.toLowerCase()) && (customer.getHomeTeam().getName().contains(homeConstraint) || homeConstraint.isEmpty() || homeConstraint == null)) {
                    matchSuggestedWithContains.add(currentCustomer);
                }
            } else {
                if (customer.getHomeTeam().getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                    matchSuggested.add(currentCustomer);
                }else if (customer.getHomeTeam().getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    matchSuggestedWithContains.add(currentCustomer);
                }
            }
        }
        for (PalimpsestMatch currentMatch:matchSuggestedWithContains
             ) {
            matchSuggested.add(currentMatch);
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
        protected FilterResults performFiltering(CharSequence constraint) {
            selectItemListener.onMatchNotSelected();
            if (constraint != null) {
                suggestions.clear();
                suggestions = setSuggestion(constraint);
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
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
