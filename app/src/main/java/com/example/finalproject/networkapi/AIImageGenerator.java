package com.example.finalproject.networkapi;


import static com.example.finalproject.ImportantUtilities.showErrorMessage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIImageGenerator {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    public String makeRequestAndGetImageLink(String description, String token){

        OkHttpClient client = new OkHttpClient();
long time = SystemClock.currentThreadTimeMillis();

        String data = "{\"version\": \"5c347a4bfa1d4523a58ae614c2194e15f2ae682b57e3797a5bb468920aa70ebf\", \"input\": {\"prompts\": \""+description+"\"}}";
        RequestBody body =  RequestBody.create(data, JSON);

        Request request = new Request.Builder()
                .url("https://api.replicate.com/v1/predictions")
                .addHeader("Authorization", "Token " + token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        String link = "";
        Gson gson = new Gson();
        String resultJson = "";

        try {
            Response response = client.newCall(request).execute();
            resultJson = response.body().string();
            System.out.println(resultJson);

            FirstResponseObject deserializedResponse = gson.fromJson(resultJson, FirstResponseObject.class);
            link = deserializedResponse.getLink();
            System.out.println(link);

        } catch (Exception e) {
            return "An error occured. Exception: \n"+ e.getClass().getSimpleName() +";\n\nThe AI server response: \n\n" + resultJson;
        }

        String outputStr = "";

        int counter = 0;
        while (outputStr.length()<1){
            Request request2 = new Request.Builder()
                    .url(link)
                    .get()
                    .addHeader("Authorization", "Token " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try {
                Response response = client.newCall(request2).execute();
                resultJson = response.body().string();
                try {
                    FirstResponseObject deserializedResponse = gson.fromJson(resultJson, FirstResponseObject.class);
                    System.out.println(resultJson);
                    ArrayList<String> output = deserializedResponse.getOutput();
                    System.out.println(output);
                    if (deserializedResponse.getStatus().equals("succeeded")) {
                        outputStr = output.get(output.size()-1);
                    }
                }
                catch (Exception e){
                    return "An error occured. Exception: \r\n"+ e +"\nThe AI server response: \r\n" + resultJson;
                }
            } catch (IOException e) {
                SystemClock.sleep(4000);
            }

            SystemClock.sleep(4000);

            counter ++;
            System.out.println(counter);
        }

        System.out.println(outputStr + "  EHEHEHHEEH WE GOT A PIC!");
        System.out.println(SystemClock.currentThreadTimeMillis() - time);
        return outputStr;
    }

    public Bitmap getGeneratedImage(String description, String token) throws Exception {

        String path = makeRequestAndGetImageLink(description, token);
        //String path = "https://replicate.com/api/models/pixray/text2image/files/dbbf9a7b-a2bf-4a77-bc02-295c1ec73f82/tempfile.png";
        //String path = "https://nywolf.org/wp-content/uploads/2011/05/IMG_2026editcomp.jpg.webp";
        if (path.contains("error")){
            throw new Exception(path);
        }

        Bitmap bmp=null;

        try{
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bmp = BitmapFactory.decodeStream(input);
            connection.disconnect();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return bmp;
    }
}
