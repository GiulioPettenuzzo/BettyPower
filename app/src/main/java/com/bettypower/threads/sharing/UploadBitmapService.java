package com.bettypower.threads.sharing;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.bettypower.entities.Bet;
import com.google.gson.Gson;
import com.renard.ocr.documents.viewing.DocumentContentProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UploadBitmapService extends IntentService {

    public static final String WEB_FILE_NAME = "file_name";
    public static final String EXTRA_BET = "bet";
    public static final String BITMAP_URI = "bitmap";

    private static final String URL_STRING = "https://www.bettypower.it/bet_sharing/sharing_uploader.php";
    private static final int CONNECTION_TIMEOUT = 15 * 1000;
    private static final int QUALITY = 100;

    public UploadBitmapService() {
        super("hello");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String fileName = intent.getStringExtra(WEB_FILE_NAME);
            //get bet in json format
            Bet bet = intent.getParcelableExtra(EXTRA_BET);
            Gson gson = new Gson();
            String jsonBet = gson.toJson(bet);

            //get bitmap if present
            String uri = intent.getStringExtra(BITMAP_URI);
            if (uri != null) {
                Cursor c = getContentResolver().query(Uri.parse(uri), new String[]{DocumentContentProvider.Columns.PHOTO_PATH}, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    Bitmap bitmap = BitmapFactory.decodeFile(c.getString(c.getPosition()));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY, stream);
                    byte[] byteArray = stream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    jsonBet = jsonBet + " --> " + encoded;
                }
                if (c != null) {
                    c.close();
                }
            }

            jsonBet = fileName + " " + jsonBet;

            try {
                URL url = new URL(URL_STRING);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.connect();

                OutputStream os = conn.getOutputStream();
                os.write(jsonBet.getBytes("UTF-8"));
                os.close();

                int code = conn.getResponseCode();
                Log.i("response code = ",String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
