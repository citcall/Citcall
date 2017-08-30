package com.citcall.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.citcall.R;
import com.citcall.activity.base.BaseActivity;
import com.citcall.constant.Const;
import com.citcall.helper.PermissionHelper;
import com.citcall.model.RequestModel;
import com.citcall.model.VerifyModel;
import com.citcall.network.RestClient;
import com.citcall.util.MyBroadcastReceiver;
import com.citcall.util.NetworkUtil;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chairul on 9/16/16.
 */
public class ActMain extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.field_phone_prefix)
    EditText fieldPhonePrefix;
    @Bind(R.id.field_phone_suffix)
    EditText fieldPhoneSuffix;
    @Bind(R.id.btn_verify)
    Button btnVerify;

    private int tryNo = 1;
    private static final int MAX_TRY = 3;
    //your citcall username and password
    private static final String username = "CITCALL_USERNAME";
    private static final String password = "CITCALL_PASSWORD";

    private MyBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnVerify.setOnClickListener(this);
    }

    private boolean isValidationSuccess() {
        fieldPhoneSuffix.setError(null);

        if (TextUtils.isEmpty(fieldPhoneSuffix.getText().toString())) {
            fieldPhoneSuffix.setError("Please enter your mobile number!");
        } else {
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Const.Request.PERMISSION_MAIN:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    request();
                } else {
                    checkingPermissions();
                }
                break;
        }
    }

    private void checkingPermissions() {
        if (PermissionHelper.isGranted(this, Manifest.permission.READ_PHONE_STATE,
                new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW},
                Const.Request.PERMISSION_MAIN,
                "Read phone state permission is needed to continue the process")) {
            request();
        }
    }

    private String getPhoneNumber(){
        return "0".concat(fieldPhoneSuffix.getText().toString());
    }

    private void request() {
        hideKeyboard();

        if (isValidationSuccess() && NetworkUtil.isConnected(getApplicationContext())) {
            registerBroadcast();

            RestClient.ApiService rest = RestClient.getClient();
            Call<ResponseBody> call = rest.request(getPhoneNumber(), tryNo, username, password);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("test", "got request response");
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            Gson gson = new Gson();
                            RequestModel result = gson.fromJson(responseBody.string(), RequestModel.class);
                            String number = Hawk.get(Const.Key.NUMBER);
                            if(number != null){
                                if(number.equals(result.getToken())){
                                    verify(result);
                                }else{
                                    navigateTo(Const.Status.FAILED);
                                    unregisterBroadcast();
                                }
                            }
                        } else {
                            shortToast(response.message());
                            navigateTo(Const.Status.FAILED);
                            unregisterBroadcast();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        navigateTo(Const.Status.FAILED);
                        unregisterBroadcast();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(tryNo <= MAX_TRY){
                        Log.d("test", "failure");
                        tryNo = tryNo + 1;
                        Log.d("test", "try " + String.valueOf(tryNo));
                        if(broadcastReceiver != null){
                            broadcastReceiver.setDialogTryCount(tryNo);
                        }
                        request();
                    }else{
                        navigateTo(Const.Status.FAILED);
                        unregisterBroadcast();
                    }
                }
            });
        }
    }

    private void verify(final RequestModel requestModel) {
        if (NetworkUtil.isConnected(getApplicationContext())) {
            Log.d("test", "verify");

            RestClient.ApiService rest = RestClient.getClient();
            Call<ResponseBody> call = rest.verify(requestModel.getTrx_id(), requestModel.getToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            navigateTo(Const.Status.SUCCESS);
                            tryNo = 1;
                        } else {
                            shortToast(response.message());
                            navigateTo(Const.Status.FAILED);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        navigateTo(Const.Status.FAILED);
                    }

                    unregisterBroadcast();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(tryNo <= MAX_TRY){
                        tryNo = tryNo + 1;
                        if(broadcastReceiver != null){
                            broadcastReceiver.setDialogTryCount(tryNo);
                        }
                        verify(requestModel);
                    }else{
                        navigateTo(Const.Status.FAILED);
                        unregisterBroadcast();
                    }
                }
            });
        }else{
            navigateTo(Const.Status.FAILED);
            unregisterBroadcast();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == Const.Page.MAIN){
                Bundle bundle = data.getExtras();
                if(bundle != null){
                    tryNo = 1;
                    boolean retry = bundle.getBoolean(Const.Extra.RETRY, false);
                    if(retry){
                        request();
                    }
                }
            }
        }
    }

    private void navigateTo(String status){
        Intent intent = new Intent(this, ActStatus.class);
        intent.putExtra(Const.Extra.STATUS, status);
        startActivityForResult(intent, Const.Page.MAIN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_verify:
                checkingPermissions();
                break;
        }
    }

    private void registerBroadcast(){
        if(broadcastReceiver == null){
            Log.d("test", "register broadcast");
            broadcastReceiver = new MyBroadcastReceiver(this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PHONE_STATE");
            intentFilter.setPriority(-1);
            registerReceiver(broadcastReceiver, intentFilter);
        }else{
            Log.d("test", "broadcast already register");
            // already set
        }
    }

    private void unregisterBroadcast(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if(broadcastReceiver != null){
                        broadcastReceiver.dismissDialog();
                        unregisterReceiver(broadcastReceiver);
                        broadcastReceiver = null;
                        Log.d("test", "unregister broadcast");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, Const.Value.DELAY);
    }
}
