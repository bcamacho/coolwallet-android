package com.coolbitx.coolwallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.coolbitx.coolwallet.R;

/**
 * Created by ShihYi on 2017/1/23.
 */

public class SettingActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    ListView lvSetting;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        context = this;
        initToolbar();
        lvSetting = (ListView) findViewById(R.id.lv_setting);

    }


    @Override
    public Intent getSupportParentActivityIntent() {
        finish();
        return null;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(getResources().getDrawable(R.mipmap.settings));
        toolbar.setTitle(getString(R.string.str_setting));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.menu_3x);
        ActionBar actionBar = getSupportActionBar();
        // 打開 up button_up
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //註冊監聽
        registerBroadcast(this, cmdManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] item = new String[]{getString(R.string.str_exchange_rate),getString(R.string.str_transaction_fee)};//,"Payment Service Provider"
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, item);
        lvSetting.setAdapter(adapter);
        lvSetting.setOnItemClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unRegisterBroadcast(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, ExchangeRateActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, TransactionFeeActivity.class);
                startActivity(intent);
                break;
//            case 2:
//                intent = new Intent(this, ServiceProviderActivity.class);
//                startActivity(intent);
//                break;

        }

    }
}
