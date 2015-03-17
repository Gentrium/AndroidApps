package com.example.maks.filesstatisticapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;



public class InternalStorageFragment extends Fragment {

    private static TextView averageFileSize;
    private static ArrayList<String> fileList = new ArrayList<>();
    private static ArrayList<String> extensionsList = new ArrayList<>();
    static ArrayAdapter<String> extensionsAdapter;
    static ArrayAdapter<String> fileListAdapter;
    ListView filesListView;
    ListView extensionsListView;


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
        View v = inflater.inflate(R.layout.fragment,null);
        TextView title = (TextView) v.findViewById(R.id.fragmentTitle);
        title.setText(R.string.internal_title);

        filesListView = (ListView) v.findViewById(R.id.biggestFilesList);
        extensionsListView = (ListView) v.findViewById(R.id.mostFrequentExtensionsList);
        averageFileSize = (TextView) v.findViewById(R.id.averageFileSize);

        extensionsAdapter = new ArrayAdapter<String>(v.getContext(),
                R.layout.list_item,
                R.id.textView,
                extensionsList);
        fileListAdapter = new ArrayAdapter<>(v.getContext(),
                R.layout.list_item,
                R.id.textView,
                fileList);

        filesListView.setAdapter(fileListAdapter);
        extensionsListView.setAdapter(extensionsAdapter);
        return v;
    }
    public static void fillData(ArrayList<String> fileList,
                         ArrayList<String> extensionsList,
                         long averageFileSizeText){
        InternalStorageFragment.fileList.clear();
        InternalStorageFragment.extensionsList.clear();
        InternalStorageFragment.fileList.addAll(fileList);
        if(extensionsList.size() < 5){
            InternalStorageFragment.extensionsList.addAll(extensionsList);
        }else{
            InternalStorageFragment.extensionsList.addAll(extensionsList.subList(0,5));
        }

        fileListAdapter.notifyDataSetChanged();
        extensionsAdapter.notifyDataSetChanged();
        String s = Long.toString(averageFileSizeText / 1024) + "Kb";
        averageFileSize.setText(s);


    }

}
