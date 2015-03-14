package com.example.maks.filesstatisticapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class MainScreen extends ActionBarActivity implements View.OnClickListener {
    public static final String LOG_TAG = "";

    private ArrayList<String> extensionsArray = new ArrayList<>();
    private ArrayList<String> fileList = new ArrayList<>();
    private long averageFileSize;
    private LinearLayout view;
    private Button btnStart;
    private Button btnStop;
    private RadioGroup radioGroup;
    private FragmentTransaction fragmentTransaction;
    private int target;
    private Intent data;
    BroadcastReceiver br;

    public static final String EXTENSIONS_LIST = "extensions";
    public static final String AVERAGE_FILE_SIZE = "average file size";
    public static final String FILE_LIST = "list";
    public static final String TEST_TARGET = "test target";
    public final static String BROADCAST_ACTION = "com.example.maks.filesstatisticapp";

    public static final int EXTERNAL_TESTING = 010;
    public static final int INTERNAL_TESTING = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupStorages);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);

        Fragment externalFrag = getFragmentManager()
                .findFragmentById(R.layout.fragment_external_storage);
        Fragment internalFrag = getFragmentManager()
                .findFragmentById(R.layout.fragment_internal_storage);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, intent.getExtras().toString());
                Log.d(LOG_TAG, intent.getStringArrayListExtra(EXTENSIONS_LIST).toString());

                target = intent.getIntExtra(TEST_TARGET,0);
                data = intent;

                    switch (target) {
                        case EXTERNAL_TESTING:
//                            new FillData().execute();
//                            try {
//                                TimeUnit.SECONDS.sleep(1);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            fileList = data.getStringArrayListExtra(FILE_LIST);
                            extensionsArray = data.getStringArrayListExtra(EXTENSIONS_LIST);
                            averageFileSize = data.getLongExtra(AVERAGE_FILE_SIZE,0);
                            target = data.getIntExtra(TEST_TARGET,0);
                            fragmentTransaction = getFragmentManager().beginTransaction();
                            switch (target){
                                case EXTERNAL_TESTING:
                                    ExternalStorageFragment.fillData(fileList, extensionsArray,averageFileSize);
                                    fragmentTransaction.commit();
                                    break;
                                case INTERNAL_TESTING:
//                InternalStorageFragment.
                                    break;
                            }
                            break;
                        case INTERNAL_TESTING:
                            new FillData().execute();
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                    }
                }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilter = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilter);
    }

    public void onClick(View v){
        Intent intent;

        switch (v.getId()){
            case R.id.btnStart:
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.externalRB:
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(TEST_TARGET, EXTERNAL_TESTING);
                        startService(intent);
                        break;
                    case R.id.internalRB:
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(TEST_TARGET, INTERNAL_TESTING);
                        startService(intent);
                        break;
                    case R.id.external_internalRB:
                        intent = new Intent(this, DataProcessing.class)
                            .putExtra(TEST_TARGET, INTERNAL_TESTING);
                        startService(intent);
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(TEST_TARGET, EXTERNAL_TESTING);
                        startService(intent);
                        break;
                    default:
                }
                btnStart.setEnabled(false);
                break;
            case R.id.btnStop:
                stopService(new Intent(this, DataProcessing.class));
                btnStart.setEnabled(true);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private  void fillData(){


    }

    private class FillData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            fileList = data.getStringArrayListExtra(FILE_LIST);
            extensionsArray = data.getStringArrayListExtra(EXTENSIONS_LIST);
            averageFileSize = data.getLongExtra(AVERAGE_FILE_SIZE,0);
            target = data.getIntExtra(TEST_TARGET,0);
            fragmentTransaction = getFragmentManager().beginTransaction();
            switch (target){
                case EXTERNAL_TESTING:
                    ExternalStorageFragment.fillData(fileList, extensionsArray,averageFileSize);
                    fragmentTransaction.commit();
                    break;
                case INTERNAL_TESTING:
//                InternalStorageFragment.
                    break;
            }
            return null;
        }
    }

  @Override
    protected void onPause(){
      super.onPause();
      // дерегистрируем (выключаем) BroadcastReceiver
      unregisterReceiver(br);
  }
}

