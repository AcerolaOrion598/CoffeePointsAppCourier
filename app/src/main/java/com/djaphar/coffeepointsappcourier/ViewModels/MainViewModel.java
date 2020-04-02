package com.djaphar.coffeepointsappcourier.ViewModels;

import android.app.Application;
import android.widget.Toast;

import com.djaphar.coffeepointsappcourier.ApiClasses.PointsApi;
import com.djaphar.coffeepointsappcourier.ApiClasses.Product;
import com.djaphar.coffeepointsappcourier.ApiClasses.UpdatableUser;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.Supervisor;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.UserDao;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.UserRoom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainViewModel extends AndroidViewModel {

    private LiveData<User> userLiveData;
    private MutableLiveData<Supervisor> supervisorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Product>> productsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UpdatableUser> updatableUserMutableLiveData = new MutableLiveData<>();
    private UserDao userDao;
    private final static String baseUrl = "http://212.109.219.69:3007/";

    public MainViewModel(@NonNull Application application) {
        super(application);
        UserRoom userRoom = UserRoom.getDatabase(application);
        userDao = userRoom.userDao();
        userLiveData = userDao.getUserLiveData();
    }

    public MutableLiveData<ArrayList<Product>> getProducts() {
        return productsMutableLiveData;
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public MutableLiveData<Supervisor> getSupervisor() {
        return supervisorMutableLiveData;
    }

    public MutableLiveData<UpdatableUser> getUpdatableUser() {
        return updatableUserMutableLiveData;
    }

    public void logout() {
        UserRoom.databaseWriteExecutor.execute(() -> userDao.deleteUser());
    }

    public void unsetOwner(String id, String token, UpdatableUser updatableUser) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PointsApi pointsApi = retrofit.create(PointsApi.class);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", token);
        Call<User> call = pointsApi.requestUnsetOwner(id, headersMap, updatableUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = response.body();
                if (user == null) {
                    return;
                }
                Integer newHash = user.determineHash();
                user.setUserHash(newHash);
                UserRoom.databaseWriteExecutor.execute(() -> userDao.updateUser(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestUser(String id, String token, Integer oldHash) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PointsApi pointsApi = retrofit.create(PointsApi.class);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", token);
        Call<User> call = pointsApi.requestUser(id, headersMap);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() == null) {
                    return;
                }

                User user = response.body();
                Integer newHash = user.determineHash();
                if (oldHash.equals(newHash)) {
                    return;
                }
                user.setUserHash(newHash);
                UserRoom.databaseWriteExecutor.execute(() -> userDao.updateUser(user));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestUpdatableUser(String id, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PointsApi pointsApi = retrofit.create(PointsApi.class);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", token);
        Call<UpdatableUser> call = pointsApi.requestUpdatableUser(id, headersMap);
        call.enqueue(new Callback<UpdatableUser>() {
            @Override
            public void onResponse(@NonNull Call<UpdatableUser> call, @NonNull Response<UpdatableUser> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() == null) {
                    return;
                }

                updatableUserMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<UpdatableUser> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestSupervisor(String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PointsApi pointsApi = retrofit.create(PointsApi.class);
        Call<Supervisor> call = pointsApi.requestSupervisor(id);
        call.enqueue(new Callback<Supervisor>() {
            @Override
            public void onResponse(@NonNull Call<Supervisor> call, @NonNull Response<Supervisor> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Supervisor supervisor = response.body();
                supervisorMutableLiveData.setValue(supervisor);
            }

            @Override
            public void onFailure(@NonNull Call<Supervisor> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestSupervisorProducts(String token, String supervisor) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PointsApi pointsApi = retrofit.create(PointsApi.class);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", token);
        Call<ArrayList<Product>> call = pointsApi.requestSupervisorProducts(supervisor, headersMap);
        call.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Product>> call, @NonNull Response<ArrayList<Product>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                productsMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Product>> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestProductsListToggle(String userId, String productId, String token, String supervisor) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PointsApi pointsApi = retrofit.create(PointsApi.class);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", token);
        Call<Void> call = pointsApi.requestProductsListToggle(productId, headersMap);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                }
                requestUpdatableUser(userId, token);
                requestSupervisorProducts(token, supervisor);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
