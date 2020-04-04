package com.djaphar.coffeepointsappcourier.SupportClasses.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.djaphar.coffeepointsappcourier.Activities.MainActivity;
import com.djaphar.coffeepointsappcourier.ApiClasses.Coordinates;
import com.djaphar.coffeepointsappcourier.ApiClasses.PointsApi;
import com.djaphar.coffeepointsappcourier.ApiClasses.UpdatableUser;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.App.CHANNEL_ID;

public class LocationUpdateService extends Service {

    private LocationManager locationManager = null;
    private UpdatableUser updatableUser = null;
    private String userId;
    private Map<String, String> userToken;
    PointsApi pointsApi = null;

    private class LocationUpdateListener implements LocationListener {

        Location lastLocation;

        LocationUpdateListener(String provider) {
            lastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            lastLocation.set(location);
            if (updatableUser == null || pointsApi == null) {
                return;
            }
            updatableUser.setCoordinates(new Coordinates(lastLocation.getLatitude(), lastLocation.getLongitude()));
            Call<User> call = pointsApi.requestUpdateCourier(userId, userToken, updatableUser);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) { }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) { }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    }

    LocationListener[] locationListeners = new LocationListener[] {
        new LocationUpdateListener(LocationManager.GPS_PROVIDER),
        new LocationUpdateListener(LocationManager.NETWORK_PROVIDER)
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            updatableUser = new UpdatableUser(extras.getBoolean("isActive"), extras.getBoolean("isCurrentlyNotHere"),
                    extras.getString("supervisor"), null, null);
            userId = extras.getString("userId");
            userToken = new HashMap<>();
            userToken.put("Authorization", Objects.requireNonNull(extras.getString("userToken")));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://212.109.219.69:3007/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            pointsApi = retrofit.create(PointsApi.class);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Test")
                .setContentText("Test2")
                .setSmallIcon(R.drawable.red_marker)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLocationManager();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1f, locationListeners[1]);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1f, locationListeners[0]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            for (LocationListener locationListener : locationListeners) {
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    private void initializeLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
