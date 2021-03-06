package com.example.npttest.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.constant.Constant;
import com.example.npttest.fragment.Fragment1;
import com.example.npttest.fragment.Fragment2;
import com.example.npttest.fragment.Fragment3;
import com.example.npttest.manager.ActivityManager;
import com.example.npttest.manager.DataCleanManager;
import com.example.npttest.util.FileSizeUtil;
import com.example.npttest.util.MD5Utils;
import com.example.npttest.util.SPUtils;
import com.example.npttest.viewpager.MyIndexviewpager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;
import util.UpdateAppUtils;

import static com.example.npttest.constant.Constant.aliUrl;
import static com.example.npttest.constant.Constant.wxUrl;
import static com.example.npttest.fragment.Fragment1.pvcarTv;
import static com.example.npttest.fragment.Fragment1.shengyTv;

/**
 * Created by liuji on 2017/7/28.
 */

public class IndexActivity extends BaseActivity {
    @Bind(R.id.index_menu_img)
    ImageView indexMenuImg;
    @Bind(R.id.index_search)
    ImageView indexSearch;
    @Bind(R.id.index_top)
    LinearLayout indexTop;
    @Bind(R.id.index_vp)
    MyIndexviewpager indexVp;
    @Bind(R.id.content)
    FrameLayout content;
    @Bind(R.id.index_home_img)
    ImageView indexHomeImg;
    @Bind(R.id.home_tv)
    TextView homeTv;
    @Bind(R.id.index_home_lin)
    LinearLayout indexHomeLin;
    @Bind(R.id.index_record_img)
    ImageView indexRecordImg;
    @Bind(R.id.record_tv)
    TextView recordTv;
    @Bind(R.id.index_record_lin)
    LinearLayout indexRecordLin;
    @Bind(R.id.index_aboutme_img)
    ImageView indexAboutmeImg;
    @Bind(R.id.aboutme_tv)
    TextView aboutmeTv;
    @Bind(R.id.index_aboutme_lin)
    LinearLayout indexAboutmeLin;
    @Bind(R.id.index_shift_lin)
    LinearLayout indexShiftLin;
    @Bind(R.id.index_qrcode)
    ImageView indexQrcode;
    @Bind(R.id.index_username)
    TextView indexUsername;
    @Bind(R.id.index_user)
    LinearLayout indexUser;
    @Bind(R.id.index_tlot)
    TextView indexTlot;
    @Bind(R.id.index_pname)
    TextView indexPname;
    @Bind(R.id.index_addr)
    TextView indexAddr;
    @Bind(R.id.index_user_lin)
    LinearLayout indexUserLin;
    @Bind(R.id.index_propo)
    LinearLayout indexPropo;
    @Bind(R.id.index_scrollView)
    ScrollView indexScrollView;
    @Bind(R.id.index_logout_lin)
    LinearLayout indexLogoutLin;
    @Bind(R.id.index_offwork_lin)
    LinearLayout indexOffworkLin;
    @Bind(R.id.index_out_lin)
    LinearLayout indexOutLin;
    @Bind(R.id.index_weather_lin)
    LinearLayout indexWeatherLin;
    @Bind(R.id.layout_draw_main)
    DrawerLayout layoutDrawMain;
    @Bind(R.id.index_weather_temp)
    TextView indexWeatherTemp;
    @Bind(R.id.index_weather_city)
    TextView indexWeatherCity;
    @Bind(R.id.index_clean)
    LinearLayout indexClean;
    @Bind(R.id.index_update)
    LinearLayout indexUpdate;
    @Bind(R.id.index_help)
    LinearLayout indexHelp;
    @Bind(R.id.index_aboutus)
    LinearLayout indexAboutus;
    @Bind(R.id.index_language)
    LinearLayout indexLanguage;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<Fragment> list = new ArrayList<Fragment>();
    private String pname, tlot, addr, username;
    private Intent intent;
    private ZLoadingDialog dialog;
    public static IndexActivity indexActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        ButterKnife.bind(this);
        indexActivity = this;
        App.serverurl = (String) SPUtils.get(IndexActivity.this, Constant.URL, "");
        Log.e("TAG", "?????????????????????" + App.serverurl + "??????code:" + Constant.CODE);
        car_depot(App.serverurl);
        //get_quantity(App.serverurl);
        indexVp.setOffscreenPageLimit(2);
        /*String jindu = "113.944539";
        String weidu = "22.59016";*/
       /* SimpleDateFormat sDateFormat    =   new    SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String    date    =    sDateFormat.format(new    java.util.Date());*/
        layoutDrawMain.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {

            }

