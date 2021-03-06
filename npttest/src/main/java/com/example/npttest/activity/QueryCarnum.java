package com.example.npttest.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.adapter.QuerynumAdapter;
import com.example.npttest.camera.CameraActivity;
import com.example.npttest.constant.Constant;
import com.example.npttest.entity.Querynum;
import com.example.npttest.util.LicenseKeyboardUtil_input;
import com.example.npttest.util.SPUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.kyleduo.switchbutton.SwitchButton;
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

import static com.example.npttest.util.LicenseKeyboardUtil_input.currentEditText_input;

/**
 * Created by liuji on 2017/8/12.
 */

public class QueryCarnum extends NoStatusbarActivity implements CompoundButton.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {


    public static final String INPUT_LICENSE_COMPLETE = "me.kevingo.licensekeyboard.input.comp";
    @Bind(R.id.query_return)
    ImageView queryReturn;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.query_camera)
    ImageView queryCamera;
    @Bind(R.id.query_inputbox1)
    EditText queryInputbox1;
    @Bind(R.id.query_inputbox2)
    EditText queryInputbox2;
    @Bind(R.id.query_inputbox3)
    EditText queryInputbox3;
    @Bind(R.id.query_inputbox4)
    EditText queryInputbox4;
    @Bind(R.id.query_inputbox5)
    EditText queryInputbox5;
    @Bind(R.id.query_inputbox6)
    EditText queryInputbox6;
    @Bind(R.id.query_inputbox7)
    EditText queryInputbox7;
    @Bind(R.id.query_inputbox8)
    EditText queryInputbox8;
    @Bind(R.id.query_lin_input)
    LinearLayout queryLinInput;
    @Bind(R.id.query_sbtn)
    SwitchButton querySbtn;
    @Bind(R.id.query_rv_list)
    RecyclerView queryRvList;
    @Bind(R.id.query_swipeLayout)
    SwipeRefreshLayout querySwipeLayout;
    @Bind(R.id.query_btn)
    Button queryBtn;
    @Bind(R.id.keyboard_view)
    KeyboardView keyboardView;
    private LicenseKeyboardUtil_input keyboardUtil;
    private EditText edits[];
    private String stringEdit;
    private String carnumb, cname, cartype;
    private int ctype, cdtp;
    private long ctime;
    private ZLoadingDialog dialog1;
    private String comCity, stringarray;
    private int rstat, ptype, preson;
    private String jfType;//????????????
    private String comfirmYy;//????????????
    private String carnum;
    public static CardView cardView;
    private static final int TOTAL_COUNTER = 0;
    private static final int PAGE_SIZE = 6;
    private int delayMillis = 1000;
    private int mCurrentCounter = 0;
    private boolean isErr;
    private boolean mLoadMoreEndGone = false;
    private QuerynumAdapter querynumAdapter;
    private List<Querynum> list = new ArrayList<>();
    private int mdposition,coutposition;
    SynthesizerListener mSynListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_carnum);
        ButterKnife.bind(this);
        querynumAdapter = new QuerynumAdapter(list);
        querySwipeLayout.setOnRefreshListener(this);
        querynumAdapter.setOnLoadMoreListener(this, queryRvList);
        querySwipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        queryRvList.setLayoutManager(new LinearLayoutManager(this));
        queryRvList.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //Toast.makeText(PresenceVehicle.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                Intent carinfointent = new Intent(QueryCarnum.this, PresenceVehicleInfo.class);
                carinfointent.putExtra("carnum", querynumAdapter.getData().get(position).getPnum());
                carinfointent.putExtra("cartype", querynumAdapter.getData().get(position).getCtype());
                carinfointent.putExtra("pztype", querynumAdapter.getData().get(position).getCdtp());
                carinfointent.putExtra("itime", querynumAdapter.getData().get(position).getItime());
                startActivity(carinfointent);
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(QueryCarnum.this, Integer.toString(position) + "long", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.prece_modify:
                        //Toast.makeText(PresenceVehicle.this, "??????", Toast.LENGTH_SHORT).show();
                        Intent modiftintent = new Intent(QueryCarnum.this, ModifyCarnum.class);
                        modiftintent.putExtra("number", querynumAdapter.getData().get(position).getPnum());
                        modiftintent.putExtra("cartype", querynumAdapter.getData().get(position).getCtype());
                        modiftintent.putExtra("pztype", querynumAdapter.getData().get(position).getCdtp());
                        modiftintent.putExtra("pvrefresh", true);
                        modiftintent.putExtra("sid", querynumAdapter.getData().get(position).getSid());
                        startActivity(modiftintent);
                        mdposition=position;
                        break;
                    case R.id.prece_outcar:
                        carnum = querynumAdapter.getData().get(position).getPnum();
                        ctype = querynumAdapter.getData().get(position).getCtype();
                        //cartype=querynumAdapter.getData().get(position).getCtype();
                        carout(App.serverurl);
                        coutposition=position;
                        break;
                    case R.id.prece_img:
                        final AlertDialog dialog = new AlertDialog.Builder(QueryCarnum.this).create();
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                        ImageView imageView = new ImageView(QueryCarnum.this);
                        Glide.with(QueryCarnum.this).load(querynumAdapter.getData().get(position).getIurl())
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
        edits = new EditText[]{queryInputbox1, queryInputbox2, queryInputbox3,
                queryInputbox4, queryInputbox5, queryInputbox6,
                queryInputbox7, queryInputbox8};

        //????????????????????????intent?????????
        IntentFilter finishFilter = new IntentFilter(INPUT_LICENSE_COMPLETE);
        keyboardUtil = new LicenseKeyboardUtil_input(QueryCarnum.this, edits);

        /*comCity = (String) SPUtils.get(QueryCarnum.this, Constant.COM_CITY, "");
        char[] chars = comCity.toCharArray();
        if (TextUtils.isEmpty(comCity)) {

        } else {
            queryInputbox1.setText(String.valueOf(chars[0]));
            queryInputbox2.setText(String.valueOf(chars[1]));
        }*/
        Boolean aBoolean = (Boolean) SPUtils.get(QueryCarnum.this, "open_new_car", false);
        querySbtn.setCheckedImmediately(aBoolean);
        querySbtn.setOnCheckedChangeListener(this);
        if (aBoolean) {
            queryInputbox8.setVisibility(View.VISIBLE);
            LicenseKeyboardUtil_input.etsize = 7;
        } else {
            queryInputbox8.setVisibility(View.GONE);
            LicenseKeyboardUtil_input.etsize = 6;
        }

    }

    @Override
    protected void onStart() {
        if (!App.pvRefresh) {
            Log.e("TAG", "onstart ??????");
            onRefresh();
            App.pvRefresh = true;
        }
        super.onStart();
    }

    private void initcolor1() {
        edits[0].setBackgroundResource(R.drawable.keyboard_bg_white);
        edits[1].setBackgroundResource(R.drawable.keyboard_bg_white);
        edits[2].setBackgroundResource(R.drawable.keyboard_bg_white);
        edits[3].setBackgroundResource(R.drawable.keyboard_bg_white);
        edits[4].setBackgroundResource(R.drawable.keyboard_bg_white);
        edits[5].setBackgroundResource(R.drawable.keyboard_bg_white);
        edits[6].setBackgroundResource(R.drawable.keyboard_bg_white);
        edits[7].setBackgroundResource(R.drawable.keyboard_bg_white);
    }

    @OnClick({R.id.query_return, R.id.query_inputbox1, R.id.query_inputbox2, R.id.query_inputbox3,
            R.id.query_inputbox4, R.id.query_inputbox5, R.id.query_inputbox6, R.id.query_inputbox7,
            R.id.query_inputbox8, R.id.query_btn, R.id.query_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.query_return:
                finish();
                break;
            case R.id.query_inputbox1:
                currentEditText_input = 0;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.query_inputbox2:
                currentEditText_input = 1;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.query_inputbox3:
                currentEditText_input = 2;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.query_inputbox4:
                currentEditText_input = 3;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.query_inputbox5:
                currentEditText_input = 4;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.query_inputbox6:
                currentEditText_input = 5;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.query_inputbox7:
                currentEditText_input = 6;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.query_inputbox8:
                currentEditText_input = 7;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.query_btn:
                stringarray = queryInputbox1.getText().toString().trim() +
                        queryInputbox2.getText().toString().trim() +
                        queryInputbox3.getText().toString().trim() +
                        queryInputbox4.getText().toString().trim() +
                        queryInputbox5.getText().toString().trim() +
                        queryInputbox6.getText().toString().trim() +
                        queryInputbox7.getText().toString().trim() +
                        queryInputbox8.getText().toString().trim();
                char[] carnumber = stringarray.toCharArray();
                if (carnumber.length < 3) {
                    Toasty.error(this, getString(R.string.enter_correct_license_plate_number), Toast.LENGTH_SHORT).show();
                } else {
                    stringEdit = (queryInputbox1.getText().toString() == null || "".equals(queryInputbox1.getText().toString().trim()) ? "*" : queryInputbox1.getText().toString()) +
                            (queryInputbox2.getText().toString() == null || "".equals(queryInputbox2.getText().toString().trim()) ? "*" : queryInputbox2.getText().toString()) +
                            (queryInputbox3.getText().toString() == null || "".equals(queryInputbox3.getText().toString().trim()) ? "*" : queryInputbox3.getText().toString()) +
                            (queryInputbox4.getText().toString() == null || "".equals(queryInputbox4.getText().toString().trim()) ? "*" : queryInputbox4.getText().toString()) +
                            (queryInputbox5.getText().toString() == null || "".equals(queryInputbox5.getText().toString().trim()) ? "*" : queryInputbox5.getText().toString()) +
                            (queryInputbox6.getText().toString() == null || "".equals(queryInputbox6.getText().toString().trim()) ? "*" : queryInputbox6.getText().toString()) +
                            (queryInputbox7.getText().toString() == null || "".equals(queryInputbox7.getText().toString().trim()) ? "*" : queryInputbox7.getText().toString());
                    //Toast.makeText(this, stringEdit, Toast.LENGTH_SHORT).show();
                    if (App.serverurl!=null){
                        list.clear();
                        query_Car(App.serverurl);
                    }

                    //keyboardUtil.hideKeyboard();
                }

                break;
            case R.id.query_camera:
                //jumpVideoRecog();
                Intent intent1 = new Intent(QueryCarnum.this, CameraActivity.class);
                intent1.putExtra("camera", true);
                //startActivity(intent);
                startActivityForResult(intent1, 0x11);
                break;

        }
    }

    //????????????{"cmd":"143","type":"2","code":"abcd","dsv":"110","ptype":"0","num":"???B1FL39","spare":"0","sign":"abcd"}
    public void query_Car(String url) {
        if (!isFinishing()) {
            dialog1 = new ZLoadingDialog(this);
            dialog1.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                    .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                    .setHintText("Loading...")
                    .setHintTextColor(Color.parseColor("#55BEB7"))
                    .setHintTextSize(16) // ?????????????????? dp
                    .setHintTextColor(Color.GRAY)  // ??????????????????
                    .show();
        }
        String queryjs = "{\"cmd\":\"160\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\",\"dsv\":\"" + Constant.DSV + "\",\"num\":\"" + stringarray + "\",\"spare\":\"0\",\"sign\":\"abcd\"}";

        Log.e("TAG", queryjs);
        OkHttpUtils.postString().url(url)
                .content(queryjs)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("TAG", "???????????????");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    dialog1.dismiss();
                    JSONObject rjsonObject = new JSONObject(response);
                    String reasonjson = rjsonObject.getString("reason");
                    JSONObject resultjson = rjsonObject.getJSONObject("result");
                    JSONArray listjsAr = resultjson.getJSONArray("list");
                    if (listjsAr.length() > 0) {
                        for (int i = listjsAr.length() - 1; i >= 0; i--) {
                            JSONObject jsonObject = listjsAr.getJSONObject(i);
                            Querynum querynum = new Querynum();
                            querynum.setPnum(jsonObject.getString("pnum"));
                            querynum.setCtype(jsonObject.getInt("ctype"));
                            querynum.setCdtp(jsonObject.getInt("cdtp"));
                            querynum.setItime(jsonObject.getLong("itime"));
                            querynum.setIurl(jsonObject.getString("iurl"));
                            querynum.setSid(jsonObject.getString("sid"));
                            list.add(querynum);
                        }
                    } else {
                        Toasty.error(QueryCarnum.this, getString(R.string.no_vehicle_was_queried), Toast.LENGTH_SHORT, true).show();
                    }
                    Log.e("TAG", "?????????????????????" + list.size());
                    handler.sendEmptyMessage(0x123);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                if (querynumAdapter != null && queryRvList != null) {
                    querynumAdapter.notifyDataSetChanged();
                    queryRvList.setAdapter(querynumAdapter);
                }
            }
        }
    };

    private void jfjudge() {
        /*switch (ctype) {
            case 1:
                cartype = "?????????";
                break;
            case 2:
                cartype = "?????????";
                break;
            case 3:
                cartype = "?????????";
                break;
            case 4:
                cartype = "?????????";
                break;
            case 5:
                cartype = "?????????";
                break;
            case 6:
                cartype = "?????????";
                break;
        }*/

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

    //???????????????????????????
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        keyboardUtil.hideKeyboard();
        return super.onTouchEvent(event);
    }

    //????????????
    private void carout(String url) {
        dialog1 = new ZLoadingDialog(QueryCarnum.this);
        dialog1.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();
        //{"cmd":"140","type":"2","code":"17083B3DE","dsv":"110","ptype":"0","io":"0",
        // "num":"???B1FL39","ctype":"2","spare":"0","sign":"abcd"}

        String intocar_jS ="{\"cmd\":\"140\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+ Constant.CODE+"\",\"dsv\":\""
                + Constant.DSV+"\",\"ptype\":\"0\",\"io\":\"1\",\"num\":\""+carnum+"\",\"ctype\":\""+ctype+
                "\",\"muna\":\"1\",\"spare\":\"0\",\"sign\":\"abcd\"}";
        Log.e("TAG", intocar_jS);
        OkHttpUtils.postString().url(url)
                .content(intocar_jS)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("TAG", "???????????????");
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG", "??????");
                Log.e("TAG", response);
                dialog1.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String reasonjson = jsonObject.getString("reason");//????????????
                    JSONObject resultjson = jsonObject.getJSONObject("result");
                    JSONObject datajson = resultjson.getJSONObject("data");
                    String carnum = datajson.getString("num");//?????????
                    rstat = datajson.getInt("rstat");//????????????
                    //rstat=2;
                    ptype = datajson.getInt("ptype");//??????????????????????????????
                    ctype = datajson.getInt("ctype");//????????????
                    cdtp = datajson.getInt("cdtp");//???????????????????????????
                    long ctime = datajson.getLong("ctime");//????????????
                    preson = datajson.getInt("preson");//??????
                    long itime = datajson.getLong("itime");//????????????
                    double nmon = datajson.getInt("nmon");//????????????
                    double rmon = datajson.getInt("rmon");//????????????
                    double smon = datajson.getInt("smon");//????????????
                    String sid = datajson.getString("sid");
                    //??????
                    jfjudge();
                    if (rstat == 0) {
                        carout_start_voice();
                        //????????????
                        Intent zdintent = new Intent(QueryCarnum.this, CaroutSuccessful.class);
                        zdintent.putExtra("carnum", carnum);
                        zdintent.putExtra("jfType", jfType);
                        zdintent.putExtra("ctype", ctype);
                        zdintent.putExtra("ctime", ctime);
                        zdintent.putExtra("itime", itime);
                        zdintent.putExtra("pvrefresh", false);
                        startActivity(zdintent);
                        finish();
                        //Toast.makeText(InputCarnum.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else if (rstat == 1) {
                        //????????????
                        confirmjudge();
                        Intent qrintent = new Intent(QueryCarnum.this, CaroutConfirmPass.class);
                        qrintent.putExtra("carnum", carnum);
                        qrintent.putExtra("jfType", jfType);
                        qrintent.putExtra("ctype", ctype);
                        qrintent.putExtra("itime", itime);
                        qrintent.putExtra("ctime", ctime);
                        qrintent.putExtra("comfirmYy", comfirmYy);
                        qrintent.putExtra("sid", sid);
                        qrintent.putExtra("cdtp", cdtp);
                        qrintent.putExtra("pvrefresh", false);
                        startActivity(qrintent);

                    } else if (rstat == 2) {
                        //????????????
                        chargejudge();
                        Intent sfintent = new Intent(QueryCarnum.this, CaroutChargeActivity.class);
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
                        sfintent.putExtra("pvrefresh", false);
                        startActivity(sfintent);
                    } else if (rstat == 3) {
                        //????????????
                        prohibitjudge();
                        Intent jzintent = new Intent(QueryCarnum.this, ProhibitPass.class);
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
        // mTts.startSpeaking(carnum+"???????????????", mSynListener);
        char[] carnumber = carnum.toCharArray();
        if (carnumber.length == 7) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + "???" + "???????????????", mSynListener);
        } else if (carnumber.length == 8) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + " " + String.valueOf(carnumber[7]) + "???" + "???????????????", mSynListener);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        carnumb = data.getStringExtra("number").toString();
        if (carnumb.equals("null")) {

        } else {
            //????????????edit??????
            char[] carnumber = carnumb.toCharArray();
            if (carnumber.length == 7) {
                queryInputbox8.setVisibility(View.GONE);
                queryInputbox1.setText(String.valueOf(carnumber[0]));
                queryInputbox2.setText(String.valueOf(carnumber[1]));
                queryInputbox3.setText(String.valueOf(carnumber[2]));
                queryInputbox4.setText(String.valueOf(carnumber[3]));
                queryInputbox5.setText(String.valueOf(carnumber[4]));
                queryInputbox6.setText(String.valueOf(carnumber[5]));
                queryInputbox7.setText(String.valueOf(carnumber[6]));
            } else if (carnumber.length == 8) {
                queryInputbox1.setText(String.valueOf(carnumber[0]));
                queryInputbox2.setText(String.valueOf(carnumber[1]));
                queryInputbox3.setText(String.valueOf(carnumber[2]));
                queryInputbox4.setText(String.valueOf(carnumber[3]));
                queryInputbox5.setText(String.valueOf(carnumber[4]));
                queryInputbox6.setText(String.valueOf(carnumber[5]));
                queryInputbox7.setText(String.valueOf(carnumber[6]));
                queryInputbox8.setText(String.valueOf(carnumber[7]));
            }

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            SPUtils.put(QueryCarnum.this, "open_new_car", true);
            queryInputbox8.setVisibility(View.VISIBLE);
            LicenseKeyboardUtil_input.etsize = 7;
            if (!TextUtils.isEmpty(queryInputbox7.getText())) {
                currentEditText_input = 7;
                initcolor1();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    edits[currentEditText_input].setBackground(getDrawable(R.drawable.keyboard_bg_red));
                }
            }

        } else {
            SPUtils.put(QueryCarnum.this, "open_new_car", false);
            queryInputbox8.setVisibility(View.GONE);
            LicenseKeyboardUtil_input.etsize = 6;
            if (!TextUtils.isEmpty(queryInputbox6.getText())) {
                currentEditText_input = 6;
                initcolor1();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    edits[currentEditText_input].setBackground(getDrawable(R.drawable.keyboard_bg_red));
                }
            }

        }
    }

    @Override
    public void onRefresh() {
        querynumAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //list.clear();
                //getPreVeh(App.serverurl);
                //vehicleAdapter.setNewData(list);
                /*querynumAdapter.notifyDataSetChanged();
                isErr = false;
                mCurrentCounter = PAGE_SIZE;*/
                if (App.mdRefresh){
                    Log.e("TAG","??????????????????***item+"+mdposition+"??????????????????"+ModifyCarnum.mdcarnum);
                    App.mdRefresh=false;
                    querynumAdapter.getData().get(mdposition).setPnum(ModifyCarnum.mdcarnum);
                    querynumAdapter.notifyDataSetChanged();
                    App.zcRefresh=false;
                }else {
                    Log.e("TAG","??????????????????***item+"+coutposition);
                    App.zcRefresh=false;
                    list.remove(coutposition);
                    querynumAdapter.notifyItemRemoved(coutposition);
                    //vehicleAdapter.notifyDataSetChanged();
                }
                if (querySwipeLayout != null) {
                    querySwipeLayout.setRefreshing(false);
                }
                querynumAdapter.setEnableLoadMore(true);
            }
        }, delayMillis);
    }

    @Override
    public void onLoadMoreRequested() {
        querySwipeLayout.setEnabled(false);
        if (querynumAdapter.getData().size() < PAGE_SIZE) {
            querynumAdapter.loadMoreEnd(true);//????????????????????????
        } else {
            if (mCurrentCounter >= TOTAL_COUNTER) {
                querynumAdapter.loadMoreEnd();//default visible
                //????????????????????????
                querynumAdapter.loadMoreEnd(mLoadMoreEndGone);//true is gone,false is visible
            } else {
                if (isErr) {
                    querynumAdapter.addData(list);
                    mCurrentCounter = querynumAdapter.getData().size();
                    querynumAdapter.loadMoreComplete();
                } else {
                    isErr = true;
                    querynumAdapter.loadMoreFail();
                }
            }
            querySwipeLayout.setEnabled(true);
        }
    }
}
