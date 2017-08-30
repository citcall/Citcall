package com.citcall.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.citcall.R;
import com.citcall.activity.base.BaseActivity;
import com.citcall.constant.Const;
import com.citcall.fragment.FragmentFailed;
import com.citcall.fragment.FragmentSuccess;

import butterknife.ButterKnife;

/**
 * Created by Agustya on 9/16/16.
 */
public class ActStatus extends BaseActivity {
    private String status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            status = bundle.getString(Const.Extra.STATUS);
        }

        mFragmentTransaction = mFragmentManager.beginTransaction();
        if(status.equals(Const.Status.SUCCESS)){
            mFragmentTransaction.add(R.id.frame_status, FragmentSuccess.newInstance()).commit();
        }else{
            mFragmentTransaction.add(R.id.frame_status, FragmentFailed.newInstance()).commit();
        }
    }
}
