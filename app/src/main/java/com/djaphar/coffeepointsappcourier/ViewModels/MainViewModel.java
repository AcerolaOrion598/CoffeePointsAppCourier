package com.djaphar.coffeepointsappcourier.ViewModels;

import android.app.Application;
import android.widget.Toast;

import com.djaphar.coffeepointsappcourier.ApiClasses.PointsApi;
import com.djaphar.coffeepointsappcourier.ApiClasses.UpdatedUser;
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
    private MutableLiveData<ArrayList<String>> products = new MutableLiveData<>();
    private UserDao userDao;
    private ArrayList<String> productList = new ArrayList<>();
    private final static String baseUrl = "http://212.109.219.69:3007/";

    public MainViewModel(@NonNull Application application) {
        super(application);

        productList.clear();
        String product1 ="Кофеёк 1";
        String product2 ="Кофеёк 2";
        String product3 ="Мороженное";
        String product4 ="Кофеёк 3";
        String product5 ="Блинчики";
        String product6 ="Мороженка 2";
        String product7 ="Блинчики 2";
        String product8 ="Блинчики 3";
        String product9 ="Блинчики 4";
        String product10 ="Блинчики 5";

        productList.add(product1);
        productList.add(product2);
        productList.add(product3);
        productList.add(product4);
        productList.add(product5);
        productList.add(product6);
        productList.add(product7);
        productList.add(product8);
        productList.add(product9);
        productList.add(product10);

        products.setValue(productList);
        UserRoom userRoom = UserRoom.getDatabase(application);
        userDao = userRoom.userDao();
        userLiveData = userDao.getUserLiveData();
    }

    public MutableLiveData<ArrayList<String>> getProducts() {
        return products;
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public void logout() {
        UserRoom.databaseWriteExecutor.execute(() -> userDao.deleteUser());
    }

    public void unsetOwner(String id, String token, UpdatedUser updatedUser) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PointsApi pointsApi = retrofit.create(PointsApi.class);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", token);
        Call<User> call = pointsApi.requestUnsetOwner(id, headersMap, updatedUser);
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
}
