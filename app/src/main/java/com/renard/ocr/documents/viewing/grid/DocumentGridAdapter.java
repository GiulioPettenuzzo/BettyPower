/*
 * Copyright (C) 2012,2013 Renard Wellnitz.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.renard.ocr.documents.viewing.grid;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bettypower.entities.Bet;
import com.bettypower.entities.HiddenResult;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.SingleBet;
import com.bettypower.entities.Team;
import com.bettypower.entities.deserialized.HiddenResultDeserialized;
import com.bettypower.entities.deserialized.PalimpsestMatchDeserialized;
import com.bettypower.entities.deserialized.TeamDeserialized;
import com.bettypower.util.Helper;
import com.bettypower.util.touchHelper.ItemTouchHelperAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.renard.ocr.documents.viewing.DocumentContentProvider;
import com.renard.ocr.R;
import com.renard.ocr.documents.viewing.grid.CheckableGridElement.OnCheckedChangeListener;
import com.renard.ocr.documents.viewing.DocumentContentProvider.Columns;
import com.renard.ocr.util.Util;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * adapter for the document grid view
 * @author renard
 *
 */
public class DocumentGridAdapter extends CursorAdapter implements OnCheckedChangeListener {


	public interface OnCheckedChangeListener {
		void onCheckedChanged(final Set<Integer> checkedIds);
	}

	private final static String[] PROJECTION = { Columns.ID, Columns.TITLE, Columns.OCR_TEXT, Columns.CREATED, Columns.PHOTO_PATH,Columns.CHILD_COUNT,Columns.HOCR_TEXT };

	private Set<Integer> mSelectedDocuments = new HashSet<Integer>();
	private LayoutInflater mInflater;
	private final DocumentGridActivity mActivity;
	private int mElementLayoutId;

	private int mIndexCreated;
	private int mIndexTitle;
	private int mIndexID;
	private int mChildCountID;
	private int photoPath;

	private int betIndex;
	private OnCheckedChangeListener mCheckedChangeListener = null;
	private Helper helper;

	private Gson gson;

	static class DocumentViewHolder {

		public CheckableGridElement gridElement;
		private TextView date;
		private TextView mPageNumber;
		public int documentId;
		public boolean updateThumbnail;
		CrossFadeDrawable transition;
		private TextView eventNumberTextView;
		private TextView totaleImportoScommesso;
		private TextView totaleImportoPagamento;
		private TextView dailyInformations;

		DocumentViewHolder(View v) {
			gridElement = (CheckableGridElement) v;
			date = (TextView) v.findViewById(R.id.date);
			mPageNumber = (TextView) v.findViewById(R.id.page_number);
			eventNumberTextView = (TextView) v.findViewById(R.id.event_number);
			totaleImportoScommesso = (TextView) v.findViewById(R.id.importo_scommesso);
			totaleImportoPagamento = (TextView) v.findViewById(R.id.importo_pagamento);
			dailyInformations = (TextView) v.findViewById(R.id.daily_information);
		}

	}

	public void clearAllSelection() {
		mSelectedDocuments.clear();
	}

	int indexHocrForManual;

