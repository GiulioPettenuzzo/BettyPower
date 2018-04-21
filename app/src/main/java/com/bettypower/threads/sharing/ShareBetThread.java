package com.bettypower.threads.sharing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.bettypower.adapters.ShareAdapter;
import com.bettypower.entities.Bet;
import com.renard.ocr.R;
import com.renard.ocr.documents.viewing.DocumentContentProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used for create the dialog and allows the user to share the bet
 * Created by giuliopettenuzzo on 22/03/18.
 */

//TODO inserire il codice per condividere bene su facebook
//TODO se l'utente riceve l'immagine e poi scarica l'app, alla prima apertura, l'applicazione dovrebbe motrare la schedina dell'amico

public class ShareBetThread extends AsyncTask<String, Void, String> {

    private static final String APPLICATION_LINK = "https://www.bettypower.it/app_link.php?";
    public static final String SHARE_CODES_FILE_NAMES = "share_code_file_name";


    private File imagePath;
    private Activity activity;
    private Bet bet;
    private Intent sharingIntent;
    private Bitmap bitmap;
    private boolean hasImage = true;
    private Uri uri;

    private String appLink;
    private List<ResolveInfo> activities;

    public ShareBetThread(Activity activity,Bet bet,Uri uri){
        this.activity = activity;
        this.bet = bet;
        this.uri = uri;
        shareSub();
    }


    @Override
    protected String doInBackground(String... urls) {
        Cursor c = activity.getContentResolver().query(uri, new String[]{DocumentContentProvider.Columns.PHOTO_PATH}, null, null, null);
        if (c != null) {
            c.moveToFirst();
            bitmap = BitmapFactory.decodeFile(c.getString(c.getPosition()));
            c.close();
        }
        if(bitmap==null) {
            bitmap = takeScreenshot();
            hasImage = false;
        }
        saveBitmap(bitmap);
        return POST(bet);
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        this.appLink = APPLICATION_LINK + result;
        writeToFile(appLink,activity);
    }

    private  String POST(Bet bet){
        //get random id for server file name
        SecureRandom random = new SecureRandom();
        String result = new BigInteger(200, random).toString(32).toUpperCase();

        Intent services = new Intent(activity, UploadBitmapService.class);
        services.putExtra(UploadBitmapService.WEB_FILE_NAME,result);
        services.putExtra(UploadBitmapService.EXTRA_BET,bet);

        if(hasImage) {
            services.putExtra(UploadBitmapService.BITMAP_URI, uri.toString());
        }
        activity.startService(services);
        return result;
    }

    private Bitmap takeScreenshot() {
        View rootView = activity.findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }


    private void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareSub() {
        sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        activities = orderApplication(activity.getPackageManager().queryIntentActivities (sharingIntent, 0));
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        builder.setTitle(R.string.share_with);
        builder.setAdapter(new ShareAdapter(activity, activities),new ClickListener());
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*
    in oder to put the most famous social media at the top of the list
     */
    private List<ResolveInfo> orderApplication(List<ResolveInfo> originalList){
        List<ResolveInfo> returnList = new LinkedList<>();
        for (ResolveInfo info:originalList
             ) {
            switch (info.activityInfo.packageName) {
                case "com.whatsapp":
                    returnList.add(info);
                    int index = returnList.indexOf(info);
                    returnList.remove(index);
                    returnList.add(0, info);
                    break;
                case "com.instagram.android":
                    returnList.add(info);
                    break;
                case "com.facebook.orca":
                    returnList.add(info);
                    break;
                case "com.facebook.katana":
                    if (info.activityInfo.targetActivity != null && info.activityInfo.targetActivity.equals("com.facebook.composer.shareintent.ImplicitShareIntentHandler")) {
                        returnList.add(info);
                    }
                    break;
                case "org.telegram.messenger":
                    returnList.add(info);
                    break;
                case "com.twitter.android":
                    returnList.add(info);
                    break;
                case "com.snapchat.android":
                    returnList.add(info);
                    break;
            }

        }
        for (ResolveInfo info:originalList
                ) {
            if(!info.activityInfo.packageName.equals("com.whatsapp") && !info.activityInfo.packageName.equals("com.instagram.android") &&
            !info.activityInfo.packageName.equals("com.facebook.katana") && !info.activityInfo.packageName.equals("org.telegram.messenger")&&
            !info.activityInfo.packageName.equals("com.twitter.android") && !info.activityInfo.packageName.equals("com.snapchat.android"))
                returnList.add(info);
        }
        return returnList;
    }

    private class ClickListener implements DialogInterface.OnClickListener{

        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog The dialog that received the click.
         * @param item  The button that was clicked (e.g.
         *               {@link DialogInterface#BUTTON1}) or the position
         */
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if(appLink!=null) {
                ResolveInfo info = activities.get(item);
                Uri uri = Uri.fromFile(imagePath);

                if (info.activityInfo.packageName.equals("com.facebook.katana")) {
                    //TODO condividere con facebook quando avrai l'id pubblico
                    // Facebook was chosen
                   /* ShareDialog shareDialog;
                    FacebookSdk.sdkInitialize(activity);
                    shareDialog = new ShareDialog(activity);
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("title")
                            //.setImageUrl(uri)
                            .setContentDescription(
                                    "Description")
                            .setContentUrl(Uri.parse(appLink)).build();
                    shareDialog.show(linkContent);*/
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "La mia schedina");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,activity.getApplicationContext().getResources().getString(R.string.socal_post)+" "+ appLink);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setPackage(info.activityInfo.packageName);
                    activity.startActivity(sharingIntent);
                } else {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "La mia schedina");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,activity.getApplicationContext().getResources().getString(R.string.socal_post)+" "+ appLink);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setPackage(info.activityInfo.packageName);
                    activity.startActivity(sharingIntent);
                }
            }
        }
    }

    private void writeToFile(String data,Context context) {
        try {
            String text = readFromFile(context);
            text = text + " " + data;
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(SHARE_CODES_FILE_NAMES, Context.MODE_PRIVATE));
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(SHARE_CODES_FILE_NAMES);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }




}
