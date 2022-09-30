package com.example.finalproject;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class ImportantUtilities {
    public static void showErrorMessage(Context context, String errorMessage){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(errorMessage);
        alertDialogBuilder.setTitle("A problem occurred =(");
        alertDialogBuilder.setNegativeButton("ok", (dialogInterface, i) -> {
            //nothing special
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
