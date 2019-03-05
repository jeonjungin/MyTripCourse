package goldmoon.MyTripCourse.LocalCourse;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * GPS 사용하기 위한 클래스
 *
 *
 */

public class GPSInfo{

    Context mContext;
    //GPS 가능?
    boolean isGPSEnabled=false;
    boolean isNetworkEnabled=false;

    private LocationManager locationManager;
    private Location location;
    private LocationListener locationListener;

    //위도, 경도
//    private double lat;
//    private double lon;

    GPSInfo(Context mContext, LocationListener locationListener){
        this.mContext=mContext;
        this.locationManager=(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        this.locationListener=locationListener;

    }

    public boolean startGetGPS(){
        //GPS 사용가능?
        Log.e("GPSInfo: ","사용 가능?");
        try {

            if(isGPSEnabled() || isNetworkEnabled()){

                if (isGPSEnabled()) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 40000, 10, locationListener);

                    Log.e("GPSInfo: ", "GPS 사용");
                    return true;
                }
                if (isNetworkEnabled()) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 40000, 10, locationListener);
                    Log.e("GPSInfo: ", "Network GPS 사용");
                    return true;
                }
            }
            else{
                Log.e("GPSInfo: ","GPS 에러");
                return false;
            }
        }catch (SecurityException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void stopGetGPS(){

        if(locationManager!=null && (isGPSEnabled() || isNetworkEnabled())) {
            locationManager.removeUpdates(locationListener);
            Log.e("GPSInfo: ","GPS 수신 해제");
        }
    }
    public boolean isGPSEnabled(){
        this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return this.isGPSEnabled;
    }
    public boolean isNetworkEnabled(){
        this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return this.isNetworkEnabled;
    }

}
