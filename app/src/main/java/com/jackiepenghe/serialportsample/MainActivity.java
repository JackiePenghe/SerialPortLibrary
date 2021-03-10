package com.jackiepenghe.serialportsample;

import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jackiepenghe.serialportlibrary.OnSerialPortDataChangedListener;
import com.jackiepenghe.serialportlibrary.SerialPortManager;
import com.jackiepenghe.serialportsample.adapter.ReceivedDataRecyclerViewAdapter;
import com.jackiepenghe.serialportsample.adapter.SendDataRecyclerViewAdapter;
import com.jackiepenghe.serialportsample.adapter.SerialPortAdapter;
import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.textwatcher.HexTextAutoAddEmptyCharInputWatcher;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.DefaultItemDecoration;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.baselibrary.widget.ReSpinner;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author pengh
 */
public class MainActivity extends BaseAppCompatActivity {

    private Button singleUseBtn;

    private Button multiUseBtn;

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
