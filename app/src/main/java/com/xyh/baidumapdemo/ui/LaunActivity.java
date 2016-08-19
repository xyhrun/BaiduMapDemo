package com.xyh.baidumapdemo.ui;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * Created by 向阳湖 on 2016/7/20.
 */
public class LaunActivity extends LauncherActivity {

    private String[] activityNames = {"基础地图", "定位地图", "搜索地图"};
    private Class<?>[] classes = {SimpleActivity.class, LocationActivity.class, SearchSome.class};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, activityNames);
        setListAdapter(mAdapter);
    }

    @Override
    protected Intent intentForPosition(int position) {
        return new Intent(LaunActivity.this, classes[position]);
    }
}
