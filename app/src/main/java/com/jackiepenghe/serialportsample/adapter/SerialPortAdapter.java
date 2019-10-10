package com.jackiepenghe.serialportsample.adapter;

import com.sscl.baselibrary.adapter.BasePurposeAdapter;
import com.sscl.baselibrary.utils.ViewHolder;

import java.util.ArrayList;

/**
 * 选择设备串口的Spinner的适配器
 *
 * @author pengh
 */
public class SerialPortAdapter extends BasePurposeAdapter<String> {

    /**
     * 构造器
     *
     * @param dataList     适配器数据源
     */
    public SerialPortAdapter(ArrayList<String> dataList) {
        super(dataList, android.R.layout.simple_list_item_1);
    }

    @Override
    protected void convert(ViewHolder viewHolder, int position, String item) {
        viewHolder.setText(android.R.id.text1, item);
    }
}
