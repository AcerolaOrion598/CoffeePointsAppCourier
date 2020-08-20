package com.djaphar.coffeepointsappcourier.ViewModels;

import android.app.Application;
import android.widget.Toast;

import com.djaphar.coffeepointsappcourier.ApiClasses.FirstCredentials;
import com.djaphar.coffeepointsappcourier.ApiClasses.PointsApi;
import com.djaphar.coffeepointsappcourier.ApiClasses.SecondCredentials;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.UserDao;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.UserRoom;
import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.ApiBuilder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private LiveData<User> userLiveData;
    private MutableLiveData<SecondCredentials> secondCredentialsMutableLiveData = new MutableLiveData<>();
    private UserDao userDao;
    private PointsApi pointsApi;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        UserRoom userRoom = UserRoom.getDatabase(application);
        userDao = userRoom.userDao();
        userLiveData = userDao.getUserLiveData();
        pointsApi = ApiBuilder.getPointsApi();
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public MutableLiveData<SecondCredentials> getSecondCredentials() {
        return secondCredentialsMutableLiveData;
    }

    public void requestCode(FirstCredentials firstCredentials) {
        pointsApi.getCode(firstCredentials).enqueue(new Callback<SecondCredentials>() {
            @Override
            public void onResponse(@NonNull Call<SecondCredentials> call, @NonNull Response<SecondCredentials> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplication(), response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                secondCredentialsMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<SecondCredentials> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(SecondCredentials secondCredentials) {
        pointsApi.login(secondCredentials).enqueue(new Callback<User>() {
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
                Integer userHash = user.determineHash();
                user.setUserHash(userHash);
                UserRoom.databaseWriteExecutor.execute(() -> userDao.setUser(user));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getApplication(), R.string.network_error_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
