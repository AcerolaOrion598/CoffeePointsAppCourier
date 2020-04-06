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
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.ApiBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {

    private LiveData<User> userLiveData;
    private MutableLiveData<Supervisor> supervisorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Product>> productsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UpdatableUser> updatableUserMutableLiveData = new MutableLiveData<>();
    private UserDao userDao;
    private PointsApi pointsApi;

    public MainViewModel(@NonNull Application application) {
        super(application);
        UserRoom userRoom = UserRoom.getDatabase(application);
        userDao = userRoom.userDao();
        userLiveData = userDao.getUserLiveData();
        pointsApi = ApiBuilder.getPointsApi();
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

    public void requestUpdateCourier(String id, HashMap<String, String> headersMap, UpdatableUser updatableUser, boolean logout) {
        Call<User> call = pointsApi.requestUpdateCourier(id, headersMap, updatableUser);
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

                if (logout) {
                    logout();
                    return;
                }

                requestUpdatableUser(id, headersMap);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestUnsubscribe(String userId, HashMap<String, String> headersMap) {
        Call<User> call = pointsApi.requestUnsubscribe(userId, headersMap);
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
                UserRoom.databaseWriteExecutor.execute(() -> userDao.updateUser(user));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestUser(String id, HashMap<String, String> headersMap, Integer oldHash) {
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

    public void requestUpdatableUser(String id, HashMap<String, String> headersMap) {
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

    public void requestSupervisorProducts(HashMap<String, String> headersMap, String supervisor) {
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

    public void requestProductsListToggle(String userId, String productId, HashMap<String, String> headersMap, String supervisor) {
        Call<Void> call = pointsApi.requestProductsListToggle(productId, headersMap);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                }
                requestUpdatableUser(userId, headersMap);
                requestSupervisorProducts(headersMap, supervisor);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
