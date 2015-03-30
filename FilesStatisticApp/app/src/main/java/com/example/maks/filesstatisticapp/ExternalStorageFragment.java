package com.example.maks.filesstatisticapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ExternalStorageFragment extends Fragment {

    private static TextView averageFileSize;
    private static ArrayList<String> fileList = new ArrayList<>();
    private static ArrayList<String> extensionsList = new ArrayList<>();
    static ArrayAdapter<String> extensionsAdapter;
    static ArrayAdapter<String> fileListAdapter;
    ListView filesListView;
    ListView extensionsListView;

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
        View v = inflater.inflate(R.layout.fragment,container,false);
        TextView title = (TextView) v.findViewById(R.id.fragmentTitle);

        filesListView = (ListView) v.findViewById(R.id.biggestFilesList);
        extensionsListView = (ListView) v.findViewById(R.id.mostFrequentExtensionsList);
        averageFileSize = (TextView) v.findViewById(R.id.averageFileSize);

        extensionsAdapter = new ArrayAdapter<String>(v.getContext(),
                R.layout.list_item,
                R.id.textViewNames,
                extensionsList);
        fileListAdapter = new ArrayAdapter<>(v.getContext(),
                R.layout.list_item,
                R.id.textViewNames,
                fileList);

        filesListView.setAdapter(fileListAdapter);
        extensionsListView.setAdapter(extensionsAdapter);

        return v;
    }

    protected static void fillData(List<String> fileList,
                                   List<String> extensionsList,
                                   long averageFileSizeText){
        ExternalStorageFragment.fileList.clear();
        ExternalStorageFragment.extensionsList.clear();
        ExternalStorageFragment.fileList.addAll(fileList);
        if(extensionsList.size() < 5){
            ExternalStorageFragment.extensionsList.addAll(extensionsList);
        }else{
            ExternalStorageFragment.extensionsList.addAll(extensionsList.subList(0,5));
        }

        fileListAdapter.notifyDataSetChanged();
        extensionsAdapter.notifyDataSetChanged();
        String s = Long.toString(averageFileSizeText / 1024) + "Kb";
        averageFileSize.setText(s);
    }

}
