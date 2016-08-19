package com.xyh.baidumapdemo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.xyh.baidumapdemo.R;
import com.xyh.baidumapdemo.base.MyAppcation;
import com.xyh.baidumapdemo.overlayutil.BusLineOverlay;
import com.xyh.baidumapdemo.overlayutil.OverlayManager;
import com.xyh.baidumapdemo.overlayutil.PoiOverlay;
import com.xyh.baidumapdemo.overlayutil.TransitRouteOverlay;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchSome extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.bdMapView)
    MapView mMapView;
    @Bind(R.id.btn_normalMap_id)
    Button btn_normalMap;
    @Bind(R.id.btn_satelliteMap_id)
    Button btn_satelliteMap;
    @Bind(R.id.btn_trafficMap_id)
    Button btn_trfficMap;
    @Bind(R.id.btn_heatMap_id)
    Button btn_heatMap;
    @Bind(R.id.btn_control_id)
    ToggleButton togBtn_control;
    @Bind(R.id.btn_search_id)
    Button btn_search;
    @Bind(R.id.btn_searchNearBy_id)
    Button btn_searchNearBy;
    @Bind(R.id.btn_searchBound_id)
    Button btn_searchBound;
    @Bind(R.id.btn_searchBus_id)
    Button btn_searchBus;
    @Bind(R.id.sp_cityNames_id)
    Spinner sp_cityNames;
    @Bind(R.id.sp_keyTypes_id)
    Spinner sp_keyTypes;
    @Bind(R.id.btn_pre_id)
    Button btn_pre;
    @Bind(R.id.btn_next_id)
    Button btn_next;

    private EditText dialog_startPosition;
    private EditText dialog_endPosition;

    private static final String TAG = "AddOverLayOptions";
    private BaiduMap mBaiduMap;

    private boolean trafficFlag = true;
    private boolean heatMapFlag = true;

    private InfoWindow mInfoWindow;

    private PoiSearch mPoiSearch;
    private boolean iSearchSuccess;
    //设置选中的城市和服务类型
    private String selectedCity;
    private String selectedKeyType;
    private BusLineSearch mBusLineSearch;
    private BusLineOverlay mBusLineOverlay;
    private TransitRouteOverlay mTransitRouteOverlay;

    private RouteLine route = null;//路线规划中的基类，表示一条路线
    private OverlayManager routeOverlay = null;//管理和显示多个overlay
    private TextView popupText = null; //泡泡view
    //搜索相关 路径规划搜索接口
    RoutePlanSearch mRouteSearch = null;

    //设置公交的uid集合
    private ArrayList<String> uidList = new ArrayList<>();
    private View dialogLayout;
    private AlertDialog mAlertDialog;
    private int nodeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

//        initDialogView();

        selectedCity = getResources().getStringArray(R.array.cityNames)[0];
        selectedKeyType = getResources().getStringArray(R.array.keyTypes)[0];
        mBaiduMap = mMapView.getMap();
        mPoiSearch = PoiSearch.newInstance();
        mBusLineOverlay = new BusLineOverlay(mBaiduMap);
        mBusLineSearch = BusLineSearch.newInstance();
        mRouteSearch = RoutePlanSearch.newInstance();
        //设置点击事件
        setClick();
        //设置定位点并显示
        setCenterPoint();
        //是否显示兴趣点
