package com.bettypower.threads;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.bettypower.adapters.ShareAdapter;
import com.bettypower.entities.Bet;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.renard.ocr.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used for create the dialog and allows the user to share the bet
 * Created by giuliopettenuzzo on 22/03/18.
 */

//TODO serve un modo per capire se la schedina è già presente nel database
//TODO inserire il codice per condividere bene su facebook

public class ShareBetThread extends AsyncTask<String, Void, String> {

    private static final String APPLICATION_LINK = "http://www.fishtagram.it/bettypower/app_link.php?";
    private static final int CONNECTION_TIMEOUT = 15 * 1000;


    private File imagePath;
    private Activity activity;
    private Bet bet;
    private Intent sharingIntent;

    private String appLink;
    private List<ResolveInfo> activities;

    public ShareBetThread(Activity activity,Bet bet){
        this.activity = activity;
        this.bet = bet;
        Bitmap bitmap = takeScreenshot();
        saveBitmap(bitmap);
        shareSub();
    }


    @Override
    protected String doInBackground(String... urls) {
        return POST(urls[0],bet);
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        this.appLink = APPLICATION_LINK + result;
    }

    private static String POST(String url_string,Bet bet){
        String result = "";
        try {
            Gson gson = new Gson();
            String postData = gson.toJson(bet);
            Log.i("sent = ",postData);
            URL url = new URL(url_string);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.close();

            int status = conn.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    result =  sb.toString();
                    Log.i("result =",result);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    in roder to put the most famous social media in the top of the list
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
                    ShareDialog shareDialog;
                    FacebookSdk.sdkInitialize(activity);
                    shareDialog = new ShareDialog(activity);
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("title")
                            //.setImageUrl(uri)
                            .setContentDescription(
                                    "Description")
                            .setContentUrl(Uri.parse(appLink)).build();
                    shareDialog.show(linkContent);

                   /* Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                            .putString("og:type", "books.book")
                            .putString("og:title", "A Game of Thrones")
                            .putString("og:description", "In the frozen wastes to the north of Winterfell, sinister and supernatural forces are mustering.")
                            .putString("books:isbn", "0-553-57340-3")
                            .build();

                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();

                    ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                            .setActionType("books.reads")
                            .putObject("book", object)
                            .putPhoto("image", photo)
                            .build();

                    ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                            .setPreviewPropertyName("book")
                            .setAction(action)
                            .build();

                    shareDialog.show(activity, content);*/

                  /*  FacebookSdk.sdkInitialize(activity);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    ShareApi.share(content, null);*/

                    /*
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();*/
                    Log.i("facebook", "facebook merda");


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




}