            @Override
            public void onDrawerOpened(View view) {
                view.setClickable(true);
            }

            @Override
            public void onDrawerClosed(View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
        //???????????????????????????
        fragmentManager = getSupportFragmentManager();
        //???????????????????????????list???
        list.add(new Fragment1());
        list.add(new Fragment2());
        list.add(new Fragment3());
        indexVp.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                return list.get(i);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        indexHomeImg.setImageResource(R.mipmap.ic_main_tab_company_pre);
        homeTv.setTextColor(Color.parseColor("#55BEB7"));
        homeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        indexVp.setCurrentItem(0, false);
        /*intent = new Intent(IndexActivity.this, Heartbeat.class);
        startService(intent);*/
        ActivityManager.getInstance().addActivity(this);
    }

    //????????????
    private void getweather(String jindu, String weidu) {

        Log.e("TAG", "??????????????????");
        String url = "http://jisutianqi.market.alicloudapi.com/weather/query";
        String appcode = "1d737752e21e44598c9eb8acf7ae2165";
        OkHttpUtils.get().url(url)
                .addHeader("Authorization", "APPCODE " + appcode)
                .addParams("location", weidu + "," + jindu)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("TAG", "??????????????????" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG", "???????????????????????????" + response);
                try {
                    JSONObject rjsonObject = new JSONObject(response);
                    String msg = rjsonObject.getString("msg");
                    Log.e("TAG", msg);
                    JSONObject resultjsonObject = rjsonObject.getJSONObject("result");
                    String city = resultjsonObject.getString("city");
                    String weather = resultjsonObject.getString("weather");
                    String temp = resultjsonObject.getString("temp");
                    String temphigh = resultjsonObject.getString("temphigh");
                    String templow = resultjsonObject.getString("templow");
                    String winddirect = resultjsonObject.getString("winddirect");//???
                    String windpower = resultjsonObject.getString("windpower");//??????
                    String humidity = resultjsonObject.getString("humidity");//??????
                    String pressure = resultjsonObject.getString("pressure");//??????
                    String date = resultjsonObject.getString("date");
                    JSONObject aqijsonObject = resultjsonObject.getJSONObject("aqi");
                    String quality = aqijsonObject.getString("quality");//????????????
                    String pm25 = aqijsonObject.getString("pm2_5");
                    JSONArray dailyjsonarray = resultjsonObject.getJSONArray("daily");
                    Log.e("TAG", "?????????" + list.size());
                    indexWeatherTemp.setText(temp + "??");
                    indexWeatherCity.setText(city);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("TAG", "????????????????????????" + e);
                }

            }
        });
    }


    //??????????????????
    @OnClick({R.id.index_menu_img, R.id.index_home_img, R.id.index_record_img,
            R.id.index_aboutme_img, R.id.index_home_lin, R.id.index_record_lin,
            R.id.index_aboutme_lin, R.id.index_shift_lin,
            R.id.index_logout_lin, R.id.index_offwork_lin, R.id.index_weather_lin, R.id.index_user,
            R.id.index_search, R.id.index_propo, R.id.index_qrcode, R.id.index_clean, R.id.index_update,
            R.id.index_help, R.id.index_aboutus,R.id.index_language})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.index_menu_img:
                layoutDrawMain.openDrawer(Gravity.START);
                break;
            case R.id.index_home_img:
                initTab();
                indexHomeImg.setImageResource(R.mipmap.ic_main_tab_company_pre);
                homeTv.setTextColor(Color.parseColor("#55BEB7"));
                homeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                //indexTop.setVisibility(View.VISIBLE);
                indexVp.setCurrentItem(0, false);
                break;
            case R.id.index_record_img:
                initTab();
                indexRecordImg.setImageResource(R.mipmap.ic_main_tab_contacts_pre);
                recordTv.setTextColor(Color.parseColor("#55BEB7"));
                recordTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                //indexTop.setVisibility(View.GONE);
                indexVp.setCurrentItem(1, false);
                break;
            case R.id.index_aboutme_img:
                initTab();
                indexAboutmeImg.setImageResource(R.mipmap.ic_main_tab_find_pre);
                aboutmeTv.setTextColor(Color.parseColor("#55BEB7"));
                aboutmeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                indexVp.setCurrentItem(2, false);
                break;
            case R.id.index_home_lin:
                initTab();
                indexHomeImg.setImageResource(R.mipmap.ic_main_tab_company_pre);
                homeTv.setTextColor(Color.parseColor("#55BEB7"));
                homeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                indexVp.setCurrentItem(0, false);
                break;
            case R.id.index_record_lin:
                initTab();
                indexRecordImg.setImageResource(R.mipmap.ic_main_tab_contacts_pre);
                recordTv.setTextColor(Color.parseColor("#55BEB7"));
                recordTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                //indexTop.setVisibility(View.GONE);
                indexVp.setCurrentItem(1, false);
                break;
            case R.id.index_aboutme_lin:
                initTab();
                indexAboutmeImg.setImageResource(R.mipmap.ic_main_tab_find_pre);
                aboutmeTv.setTextColor(Color.parseColor("#55BEB7"));
                aboutmeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                indexVp.setCurrentItem(2, false);
                break;
            case R.id.index_shift_lin:
                //Toasty.info(IndexActivity.this, "??????????????????", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.index_logout_lin:
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(IndexActivity.this);
                normalDialog.setTitle(getString(R.string.reminder));
                normalDialog.setMessage(getString(R.string.are_you_sure_to_quit));
                normalDialog.setPositiveButton(getString(R.string.submit),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                                ActivityManager.getInstance().exit();
                                SPUtils.remove(IndexActivity.this, Constant.ID);
                                SPUtils.remove(IndexActivity.this, Constant.PASS);
                            }
                        });
                normalDialog.setNegativeButton(getString(R.string.cancel), null);
                // ??????
                normalDialog.show();
                break;
            case R.id.index_offwork_lin:
                String wmon = String.format("%.2f", App.wmon / 100);
                if (Constant.domeLoginBoo) {
                    AlertDialog.Builder offDialog = new AlertDialog.Builder(IndexActivity.this);
                    offDialog.setTitle(getString(R.string.reminder));
                    offDialog.setMessage(getString(R.string.check_the_total_fee_before_work) + wmon + getString(R.string.to_get_off_work));
                    offDialog.setPositiveButton(getString(R.string.submit),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //...To-do
                                    String jsons = "{\"cmd\":\"150\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\",\"dsv\":\"" + Constant.DSV + "\"," +
                                            "\"ltype\":\"0\",\"user\":\"" + Constant.testusername + "\",\"pass\":\"" + MD5Utils.encode(Constant.testuserpwd) + "\"," +
                                            "\"emon\":\"0\",\"sign\":\"abcd\"}";
                                    offWork(Constant.DOMEURL, jsons);
                                }
                            });
                    offDialog.setNegativeButton(getString(R.string.cancel), null);
                    // ??????
                    offDialog.show();
                } else {
                    startActivity(new Intent(IndexActivity.this, OffworkActivity.class));
                }
                break;
            case R.id.index_weather_lin:
                startActivity(new Intent(IndexActivity.this, WeatherActivity.class));
                break;
            case R.id.index_user:
                //startActivity(new Intent(IndexActivity.this, UserInformation.class));
                break;
            case R.id.index_search:
                startActivity(new Intent(IndexActivity.this, QueryActivity.class));
                break;
            case R.id.index_propo:
                startActivity(new Intent(IndexActivity.this, Proposal.class));
                break;
            case R.id.index_qrcode:
                startActivity(new Intent(IndexActivity.this, QrCodeActivity.class));
                break;
            case R.id.index_clean:
                String compressfileSize = FileSizeUtil.getAutoFileOrFilesSize(Constant.compressPath);
                String nocompressPathfileSize = FileSizeUtil.getAutoFileOrFilesSize(Constant.nocompressPath);
                Log.e("TAG", "???????????????????????????" + nocompressPathfileSize);
                DataCleanManager.cleanCustomCache(Constant.compressPath);
                DataCleanManager.cleanCustomCache(Constant.nocompressPath);
                Log.e("TAG", Constant.compressPath);
                Log.e("TAG", Constant.nocompressPath);
                Toasty.info(IndexActivity.this, getString(R.string.clean_up_successfully) + nocompressPathfileSize + getString(R.string.cache), Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.index_update:
                //Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
                //Toasty.info(IndexActivity.this,"??????????????????",Toast.LENGTH_SHORT,true).show();
                UpdateAppUtils.from(IndexActivity.this)
                        .checkBy(UpdateAppUtils.CHECK_BY_VERSION_CODE) //??????????????????????????????VersionCode
                        .serverVersionCode(2)
                        .serverVersionName("2.0")
                        .apkPath("http://d.parkos.cn/d/ppm")
                        .downloadBy(UpdateAppUtils.DOWNLOAD_BY_APP) //???????????????app???????????????????????????????????????app??????
                        .isForce(false) //???????????????????????????false ?????????????????????????????????????????????????????????app
                        .update();
                break;
            case R.id.index_help:
                //Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(IndexActivity.this, UseHelp.class));
                break;
            case R.id.index_aboutus:
                //Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(IndexActivity.this, AboutUs.class));
                break;
            case R.id.index_language:
                startActivity(new Intent(IndexActivity.this, ChoiceOfLanguage.class));
                break;
        }
    }

    //??????
    private void offWork(String url, String jsons) {
        dialog = new ZLoadingDialog(IndexActivity.this);
        dialog.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();

        Log.e("TAG", "??????????????????" + jsons);
        OkHttpUtils.postString().url(url)
                .content(jsons)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(IndexActivity.this, getString(R.string.please_check_the_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                dialog.dismiss();
                Log.e("TAG", "???????????????????????????" + response);
                JSONObject rpjson = null;
                try {
                    rpjson = new JSONObject(response);
                    JSONObject resultjson = rpjson.getJSONObject("result");
                    JSONObject datajson = resultjson.getJSONObject("data");
                    int lrs = datajson.getInt("lrs");
                    if (lrs == 0) {
                        //SPUtils.clear(OffworkActivity.this);
                        SPUtils.put(IndexActivity.this, "domeloginboo", false);
                        Constant.domeLoginBoo = false;
                        //dialog.dismiss();
                        Toasty.success(IndexActivity.this, getString(R.string.get_off_work_success), Toast.LENGTH_SHORT, true).show();
                        startActivity(new Intent(IndexActivity.this, LoginActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //?????????????????????
    private void initTab() {
        indexHomeImg.setImageResource(R.mipmap.ic_main_tab_company_nor);
        homeTv.setTextColor(Color.parseColor("#8c8c8c"));
        homeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        indexRecordImg.setImageResource(R.mipmap.ic_main_tab_contacts_nor);
        recordTv.setTextColor(Color.parseColor("#8c8c8c"));
        recordTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        indexAboutmeImg.setImageResource(R.mipmap.ic_main_tab_find_nor);
        aboutmeTv.setTextColor(Color.parseColor("#8c8c8c"));
        aboutmeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isMainActivityDestroy", true);
    }


    //?????????????????????
    private void car_depot(String url) {
        String jsons = "{\"cmd\":\"153\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\",\"dsv\":\"" + Constant.DSV + "\",\"spare\":\"\",\"sign\":\"abcd\"}";
        Log.e("TAG", "??????????????????????????????" + jsons);
        OkHttpUtils.postString().url(url)
                .content(jsons)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //Toasty.error(IndexActivity.this, "???????????????", Toast.LENGTH_SHORT, true).show();
                //url_onError();
            }

            @Override
            public void onResponse(String response, int id) {
                //Toasty.success(SpalshActivity.this, "????????????", Toast.LENGTH_SHORT, true).show();
                Log.e("TAG", "????????????????????????????????????" + response);
                try {
                    JSONObject rpjson = new JSONObject(response);
                    String reasonjson = rpjson.getString("reason");
                    JSONObject resultjson = rpjson.getJSONObject("result");
                    JSONObject datajson = resultjson.getJSONObject("data");
                    Log.e("TAG", "?????????????????????????????????data???" + datajson.toString());
                    if (reasonjson.equals("????????????")) {
                        Log.e("TAG", "???????????????????????????");
                        pname = datajson.getString("pname");
                        addr = datajson.getString("addr");
                        tlot = datajson.getString("tlot");
                        wxUrl = datajson.getString("wxUrl");
                        aliUrl = datajson.getString("aliUrl");
                        Constant.lat = datajson.getString("lat");
                        Constant.lng = datajson.getString("lng");
                        Constant.pname = pname;
                        indexPname.setText(getString(R.string.parking_lot) + pname);
                        indexTlot.setText(getString(R.string.total_parking) + tlot);//??????????????????
                        indexAddr.setText(getString(R.string.address_information) + addr);
                        Constant.adds = addr;
                        Log.e("TAG", "?????????????????????" + wxUrl);
                        Log.e("TAG", "????????????????????????" + aliUrl);

                        getweather(Constant.lng, Constant.lat);
                    } else {
                        Log.e("TAG", "???????????????????????????");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //??????????????????????????????????????????
    private void get_quantity(String url) {
        String jsons = "{\"cmd\":\"176\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\",\"dsv\":\"" + Constant.DSV + "\",\"sign\":\"abcd\"}";
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
                    int code = rpjson.getInt("code");
                    // Log.e("TAG", datajsonObject.toString());
                    if (code == 100) {
                        JSONObject datajsonObject = resultjson.getJSONObject("data");
                        int elot = datajsonObject.getInt("elot");
                        int number = datajsonObject.getInt("number");
                        int tlot = datajsonObject.getInt("tlot");
                        if (shengyTv != null) {
                            shengyTv.setText(String.valueOf(elot + tlot));
                        }
                        //App.surpluscar= String.valueOf(elot+tlot);
                        if (pvcarTv != null) {
                            pvcarTv.setText(String.valueOf(number));
                        }
                    } else {
                        Log.e("TAG", "????????????");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {

        Log.e("TAG", "IndexOnResume");
        username = (String) SPUtils.get(IndexActivity.this, Constant.USERNAME, "");
        indexUsername.setText(username);
        Constant.username = username;
        //heartbeat();
        //??????vp??????
        indexVp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG", "IndexOnStart");
        App.serverurl = (String) SPUtils.get(IndexActivity.this, Constant.URL, "");
        Log.e("TAG", "IndexOnStart###????????????????????????" + App.serverurl);
    }

    @Override
    protected void onDestroy() {
        //stopService(new Intent(IndexActivity.this,Heartbeat.class));
        super.onDestroy();
        //Heartbeat.aBoolean = false;
        //stopService(intent);
    }

}

 /*indexVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                initTab();
                switch (i) {
                    case 0:
                        indexHomeImg.setImageResource(R.mipmap.home_blue);
                        homeTv.setTextColor(Color.parseColor("#1CB9F0"));
                        homeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        //indexTop.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        indexRecordImg.setImageResource(R.mipmap.record_blue);
                        recordTv.setTextColor(Color.parseColor("#1CB9F0"));
                        recordTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        //indexTop.setVisibility(View.GONE);
                        break;
                    case 2:
                        indexAboutmeImg.setImageResource(R.mipmap.setup_blue);
                        aboutmeTv.setTextColor(Color.parseColor("#1CB9F0"));
                        aboutmeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        //indexTop.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });*/