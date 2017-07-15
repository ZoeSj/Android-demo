package com.mikyou.maptest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.mybaidumap.R;
import com.mikyou.beans.MarkInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener,
		OnMapClickListener, OnMarkerClickListener {
	private MapView mapView = null;
	private BaiduMap myBaiduMap = null;
	// 修改默认View相关
	private View defaultBaiduMapScaleButton, defaultBaiduMapLogo,
			defaultBaiduMapScaleUnit;
	// 基本地图类型,实时交通，及覆盖物相关
	private ImageView mapRoad;
	private ImageView mapType;
	private String[] types = { "普通地图", "卫星地图", "热力地图(已关闭)" };
	private float current;// 放大或缩小的比例系数
	private ImageView expandMap;// 放大地图控件
	private ImageView narrowMap;// 缩小地图
	private ImageView addMarks;// 添加覆盖物控件
	private BitmapDescriptor myMarks;
	private List<MarkInfo> markInfoList;
	private LinearLayout markLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		initView();
		initMapView();
		changeDefaultBaiduMapView();
	}

	private void initView() {
		mapView = (MapView) findViewById(R.id.map_view_test);

	}

	/**
	 * @author zoe 初始化地图的View
	 * */
	private void initMapView() {
		registerAllIds();
		registerAllEvents();
	}

	private void registerAllIds() {
		mapRoad = (ImageView) findViewById(R.id.road_condition);
		mapType = (ImageView) findViewById(R.id.map_type);
		expandMap = (ImageView) findViewById(R.id.add_scale);
		narrowMap = (ImageView) findViewById(R.id.low_scale);
		addMarks = (ImageView) findViewById(R.id.map_marker);
		markLayout = (LinearLayout) findViewById(R.id.mark_layout);
	}

	private void registerAllEvents() {
		mapRoad.setOnClickListener(this);
		mapType.setOnClickListener(this);
		expandMap.setOnClickListener(this);
		narrowMap.setOnClickListener(this);
		addMarks.setOnClickListener(this);
	}

	/**
	 * @author zoe 除去百度地图上的默认控件
	 * */
	private void changeDefaultBaiduMapView() {
		changeInitialzeScaleView();// 改变默认百度地图初始加载的地图比例
		// 设置隐藏缩放和扩大的百度地图的默认的比例按钮
		for (int i = 0; i < mapView.getChildCount(); i++) {// 遍历百度地图中的所有子View,找到这个扩大和缩放的按钮控件View，然后设置隐藏View即可
			View child = mapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				defaultBaiduMapScaleButton = child;// 该defaultBaiduMapScaleButton子View是指百度地图默认产生的放大和缩小的按钮，得到这个View
				break;
			}
		}
		defaultBaiduMapScaleButton.setVisibility(View.GONE);// 然后将该View的Visiblity设为不存在和不可见，即隐藏
		defaultBaiduMapLogo = mapView.getChildAt(1);// 该View是指百度地图中默认的百度地图的Logo,得到这个View
		defaultBaiduMapLogo.setPadding(300, -10, 100, 100);// 设置该默认Logo
															// View的位置，因为这个该View的位置会影响下面的刻度尺单位View显示的位置
		mapView.removeViewAt(1);// 最后移除默认百度地图的logo View
		defaultBaiduMapScaleUnit = mapView.getChildAt(2);// 得到百度地图的默认单位刻度的View
		defaultBaiduMapScaleUnit.setPadding(100, 0, 115, 200);// 最后设置调整百度地图的默认单位刻度View的位置
	}

	/**
	 * @author zoe 改变默认初始化的地图的比例
	 * */
	private void changeInitialzeScaleView() {
		myBaiduMap = mapView.getMap();// 改变百度地图的放大比例,让首次加载地图就开始扩大到500米的距离,获得百度地图对象
		MapStatusUpdate factory = MapStatusUpdateFactory.zoomTo(15.0f);
		myBaiduMap.animateMapStatus(factory);
	}

	/**
	 * @author zoe 管理地图的生命周期
	 * */
	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}

	// 点击事件相关
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.road_condition:// 是否打开实时交通
			switchRoadCondition();
			break;
		case R.id.map_type:// 选择地图的类型
			selectMapType();
			break;
		case R.id.add_scale:// 放大地图比例
			expandMapScale();
			break;
		case R.id.low_scale:// 缩小地图比例
			narrowMapScale();
			break;
		case R.id.map_marker:
			addMapMarks();
			break;
		default:
			break;
		}
	}

	/**
	 * @author zoe 是否打开实时交通
	 * */
	private void switchRoadCondition() {
		if (myBaiduMap.isTrafficEnabled()) {// 如果是开着的状态，当点击后，就会出关闭状态
			myBaiduMap.setTrafficEnabled(false);
			mapRoad.setImageResource(R.drawable.main_icon_roadcondition_off);
		} else {// 如果是的关闭的状态，当点击后，就会处于开启的状态
			myBaiduMap.setTrafficEnabled(true);
			mapRoad.setImageResource(R.drawable.main_icon_roadcondition_on);
		}
	}

	/**
	 * @author zoe 选择地图的类型
	 * */
	private void selectMapType() {
		/**
		 * 通过弹出一个选择的对话框来供用户选择不同类型的地图。
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(R.drawable.icon).setTitle("请选择地图的类型")
				.setItems(types, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String select = types[which];
						if (select.equals("普通地图")) {
							myBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
						} else if (select.equals("卫星地图")) {
							myBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
						} else if (select.equals("热力地图(已关闭)")
								|| select.equals("热力地图(已打开)")) {
							if (myBaiduMap.isBaiduHeatMapEnabled()) {
								myBaiduMap.setBaiduHeatMapEnabled(false);

								Toast.makeText(MainActivity.this, "热力地图已关闭", Toast.LENGTH_SHORT)
										.show();
								types[which] = "热力地图(已关闭)";
							} else {
								myBaiduMap.setBaiduHeatMapEnabled(true);
								Toast.makeText(MainActivity.this, "热力地图已打开", Toast.LENGTH_SHORT)
										.show();
								types[which] = "热力地图(已打开)";
							}
						}
					}
				}).show();
	}

	/**
	 * 给控制地图比例放大缩小控件加上，放大和缩小的功能。具体实现是这样的，设置一个全局的变量来表示实时的比例，然后通过点击放大和缩小的控件，
	 * 来控制这个变量增加和减少，并实时将这个变量设置给创建地图状态对象的参数，从而实现实时控制放大和缩小地图。
	 */
	/**
	 * @author zoe 放大地图的比例
	 * */
	private void narrowMapScale() {
		current -= 0.5f;
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f + current);
		myBaiduMap.animateMapStatus(msu);
	}

	/**
	 * @author zoe 缩小地图的比例
	 * */
	private void expandMapScale() {
		current += 0.5f;
		MapStatusUpdate msu2 = MapStatusUpdateFactory.zoomTo(15.0f + current);
		myBaiduMap.animateMapStatus(msu2);
	}

	/**
	 * 首先得创建一个bean来保存每一个覆盖物的详细信息。包括该覆盖物的经纬度，图片路径，覆盖物的名称等；
	 * 实际这也就是相当于ListVIew中的每个Item的信息
	 * ，而显示覆盖物信息只是分散的Item而已，每个覆盖物详细信息项都一样。所以写一个Bean可以更好地管理数据
	 * 。我们有很多个覆盖物，那么可以创建一个bean类型的List集合存放着不同覆盖物对象
	 * ，不同覆盖物处于地图中不同的位置，可以根据不同Bean中经纬度值构成的经纬度对象不同
	 * ，只要拿到了经纬度对象，那么就可以在地图上唯一确定这个点，并且在这个点会显示一个自定义的图标。
	 */
	/**
	 * @author zoe 初始化覆盖物信息数据
	 * */
	private void initMarksData() {
		markInfoList = new ArrayList<MarkInfo>();
		markInfoList.add(new MarkInfo(32.079254, 118.787623, R.drawable.pic1,
				"英伦贵族小旅馆", "距离209米", 1888));
		markInfoList.add(new MarkInfo(32.064355, 118.787624, R.drawable.pic2,
				"沙井国际高级会所", "距离459米", 388));
		markInfoList.add(new MarkInfo(28.7487420000, 115.8748860000,
				R.drawable.pic4, "华东交通大学南区", "距离5米", 888));
		markInfoList.add(new MarkInfo(28.7534890000, 115.8767960000,
				R.drawable.pic3, "华东交通大学北区", "距离10米", 188));
		myBaiduMap.setOnMarkerClickListener(this);
		myBaiduMap.setOnMapClickListener(this);
	}

	/**
	 * @author zoe 添加覆盖物
	 * */
	private void addMapMarks() {
		initMarksData();
		myBaiduMap.clear();// 先清除一下图层
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;
		myMarks = BitmapDescriptorFactory.fromResource(R.drawable.mark);// 引入自定义的覆盖物图标，将其转化成一个BitmapDescriptor对象
		// 遍历MarkInfo的List一个MarkInfo就是一个Mark
		for (int i = 0; i < markInfoList.size(); i++) {
			// 经纬度对象
			latLng = new LatLng(markInfoList.get(i).getLatitude(), markInfoList
					.get(i).getLongitude());// 需要创建一个经纬对象，通过该对象就可以定位到处于地图上的某个具体点
			// 图标
			options = new MarkerOptions().position(latLng).icon(myMarks)
					.zIndex(6);
			marker = (Marker) myBaiduMap.addOverlay(options);// 将覆盖物添加到地图上
			Bundle bundle = new Bundle();// 创建一个Bundle对象将每个mark具体信息传过去，当点击该覆盖物图标的时候就会显示该覆盖物的详细信息
			bundle.putSerializable("mark", markInfoList.get(i));
			marker.setExtraInfo(bundle);
		}
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// 通过这个经纬度对象，地图就可以定位到该点
		myBaiduMap.animateMapStatus(msu);
	}

	
	/**
	 * @author zoe 覆盖物的点击事件
	 * */
	@Override
	public boolean onMarkerClick(Marker marker) {
		Bundle bundle = marker.getExtraInfo();
		MarkInfo MyMarker = (MarkInfo) bundle.getSerializable("mark");
		ImageView iv = (ImageView) markLayout.findViewById(R.id.mark_image);
		TextView distanceTv = (TextView) markLayout.findViewById(R.id.distance);
		TextView nameTv = (TextView) markLayout.findViewById(R.id.name);
		TextView zanNumsTv = (TextView) markLayout.findViewById(R.id.zan_nums);
		iv.setImageResource(MyMarker.getImageId());
		distanceTv.setText(MyMarker.getDistance() + "");
		nameTv.setText(MyMarker.getName());
		zanNumsTv.setText(MyMarker.getZanNum() + "");
		// 初始化一个InfoWindow
		initInfoWindow(MyMarker, marker);
		markLayout.setVisibility(View.VISIBLE);
		return true;
	}

	/**
	 * @author zoe 初始化出一个InfoWindow
	 * 
	 * */
	private void initInfoWindow(MarkInfo MyMarker, Marker marker) {
		// TODO Auto-generated method stub
		InfoWindow infoWindow;
		// InfoWindow中显示的View内容样式，显示一个TextView
		TextView infoWindowTv = new TextView(MainActivity.this);
		infoWindowTv.setBackgroundResource(R.drawable.location_tips);
		infoWindowTv.setPadding(30, 20, 30, 50);
		infoWindowTv.setText(MyMarker.getName());
		infoWindowTv.setTextColor(Color.parseColor("#FFFFFF"));

		final LatLng latLng = marker.getPosition();
		Point p = myBaiduMap.getProjection().toScreenLocation(latLng);// 将地图上的经纬度转换成屏幕中实际的点
		p.y -= 47;// 设置屏幕中点的Y轴坐标的偏移量
		LatLng ll = myBaiduMap.getProjection().fromScreenLocation(p);// 把修改后的屏幕的点有转换成地图上的经纬度对象
		/**
		 * @author zoe 实例化一个InfoWindow的对象 public InfoWindow(View view,LatLng
		 *         position, int yOffset)通过传入的 view 构造一个 InfoWindow,
		 *         此时只是利用该view生成一个Bitmap绘制在地图中，监听事件由开发者实现。 参数: view - InfoWindow
		 *         展示的 view position - InfoWindow 显示的地理位置 yOffset - InfoWindow Y
		 *         轴偏移量
		 * */
		infoWindow = new InfoWindow(infoWindowTv, ll, 10);
		myBaiduMap.showInfoWindow(infoWindow);// 显示InfoWindow
	}

	/**
	 * @author zoe 给整个地图添加的点击事件
	 * */
	@Override
	public void onMapClick(LatLng arg0) {// 表示点击地图其他的地方使得覆盖物的详情介绍的布局隐藏，但是点击已显示的覆盖物详情布局上，则不会消失，因为在详情布局上添加了Clickable=true
		// 由于事件的传播机制，因为点击事件首先会在覆盖物布局的父布局(map)中,由于map是可以点击的，map则会把点击事件给消费掉，如果加上Clickable=true表示点击事件由详情布局自己处理，不由map来消费
		markLayout.setVisibility(View.GONE);
		myBaiduMap.hideInfoWindow();// 隐藏InfoWindow
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}
}
