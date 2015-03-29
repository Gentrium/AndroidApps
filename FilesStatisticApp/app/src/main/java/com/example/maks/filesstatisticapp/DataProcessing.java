package com.example.maks.filesstatisticapp;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataProcessing extends Service {

    public static final String LOG_TAG = "data";

    ExecutorService es;
    File root;
    static int target;
    int count = 0;
    long totalFilesSize = 0;

    public void onCreate() {
        super.onCreate();
        es = Executors.newFixedThreadPool(2);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        int testingTarget = intent.getIntExtra(Constants.TEST_TARGET, 1);
        switch (testingTarget){
            case Constants.EXTERNAL_TESTING:
                root = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath());
                target = Constants.EXTERNAL_TESTING;
                break;
            case Constants.INTERNAL_TESTING:
                root = new File(Environment.getDataDirectory().getAbsolutePath());
                target = Constants.INTERNAL_TESTING;
                break;
            default:
        }

        es.execute(new MyRun(startId, root, target));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MyRun implements Runnable {

        int startId;
        File root;
        int target;

        public MyRun( int startId, File root, int target) {
            this.startId = startId;
            this.root = root;
            this.target = target;
        }

        public void run() {
            scanFiles(root);
            stop();
        }

        void stop() {
            stopSelf(startId);
            sendBroadcast(new Intent(Constants.BROADCAST_ACTION)
                    .putExtra(Constants.TEST_TARGET, Constants.TESTING_FINISH));
            Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
                    + startId + ") = " + stopSelfResult(startId));
            Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelf(" + startId + ")");
        }

        protected void sendExtensionsAndSize(File[] files, TreeMap<String, Integer> extensions, long averageFileSize){
            SortingInformation.sortByValues(extensions);
            ArrayList<String> extensionsValues = new ArrayList<>();
            ArrayList<String> extensionsNames = new ArrayList<>();

            ArrayList<String> filesNames = new ArrayList<>();
            ArrayList<String> filesSize = new ArrayList<>();

            for(Object o : extensions.values()) extensionsValues.add(o.toString());
            for(Object o : extensions.keySet()) extensionsNames.add(o.toString());

            for (File f : files) {
                filesNames.add(f.getName());
                filesSize.add((f.length() / 1024 + "Kb").toString());
            }
            Intent intent = new Intent(Constants.BROADCAST_ACTION)
                    .putExtra(Constants.AVERAGE_FILE_SIZE, averageFileSize)
                    .putStringArrayListExtra(Constants.EXTENSIONS_NAMES, extensionsNames)
                    .putStringArrayListExtra(Constants.EXTENSIONS_FREQUENCY, extensionsValues)
                    .putStringArrayListExtra(Constants.FILE_NAMES, filesNames)
                    .putStringArrayListExtra(Constants.FILE_SIZE, filesSize)
                    .putExtra(Constants.TEST_TARGET,target);

            sendBroadcast(intent);
        }

        public File[] scanFiles(File dir) {
            File[] files = new File[10];
            TreeMap<String, Integer> extensions = new TreeMap<>();
            long averageFileSize = 0;
            File currentFile[] = dir.listFiles();
            if (currentFile != null && currentFile.length > 0) {
                for (int i = 0; i < currentFile.length; i++) {
                    if (currentFile[i].isDirectory()) {
                        scanFiles(currentFile[i]);
                    } else {
                        //Adding to Extensions map
                        if(currentFile[i].getName().contains(".")) {
                            int extensionPoint = currentFile[i].getName().lastIndexOf('.');
                            String extension  = currentFile[i].getName().substring(extensionPoint);
                            if (extensions.containsKey(extension)) {
                                extensions.put(extension, extensions.get(extension) + 1);
                            } else {
                                extensions.put(extension, 1);
                            }
                        }

                        //Total file size
                        totalFilesSize += currentFile[i].length();
                        ++count;
                        averageFileSize = (totalFilesSize / count);

                        //Sorting file list
                        if(count <= 10) {
                            files[count - 1] = currentFile[i];
                            if(count == 10)
                                SortingInformation.insertionSort(files);
                        }else if (count > 10 &&
                                currentFile[i].length() > files[0].length()) {
                            files[0] = currentFile[i];
                            SortingInformation.insertionSort(files);
                            sendExtensionsAndSize(files, extensions, averageFileSize);
                        }
                    }
                }
            }

            return null;
        }
    }
}
