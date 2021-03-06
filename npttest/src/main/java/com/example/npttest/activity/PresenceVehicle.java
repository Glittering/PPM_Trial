package com.example.npttest.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.adapter.PresenceVehicleAdapter;
import com.example.npttest.constant.Constant;
import com.example.npttest.entity.MyJson;
import com.example.npttest.entity.Prese;
import com.example.npttest.view.CustomLoadMoreView;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
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
import okhttp3.Call;
import okhttp3.MediaType;

public class PresenceVehicle extends NoStatusbarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int PAGE_SIZE = 6;
    private static final int TOTAL_COUNTER = 100;
    private String carnum;
    private int cartype;
    private int cdtp;
    private String comfirmYy;
    private int ctype;
    private int delayMillis = 1000;
    private ZLoadingDialog dialog;
    private ZLoadingDialog dialog1;
    private Gson gson = new Gson();
    private int index = 0;
    private boolean isErr = false;
    private String jfType;
    private int mCurrentCounter = 0;
    private boolean mLoadMoreEndGone = false;
    SynthesizerListener mSynListener;
    private MyJson myJson = new MyJson();
    @Bind({R.id.prece_return})
    ImageView preceReturn;
    @Bind({R.id.prece_rv_list})
    RecyclerView preceRvList;
    @Bind({R.id.prece_search})
    ImageView preceSearch;
    @Bind({R.id.prece_swipeLayout})
    SwipeRefreshLayout preceSwipeLayout;
    private int preson;
    private int ptype;
    private int rstat;
    private String sid;
    private PresenceVehicleAdapter vehicleAdapter;
    private List<Prese> list = new ArrayList();
    private int mdposition,coutposition;
    private View notDataView;
    private boolean noData = false;

    protected void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.presence_vehicle);
        ButterKnife.bind(this);
        SpeechUtility.createUtility(this, "appid=59df2c0c");
        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) preceRvList.getParent(), false);
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        initAdapter();
        preceSwipeLayout.setOnRefreshListener(this);
        preceSwipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        preceRvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (!isFinishing()) {
            dialog = new ZLoadingDialog(PresenceVehicle.this);
            dialog.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                    .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                    .setHintText("Loading...")
                    .setHintTextColor(Color.parseColor("#55BEB7"))
                    .setHintTextSize(16) // ?????????????????? dp
                    .setHintTextColor(Color.GRAY) // ??????????????????
                    //.setCanceledOnTouchOutside(false)
                    .show();
        }
        getData();
    }
    //????????????
    private void carout(String url) {
        dialog1 = new ZLoadingDialog(PresenceVehicle.this);
        dialog1.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();
        //{"cmd":"140","type":"2","code":"17083B3DE","dsv":"110","ptype":"0","io":"0",
        // "num":"???B1FL39","ctype":"2","spare":"0","sign":"abcd"}
        /*String intocar_jS = "{\"cmd\":\"140\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"ptype\":\"0\",\"io\":\"1\",\"num\":\"" + carnum + "\"," +
                "\"ctype\":\"" + cartype + "\",\"spare\":\"0\",\"sign\":\"abcd\"}";*/

        String intocar_jS ="{\"cmd\":\"140\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+ Constant.CODE+"\",\"dsv\":\""
                + Constant.DSV+"\",\"ptype\":\"6\",\"io\":\"1\",\"num\":\""+sid+"\",\"ctype\":\""+cartype+
                "\",\"muna\":\"1\",\"spare\":\"0\",\"sign\":\"abcd\"}";

        Log.e("TAG", intocar_jS);
        OkHttpUtils.postString().url(url)
                .content(intocar_jS)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("TAG", "?????????");
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG", response);
                dialog1.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String reasonjson = jsonObject.getString("reason");//????????????
                    JSONObject resultjson = jsonObject.getJSONObject("result");
                    JSONObject datajson = resultjson.getJSONObject("data");
                    String carnum1 = datajson.getString("num");//?????????
                    rstat = datajson.getInt("rstat");//????????????
                    //rstat=2;
                    ptype = datajson.getInt("ptype");//??????????????????????????????
                    ctype = datajson.getInt("ctype");//????????????
                    cdtp = datajson.getInt("cdtp");//???????????????????????????
                    long ctime = datajson.getLong("ctime");//????????????
                    preson = datajson.getInt("preson");//??????
                    long itime = datajson.getLong("itime");//????????????
                    double nmon = datajson.getDouble("nmon");//????????????
                    double rmon = datajson.getDouble("rmon");//????????????
                    double smon = datajson.getDouble("smon");//????????????
                    String sid = datajson.getString("sid");
                    //??????
                    jfjudge();
                    if (rstat == 0) {
                        carout_start_voice();
                        //????????????
                        Intent zdintent = new Intent(PresenceVehicle.this, CaroutSuccessful.class);
                        zdintent.putExtra("carnum", carnum);
                        zdintent.putExtra("jfType", jfType);
                        zdintent.putExtra("ctype", ctype);
                        zdintent.putExtra("ctime", ctime);
                        zdintent.putExtra("itime", itime);
                        zdintent.putExtra("pvrefresh", true);
                        startActivity(zdintent);
                        //Toast.makeText(InputCarnum.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else if (rstat == 1) {
                        //????????????
                        confirmjudge();
                        Intent qrintent = new Intent(PresenceVehicle.this, CaroutConfirmPass.class);
                        qrintent.putExtra("carnum", carnum);
                        qrintent.putExtra("jfType", jfType);
                        qrintent.putExtra("ctype", ctype);
                        qrintent.putExtra("itime", itime);
                        qrintent.putExtra("ctime", ctime);
                        qrintent.putExtra("comfirmYy", comfirmYy);
                        qrintent.putExtra("sid", sid);
                        qrintent.putExtra("cdtp", cdtp);
                        qrintent.putExtra("pvrefresh", true);
                        startActivity(qrintent);

                    } else if (rstat == 2) {
                        //????????????
                        chargejudge();
                        Intent sfintent = new Intent(PresenceVehicle.this, CaroutChargeActivity.class);
                        sfintent.putExtra("carnum", carnum);
                        sfintent.putExtra("jfType", jfType);
                        sfintent.putExtra("ctype", ctype);
                        sfintent.putExtra("itime", itime);
                        sfintent.putExtra("ctime", ctime);
                        sfintent.putExtra("comfirmYy", comfirmYy);
                        sfintent.putExtra("nmon", nmon);
                        sfintent.putExtra("rmon", rmon);
                        sfintent.putExtra("smon", smon);
                        sfintent.putExtra("sid", sid);
                        sfintent.putExtra("cdtp", cdtp);
                        sfintent.putExtra("pvrefresh", true);
                        startActivity(sfintent);
                    } else if (rstat == 3) {
                        //????????????
                        prohibitjudge();
                        Intent jzintent = new Intent(PresenceVehicle.this, ProhibitPass.class);
                        jzintent.putExtra("carnum", carnum);
                        jzintent.putExtra("jfType", jfType);
                        jzintent.putExtra("ctype", ctype);
                        jzintent.putExtra("comfirmYy", comfirmYy);
                        startActivity(jzintent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void carout_start_voice() {
        //1.??????SpeechSynthesizer??????, ????????????????????????????????????InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.??????????????????????????????????????????MSC API??????(Android)???SpeechSynthesizer ???
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//???????????????
        mTts.setParameter(SpeechConstant.SPEED, "60");//????????????
        mTts.setParameter(SpeechConstant.VOLUME, "100");//?????????????????????0~100
        //mTts.setParameter(SpeechConstant.PITCH, "50");// ????????????
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //????????????

        //???????????????????????????????????????????????????????????????????????????./sdcard/iflytek.pcm???
        //?????????SD????????????AndroidManifest.xml?????????SD?????????
        //??????????????????????????????????????????????????????
        // mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");

        //3.????????????
        char[] carnumber = carnum.toCharArray();
        if (carnumber.length == 7) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + " " + "???????????????", mSynListener);
        } else if (carnumber.length == 8) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + " " + String.valueOf(carnumber[7]) + " " + "???????????????", mSynListener);
        }

        //???????????????
        mSynListener = new SynthesizerListener() {
            //?????????????????????????????????????????????error???null
            public void onCompleted(SpeechError error) {
                System.out.println("error--------" + error);
            }

            //??????????????????
            //percent???????????????0~100???beginPos??????????????????????????????????????????endPos?????????????????????????????????????????????info??????????????????
            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            }

            //????????????
            public void onSpeakBegin() {
                System.out.println("????????????");
            }

            //????????????
            public void onSpeakPaused() {
            }

            //??????????????????
            //percent???????????????0~100,beginPos??????????????????????????????????????????endPos??????????????????????????????????????????.
            public void onSpeakProgress(int percent, int beginPos, int endPos) {
            }

            //????????????????????????
            public void onSpeakResumed() {
            }

            //????????????????????????
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            }

        };
    }

    private void getData(){
        OkHttpUtils
                .postString()
                .url(App.serverurl)
                .content(gson.toJson(myJson))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        vehicleAdapter.loadMoreEnd(false);
                    }

                    @Override
                    public void onResponse(String paramAnonymousString, int paramAnonymousInt) {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        Log.e("TAG", gson.toJson(PresenceVehicle.this.myJson));
                        try {
                            JSONObject JSONObject = new JSONObject(paramAnonymousString);
                            ArrayList arrayList = new ArrayList();
                            if (!(JSONObject.getJSONObject("result").getJSONObject("data").getString("inners")).equals("null")){
                                JSONArray JSONArray = JSONObject.getJSONObject("result").getJSONObject("data").getJSONArray("inners");
                                //??????????????????????????????
                                for (int i=0;i<JSONArray.length();i++) {
                                    arrayList.add(gson.fromJson(JSONArray.get(i).toString(), Prese.class));
                                }
                            }
                            if (arrayList.size() > 0) {
                                vehicleAdapter.addData(arrayList);
                                vehicleAdapter.loadMoreComplete();
                                return;
                            }else {
                                vehicleAdapter.setEmptyView(notDataView);
                            }
                            vehicleAdapter.loadMoreEnd(false);
                            return;
                        } catch (JSONException localJSONException) {
                            localJSONException.printStackTrace();
                            vehicleAdapter.loadMoreEnd(false);
                            Log.e("TAG","??????????????????"+localJSONException);
                        }
                    }
                });
    }

    private void getData1(){
        OkHttpUtils
                .postString()
                .url(App.serverurl)
                .content(gson.toJson(myJson))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        vehicleAdapter.loadMoreEnd(false);
                    }

                    @Override
                    public void onResponse(String paramAnonymousString, int paramAnonymousInt) {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        Log.e("TAG", gson.toJson(PresenceVehicle.this.myJson));
                        try {
                            JSONObject JSONObject = new JSONObject(paramAnonymousString);
                            ArrayList arrayList = new ArrayList();
                            JSONArray JSONArray = JSONObject.getJSONObject("result").getJSONObject("data").getJSONArray("inners");
                            //??????????????????????????????
                            for (int i=0;i<JSONArray.length();i++) {
                                arrayList.add(gson.fromJson(JSONArray.get(i).toString(), Prese.class));
                            }
                            if (arrayList.size() > 0) {
                                vehicleAdapter.addData(arrayList);
                                vehicleAdapter.loadMoreComplete();
                                return;
                            }
                            vehicleAdapter.loadMoreEnd(false);
                            return;
                        } catch (JSONException localJSONException) {
                            localJSONException.printStackTrace();
                            vehicleAdapter.loadMoreEnd(false);
                        }
                    }
                });
    }

    public void onRefresh() {
        Log.e("TAG", "??????***"+App.zcRefresh);
        vehicleAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //arrayList.clear();

                /*list.clear();
                myJson.setIndex(0);
                getData();
                if (!App.zcRefresh){
                    vehicleAdapter.notifyDataSetChanged();//?????????????????????
                    App.zcRefresh=false;
                }else {
                    App.zcRefresh=false;
                }*/

                if (!App.zcRefresh){
                    Log.e("TAG","????????????");
                    list.clear();
                    myJson.setIndex(0);
                    getData();
                    vehicleAdapter.notifyDataSetChanged();//?????????????????????
                    App.zcRefresh=false;
                }else {
                    if (App.mdRefresh){
                        Log.e("TAG","??????????????????***item+"+mdposition+"??????????????????"+ModifyCarnum.mdcarnum);
                        App.mdRefresh=false;
                        vehicleAdapter.getData().get(mdposition).setPnum(ModifyCarnum.mdcarnum);
                        vehicleAdapter.notifyDataSetChanged();
                        App.zcRefresh=false;
                    }else {
                        Log.e("TAG","??????????????????***item+"+coutposition);
                        App.zcRefresh=false;
                        list.remove(coutposition);
                        vehicleAdapter.notifyItemRemoved(coutposition);
                        //vehicleAdapter.notifyDataSetChanged();
                    }
                }

                //vehicleAdapter.notifyItemRemoved(2);
                //vehicleAdapter.notifyItemChanged(2);


                //PresenceVehicle.access$2302(PresenceVehicle.this, 6);
                if (preceSwipeLayout != null) {
                    preceSwipeLayout.setRefreshing(false);
                }
                vehicleAdapter.setEnableLoadMore(true);
            }
        }, this.delayMillis);
    }

    private void initAdapter() {
        vehicleAdapter = new PresenceVehicleAdapter(list);
        vehicleAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            public void onLoadMoreRequested() {
                myJson.setIndex(myJson.getIndex()+1);
                getData();
            }
        }, preceRvList);
        vehicleAdapter.setLoadMoreView(new CustomLoadMoreView());
        preceRvList.setAdapter(vehicleAdapter);
        preceRvList.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //Toast.makeText(PresenceVehicle.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                Intent carinfointent = new Intent(PresenceVehicle.this, PresenceVehicleInfo.class);
                carinfointent.putExtra("carnum", vehicleAdapter.getData().get(position).getPnum());
                carinfointent.putExtra("cartype", vehicleAdapter.getData().get(position).getCtype());
                carinfointent.putExtra("pztype", vehicleAdapter.getData().get(position).getCdtp());
                carinfointent.putExtra("itime", vehicleAdapter.getData().get(position).getItime());
                carinfointent.putExtra("sid", vehicleAdapter.getData().get(position).getSid());
                startActivity(carinfointent);
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(PresenceVehicle.this, Integer.toString(position) + "long", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.prece_modify:
                        //Toast.makeText(PresenceVehicle.this, "??????", Toast.LENGTH_SHORT).show();
                        Intent modiftintent = new Intent(PresenceVehicle.this, ModifyCarnum.class);
                        modiftintent.putExtra("number", vehicleAdapter.getData().get(position).getPnum());
                        modiftintent.putExtra("cartype", vehicleAdapter.getData().get(position).getCtype());
                        modiftintent.putExtra("pztype", vehicleAdapter.getData().get(position).getCdtp());
                        modiftintent.putExtra("pvrefresh", true);
                        modiftintent.putExtra("sid", vehicleAdapter.getData().get(position).getSid());
                        modiftintent.putExtra("position", position);
                        startActivity(modiftintent);
                        mdposition=position;
                        break;
                    case R.id.prece_outcar:
                        carnum = vehicleAdapter.getData().get(position).getPnum();
                        cartype = vehicleAdapter.getData().get(position).getCtype();
                        sid=vehicleAdapter.getData().get(position).getSid();
                        carout(App.serverurl);
                        coutposition=position;
                        break;
                    case R.id.prece_img:
                        final AlertDialog dialog = new AlertDialog.Builder(PresenceVehicle.this).create();
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                        ImageView imageView = new ImageView(PresenceVehicle.this);
                        Glide.with(PresenceVehicle.this).load(vehicleAdapter.getData().get(position).getIurl())
                                .centerCrop()
                                .placeholder(R.mipmap.carnum_default)//?????????
                                .error(R.mipmap.carnum_default)//????????????????????????
                                .crossFade().into(imageView);
                        Window window = dialog.getWindow();
                        window.getDecorView().setPadding(0, 0, 0, 0);
                        window.setGravity(Gravity.CENTER);
                        window.setContentView(imageView);
                        WindowManager.LayoutParams lp = window.getAttributes();
                        lp.width = WindowManager.LayoutParams.FILL_PARENT;
                        lp.height = WindowManager.LayoutParams.FILL_PARENT;
                        window.setAttributes(lp);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        break;
                }
            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    private void jfjudge() {
        switch (cdtp) {
            case 1:
                jfType = getString(R.string.VIP_car);
                break;
            case 2:
                jfType = getString(R.string.monthly_ticket_car);
                break;
            case 3:
                jfType = getString(R.string.reserve_car);
                break;
            case 4:
                jfType = getString(R.string.temporary_car);
                break;
            case 5:
                jfType = getString(R.string.free_car);
                break;
            case 6:
                jfType = getString(R.string.parking_pool_car);
                break;
            case 7:
                jfType = getString(R.string.car_rental);
                break;
        }
    }

    private void prohibitjudge() {
        switch (preson) {
            case 0:
                comfirmYy = getString(R.string.blacklist);
                break;
            case 1:
                comfirmYy = getString(R.string.validity_is_not_started);
                break;
            case 2:
                comfirmYy = getString(R.string.Expired);
                break;
            case 3:
                comfirmYy = getString(R.string.repeat_io);
                break;
            case 4:
                comfirmYy = getString(R.string.information_error);
                break;
            case 5:
                comfirmYy = getString(R.string.unauthorized);
                break;
            case 6:
                comfirmYy = getString(R.string.no_information_on_the_present_vehicle);
                break;
            case 7:
                comfirmYy = getString(R.string.passageway_prohibition_of_passage);
                break;
            case 8:
                comfirmYy = getString(R.string.parking_lots_are_not_allowed_to_pass);
                break;
            case 9:
                comfirmYy = getString(R.string.full_seat_no_entry);
                break;
            case 10:
                comfirmYy = getString(R.string.invalid_request);
                break;
        }
    }

    private void chargejudge() {
        switch (preson) {
             case 0:
                comfirmYy = getString(R.string.temporary_car);
                break;
            case 1:
                comfirmYy = getString(R.string.storage_car);
                break;
            case 2:
                comfirmYy = getString(R.string.time_car);
                break;
            case 3:
                comfirmYy = getString(R.string.no_term_of_validity);
                break;
            case 4:
                comfirmYy = getString(R.string.expired);
                break;
            case 5:
                comfirmYy = getString(R.string.insufficient_balance);
                break;
            case 6:
                comfirmYy = getString(R.string.parking_lot_full);
                break;
            case 7:
                comfirmYy = getString(R.string.garage_not_authorized);
                break;
            case 8:
                comfirmYy = getString(R.string.sublibrary_unauthorized);
                break;
            case 9:
                comfirmYy = getString(R.string.stop_vehicle);
                break;
            case 10:
                comfirmYy = getString(R.string.disable_vehicles);
                break;
        }
    }

    private void confirmjudge() {
        switch (preson) {
            case 0:
                comfirmYy = getString(R.string.channel_confirmation);
                break;
            case 1:
                comfirmYy = getString(R.string.seat_full_confirmation_release);
                break;
            case 2:
                comfirmYy = getString(R.string.the_seat_pool_is_full_of_confirmation);
                break;
            case 3:
                comfirmYy = getString(R.string.the_period_of_validity_is_not_started);
                break;
            case 4:
                comfirmYy = getString(R.string.expired);
                break;

        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.dialog != null)
            this.dialog.cancel();
    }


    public void onStart() {
        super.onStart();
        if (!App.pvRefresh) {
            Log.e("TAG", "onstart ??????");
            onRefresh();
            App.pvRefresh = true;
        }

        /*if (App.mdRefresh){
            vehicleAdapter.notifyItemChanged(ModifyCarnum.midefyposition);
            Log.e("TAG","?????????item**"+ModifyCarnum.midefyposition);
            App.mdRefresh=false;
        }*/
    }

    @OnClick({R.id.prece_return, R.id.prece_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.prece_return:
                finish();
                break;
            case R.id.prece_search:
                startActivity(new Intent(PresenceVehicle.this, QueryCarnum.class));
                break;
        }
    }
}