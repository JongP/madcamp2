package com.example.madcamp_week1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class Fragment3 extends Fragment implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    MapView mapView;
    private static final String LOG_TAG = "Fragment3";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    FloatingActionButton gps_button;
    boolean gps_on = false;


    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private MapPOIItem createMarkers(String place, double latitude, double longitude){

        MapPOIItem marker = new MapPOIItem();

        marker.setItemName(place);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin); // 마커를 클릭했을때, 기본으로 제공하는 YellowPin 마커 모양.
        marker.setShowDisclosureButtonOnCalloutBalloon(false);

        return marker;
    }

    private Coord[] createCoords(){
        Coord[] coords = new Coord[20];

        coords[0] = new Coord(36.36267352354882,127.35791396778204);
        coords[1] = new Coord(36.36351372100507, 127.35721028348622);
        coords[2] = new Coord(36.36299396642154, 127.35623018342451);
        coords[3] = new Coord(36.362026813544816, 127.35341241453233);
        coords[4] = new Coord(36.36363775538186, 127.35867327094368);
        coords[5] = new Coord(36.36313293783038, 127.35872666517896);
        coords[6] = new Coord(36.36281587360104, 127.35777534296157);
        coords[7] = new Coord(36.363362434975976, 127.35732379972953);
        coords[8] = new Coord(36.363921648692795, 127.35791411233888);
        coords[9] = new Coord(36.36307281302966, 127.35774030706231);
        coords[10] = new Coord(36.36204705787819, 127.35418409141468);
        coords[11] = new Coord(36.36233053115967, 127.35507674131362);
        coords[12] = new Coord(36.36320980385998, 127.35713368467495);
        coords[13] = new Coord(36.363252085749686, 127.35806146578395 );
        coords[14] = new Coord(36.36257295195989, 127.35764331130726);
        coords[15] = new Coord(36.36364603925113, 127.35891565295813);
        coords[16] = new Coord(36.36336164618246, 127.35908983679943);
        coords[17] = new Coord(36.36259191694817, 127.3573286336779);
        coords[18] = new Coord(36.362151648881444, 127.35386144616314);
        coords[19] = new Coord(36.36105869295062, 127.35395677507192);

        return coords;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);
        mapView = new MapView(getActivity());

        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.map_view_id);
        gps_button = view.findViewById(R.id.gps_button);
        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps_on = !gps_on;
                if (gps_on) {
                    if (!checkLocationServicesStatus()) {
                        showDialogForLocationServiceSetting();
                        if (checkLocationServicesStatus()) {
                            Drawable img_on = getActivity().getResources().getDrawable(R.drawable.location_on);
                            gps_button.setImageDrawable(img_on);
                        }
                        else {
                            gps_on = false;
                        }
                    } else {
                        Drawable img_on = getActivity().getResources().getDrawable(R.drawable.location_on);
                        gps_button.setImageDrawable(img_on);
                    }
                } else {
                    Drawable img_off = getActivity().getResources().getDrawable(R.drawable.location_off);
                    gps_button.setImageDrawable(img_off);
                }
                checkRunTimePermission();
            }
        });

        mapView.setCurrentLocationEventListener(this);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.3622662202898, 127.3562463651629), true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);

        ArrayList<String> restList = new ArrayList<>();

        try {
            AssetManager assetManager = getActivity().getAssets();

            InputStream is = assetManager.open("jsonDirectory/restaurantList.json");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while (line!=null){
                buffer.append(line+"\n");
                line = reader.readLine();
            }

            String jsonData = buffer.toString();
            JSONArray jsonArray = new JSONArray(jsonData);

            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject jo = jsonArray.getJSONObject(i);
                String name = jo.getString("name");
                restList.add(name.replace("\n", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Coord[] coords = createCoords();

        for(int i = 0; i < restList.size(); i++){
            mapView.addPOIItem(createMarkers(restList.get(i),coords[i].getLatitude(), coords[i].getLongitude()));
        }

        mapViewContainer.addView(mapView);

        return view;
    }

    public void onDestroy() {
        super.onDestroy();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result){
        //Toast.makeText(LocationDemoAc)
    }

    // ActivityCompat를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults){
        if(permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PREMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크함
            for(int result : grandResults){
                if (result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 위치 값을 가져올 수 있음
                System.out.println("@@@" + "start");
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료함. 2가지 경우가 있음.
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(getContext(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {
        // 런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // (안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식함)

            // 3. 위치 값을 가져올 수 있음.
            if (gps_on && checkLocationServicesStatus())
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            else
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
        else {
            // 2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요함. 2가지 경우 (3-1, 4-1) 가 있음.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자에게 퍼미션이 필요한 이유를 설명해줄 필요가 있음.
                Toast.makeText(getContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자에게 퍼미션 요청을 함. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됨.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_ENABLE_REQUEST_CODE) {// 사용자가 GPS 활성 시켰는지 검사
            if (checkLocationServicesStatus()) {
                Log.d("@@@", "onActivityResult : GPS 활성화 되어있음");
                checkRunTimePermission();
                return;
            }
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    class Coord{
        double latitude;
        double longitude;

        public Coord(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}