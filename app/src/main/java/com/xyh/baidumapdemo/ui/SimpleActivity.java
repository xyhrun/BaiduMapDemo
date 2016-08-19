package com.xyh.baidumapdemo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.xyh.baidumapdemo.R;
import com.xyh.baidumapdemo.base.MyAppcation;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 向阳湖 on 2016/7/20.
 */
public class SimpleActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SimpleActivity";

    @Bind(R.id.simple_bdMapView_id)
    MapView mMapView;
    @Bind(R.id.btn_simple_normalMap_id)
    Button btn_normalMap;
    @Bind(R.id.btn_simple_satelliteMap_id)
    Button btn_satelliteMap;
    @Bind(R.id.btn_simple_trafficMap_id)
    Button btn_trfficMap;
    @Bind(R.id.btn_simple_heatMap_id)
    Button btn_heatMap;
    @Bind(R.id.btn_simple_control_id)
    ToggleButton togBtn_control;

    private boolean trafficFlag = true;
    private boolean heatMapFlag = true;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);
        mBaiduMap = mMapView.getMap();

        //设置定位点并显示
        setCenterPoint();
        //是否显示兴趣点
        setClick();
//        mBaiduMap.showMapPoi(false);
    }

    private void setCenterPoint() {
        //设置定位点. 参数1为维度, 参数2经度
        LatLng point = new LatLng(33.930723, 103.193204);
        //设置状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(point) //设置点
                .zoom(5)      //设置缩放比例3~20
                .build();
        //显示出来
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //普通地图
            case R.id.btn_simple_normalMap_id:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            //卫星地图
            case R.id.btn_simple_satelliteMap_id:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            //交通地图
            case R.id.btn_simple_trafficMap_id:
                if (trafficFlag) {
                    mBaiduMap.setTrafficEnabled(true);
                    btn_trfficMap.setTextColor(getResources().getColor(R.color.color_green_blue));
                    trafficFlag = false;
                } else {
                    mBaiduMap.setTrafficEnabled(false);
                    btn_trfficMap.setTextColor(getResources().getColor(R.color.color_black));
                    trafficFlag = true;
                }
                break;
            //热力地图
            case R.id.btn_simple_heatMap_id:
                if (heatMapFlag) {
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                    btn_heatMap.setTextColor(getResources().getColor(R.color.color_green_blue));
                    heatMapFlag = false;
                } else {
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                    btn_heatMap.setTextColor(getResources().getColor(R.color.color_black));
                    heatMapFlag = true;
                }
                break;
            default:
                break;
        }
    }

    private void setClick() {
        btn_normalMap.setOnClickListener(this);
        btn_satelliteMap.setOnClickListener(this);
        btn_trfficMap.setOnClickListener(this);
        btn_heatMap.setOnClickListener(this);
        togBtn_control.setOnCheckedChangeListener(this);


        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MyAppcation.MyToast("经度为 " + latLng.longitude + "\n维度为 " + latLng.latitude);
                Log.i(TAG, "onMapClick: " + "经度为 " + latLng.longitude + " 维度为 " + latLng.latitude);
            }

            //点击地图 Poi 点时，该兴趣点的描述信息.兴趣点包含:车站, 学校, 医院等
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                MyAppcation.MyToast("名称 " + mapPoi.getName() + "\n坐标 " + mapPoi.getPosition() + "\nUid " + mapPoi.getUid());
                Log.i(TAG, "onMapPoiClick: " + mapPoi.getName() + mapPoi.getPosition() + mapPoi.getUid());
                return false;
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //如果点击按钮  则全部开启
        if (isChecked) {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            mBaiduMap.setTrafficEnabled(true);
            mBaiduMap.setBaiduHeatMapEnabled(true);
            togBtn_control.setTextColor(getResources().getColor(R.color.color_green_blue));
        } else {
            //否则关闭
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
            mBaiduMap.setTrafficEnabled(false);
            mBaiduMap.setBaiduHeatMapEnabled(false);
            togBtn_control.setTextColor(getResources().getColor(R.color.color_black));
        }
    }
}
