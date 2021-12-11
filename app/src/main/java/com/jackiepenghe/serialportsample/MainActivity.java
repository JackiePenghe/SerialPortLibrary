package com.jackiepenghe.serialportsample;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.jackiepenghe.serialportlibrary.MultipleSerialPortManager;
import com.jackiepenghe.serialportlibrary.SerialPortManager;
import com.sscl.baselibrary.activity.BaseAppCompatActivity;

/**
 * @author pengh
 */
public class MainActivity extends BaseAppCompatActivity {

    private Button singleUseBtn;

    private Button multiUseBtn;

    private Button exitBtn;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.single:
                    jumpToSingleUseSampleActivity();
                    break;
                case R.id.multi:
                    jumpToMultiUseSampleActivity();
                    break;
                case R.id.exit:
                    onBackPressed();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void titleBackClicked() {

    }

    @Override
    protected void doBeforeSetLayout() {

    }

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void doBeforeInitOthers() {
        hideTitleBar();
    }

    @Override
    protected void initViews() {
        singleUseBtn = findViewById(R.id.single);
        multiUseBtn = findViewById(R.id.multi);
        exitBtn = findViewById(R.id.exit);
    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void initOtherData() {

    }

    @Override
    protected void initEvents() {
        singleUseBtn.setOnClickListener(onClickListener);
        multiUseBtn.setOnClickListener(onClickListener);
        exitBtn.setOnClickListener(onClickListener);
    }

    @Override
    protected void doAfterAll() {
    }

    @Override
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    @Override
    protected boolean optionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SerialPortManager.closeSerialPort();
        MultipleSerialPortManager.closeAll();
    }


    private void jumpToSingleUseSampleActivity() {
        Intent intent = new Intent(this, SerialPortSampleActivity.class);
        startActivity(intent);
    }

    private void jumpToMultiUseSampleActivity() {
        Intent intent = new Intent(this, MultiSerialPortSampleActivity.class);
        startActivity(intent);
    }

}
