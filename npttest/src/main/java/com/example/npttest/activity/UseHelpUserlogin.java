package com.example.npttest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.npttest.R;
import com.example.npttest.loader.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UseHelpUserlogin extends Activity {

    @Bind(R.id.user_login_banner)
    Banner userLoginBanner;
    @Bind(R.id.user_login_return)
    ImageView userLoginReturn;
    private List<Integer> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_help_userlogin);
        ButterKnife.bind(this);
        images.add(R.mipmap.use_help_login);
        images.add(R.mipmap.use_help_vlogin);
        userLoginBanner.setImages(images).setImageLoader(new GlideImageLoader())
                .setBannerAnimation(Transformer.Default).isAutoPlay(false).start();
    }


    @OnClick(R.id.user_login_return)
    public void onViewClicked() {
        finish();
    }
}
