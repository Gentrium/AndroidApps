package com.example.maks.filesstatisticapp;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


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

//    public static void setContext(Context context) {
//        ExternalStorageFragment.context = context;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_external_storage,container,false);

        filesListView = (ListView) v.findViewById(R.id.biggestExternalFilesList);
        extensionsListView = (ListView) v.findViewById(R.id.mostFrequentExtensionsExternalList);
        averageFileSize = (TextView) v.findViewById(R.id.averageExternalFileSize);

        extensionsAdapter = new ArrayAdapter<>(v.getContext(),
                R.layout.list_item,
                extensionsList);
        fileListAdapter = new ArrayAdapter<>(v.getContext(),
                R.layout.list_item,
                fileList);

        filesListView.setAdapter(fileListAdapter);
        extensionsListView.setAdapter(extensionsAdapter);

        return v;
    }

    protected static void fillData(ArrayList<String> fileList,
                                   ArrayList<String> extensionsList,
                                   long averageFileSizeText){
        ExternalStorageFragment.fileList = fileList;
        ExternalStorageFragment.extensionsList = extensionsList;

        fileListAdapter.notifyDataSetChanged();
        extensionsAdapter.notifyDataSetChanged();
        String s = Long.toString(averageFileSizeText);

        averageFileSize.setText(s);
    }

}
