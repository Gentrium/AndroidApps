package com.example.maks.filesstatisticapp;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TabHost;

public class MainScreen extends ActionBarActivity implements View.OnClickListener {
    public static final String LOG_TAG = "MainScreen";
    //ui
    private static Button btnStart;
    private RadioGroup radioGroup;
    private BroadcastReceiver br;
    StorageFragment externalFragment = new StorageFragment();
    StorageFragment internalFragment = new StorageFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // initUO();
        initializeUI();

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                Log.d(LOG_TAG, intent.getExtras().toString());
//                Log.d(LOG_TAG, intent.getStringArrayListExtra(Constants.EXTENSIONS_NAMES).toString());
                int target = intent.getIntExtra(Constants.TEST_TARGET,0);
                switch (target){
                    case Constants.EXTERNAL_TESTING:
                        externalFragment.fillData(intent);
                        break;
                    case Constants.INTERNAL_TESTING:
                        internalFragment.fillData(intent);
                        break;
                    case Constants.TESTING_FINISH:
                        btnStart.setEnabled(true);
                        break;
                }
            }
        };
    }

    public void onClick(View v){
        Intent intent = null;

        switch (v.getId()){
            case R.id.btnStart:
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.externalRB:
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(Constants.TEST_TARGET, Constants.EXTERNAL_TESTING);
                        break;
                    case R.id.internalRB:
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(Constants.TEST_TARGET, Constants.INTERNAL_TESTING);
                        break;
                    case R.id.external_internalRB:
                        intent = new Intent(this, DataProcessing.class)
                            .putExtra(Constants.TEST_TARGET, Constants.INTERNAL_TESTING);
                        startService(intent);
                        intent = new Intent(this, DataProcessing.class)
                                .putExtra(Constants.TEST_TARGET, Constants.EXTERNAL_TESTING);
                        break;
                }
                startService(intent);

                btnStart.setEnabled(false);
                break;
            case R.id.btnStop:
                stopService(new Intent(this, DataProcessing.class));
                btnStart.setEnabled(true);
                break;
        }
    }

    private void initializeUI(){
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupStorages);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        Button btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.internalFrag, this.internalFragment)
                .add(R.id.externalFrag, this.externalFragment)
                .commit();

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("external");
        tabSpec.setIndicator("External Testing");
        tabSpec.setContent(R.id.externalFrag);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("internal");
        tabSpec.setIndicator("Internal testing");
        tabSpec.setContent(R.id.internalFrag);
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        registerReceiver(br, intFilter);
    }

    @Override
    protected void onPause(){
      super.onPause();
      unregisterReceiver(br);
    }
}

