package com.example.npttest.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.activity.Admission;
import com.example.npttest.activity.Appearance;
import com.example.npttest.activity.InputCarnum;
import com.example.npttest.activity.PresenceVehicle;
import com.example.npttest.activity.SpalshActivity;
import com.example.npttest.activity.SurplusCarParkingLot;
import com.example.npttest.camera.CameraActivity;
import com.example.npttest.constant.Constant;
import com.example.npttest.linearlayout.MyLayout;
import com.example.npttest.linearlayout.MyLayoutIn;
import com.example.npttest.loader.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by liuji on 2017/7/28.
 */

public class Fragment1 extends Fragment implements OnBannerListener {
    @Bind(R.id.fg_banner)
    Banner fgBanner;
    @Bind(R.id.textView6)
    TextView textView6;
    @Bind(R.id.fg_zaicar)
    TextView fgZaicar;
    @Bind(R.id.fg_prece)
    LinearLayout fgPrece;
    @Bind(R.id.fg_shouf)
    TextView fgShouf;
    @Bind(R.id.fg_shengy)
    TextView fgShengy;
    @Bind(R.id.fg_Surplus_car)
    LinearLayout fgSurplusCar;
    @Bind(R.id.fg_car_into)
    MyLayoutIn fgCarInto;
    @Bind(R.id.fg_car_out)
    MyLayout fgCarOut;
    @Bind(R.id.fg_inputnum)
    Button fgInputnum;
    private List<View> list = new ArrayList<View>();
    private List<ImageView> imageViews = new ArrayList<ImageView>();
    private int select_index = 0;//????????????item ?????????
    public static final String INPUT_LICENSE_COMPLETE = "me.kevingo.licensekeyboard.input.comp";
    public static final String INPUT_LICENSE_KEY = "LICENSE";
    private String number, color, bitmapPath;
    private int type;
    public static TextView shengyTv;
    public static TextView pvcarTv;
    List<Integer> images = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        images.add(R.mipmap.img_1);
        images.add(R.mipmap.img_2);
        images.add(R.mipmap.img_3);
        images.add(R.mipmap.img_4);
        images.add(R.mipmap.img_5);

    }

    private void jump() {
        TranslateAnimation down = new TranslateAnimation(0, 0, -300, 0);//??????????????????button?????????300??????????????????
        down.setFillAfter(true);
        down.setInterpolator(new BounceInterpolator());//????????????,??????????????????????????????????????????????????????
        down.setDuration(2000);//????????????
        fgCarInto.startAnimation(down);//?????????????????????????????????
        fgCarOut.startAnimation(down);//?????????????????????????????????
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment1, null);
        ButterKnife.bind(this, v);
        jump();
        shengyTv = fgShengy;
        pvcarTv=fgZaicar;
        if (App.serverurl != null) {
            get_quantity(App.serverurl);
            //get_quantity(App.serverurl);
        }
        fgBanner.setImages(images).setImageLoader(new GlideImageLoader())
                .setBannerAnimation(Transformer.Default).isAutoPlay(true)
                .setOnBannerListener(this).setDelayTime(3000).start();
        return v;
    }

    static final String[] PERMISSION = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// ????????????
            Manifest.permission.READ_EXTERNAL_STORAGE, // ????????????
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE, Manifest.permission.INTERNET};


    @Override
    public void onStart() {
        super.onStart();
        //????????????
        if (App.surpluscar != null) {
            fgShengy.setText(App.surpluscar);
        }
        if (Constant.pvcar!=null){
            fgZaicar.setText(Constant.pvcar);
        }
        fgBanner.startAutoPlay();
        String wmon = String.format("%.2f", App.wmon / 100);
        fgShouf.setText(wmon);
    }

    @Override
    public void onStop() {
        super.onStop();
        //????????????
        fgBanner.stopAutoPlay();
    }

    @OnClick({R.id.fg_car_into, R.id.fg_car_out, R.id.fg_inputnum, R.id.fg_prece, R.id.fg_zaicar, R.id.fg_Surplus_car})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_car_into:
                type = 1;
                //jumpVideoRecog();
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                intent.putExtra("camera", true);
                //startActivity(intent);
                startActivityForResult(intent, 0x11);
                break;
            case R.id.fg_car_out:
                type = 2;
                Intent intent1 = new Intent(getActivity(), CameraActivity.class);
                intent1.putExtra("camera", true);
                //startActivity(intent);
                startActivityForResult(intent1, 0x11);
                //jumpVideoRecog();
                break;
            case R.id.fg_inputnum:
                startActivity(new Intent(getActivity(), InputCarnum.class));
                break;
            case R.id.fg_prece:
                startActivity(new Intent(getActivity(), PresenceVehicle.class));
                break;
            case R.id.fg_Surplus_car:
                startActivity(new Intent(getActivity(), SurplusCarParkingLot.class));
                break;
        }
    }

    //??????????????????????????????????????????
    private void get_quantity(String url) {
        String jsons = "{\"cmd\":\"176\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+ Constant.CODE+"\",\"dsv\":\""+ Constant.DSV+"\",\"sign\":\"abcd\"}";
        Log.e("TAG", jsons);
        OkHttpUtils.postString().url(url)
                .content(jsons)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG", response);
                try {
                    JSONObject rpjson = new JSONObject(response);
                    String reasonjson = rpjson.getString("reason");
                    JSONObject resultjson = rpjson.getJSONObject("result");
                    int code=rpjson.getInt("code");
                    // Log.e("TAG", datajsonObject.toString());
                    if (code==100) {
                        JSONObject datajsonObject = resultjson.getJSONObject("data");
                        int elot=datajsonObject.getInt("elot");
                        int number=datajsonObject.getInt("number");
                        int tlot=datajsonObject.getInt("tlot");
                        Constant.pvcar= String.valueOf(number);
                        fgShengy.setText(String.valueOf(elot+tlot));
                        fgZaicar.setText(number+"");
                    } else {
                        Log.e("TAG", "????????????");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //?????????????????????????????????
  /*  public void jumpVideoRecog() {
        Intent video_intent = new Intent();
        video_intent.putExtra("camera", true);
        RecogService.recogModel = true;//true  ???????????? ????????????  false:????????????  ????????????
        video_intent = new Intent(getActivity(), MemoryCameraActivity.class);
        if (Build.VERSION.SDK_INT >= 23) {
            CheckPermission checkPermission = new CheckPermission(getActivity());
            if (checkPermission.permissionSet(PERMISSION)) {
                PermissionActivity.startActivityForResult(getActivity(), 0, "true", PERMISSION);
                startActivityForResult(video_intent, 0x11);
            } else {
                video_intent.setClass(getActivity().getApplication(), MemoryCameraActivity.class);
                video_intent.putExtra("camera", true);
                //startActivity(video_intent);
                startActivityForResult(video_intent, 0x11);
            }
        } else {
            video_intent.setClass(getActivity().getApplication(), MemoryCameraActivity.class);
            video_intent.putExtra("camera", true);
            startActivityForResult(video_intent, 0x11);
        }
        //finish();
    }*/

    //???????????????????????????
   /* public void jumpTakePic() {
        Intent cameraintent = new Intent(getActivity(), MemoryCameraActivity.class);
        if (Build.VERSION.SDK_INT >= 23) {
            CheckPermission checkPermission = new CheckPermission(getActivity());
            if (checkPermission.permissionSet(PERMISSION)) {
                PermissionActivity.startActivityForResult(getActivity(), 0, "false", PERMISSION);
            } else {
                cameraintent.putExtra("camera", false);
                startActivity(cameraintent);
            }
        } else {
            cameraintent.putExtra("camera", false);
            startActivity(cameraintent);
        }
    }*/

    public void test() {
        startActivity(new Intent(getActivity(), SpalshActivity.class));
        Log.e("TAG", getActivity() + "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            number = data.getStringExtra("number").toString();
            color = data.getStringExtra("color").toString();
            bitmapPath = data.getStringExtra("path").toString();
            if (number.equals("null")) {

            } else if (type == 1) {
                Intent intent = new Intent(getActivity(), Admission.class);
                intent.putExtra("number", number);
                intent.putExtra("color", color);
                intent.putExtra("path", bitmapPath);
                startActivity(intent);
            } else if (type == 2) {
                Intent intent = new Intent(getActivity(), Appearance.class);
                intent.putExtra("number", number);
                intent.putExtra("color", color);
                intent.putExtra("path", bitmapPath);
                startActivity(intent);
            }
        }
        //Toast.makeText(getActivity(), bitmapPath, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(),number, Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnBannerClick(int position) {
        //Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.baidu.com"));
                startActivity(intent);*/
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
