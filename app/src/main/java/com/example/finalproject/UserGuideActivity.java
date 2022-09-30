package com.example.finalproject;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserGuideActivity extends AppCompatActivity {

    private Button loadExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);

        loadExample = findViewById(R.id.button12);

        loadExample.setOnClickListener(view -> {
            saveData();
        });

        TextView descriptionTV = findViewById(R.id.textView10);
        String htmlDescription = "<p><br>Welcome to the Magic of Random app! This app is designed to help writers and dreamers of all kinds. It allows you to create descriptions of any object by randomly combining characteristics and values specified by the user. In other words, this app performs automated brainstorming.<br><br>What's so special about it? The Magic of Random allows to set probability weights and other constraints, which makes it possible to create realistic descriptions, and also offers some other additional features.<br><br><b><font color=\"#0000FF\">Manual Generator<br><br></font></b>The Manual Generator allows you to enter the settings manually. An example is shown below.<br><br>What to generate: animal<br><br>Characteristic: type<br>Value 1: cat<br>Value 2: dog<br><br>Characteristic: temper<br>Value 1: kind<br>Value 2: agressive<br>Value 3: friendly<br><br><font color=\"#0000FF\"><b>Excel Input generator<br><br></b></font>The Excel Input Generator allows you to enter the settings using an excel file. You can download the example file at the bottom of the screen. When using this option, please make sure that the file structure is correct: no missing rows or columns are allowed; rows with characteristics must be named Char, rows with values Val. At the moment, the option to configure the taxonomy when inputting with a file is not available in the app.<br><br><b><font color=\"#0000FF\">Taxonomy Settings<br><br></font></b>1) Setting multiple values, from ___ to ___.<br>For example, a cat's temper can be both kind and friendly.<br>2) Setting weights within a single characteristic, i.e. changing the probability of a value falling out. For example, the probability of a cat being striped is twice as high as a red cat. The weight for the «striped» value would be two. By default, the weight is always one.<br>3) Factor setting. <b>This option is not implemented to the date. Please look for it in the upcoming versions. </b><br>«If a cat's colour is gray, its eyes are green with the normal frequency of...»: factor 0.5 (a factor can take values from zero to infinity or be equal to «A», the default is one) means that the probability weight of this value is divided in two with the already calculated grayness of the cat. Factor 0: there are no green eyes possible for gray cats. Factor «A» (absolutely): all cats have green eyes.<br><br>First the given weights are always counted, then the factors are applied in the second step.<br><br><b><font color=\"#0000FF\">AI Image Generator<br><br></font></b>To generate images using artificial intelligence, you can get a token after registering at <a href=«https://replicate.com/»>https://replicate.com/</a>. You will be able to use the neural network for a free trial period and then pay for it in your personal account if you wish.</p><p style=«text-align: center;»><em><font color=\"#0000FF\"><br>Have a great experience with the Magic of Random!</font></em></p>";
        Spanned sp = Html.fromHtml(htmlDescription);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            descriptionTV.setText(Html.fromHtml(htmlDescription, Html.FROM_HTML_MODE_COMPACT));
        } else {
            descriptionTV.setText(Html.fromHtml(htmlDescription));
        }
        descriptionTV.setMovementMethod(new ScrollingMovementMethod());
    }

    //a very bad code (crutch). Unfortunately I didn't find a proper solution to download a file from raw to downloads :(
    private void saveData(){

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        root = new File(root, "exampleexcelinput.xls");

        try {
            FileOutputStream fout = new FileOutputStream(root);
            String uri = "@raw/example_excel_input";
            int fileResource = getResources().getIdentifier(uri, null, getPackageName());
            InputStream inputStream = getResources().openRawResource(fileResource);

            byte buf[]=new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0) {
                fout.write(buf,0,len);
            }
            inputStream.close();

            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            boolean bool = false;
            try {
                bool = root.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (bool){
                saveData();
            }else {
                throw new IllegalStateException("Failed to create image file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}