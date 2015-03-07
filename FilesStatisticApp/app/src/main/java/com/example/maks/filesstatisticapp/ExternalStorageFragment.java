package com.example.maks.filesstatisticapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class ExternalStorageFragment extends Fragment {

    private static LinearLayout listOfFiles;
    private static LinearLayout listOfExtensions;
    private static TextView averageFileSize;
    private ArrayList<String> fileList;
    private ArrayList<String> extensionsList;
    private long averageFileSizeText;
    static Context context;

    public ExternalStorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_external_storage,null);

        listOfFiles = (LinearLayout) v.findViewById(R.id.biggestExternalFilesList);

        listOfExtensions = (LinearLayout) v.findViewById(R.id.mostFrequentExtensionsExternalList);

        averageFileSize = (TextView) v.findViewById(R.id.averageExternalFileSize);


        return v;
    }

    public static void fillData(ArrayList<String> fileList,
                                ArrayList<String> extensionsList,
                                long averageFileSizeText){
        for(String e: extensionsList.subList(0,5)){
            TextView textView = new TextView(context);
            textView.setText(e);
            textView.setPadding(5, 5, 5, 5);
            listOfExtensions.addView(textView);
        }
        for(String e: fileList){
            TextView textView = new TextView(context);
            textView.setText(e);
            textView.setPadding(5, 5, 5, 5);
            listOfFiles.addView(textView);
        }
        averageFileSize.setText((int) averageFileSizeText);
    }

}
