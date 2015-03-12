package com.example.maks.filesstatisticapp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataProcessing extends Service {

    ExecutorService es;
    File root;
    int count = 0;
    long totalFilesSize = 0;

    public void onCreate() {
        super.onCreate();
        es = Executors.newFixedThreadPool(2);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        PendingIntent pi = intent.getParcelableExtra(MainScreen.PENDING_INTENT_PARAMS);
        root = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());

        int testingTarget = intent.getIntExtra(MainScreen.TEST_TARGET, 1);
        switch (testingTarget){
            case MainScreen.EXTERNAL_TESTING:
                root = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath());
                break;
            case MainScreen.INTERNAL_TESTING:
                root = new File(Environment.getRootDirectory().getAbsolutePath());
                break;
            case MainScreen.INTERNAL_AND_EXTERNAL_TESTING:
                break;
        }


        MyRun mr = new MyRun(startId, pi);
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

        private ArrayList<File> fileList = new ArrayList<>();
        private TreeMap<String, Integer> extensions = new TreeMap<String,Integer>();
        private long averageFileSize = 0;
        int startId;
        PendingIntent pi;

        public MyRun( int startId, PendingIntent pi) {
            this.startId = startId;
            this.pi = pi;
        }

        public void run() {

            try {
                pi.send(MainScreen.STATUS_START);
                scanFiles(root);
                // сообщаем об окончании задачи
                Intent intent = new Intent().putExtra(MainScreen.PENDING_INTENT_PARAMS,true);
                pi.send(DataProcessing.this, MainScreen.STATUS_FINISH, intent);

            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            stop();
        }

        void stop() {
        }

//        protected void sendFileList() throws PendingIntent.CanceledException {
//
//            Intent intent = new Intent()
//            try {
//                pi.send(DataProcessing.this, MainScreen.FILE_LIST_RESULT, intent);
//            } catch (PendingIntent.CanceledException e) {
//                e.printStackTrace();
//            }
//        }

        protected void sendExtensionsAndSize(){
            TreeMap temp = new TreeMap();
            temp =(TreeMap)SortingInformation.sortByValues(extensions);
            ArrayList<String> extensionsArray = new ArrayList(temp.entrySet());
            ArrayList<String> tempFileList = null;
            for (File f : fileList) {
                tempFileList.add((f.getName() + ' ' + f.length() / 1024).toString());
            }
            Intent intent = new Intent()
                    .putExtra(MainScreen.AVERAGE_FILE_SIZE, averageFileSize)
                    .putStringArrayListExtra(MainScreen.EXTENSIONS_LIST, extensionsArray)
                    .putStringArrayListExtra(MainScreen.FILE_LIST, tempFileList);
            try {
                pi.send(DataProcessing.this,MainScreen.EXTENSIONS_AND_AVERAGE_SIZE, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }


        public File[] scanFiles(File dir) {

            File listFile[] = dir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {
                    if (listFile[i].isDirectory()) {
                        scanFiles(listFile[i]);
                    } else {
                        int extensionPoint = listFile[i].getName().lastIndexOf('.');
                        String extension = listFile[i].getName().substring(extensionPoint);
                        if (extensions.containsKey(extension)) {
                            extensions.put(extension, extensions.get(extension) + 1);
                        } else {
                            extensions.put(extension, 1);
                        }
                        totalFilesSize += listFile[i].length();
                        ++count;

                        averageFileSize = (totalFilesSize / count);
                        if(fileList.size() < 9){
                            fileList.add(listFile[i]);
                            File[] fileListArr = new File[fileList.size()];
                            fileListArr = fileList.toArray(fileListArr);
                            Arrays.sort(fileListArr ,new Comparator<File>() {
                                @Override
                                public int compare(File first, File second) {
                                    int result = Long.valueOf(first.length())
                                            .compareTo(Long.valueOf(second.length()));
                                    return result;
                                }
                            });
                        }else if (fileList.size() > 9 &&
                                listFile[i].length() > fileList.get(0).length()) {
                            File[] fileListArr = new File[fileList.size()];
                            fileList.add(listFile[i]);
                            fileListArr = fileList.toArray(fileListArr);
                            Arrays.sort(fileListArr ,new Comparator<File>() {
                                @Override
                                public int compare(File first, File second) {
                                    int result = Long.valueOf(first.length())
                                            .compareTo(Long.valueOf(second.length()));
                                    return result;
                                }
                            });
//                            SortingInformation.insertionSort(fileList);
//                                sendFileList();
                            sendExtensionsAndSize();

                        }
                    }
                }
            }
            return null;
        }
    }
}
