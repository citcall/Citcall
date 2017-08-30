package com.citcall.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.citcall.R;
import com.citcall.constant.Const;
import com.citcall.fragment.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Agustya on 9/20/16.
 */

public class FragmentFailed extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.btn_verify_again)
    Button btnVerifyAgain;
    @Bind(R.id.btn_back)
    Button btnBack;

    public static FragmentFailed newInstance(){
        FragmentFailed fragmentFailed = new FragmentFailed();
        return fragmentFailed;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_failed, container, false);
        ButterKnife.bind(this, view);

        btnVerifyAgain.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify_again:
                backToMainPage(true);
                break;
            case R.id.btn_back:
                backToMainPage(false);
                break;
        }
    }

    private void backToMainPage(boolean retry){
        Intent intent = new Intent();
        intent.putExtra(Const.Extra.RETRY, retry);
        if(getActivity().getParent() != null){
            getActivity().getParent().setResult(Activity.RESULT_OK, intent);
        }else{
            getActivity().setResult(Activity.RESULT_OK, intent);
        }
        closeActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
