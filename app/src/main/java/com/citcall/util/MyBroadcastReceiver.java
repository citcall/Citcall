package com.citcall.util;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.citcall.R;
import com.citcall.constant.Const;
import com.orhanobut.hawk.Hawk;

import java.lang.reflect.Method;

/**
 * Created by Agustya on 9/19/16.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private DialogOverride dialogOverride;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private static int mLastState;

    public MyBroadcastReceiver(Context context){
        if(dialogOverride == null){
            dialogOverride = new DialogOverride(context, R.style.DialogTheme);
            dialogOverride.setCancelable(false);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            }else{
                dialogOverride.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            dialogOverride.show();
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(!intent.getAction().equals("android.intent.action.PHONE_STATE")){
            return;
        }else{
//            final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            phoneStateListener = new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if(state != mLastState){
                        mLastState = state;

                        switch (state){
                            case TelephonyManager.CALL_STATE_IDLE:
                                Log.d("test", "idle");
                                break;
                            case TelephonyManager.CALL_STATE_OFFHOOK:
                                Log.d("test", "off hook");
                                break;
                            case TelephonyManager.CALL_STATE_RINGING:
                                Log.d("test", "ringing");

                                if(incomingNumber.length() == 6){
                                    String filterString = incomingNumber.replace("+", "");
                                    Hawk.put(Const.Key.NUMBER, filterString);
                                    silentRinger(context);
                                    Log.d("test", "disconnect now...");
                                    disconnectPhoneItelephony(context);
                                }else{
                                    if(incomingNumber.length() > 6){
                                        if(incomingNumber.startsWith("+62890")){
                                            String lastCharNumber = incomingNumber.substring(incomingNumber.length()-5, incomingNumber.length());
                                            Hawk.put(Const.Key.NUMBER, lastCharNumber);
                                            silentRinger(context);
                                            Log.d("test", "disconnect now...");
                                            disconnectPhoneItelephony(context);
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
            };

            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    public void dismissDialog(){
        if(dialogOverride != null){
            dialogOverride.dismiss();
        }
    }

    private void silentRinger(Context context){
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDialogTryCount(int tryCount){
        if(dialogOverride != null){
            dialogOverride.setTxtTry(tryCount);
        }
    }

    class DialogOverride extends Dialog {
        private TextView txtNetworkError;
        private TextView txtTry;

        public DialogOverride(Context context, int theme){
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            setContentView(R.layout.dialog_override);

            txtTry = (TextView) findViewById(R.id.txt_try);
            txtNetworkError = (TextView) findViewById(R.id.txt_network_error);
        }

        public void setTxtTry(int tryCount){
            txtTry.setText(tryCount + " of 3 try");
            if(tryCount > 1){
                txtNetworkError.setVisibility(View.VISIBLE);
            }else{
                txtNetworkError.setVisibility(View.INVISIBLE);
            }
        }
    }
}
