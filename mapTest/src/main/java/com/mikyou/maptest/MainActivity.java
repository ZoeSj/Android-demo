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
	// �޸�Ĭ��View���
	private View defaultBaiduMapScaleButton, defaultBaiduMapLogo,
			defaultBaiduMapScaleUnit;
	// ������ͼ����,ʵʱ��ͨ�������������
	private ImageView mapRoad;
	private ImageView mapType;
	private String[] types = { "��ͨ��ͼ", "���ǵ�ͼ", "������ͼ(�ѹر�)" };
	private float current;// �Ŵ����С�ı���ϵ��
	private ImageView expandMap;// �Ŵ��ͼ�ؼ�
	private ImageView narrowMap;// ��С��ͼ
	private ImageView addMarks;// ��Ӹ�����ؼ�
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
	 * @author zoe ��ʼ����ͼ��View
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
	 * @author zoe ��ȥ�ٶȵ�ͼ�ϵ�Ĭ�Ͽؼ�
	 * */
	private void changeDefaultBaiduMapView() {
		changeInitialzeScaleView();// �ı�Ĭ�ϰٶȵ�ͼ��ʼ���صĵ�ͼ����
		// �����������ź�����İٶȵ�ͼ��Ĭ�ϵı�����ť
		for (int i = 0; i < mapView.getChildCount(); i++) {// �����ٶȵ�ͼ�е�������View,�ҵ������������ŵİ�ť�ؼ�View��Ȼ����������View����
			View child = mapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				defaultBaiduMapScaleButton = child;// ��defaultBaiduMapScaleButton��View��ָ�ٶȵ�ͼĬ�ϲ����ķŴ����С�İ�ť���õ����View
				break;
			}
		}
		defaultBaiduMapScaleButton.setVisibility(View.GONE);// Ȼ�󽫸�View��Visiblity��Ϊ�����ںͲ��ɼ���������
		defaultBaiduMapLogo = mapView.getChildAt(1);// ��View��ָ�ٶȵ�ͼ��Ĭ�ϵİٶȵ�ͼ��Logo,�õ����View
		defaultBaiduMapLogo.setPadding(300, -10, 100, 100);// ���ø�Ĭ��Logo
															// View��λ�ã���Ϊ�����View��λ�û�Ӱ������Ŀ̶ȳߵ�λView��ʾ��λ��
		mapView.removeViewAt(1);// ����Ƴ�Ĭ�ϰٶȵ�ͼ��logo View
		defaultBaiduMapScaleUnit = mapView.getChildAt(2);// �õ��ٶȵ�ͼ��Ĭ�ϵ�λ�̶ȵ�View
		defaultBaiduMapScaleUnit.setPadding(100, 0, 115, 200);// ������õ����ٶȵ�ͼ��Ĭ�ϵ�λ�̶�View��λ��
	}

	/**
	 * @author zoe �ı�Ĭ�ϳ�ʼ���ĵ�ͼ�ı���
	 * */
	private void changeInitialzeScaleView() {
		myBaiduMap = mapView.getMap();// �ı�ٶȵ�ͼ�ķŴ����,���״μ��ص�ͼ�Ϳ�ʼ����500�׵ľ���,��ðٶȵ�ͼ����
		MapStatusUpdate factory = MapStatusUpdateFactory.zoomTo(15.0f);
		myBaiduMap.animateMapStatus(factory);
	}

	/**
	 * @author zoe �����ͼ����������
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

	// ����¼����
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.road_condition:// �Ƿ��ʵʱ��ͨ
			switchRoadCondition();
			break;
		case R.id.map_type:// ѡ���ͼ������
			selectMapType();
			break;
		case R.id.add_scale:// �Ŵ��ͼ����
			expandMapScale();
			break;
		case R.id.low_scale:// ��С��ͼ����
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
	 * @author zoe �Ƿ��ʵʱ��ͨ
	 * */
	private void switchRoadCondition() {
		if (myBaiduMap.isTrafficEnabled()) {// ����ǿ��ŵ�״̬��������󣬾ͻ���ر�״̬
			myBaiduMap.setTrafficEnabled(false);
			mapRoad.setImageResource(R.drawable.main_icon_roadcondition_off);
		} else {// ����ǵĹرյ�״̬��������󣬾ͻᴦ�ڿ�����״̬
			myBaiduMap.setTrafficEnabled(true);
			mapRoad.setImageResource(R.drawable.main_icon_roadcondition_on);
		}
	}

	/**
	 * @author zoe ѡ���ͼ������
	 * */
	private void selectMapType() {
		/**
		 * ͨ������һ��ѡ��ĶԻ��������û�ѡ��ͬ���͵ĵ�ͼ��
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(R.drawable.icon).setTitle("��ѡ���ͼ������")
				.setItems(types, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String select = types[which];
						if (select.equals("��ͨ��ͼ")) {
							myBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
						} else if (select.equals("���ǵ�ͼ")) {
							myBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
						} else if (select.equals("������ͼ(�ѹر�)")
								|| select.equals("������ͼ(�Ѵ�)")) {
							if (myBaiduMap.isBaiduHeatMapEnabled()) {
								myBaiduMap.setBaiduHeatMapEnabled(false);

								Toast.makeText(MainActivity.this, "������ͼ�ѹر�", Toast.LENGTH_SHORT)
										.show();
								types[which] = "������ͼ(�ѹر�)";
							} else {
								myBaiduMap.setBaiduHeatMapEnabled(true);
								Toast.makeText(MainActivity.this, "������ͼ�Ѵ�", Toast.LENGTH_SHORT)
										.show();
								types[which] = "������ͼ(�Ѵ�)";
							}
						}
					}
				}).show();
	}

	/**
	 * �����Ƶ�ͼ�����Ŵ���С�ؼ����ϣ��Ŵ����С�Ĺ��ܡ�����ʵ���������ģ�����һ��ȫ�ֵı�������ʾʵʱ�ı�����Ȼ��ͨ������Ŵ����С�Ŀؼ���
	 * ����������������Ӻͼ��٣���ʵʱ������������ø�������ͼ״̬����Ĳ������Ӷ�ʵ��ʵʱ���ƷŴ����С��ͼ��
	 */
	/**
	 * @author zoe �Ŵ��ͼ�ı���
	 * */
	private void narrowMapScale() {
		current -= 0.5f;
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f + current);
		myBaiduMap.animateMapStatus(msu);
	}

	/**
	 * @author zoe ��С��ͼ�ı���
	 * */
	private void expandMapScale() {
		current += 0.5f;
		MapStatusUpdate msu2 = MapStatusUpdateFactory.zoomTo(15.0f + current);
		myBaiduMap.animateMapStatus(msu2);
	}

	/**
	 * ���ȵô���һ��bean������ÿһ�����������ϸ��Ϣ�������ø�����ľ�γ�ȣ�ͼƬ·��������������Ƶȣ�
	 * ʵ����Ҳ�����൱��ListVIew�е�ÿ��Item����Ϣ
	 * ������ʾ��������Ϣֻ�Ƿ�ɢ��Item���ѣ�ÿ����������ϸ��Ϣ�һ��������дһ��Bean���Ը��õع�������
	 * �������кܶ���������ô���Դ���һ��bean���͵�List���ϴ���Ų�ͬ���������
	 * ����ͬ�����ﴦ�ڵ�ͼ�в�ͬ��λ�ã����Ը��ݲ�ͬBean�о�γ��ֵ���ɵľ�γ�ȶ���ͬ
	 * ��ֻҪ�õ��˾�γ�ȶ�����ô�Ϳ����ڵ�ͼ��Ψһȷ������㣬��������������ʾһ���Զ����ͼ�ꡣ
	 */
	/**
	 * @author zoe ��ʼ����������Ϣ����
	 * */
	private void initMarksData() {
		markInfoList = new ArrayList<MarkInfo>();
		markInfoList.add(new MarkInfo(32.079254, 118.787623, R.drawable.pic1,
				"Ӣ�׹���С�ù�", "����209��", 1888));
		markInfoList.add(new MarkInfo(32.064355, 118.787624, R.drawable.pic2,
				"ɳ�����ʸ߼�����", "����459��", 388));
		markInfoList.add(new MarkInfo(28.7487420000, 115.8748860000,
				R.drawable.pic4, "������ͨ��ѧ����", "����5��", 888));
		markInfoList.add(new MarkInfo(28.7534890000, 115.8767960000,
				R.drawable.pic3, "������ͨ��ѧ����", "����10��", 188));
		myBaiduMap.setOnMarkerClickListener(this);
		myBaiduMap.setOnMapClickListener(this);
	}

	/**
	 * @author zoe ��Ӹ�����
	 * */
	private void addMapMarks() {
		initMarksData();
		myBaiduMap.clear();// �����һ��ͼ��
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;
		myMarks = BitmapDescriptorFactory.fromResource(R.drawable.mark);// �����Զ���ĸ�����ͼ�꣬����ת����һ��BitmapDescriptor����
		// ����MarkInfo��Listһ��MarkInfo����һ��Mark
		for (int i = 0; i < markInfoList.size(); i++) {
			// ��γ�ȶ���
			latLng = new LatLng(markInfoList.get(i).getLatitude(), markInfoList
					.get(i).getLongitude());// ��Ҫ����һ����γ����ͨ���ö���Ϳ��Զ�λ�����ڵ�ͼ�ϵ�ĳ�������
			// ͼ��
			options = new MarkerOptions().position(latLng).icon(myMarks)
					.zIndex(6);
			marker = (Marker) myBaiduMap.addOverlay(options);// ����������ӵ���ͼ��
			Bundle bundle = new Bundle();// ����һ��Bundle����ÿ��mark������Ϣ����ȥ��������ø�����ͼ���ʱ��ͻ���ʾ�ø��������ϸ��Ϣ
			bundle.putSerializable("mark", markInfoList.get(i));
			marker.setExtraInfo(bundle);
		}
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);// ͨ�������γ�ȶ��󣬵�ͼ�Ϳ��Զ�λ���õ�
		myBaiduMap.animateMapStatus(msu);
	}

	
	/**
	 * @author zoe ������ĵ���¼�
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
		// ��ʼ��һ��InfoWindow
		initInfoWindow(MyMarker, marker);
		markLayout.setVisibility(View.VISIBLE);
		return true;
	}

	/**
	 * @author zoe ��ʼ����һ��InfoWindow
	 * 
	 * */
	private void initInfoWindow(MarkInfo MyMarker, Marker marker) {
		// TODO Auto-generated method stub
		InfoWindow infoWindow;
		// InfoWindow����ʾ��View������ʽ����ʾһ��TextView
		TextView infoWindowTv = new TextView(MainActivity.this);
		infoWindowTv.setBackgroundResource(R.drawable.location_tips);
		infoWindowTv.setPadding(30, 20, 30, 50);
		infoWindowTv.setText(MyMarker.getName());
		infoWindowTv.setTextColor(Color.parseColor("#FFFFFF"));

		final LatLng latLng = marker.getPosition();
		Point p = myBaiduMap.getProjection().toScreenLocation(latLng);// ����ͼ�ϵľ�γ��ת������Ļ��ʵ�ʵĵ�
		p.y -= 47;// ������Ļ�е��Y�������ƫ����
		LatLng ll = myBaiduMap.getProjection().fromScreenLocation(p);// ���޸ĺ����Ļ�ĵ���ת���ɵ�ͼ�ϵľ�γ�ȶ���
		/**
		 * @author zoe ʵ����һ��InfoWindow�Ķ��� public InfoWindow(View view,LatLng
		 *         position, int yOffset)ͨ������� view ����һ�� InfoWindow,
		 *         ��ʱֻ�����ø�view����һ��Bitmap�����ڵ�ͼ�У������¼��ɿ�����ʵ�֡� ����: view - InfoWindow
		 *         չʾ�� view position - InfoWindow ��ʾ�ĵ���λ�� yOffset - InfoWindow Y
		 *         ��ƫ����
		 * */
		infoWindow = new InfoWindow(infoWindowTv, ll, 10);
		myBaiduMap.showInfoWindow(infoWindow);// ��ʾInfoWindow
	}

	/**
	 * @author zoe ��������ͼ��ӵĵ���¼�
	 * */
	@Override
	public void onMapClick(LatLng arg0) {// ��ʾ�����ͼ�����ĵط�ʹ�ø������������ܵĲ������أ����ǵ������ʾ�ĸ��������鲼���ϣ��򲻻���ʧ����Ϊ�����鲼���������Clickable=true
		// �����¼��Ĵ������ƣ���Ϊ����¼����Ȼ��ڸ����ﲼ�ֵĸ�����(map)��,����map�ǿ��Ե���ģ�map���ѵ���¼������ѵ����������Clickable=true��ʾ����¼������鲼���Լ���������map������
		markLayout.setVisibility(View.GONE);
		myBaiduMap.hideInfoWindow();// ����InfoWindow
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}
}
