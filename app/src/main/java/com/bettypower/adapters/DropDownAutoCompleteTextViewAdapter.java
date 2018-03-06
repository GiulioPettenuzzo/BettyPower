package com.bettypower.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.bettypower.entities.PalimpsestMatch;
import com.renard.ocr.R;
import java.util.ArrayList;


/**
 * Created by giuliopettenuzzo on 28/01/18.
 * Adapter for autoCompleteTextView used for show all the match in the database
 */

public class DropDownAutoCompleteTextViewAdapter<M extends Parcelable> extends ArrayAdapter {

    private ArrayList<PalimpsestMatch> items;
    private ArrayList<PalimpsestMatch> itemsAll;
    private ArrayList<PalimpsestMatch> suggestions;
    private int viewResourceId;
    private PalimpsestMatch lastMatchSelected;
    private SelectItemListener selectItemListener;

    public DropDownAutoCompleteTextViewAdapter(Context context, int viewResourceId, ArrayList<PalimpsestMatch> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<PalimpsestMatch>) items.clone();
        this.suggestions = new ArrayList<PalimpsestMatch>();
        this.viewResourceId = viewResourceId;
    }

    public void setAllMatches(ArrayList<PalimpsestMatch> items){
        this.items = items;
        this.itemsAll = (ArrayList<PalimpsestMatch>) items.clone();
        this.suggestions = new ArrayList<PalimpsestMatch>();
        notifyDataSetChanged();
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public DropDownAutoCompleteTextViewAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (vi != null) {
                v = vi.inflate(viewResourceId, null);
            }
        }
        PalimpsestMatch match = items.get(position);
        if (match != null && v!=null) {
            TextView homeTeamName = (TextView) v.findViewById(R.id.home_team_item_autocomplete);
            if (homeTeamName != null) {
                homeTeamName.setText(match.getHomeTeam().getName());
            }
            TextView awayTeamName = (TextView) v.findViewById(R.id.away_team_autocomplete);
            if(awayTeamName != null){
                awayTeamName.setText(match.getAwayTeam().getName());
            }
            TextView matchDate = (TextView) v.findViewById(R.id.match_date_item_autocomplete);
            if(matchDate != null){
                matchDate.setText(match.getDate());
            }
        }
        return v;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((PalimpsestMatch)(resultValue)).getHomeTeam().getName() + " - " +  ((PalimpsestMatch)(resultValue)).getAwayTeam().getName();
            lastMatchSelected = (PalimpsestMatch) resultValue;
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            selectItemListener.onMatchNotSelected();
            if(constraint != null) {
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
            ArrayList<PalimpsestMatch> filteredList = (ArrayList<PalimpsestMatch>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (PalimpsestMatch c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

    private ArrayList<PalimpsestMatch> setSuggestion(CharSequence constraint){
        ArrayList<PalimpsestMatch> matchSuggested = new ArrayList<>();
        for (PalimpsestMatch currentCustomer : itemsAll) {
            PalimpsestMatch customer = currentCustomer;
            if(customer.getHomeTeam().getName().contains("-")) {
                customer.getHomeTeam().setName(customer.getHomeTeam().getName().replace("-"," "));
            }
            if(customer.getAwayTeam().getName().contains("-")) {
                customer.getAwayTeam().setName(customer.getAwayTeam().getName().replace("-"," "));
            }
            if(constraint.toString().contains("-")){
                String homeConstraint = constraint.subSequence(0,constraint.toString().indexOf("-")-1).toString();
                String awayConstraint = constraint.subSequence(constraint.toString().indexOf("-")+2,constraint.length()).toString();
                if (customer.getAwayTeam().getName().toLowerCase().startsWith(awayConstraint.toLowerCase()) && customer.getHomeTeam().getName().equalsIgnoreCase(homeConstraint)) {
                    matchSuggested.add(currentCustomer);
                }
            }else {
                if (customer.getHomeTeam().getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                    matchSuggested.add(currentCustomer);
                }
            }
        }
        return matchSuggested;
    }

    public void setSelectedItemListener(SelectItemListener selectedItemListener){
        this.selectItemListener = selectedItemListener;
    }

    public interface SelectItemListener{
        void onMatchNotSelected();
    }

    public PalimpsestMatch getLastMatchSelected(){
        return lastMatchSelected;
    }

}