	public DocumentGridAdapter(DocumentGridActivity activity, int elementLayout, OnCheckedChangeListener listener) {
		super(activity, activity.getContentResolver().query(DocumentContentProvider.CONTENT_URI, PROJECTION, DocumentContentProvider.Columns.PARENT_ID + "=-1", null, null), true);
		mElementLayoutId = elementLayout;
		mActivity = activity;
		mInflater = LayoutInflater.from(activity);
		Cursor c = getCursor();
		mIndexCreated = c.getColumnIndex(Columns.CREATED);
		mIndexID = c.getColumnIndex(Columns.ID);
		mIndexTitle = c.getColumnIndex(Columns.TITLE);
		mChildCountID = c.getColumnIndex(Columns.CHILD_COUNT);
		betIndex = c.getColumnIndex(Columns.OCR_TEXT);
		indexHocrForManual = c.getColumnIndex(Columns.HOCR_TEXT);
		photoPath = c.getColumnIndex(Columns.PHOTO_PATH);
		mCheckedChangeListener = listener;
		helper = new Helper(activity.getApplicationContext());
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(HiddenResult.class,new HiddenResultDeserialized());
		gsonBuilder.registerTypeAdapter(Team.class,new TeamDeserialized());
		gsonBuilder.registerTypeAdapter(PalimpsestMatch.class,new PalimpsestMatchDeserialized());
		gson = gsonBuilder.create();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final DocumentViewHolder holder = (DocumentViewHolder) view.getTag();
		final int documentId = cursor.getInt(mIndexID);
		final int childCount = cursor.getInt(mChildCountID);
		final boolean isSelected = mSelectedDocuments.contains(documentId);
		holder.documentId = documentId;

		String title = cursor.getString(mIndexTitle);
		if (title != null && title.length() > 0) {
			holder.date.setText(title);
		} else {
			long created = cursor.getLong(mIndexCreated);
			CharSequence formattedDate = DateFormat.format("MMM dd, yyyy h:mmaa", new Date(created));
			holder.date.setText(formattedDate);
		}

		if (holder.mPageNumber != null) {
			holder.mPageNumber.setText(String.valueOf(childCount+1));
		}
		String hocrForManual = cursor.getString(indexHocrForManual);

		if (holder.gridElement != null) {
			if(hocrForManual!=null) {
			/*	if(hocrForManual.equals("image_shared")){
					Picasso.with(context).load(photoPath).into(holder.imageView);

					String photoPathString = cursor.getString(photoPath);
					Drawable d = Drawable.createFromPath(photoPathString);
					holder.gridElement.setImage(d);



				}else {*/
					if (mActivity.getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_FLING || mActivity.isPendingThumbnailUpdate()) {
						holder.gridElement.setImage(Util.sDefaultDocumentThumbnail);
						holder.updateThumbnail = true;
					} else {
						final Drawable d = Util.getDocumentThumbnail(documentId);
						holder.gridElement.setImage(d);
						holder.updateThumbnail = false;
					}
			//	}
			}else{
				int resId[]={R.drawable.ic_soccer_player_yellow_green_silhouette
						,R.drawable.ic_soccer_player_dark_green_silhouette
						,R.drawable.ic_soccer_player_dodger_blue_silhouette
						,R.drawable.ic_soccer_player_grey_silhouette
						,R.drawable.ic_soccer_player_indigo_silhouette
						,R.drawable.ic_soccer_player_light_sea_green_silhouette
						,R.drawable.ic_soccer_player_purple_silhouette
				};
				Random rand = new Random();
				int index = rand.nextInt((resId.length- 1) - 0 + 1) + 0;

				//imgView.setImageResource(resId[index]);
				holder.gridElement.setImage(context.getResources().getDrawable(resId[index]));
			}
		}

		String betText = cursor.getString(betIndex);
		Bet bet = gson.fromJson(betText, SingleBet.class);

		String eventNumber = String.valueOf(bet.getArrayMatch().size());

		int numberMatchRemained = helper.getNumberMatchNotFinished(bet.getArrayMatch());
		//Log.i("event number",eventNumber);
		String importoPagamento = bet.getVincita();
	//	Log.i("importo pagamento",importoPagamento);
		String importoScommesso = bet.getPuntata();
	//	Log.i("importo scommesso",importoScommesso);
		if(numberMatchRemained != 0) {
			holder.dailyInformations.setText(context.getResources().getString(R.string.eventi_rimanenti)+" "+String.valueOf(numberMatchRemained));
		}
		else{
			holder.dailyInformations.setText(context.getResources().getString(R.string.scommessa_completata));
		}

		if(eventNumber!=null){
			holder.eventNumberTextView.setText(context.getResources().getString(R.string.number_avveniments) + " " +eventNumber);
		}else{
			holder.eventNumberTextView.setText(context.getResources().getString(R.string.number_avveniments) + " " + context.getResources().getString(R.string.field_undefined));
		}
		if(importoScommesso!=null){
			holder.totaleImportoScommesso.setText(context.getResources().getString(R.string.totale_importo_scommesso) + " " +importoScommesso);
		}else{
			holder.totaleImportoScommesso.setText(context.getResources().getString(R.string.totale_importo_scommesso) + " " + context.getResources().getString(R.string.field_undefined));
		}
		if(importoPagamento!=null){
			holder.totaleImportoPagamento.setText(context.getResources().getString(R.string.totale_vincita) + " " +importoPagamento);
		}else{
			holder.totaleImportoPagamento.setText(context.getResources().getString(R.string.totale_vincita) + " " + context.getResources().getString(R.string.field_undefined));
		}
		holder.gridElement.setCheckedNoAnimate(isSelected);
		if(!mSelectedDocuments.contains(documentId))
			holder.gridElement.setChecked(false);
	};

	
	@Override
	public long getItemId(int position) {
		if (getCursor().moveToPosition(position)) {
			int index = getCursor().getColumnIndex(Columns.ID);
			return getCursor().getLong(index);
		}
		return -1;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = null;
		DocumentViewHolder holder = null;
		Log.i("new","view");
		v = mInflater.inflate(mElementLayoutId,null, false);
		int index = cursor.getColumnIndex(Columns.ID);
		int documentId = cursor.getInt(index);
		holder = new DocumentViewHolder(v);
		holder.documentId = documentId;
		holder.gridElement.setChecked(mSelectedDocuments.contains(documentId));
		holder.gridElement.setOnCheckedChangeListener(this);
		//holder.gridElement.setImage(null);
		v.setTag(holder);
		FastBitmapDrawable start = Util.sDefaultDocumentThumbnail;
		Bitmap startBitmap = null;
		if(start!=null){
			startBitmap = start.getBitmap();
		}
		final CrossFadeDrawable transition = new CrossFadeDrawable(startBitmap, null);
		transition.setCallback(v);
		transition.setCrossFadeEnabled(true);
		holder.transition = transition;
		return v;
	}
	
	public void setSelectedDocumentIds(List<Integer> selection) {
		mSelectedDocuments.addAll(selection);
		if (mCheckedChangeListener!=null){
			mCheckedChangeListener.onCheckedChanged(mSelectedDocuments);
		}
	}

	public Set<Integer> getSelectedDocumentIds() {
		return mSelectedDocuments;
	}

	@Override
	public void onCheckedChanged(View documentView, boolean isChecked) {
		DocumentViewHolder holder = (DocumentViewHolder) documentView.getTag();
		if (isChecked) {
			mSelectedDocuments.add(holder.documentId);
		} else {
			mSelectedDocuments.remove(holder.documentId);
		}
		mCheckedChangeListener.onCheckedChanged(mSelectedDocuments);
	}
}
