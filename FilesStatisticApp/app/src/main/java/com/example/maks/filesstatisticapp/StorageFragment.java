package com.example.maks.filesstatisticapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StorageFragment extends Fragment {

    private TextView averageFileSize;
    private ArrayList<String> filesNames = new ArrayList<>();
    private ArrayList<String> filesSizes = new ArrayList<>();
    private ArrayList<String> extensionsNames = new ArrayList<>();
    private ArrayList<String> extensionsFrequency = new ArrayList<>();
    private SimpleAdapter extensionsAdapter;
    private SimpleAdapter fileListAdapter;
    private ArrayList<Map<String, Object>> filesDataSet = new ArrayList<>();
    private ArrayList<Map<String, Object>> extensionsDataSet = new ArrayList<>();
    private Map<String, Object> m;
    ListView filesListView;
    ListView extensionsListView;

    public StorageFragment() {
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

        filesListView = (ListView) v.findViewById(R.id.biggestFilesList);
        extensionsListView = (ListView) v.findViewById(R.id.mostFrequentExtensionsList);
        averageFileSize = (TextView) v.findViewById(R.id.averageFileSize);

        String[] filesData = {Constants.FILE_NAMES, Constants.FILE_SIZE};
        String[] extensionsData = {Constants.EXTENSIONS_NAMES, Constants.EXTENSIONS_FREQUENCY};

        int[] to = {R.id.textViewNames, R.id.textViewValues};

        extensionsAdapter = new SimpleAdapter(v.getContext(),
                extensionsDataSet,
                R.layout.list_item,
                extensionsData,
                to);
        fileListAdapter = new SimpleAdapter(v.getContext(),
                filesDataSet,
                R.layout.list_item,
                filesData,
                to);

        filesListView.setAdapter(fileListAdapter);
        filesListView.setClickable(false);
        filesListView.setItemsCanFocus(false);
        extensionsListView.setAdapter(extensionsAdapter);
        extensionsListView.setClickable(false);
        extensionsListView.setItemsCanFocus(false);

        return v;
    }

    public void fillData(Intent intent){

        filesNames.clear();
        filesSizes.clear();
        filesDataSet.clear();
        extensionsFrequency.clear();
        extensionsNames.clear();
        extensionsDataSet.clear();

        filesNames.addAll(intent.getStringArrayListExtra(Constants.FILE_NAMES));
        filesSizes.addAll(intent.getStringArrayListExtra(Constants.FILE_SIZE));
        if(intent.getStringArrayListExtra(Constants.EXTENSIONS_NAMES).size() < 5){
            extensionsNames.addAll(intent.getStringArrayListExtra(Constants.EXTENSIONS_NAMES));
            extensionsFrequency
                    .addAll(intent.getStringArrayListExtra(Constants.EXTENSIONS_FREQUENCY));
        }else{
            extensionsNames
                    .addAll(intent.getStringArrayListExtra(Constants.EXTENSIONS_NAMES).subList(0, 5));
            extensionsFrequency
                    .addAll(intent.getStringArrayListExtra(Constants.EXTENSIONS_FREQUENCY).subList(0, 5));
        }

        for(int i = 0; i < filesNames.size();i++){
            m = new HashMap<>();
            m.put(Constants.FILE_NAMES,filesNames.get(i));
            m.put(Constants.FILE_SIZE,filesSizes.get(i));
            filesDataSet.add(m);
        }
        for(int i = 0; i < extensionsNames.size();i++){
            m = new HashMap<>();
            m.put(Constants.EXTENSIONS_NAMES, extensionsNames.get(i));
            m.put(Constants.EXTENSIONS_FREQUENCY,extensionsFrequency.get(i));
            extensionsDataSet.add(m);
        }
        fileListAdapter.notifyDataSetChanged();
        extensionsAdapter.notifyDataSetChanged();

        String s = intent.getLongExtra(Constants.AVERAGE_FILE_SIZE, 0) / 1024 + "Kb";
        averageFileSize.setText(s);
    }

}