//        mBaiduMap.showMapPoi(false);
    }

    //初始化弹出框的信息
    private void initDialogView() {

        Log.i(TAG, "initDialogView: dialog_startPosition = " + dialog_startPosition);
    }

    private void searchEvent() {
        sp_cityNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = getResources().getStringArray(R.array.cityNames)[position];
                Log.i(TAG, "onItemSelected: selectedCity =" + selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_keyTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedKeyType = getResources().getStringArray(R.array.keyTypes)[position];
                Log.i(TAG, "onItemSelected: selectedKeyType =" + selectedKeyType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCenterPoint() {
        //设置定位点. 参数1为维度, 参数2经度.
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

    private void setClick() {
        btn_normalMap.setOnClickListener(this);
        btn_satelliteMap.setOnClickListener(this);
        btn_trfficMap.setOnClickListener(this);
        btn_heatMap.setOnClickListener(this);
        togBtn_control.setOnCheckedChangeListener(this);
        btn_search.setOnClickListener(this);
        btn_searchNearBy.setOnClickListener(this);
        btn_searchBound.setOnClickListener(this);
        btn_searchBus.setOnClickListener(this);

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                MyAppcation.MyToast("经度为 " + latLng.longitude + "\n维度为 " + latLng.latitude);
//                Log.i(TAG, "onMapClick: " + "经度为 " + latLng.longitude + " 维度为 " + latLng.latitude);
            }

            //点击地图 Poi 点时，该兴趣点的描述信息.兴趣点包含:车站, 学校, 医院等
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
//                MyAppcation.MyToast("名称 " + mapPoi.getName() + "\n坐标 " + mapPoi.getPosition() + "\nUid " + mapPoi.getUid());
//                Log.i(TAG, "onMapPoiClick: " + mapPoi.getName() + mapPoi.getPosition() + mapPoi.getUid());
                return true;
            }
        });

//        地图 Marker 覆盖物拖拽事件监听接口,必须在地图添加marker才生效
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                MyAppcation.MyToast(marker.getPosition() + marker.getTitle());
//                mBaiduMap.showInfoWindow(mInfoWindow);
//                Log.i(TAG, "onMapPoiClick: " + marker.getPosition() + marker.getTitle());
                return true;
            }
        });

        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            //拖拽过程中的事件处理事件
            @Override
            public void onMarkerDrag(Marker marker) {
            }

            //拖拽过程结束的事件处理事件
            @Override
            public void onMarkerDragEnd(Marker marker) {
            }

            //拖拽过程开始的事件处理事件
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
        });

        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    MyAppcation.MyToast("没有查找到结果");
                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    //清除之前的标记物
                    mBaiduMap.clear();
                    PoiOverlay myPoioverLay = new MyPoiOverLay(mBaiduMap);
                    //设置overlay可以处理标注点击事件
                    mBaiduMap.setOnMarkerClickListener(myPoioverLay);
                    //设置PoiOverlay数据
                    myPoioverLay.setData(poiResult);
                    //添加PoiOverlay到地图中
                    myPoioverLay.addToMap();
                    myPoioverLay.zoomToSpan();

                    //添加公交站的站点标识信息
                    List<PoiInfo> poiInfoList = poiResult.getAllPoi();
                    Log.i(TAG, "----onGetPoiResult: 公交执行??");
                    for (PoiInfo mPoiInfo : poiInfoList) {
                        if (mPoiInfo.type == PoiInfo.POITYPE.BUS_LINE || mPoiInfo.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                            uidList.add(mPoiInfo.uid);
                            Log.i(TAG, "onGetPoiResult: uid = " + mPoiInfo.uid);
                        }
                    }
                    return;
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                MyAppcation.MyLongToast("名称: " + poiDetailResult.getName() + "\n地址: " + poiDetailResult.getAddress()
                        + "\n联系方式: " + poiDetailResult.getTelephone() + "\n价格: " + poiDetailResult.getPrice());
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });

        mBusLineSearch.setOnGetBusLineSearchResultListener(new OnGetBusLineSearchResultListener() {
            @Override
            public void onGetBusLineResult(BusLineResult busLineResult) {
                Log.i(TAG, "---onGetBusLineResult: 公交执行?");
                if (busLineResult == null || busLineResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    MyAppcation.MyToast("没有查找到结果");
                    return;
                }
                if (busLineResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    //清除之前的标记物
                    mBaiduMap.clear();
//                    //设置overlay可以处理标注点击事件
//                    mBaiduMap.setOnMarkerClickListener(myPoioverLay);
                    //设置PoiOverlay数据
                    mBusLineOverlay.setData(busLineResult);
                    //添加PoiOverlay到地图中
                    mBusLineOverlay.addToMap();
                    mBusLineOverlay.zoomToSpan();

                    //获得公交站的所有站点信息
                    List<BusLineResult.BusStation> stationsList = busLineResult.getStations();
                    for (BusLineResult.BusStation mBusStation : stationsList) {
                        Log.i(TAG, mBusStation.getTitle() + "--->");
                    }
                    return;
                }
            }
        });

        mRouteSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //未找到结果
                    return;
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    result.getSuggestAddrInfo();
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    route = result.getRouteLines().get(0);
                    //创建公交路线规划线路覆盖物
                    TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
                    //设置公交路线规划数据
                    //设置 Marker拖拽事件监听者 Marker为标记
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    routeOverlay = overlay;
                    overlay.setData(result.getRouteLines().get(0));
                    //将公交路线规划覆盖物添加到地图中
                    overlay.addToMap();
                    overlay.zoomToSpan();
                    Log.i(TAG, "---onGetTransitRouteResult: " + route);
                }
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
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


    /*
     * 节点浏览
     */

    public void nodeClick(View v) {
        if (route == null) {
            MyAppcation.MyToast("请在搜索后点击");
            return;
        }

        if (route.getAllStep() == null) {
            return;
        }

        if (nodeIndex == -1 && v.getId() == R.id.btn_pre_id) {
            return;
        }

        //设置节点索引
        if (v.getId() == R.id.btn_next_id) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.btn_pre_id) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }

        //获取节点结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        //通过instanceof 哦按段step 是否为TransitRouteLine.TransitStep的一个对象

        if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            Log.i("text", "这是节点坐标" + nodeLocation);
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            Log.i("text", "这是节点信息" + nodeTitle);
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }

        //移动节点至中心

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        //show popup
        popupText = new TextView(SearchSome.this);
        popupText.setBackgroundResource(R.color.color_gray_light);
