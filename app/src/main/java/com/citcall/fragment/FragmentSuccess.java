package com.citcall.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.citcall.R;
import com.citcall.fragment.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Agustya on 9/20/16.
 */

public class FragmentSuccess extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.btn_more_info)
    Button btnMoreInfo;
    @Bind(R.id.btn_back)
    Button btnBack;

    public static FragmentSuccess newInstance(){
        FragmentSuccess fragmentSuccess = new FragmentSuccess();
        return fragmentSuccess;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);
        ButterKnife.bind(this, view);

        btnMoreInfo.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_more_info:
                navigateToMoreInfo();
                break;
            case R.id.btn_back:
                closeActivity();
                break;
        }
    }

    private void navigateToMoreInfo(){
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.frame_status, FragmentMoreInfo.newInstance()).addToBackStack(null).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
