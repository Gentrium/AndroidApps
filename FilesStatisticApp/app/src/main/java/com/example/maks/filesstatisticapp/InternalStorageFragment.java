package com.example.maks.filesstatisticapp;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;



public class InternalStorageFragment extends Fragment {

    private LinearLayout listOfFiles;
    private static LinearLayout listOfExtensions;
    private TextView averageFileSize;
    private ArrayList<String> fileList;
    private ArrayList<String> extensionsList;
    private String averageFileSizeText;
    Context context;

    public InternalStorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_internal_storage,null);

        listOfFiles = (LinearLayout) v.findViewById(R.id.biggestInternalFilesList);
        listOfExtensions = (LinearLayout) v.findViewById(R.id.mostFrequentExtensionsInternalList);
        averageFileSize = (TextView) v.findViewById(R.id.averageInternalFileSize);
        return v;
    }
    public void fillData(ArrayList<String> fileList,
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
