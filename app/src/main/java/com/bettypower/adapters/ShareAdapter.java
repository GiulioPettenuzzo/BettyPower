package com.bettypower.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.renard.ocr.R;

import java.util.List;

/**
 * Created by giuliopettenuzzo on 22/03/18.
 */

public class ShareAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ResolveInfo> mApps;
    private Context context;

    public ShareAdapter(Context context, List<ResolveInfo> mApps) {
        this.inflater = LayoutInflater.from(context);
        this.mApps = mApps;
        this.context = context;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHendler hendler;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dialog_share, null);
            hendler = new ViewHendler();
            hendler.textLable = (TextView) convertView.findViewById(R.id.app_name);
            hendler.iconImage = (ImageView)convertView.findViewById(R.id.app_icon);
            convertView.setTag(hendler);
        } else {
            hendler = (ViewHendler)convertView.getTag();
        }
        ResolveInfo info = this.mApps.get(position);
        hendler.iconImage.setImageDrawable(info.loadIcon(context.getPackageManager()));
        hendler.textLable.setText(info.loadLabel(context.getPackageManager()));

        return convertView;

    }
    class ViewHendler{
        TextView textLable;
        ImageView iconImage;
    }


    public final int getCount() {
        return mApps.size();
    }

    public final Object getItem(int position) {
        return mApps.get(position);
    }

    public final long getItemId(int position) {
        return position;
    }
}
