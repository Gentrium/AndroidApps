package com.example.maks.filesstatisticapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

    private ArrayList<String> extensionsArray = new ArrayList<String>();
    private ArrayList<String> fileList = new ArrayList<String>();
    private long averageFileSize;
    private LinearLayout view;
    private Button btnStart;
    private Button btnStop;
    private RadioGroup radioGroup;
    private FragmentTransaction fragmentTransaction;
    private byte trigger;
    private Intent data;

    public static final String EXTENSIONS_LIST = "extensions";
    public static final String AVERAGE_FILE_SIZE = "average file size";
    public static final String FILE_LIST = "list";
    public static final String PENDING_INTENT_PARAMS = "pending intent";
    public static final String TEST_TARGET = "test target";

    public static final int FILE_LIST_RESULT = 1;
    public static final int EXTENSIONS_AND_AVERAGE_SIZE = 2;
    public static final int EXTERNAL_TESTING = 010;
    public static final int INTERNAL_TESTING = 101;
    public static final int INTERNAL_AND_EXTERNAL_TESTING = 111;
    public static final int STATUS_START = 5;
    public static final int STATUS_FINISH = 6;


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

    }

    public void onClick(View v){
        PendingIntent pi;
        Intent intent = new Intent(this, DataProcessing.class);

        switch (v.getId()){
            case R.id.btnStart:
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.externalRB:
                        pi = createPendingResult(EXTERNAL_TESTING, intent, 0);
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(PENDING_INTENT_PARAMS, pi)
                                .putExtra(TEST_TARGET, EXTERNAL_TESTING);
                        startService(intent);
                        break;
                    case R.id.internalRB:
                        pi = createPendingResult(INTERNAL_TESTING, intent, 0);
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(PENDING_INTENT_PARAMS, pi)
                                .putExtra(TEST_TARGET, INTERNAL_TESTING);
                        startService(intent);
                        break;
                    case R.id.external_internalRB:
                        pi = createPendingResult(INTERNAL_AND_EXTERNAL_TESTING , intent, 0);
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(PENDING_INTENT_PARAMS, pi)
                                .putExtra(TEST_TARGET, INTERNAL_AND_EXTERNAL_TESTING);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.data = data;
        // Ловим сообщения о старте задач
        if (resultCode == EXTENSIONS_AND_AVERAGE_SIZE) {
            switch (requestCode) {
                case EXTERNAL_TESTING:
                    trigger = 1;
                    new FillData().execute();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case INTERNAL_TESTING:
                    trigger = 2;
                    new FillData().execute();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case INTERNAL_AND_EXTERNAL_TESTING:
                    trigger = 0;
                    new FillData().execute();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        // Ловим сообщения об окончании задач
        if (resultCode == STATUS_FINISH) {
            int result = data.getIntExtra(PENDING_INTENT_PARAMS, 0);
            btnStart.setEnabled(true);
        }
    }

    private  void fillData(){
        fileList = data.getStringArrayListExtra(FILE_LIST);
        extensionsArray = data.getStringArrayListExtra(EXTENSIONS_LIST);
        averageFileSize = data.getLongExtra(AVERAGE_FILE_SIZE,0);
        fragmentTransaction = getFragmentManager().beginTransaction();
        switch (trigger){
            case 1:
                ExternalStorageFragment.fillData(fileList, extensionsArray,averageFileSize);
                fragmentTransaction.commit();
        }

    }

    private class FillData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            fillData();
            return null;
        }
    }
}

