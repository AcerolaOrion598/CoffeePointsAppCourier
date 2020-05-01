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
import com.djaphar.coffeepointsappcourier.ApiClasses.PointsApi;
import com.djaphar.coffeepointsappcourier.ApiClasses.UpdatableUser;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.ApiBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.App.CHANNEL_ID;

public class LocationUpdateService extends Service {

    private LocationManager locationManager;
    private UpdatableUser updatableUser;
    private Map<String, String> headersMap;
    private PointsApi pointsApi;
    private LocationUpdateListener[] locationListeners = new LocationUpdateListener[] {
            new LocationUpdateListener(LocationManager.GPS_PROVIDER),
            new LocationUpdateListener(LocationManager.NETWORK_PROVIDER)
    };

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
            ArrayList<Double> coordinates = new ArrayList<>();
            coordinates.add(lastLocation.getLongitude());
            coordinates.add(lastLocation.getLatitude());
            updatableUser.setCoordinates(coordinates);
            Call<UpdatableUser> callCheck = pointsApi.requestUpdatableUser(headersMap);
            callCheck.enqueue(new Callback<UpdatableUser>() {
                @Override
                public void onResponse(@NonNull Call<UpdatableUser> callCheck, @NonNull Response<UpdatableUser> response) {
                    UpdatableUser userToCheck = response.body();
                    if (userToCheck == null) {
                        stopSelf();
                        return;
                    }

                    if (!updatableUser.getSupervisor().equals(userToCheck.getSupervisor())) {
                        stopSelf();
                        return;
                    }

                    Call<User> call = pointsApi.requestUpdateCourier(headersMap, updatableUser);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) { }

                        @Override
                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) { }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<UpdatableUser> callCheck, @NonNull Throwable t) { }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    }

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
                    extras.getString("supervisor"), extras.getString("hint"), null, null);
            headersMap = new HashMap<>();
            String authorizationHeader = extras.getString("authorization_header");
            if (authorizationHeader != null) {
                headersMap.put(authorizationHeader, Objects.requireNonNull(extras.getString("userToken")));
            }
            pointsApi = ApiBuilder.getPointsApi();
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.location_service_title))
                .setContentText(getString(R.string.location_service_text))
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1f, locationListeners[0]);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1f, locationListeners[1]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (LocationUpdateListener listener : locationListeners) {
            locationManager.removeUpdates(listener);
        }
    }

    private void initializeLocationManager() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }
}
