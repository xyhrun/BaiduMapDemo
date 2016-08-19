package com.xyh.baidumapdemo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.xyh.baidumapdemo.R;
import com.xyh.baidumapdemo.base.MyAppcation;
import com.xyh.baidumapdemo.overlayutil.PoiOverlay;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 向阳湖 on 2016/7/21.
 */
public class LocationActivity extends Activity implements View.OnClickListener, OnGetPoiSearchResultListener {
    private static final String TAG = "LocationActivity";
    @Bind(R.id.location_bdMapView_id)
    MapView mMapView;
    @Bind(R.id.location_city_id)
    Button location_city;
    @Bind(R.id.et_input_id)
    EditText et_input;
    @Bind(R.id.btn_searchSome_id)
    Button btn_searchSome;

    //设置搜索
    private PoiSearch mPoiSearh;
    //当前城市
    private String currentCity;
    //搜索关键字
    private String et_key;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    //    定位请求成功后回调接口
    private BDLocationListener myListener = new MyLocationListener();
    private List<PoiDetailResult> detailResultList = new ArrayList<>();
    private LatLng mCurrentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(getApplicationContext());
        mPoiSearh = PoiSearch.newInstance();
        initLocation();
        //设置定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.registerLocationListener(myListener);
        setClick();

        //才开始是false,极短时间后变为true
//        MyAppcation.MyToast(mLocationClient.isStarted() + "");

        //设置定位的点, mode - 定位图层显示方式, 默认为 LocationMode.NORMAL 普通态
//        enableDirection - 是否允许显示方向信息
//        customMarker - 设置用户自定义定位图标，可以为 null
//        罗盘态，显示定位方向圈，保持定位图标在地图中心 COMPASS
//        跟随态，保持定位图标在地图中心 FOLLOWING
//        普通态： 更新定位数据时不对地图做任何操作  NORMAL
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS
                , true, BitmapDescriptorFactory.fromResource(R.mipmap.pos2)));
    }

    private void setClick() {
        location_city.setOnClickListener(this);
        btn_searchSome.setOnClickListener(this);
        mPoiSearh.setOnGetPoiSearchResultListener(this);
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            MyAppcation.MyToast("没有查到结果");
            return;
        }

        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            //清除标记物
            mBaiduMap.clear();
            PoiOverlay myPoiOverlay = new MyPoiOverlay(mBaiduMap);
            //设置poi点击事件
            mBaiduMap.setOnMarkerClickListener(myPoiOverlay);
            //配置地图状态
            myPoiOverlay.setData(poiResult);
            myPoiOverlay.addToMap();
            myPoiOverlay.zoomToSpan();
            List<PoiInfo> poiInfoList = poiResult.getAllPoi();
            for (int i = 0; i < poiInfoList.size(); i++) {
                Log.i(TAG, "onGetPoiResult: poiInfoList.size = " + poiInfoList.size());
                PoiInfo mPoiInfo = poiInfoList.get(i);
                Log.i(TAG, "onGetPoiResult: " + mPoiInfo.name + "地址: " + mPoiInfo.address
                        + "" + mPoiInfo.phoneNum + " 描述:" + mPoiInfo.describeContents());
//                        PoiDetailResult mPoiDetailResult = new PoiDetailResult(SearchResult.ERRORNO.NO_ERROR);
                LatLng desLatlng = mPoiInfo.location;
                Log.i(TAG, "onGetPoiResult: 我的坐标"+mCurrentLatLng+"  ,目标坐标 "+desLatlng);
                double distance = getDistance(mCurrentLatLng.latitude, mCurrentLatLng.longitude, desLatlng.latitude, desLatlng.longitude);
                Log.i(TAG, "onGetPoiResult: 距离 = "+Math.round(distance /10) / 100.0);
                Log.i(TAG, "onGetPoiResult: distance = "+distance);
                mPoiSearh.searchPoiDetail(new PoiDetailSearchOption().poiUid(mPoiInfo.uid));
//                        Log.i(TAG, "----onGetPoiResult: 链接"+mPoiDetailResult.getDetailUrl());
            }
        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        detailResultList.add(poiDetailResult);
        Log.i(TAG, "onGetPoiDetailResult: poiDetailResult = " + poiDetailResult);
        Log.i(TAG, "onGetPoiDetailResult: size = " + detailResultList.size());
        Log.i(TAG, "onGetPoiDetailResult: 名称: " + poiDetailResult.getName() + " 地址: " + poiDetailResult.getAddress()
                + "价格: " + poiDetailResult.getPrice() + " 联系方式: " + poiDetailResult.getTelephone()
                + " 评论数量: " + poiDetailResult.getCommentNum()
                + " 详情链接: " + poiDetailResult.getDetailUrl()
                + " 营业时间: " + poiDetailResult.getShopHours() + " 服务评价 " + poiDetailResult.getServiceRating()
                + " 综合评价: " + poiDetailResult.getOverallRating());
