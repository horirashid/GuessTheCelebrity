/********************************************
 *  Author: Hori Jawad Rashid               *
 *  Date created: March 30, 2022            *
 *  Master student at Hiroshima University  *
 ********************************************/

package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;
    ImageView imageView;


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("https://kids.nationalgeographic.com/animals/").get();
            Log.i("Contenets of URL", result);

            String[] splitResult = result.split("sounds-list w-dyn-items w-row");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebURLs.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebNames.add(m.group(1));
            }

            Random rand = new Random();
            chosenCeleb = rand.nextInt(celebURLs.size());

            ImageDownloader imageTask = new ImageDownloader();

            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}