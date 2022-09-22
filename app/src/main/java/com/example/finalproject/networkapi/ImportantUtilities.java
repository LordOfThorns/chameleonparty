package com.example.finalproject.networkapi;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.net.ContentHandler;

public class ImportantUtilities {
    public static void showErrorMessage(Context context, String errorMessage){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(errorMessage);
        alertDialogBuilder.setTitle("Incorrect input =(");
        alertDialogBuilder.setNegativeButton("ok", (dialogInterface, i) -> {
            //nothing special
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