//                MyAppcation.MyLongToast("名称: " + poiDetailResult.getName() + "\n地址: " + poiDetailResult.getAddress()
//                        + "\n联系方式: " + poiDetailResult.getTelephone() + "\n价格: " + poiDetailResult.getPrice());
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
//        设置是否打开gps进行定位
        option.setOpenGps(true);
//        //可选，设置是否需要地址信息，默认不需要.有了它才可以才可以获取到城市和省信息
        option.setIsNeedAddress(true);
//        设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
        option.setScanSpan(2000);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);
//        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
//        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
//        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.setIgnoreKillProcess(false);
//        //可选，默认false，设置是否收集CRASH信息，默认收集
//        option.SetIgnoreCacheException(false);
//        //可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_city_id:
                mLocationClient.start();
                break;
            case R.id.btn_searchSome_id:
                mLocationClient.stop();
                et_key = et_input.getText().toString().trim();
                PoiCitySearchOption mPoiCitySearchOption = new PoiCitySearchOption().city(currentCity)
                        .keyword(et_key)
                        .pageCapacity(9);
                mPoiSearh.searchInCity(mPoiCitySearchOption);
                break;
            default:
                break;
        }
    }


    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //        获取定位类型: 参考 定位结果描述 相关的字段
            Log.i(TAG, "onCreate: locType = " + bdLocation.getLocType()); // 161,网络定位结果，网络定位定位成功
            currentCity = bdLocation.getCity();
            location_city.setText(currentCity);
            Log.i(TAG, "onReceiveLocation: 城市" + bdLocation.getCity());
            Log.i(TAG, "onReceiveLocation: 维度 =" + bdLocation.getLatitude() + ", 经度 = " + bdLocation.getLongitude());
            mCurrentLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()); //维度,经度
            Log.i(TAG, "onReceiveLocation: " + bdLocation.getAddrStr());
            MapStatus mMapStatus = new MapStatus.Builder().target(mCurrentLatLng).zoom(18).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//            设置定位数据, 只有先允许定位图层后设置数据才会生效，参见 setMyLocationEnabled(boolean)
            mBaiduMap.setMyLocationData(new MyLocationData.Builder().latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build());
            mBaiduMap.setMapStatus(mapStatusUpdate);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭定位
        mLocationClient.stop();
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

    class MyPoiOverlay extends PoiOverlay {
        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {
            PoiInfo mPoiInfo = this.getPoiResult().getAllPoi().get(i);
            //只能触发一次即使在for循环也不行
            mPoiSearh.searchPoiDetail(new PoiDetailSearchOption().poiUid(mPoiInfo.uid));
//            mPoiSearh.searchPoiDetail(new PoiDetailSearchOption().poiUid(mPoiInfo.uid));
            return super.onPoiClick(i);
        }
    }

    //获取距离
    public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b){
        double pk = 180 / 3.14169;
        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;
        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        return 6371000 * tt;
    }
}
