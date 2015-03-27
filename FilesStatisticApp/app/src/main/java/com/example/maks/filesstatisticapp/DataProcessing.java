package com.example.maks.filesstatisticapp;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataProcessing extends Service {

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

        int testingTarget = intent.getIntExtra(MainScreen.TEST_TARGET, 1);
        switch (testingTarget){
            case MainScreen.EXTERNAL_TESTING:
                root = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath());
                target = MainScreen.EXTERNAL_TESTING;
                break;
            case MainScreen.INTERNAL_TESTING:
                root = new File(Environment.getDataDirectory().getAbsolutePath());
                target = MainScreen.INTERNAL_TESTING;
                break;
            default:
        }


        MyRun mr = new MyRun(startId, root, target);
        es.execute(mr);

        return super.onStartCommand(intent, flags, startId);
    }


    public DataProcessing() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MyRun implements Runnable {
        private File[] temporary = new File[10];
        private TreeMap<String, Integer> extensions = new TreeMap<>();
        private long averageFileSize = 0;
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
            stopSelf();
            if(stopSelfResult(startId)){

            }
            stop();
        }
        void stop() {
        }

        protected void sendExtensionsAndSize(){
            TreeMap temp = new TreeMap();
            temp =(TreeMap)SortingInformation.sortByValues(extensions);
            ArrayList<String> extensionsArray = new ArrayList<>();
            ArrayList<String> tempFileList = new ArrayList<>();
            for(Object o : temp.entrySet()){
                extensionsArray.add(o.toString());
            }
            for (File f : temporary) {
                tempFileList.add((f.getName() + ' ' + f.length() / 1024 + "Kb").toString());
            }
            Intent intent = new Intent(MainScreen.BROADCAST_ACTION)
                    .putExtra(MainScreen.AVERAGE_FILE_SIZE, averageFileSize)
                    .putStringArrayListExtra(MainScreen.EXTENSIONS_LIST, extensionsArray)
                    .putStringArrayListExtra(MainScreen.FILE_LIST, tempFileList)
                    .putExtra(MainScreen.TEST_TARGET,target);

            sendBroadcast(intent);
        }

        public File[] scanFiles(File dir) {
            File currentFile[] = dir.listFiles();
            if (currentFile != null && currentFile.length > 0) {
                for (int i = 0; i < currentFile.length; i++) {
                    if (currentFile[i].isDirectory()) {
                        scanFiles(currentFile[i]);
                    } else {
                        int extensionPoint = currentFile[i].getName().lastIndexOf('.');
                        String extension = null;
                        try{
                            extension = currentFile[i].getName().substring(extensionPoint);
                        }catch (StringIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                        if (extensions.containsKey(extension)) {
                            extensions.put(extension, extensions.get(extension) + 1);
                        } else {
                            extensions.put(extension, 1);
                        }
                        totalFilesSize += currentFile[i].length();
                        ++count;
                        averageFileSize = (totalFilesSize / count);
                        if(count <= 10) {
                            temporary[count - 1] = currentFile[i];
                        }else if (count > 10 &&
                                currentFile[i].length() > temporary[0].length()) {
                            temporary[0] = currentFile[i];
                            SortingInformation.insertionSort(temporary);
                            sendExtensionsAndSize();

                        }if(count == 10){
                            SortingInformation.insertionSort(temporary);
                        }
                    }
                }
            }
            return null;
        }
    }
}
