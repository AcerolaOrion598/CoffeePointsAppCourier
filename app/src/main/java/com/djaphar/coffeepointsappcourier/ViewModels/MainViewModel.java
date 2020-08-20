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
import com.djaphar.coffeepointsappcourier.R;
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

    public void requestUpdateCourier(HashMap<String, String> headersMap, UpdatableUser updatableUser, boolean logout) {
        pointsApi.requestUpdateCourier(headersMap, updatableUser).enqueue(new Callback<User>() {
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

                requestUpdatableUser(headersMap);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestUnsubscribe(HashMap<String, String> headersMap) {
        pointsApi.requestUnsubscribe(headersMap).enqueue(new Callback<User>() {
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
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestUser(HashMap<String, String> headersMap, Integer oldHash) {
        pointsApi.requestUser(headersMap).enqueue(new Callback<User>() {
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
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestUpdatableUser(HashMap<String, String> headersMap) {
        pointsApi.requestUpdatableUser(headersMap).enqueue(new Callback<UpdatableUser>() {
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
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestSupervisor(String id) {
        pointsApi.requestSupervisor(id).enqueue(new Callback<Supervisor>() {
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
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestSupervisorProducts(HashMap<String, String> headersMap, String supervisor) {
        pointsApi.requestSupervisorProducts(supervisor, headersMap).enqueue(new Callback<ArrayList<Product>>() {
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
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestProductsListToggle(String productId, HashMap<String, String> headersMap, String supervisor) {
        pointsApi.requestProductsListToggle(productId, headersMap).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                }
                requestUpdatableUser(headersMap);
                requestSupervisorProducts(headersMap, supervisor);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