//        popupText.setBackgroundResource(R.drawable.shape_pop);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);

        //infowindow 在地图上显示一个窗体
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, -50));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //普通地图
            case R.id.btn_normalMap_id:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            //卫星地图
            case R.id.btn_satelliteMap_id:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            //交通地图
            case R.id.btn_trafficMap_id:
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
            case R.id.btn_heatMap_id:
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
            case R.id.btn_search_id:
                //设置搜索
                searchEvent();
                PoiCitySearchOption option = new PoiCitySearchOption().city(selectedCity).keyword(selectedKeyType).pageCapacity(9);
//                Log.i(TAG, "searchEvent: " + option);
                iSearchSuccess = mPoiSearch.searchInCity(option);
                Log.i(TAG, "searchEvent: iSearchSuccess = " + iSearchSuccess);
                break;
            case R.id.btn_searchNearBy_id:
                searchEvent();
//                学校位置
                PoiNearbySearchOption mPoiNearbySearchOption = new PoiNearbySearchOption().location(new LatLng(30.489946, 114.315349))
                        .radius(2000).keyword(selectedKeyType).pageCapacity(9);  //半径单位m
                mPoiSearch.searchNearby(mPoiNearbySearchOption);
                break;
            case R.id.btn_searchBound_id:
                searchEvent();
                LatLng northEast = new LatLng(30.495629, 114.328528);
                LatLng southWest = new LatLng(30.482683, 114.311783);
                LatLngBounds mLatLngBounds = new LatLngBounds.Builder().include(northEast).include(southWest).build();
                PoiBoundSearchOption mPoiBoundSearchOption = new PoiBoundSearchOption().bound(mLatLngBounds)
                        .keyword(selectedKeyType).pageCapacity(9);
                mPoiSearch.searchInBound(mPoiBoundSearchOption);
                break;
            //不执行
            case R.id.btn_searchBus_id:
//                放在这里要不然会报错 The specified child already has a parent. You must call removeView() on the child's parent first.
                dialogLayout = getLayoutInflater().from(this).inflate(R.layout.dialog_layout, null);
                mAlertDialog = new AlertDialog.Builder(SearchSome.this)
                        .setView(dialogLayout).setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog_startPosition = (EditText) dialogLayout.findViewById(R.id.dialog_startPosition_id);
                                dialog_endPosition = (EditText) dialogLayout.findViewById(R.id.dialog_endPosition_id);
                                String startPos = dialog_startPosition.getText().toString().trim();
                                String EndPos = dialog_endPosition.getText().toString().trim();
                                //获取起点,终点
                                Log.i(TAG, "onClick: 起点 = " + startPos);
                                Log.i(TAG, "onClick: 终点 = " + EndPos);
                                searchEvent();
                                //设置起点 终点信息，对于tranist searcch 来说，城市名无意义
                                PlanNode stNode = PlanNode.withCityNameAndPlaceName(selectedCity, startPos);
                                PlanNode enNode = PlanNode.withCityNameAndPlaceName(selectedCity, EndPos);
                                mRouteSearch.transitSearch(new TransitRoutePlanOption().city(selectedCity).from(stNode).to(enNode));
                            }
                        }).setNegativeButton("取消", null).create();
                mAlertDialog.show();
                break;
            default:
                break;
        }
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

    class MyPoiOverLay extends PoiOverlay {
        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public MyPoiOverLay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {
            PoiInfo mPoiIndo = this.getPoiResult().getAllPoi().get(i);
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(mPoiIndo.uid));
            return super.onPoiClick(i);
        }
    }

    class MyTransitRouteOverlay extends TransitRouteOverlay {
        /**
         * 构造函数
         *
         * @param baiduMap 该TransitRouteOverlay引用的 BaiduMap 对象
         */
        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
    }
}
