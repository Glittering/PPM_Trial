package com.example.npttest.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.constant.Constant;
import com.example.npttest.tool.DateTools;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by liuji on 2017/9/25.
 */

public class CarinChargeActivity extends NoStatusbarActivity {


    @Bind(R.id.carin_charge_return)
    ImageView carinChargeReturn;
    @Bind(R.id.carin_charge_nb)
    LinearLayout carinChargeNb;
    @Bind(R.id.carin_charge_ysmon)
    TextView carinChargeYsmon;
    @Bind(R.id.carin_charge_ssmon)
    TextView carinChargeSsmon;
    @Bind(R.id.carin_charge_yhmon)
    TextView carinChargeYhmon;
    @Bind(R.id.charge)
    LinearLayout charge;
    @Bind(R.id.carin_charge_carnum)
    TextView carinChargeCarnum;
    @Bind(R.id.carin_charge_cartype)
    TextView carinChargeCartype;
    @Bind(R.id.carin_charge_pztype)
    TextView carinChargePztype;
    @Bind(R.id.carin_charge_yy)
    TextView carinChargeYy;
    @Bind(R.id.carin_charge_ctime)
    TextView carinChargeCtime;
    @Bind(R.id.carin_charge_cancel)
    Button carinChargeCancel;
    @Bind(R.id.carin_charge_confirm)
    Button carinChargeConfirm;
    @Bind(R.id.carin_charge_free)
    Button carinChargeFree;
    private String carnum, cartype, jfType, comfirmYy, sid, srmon, snmon, ssmon;
    private int ctype, cdtp;
    private double nmom, rmon, smon;
    private long itime;
    private ZLoadingDialog dialog1, dialog;
    SynthesizerListener mSynListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carin_charge);
        ButterKnife.bind(this);
        SpeechUtility.createUtility(CarinChargeActivity.this, SpeechConstant.APPID + "=59df2c0c");
        Intent intent = getIntent();
        carnum = intent.getStringExtra("carnum");
        ctype = intent.getIntExtra("ctype", 0);
        jfType = intent.getStringExtra("jfType");
        itime = intent.getLongExtra("itime", 0);
        comfirmYy = intent.getStringExtra("comfirmYy");
        nmom = (double) intent.getIntExtra("nmon", 0);
        rmon = (double) intent.getIntExtra("rmon", 0);
        smon = (double) intent.getIntExtra("smon", 0);
        sid = intent.getStringExtra("sid");
        cdtp = intent.getIntExtra("cdtp", 0);
        judge();
        carinChargeCarnum.setText(carnum);
        carinChargeCartype.setText(cartype);
        carinChargePztype.setText(jfType);
        carinChargeCtime.setText(DateTools.getDate(itime * 1000) + "");
        carinChargeYy.setText(comfirmYy);
        srmon = String.format("%.2f", rmon / 100);
        snmon = String.format("%.2f", nmom / 100);
        ssmon = String.format("%.2f", smon / 100);
        carinChargeYsmon.setText(snmon);
        carinChargeSsmon.setText(srmon);
        carinChargeYhmon.setText(ssmon);

    }

    private void carin_start_voice() {
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
        //mTts.startSpeaking(carnum+"???????????????????????????"+srmon+"???", mSynListener);
        char[] carnumber = carnum.toCharArray();
        if (carnumber.length == 7) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + "???" + "???????????????????????????" + srmon + "???", mSynListener);
        } else if (carnumber.length == 8) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + " " + String.valueOf(carnumber[7]) + "???" + "???????????????????????????" + srmon + "???", mSynListener);
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

    @OnClick({R.id.carin_charge_return, R.id.carin_charge_cancel, R.id.carin_charge_confirm, R.id.carin_charge_free})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.carin_charge_return:
                finish();
                break;
            case R.id.carin_charge_cancel:
                cancelPass(App.serverurl);
                break;
            case R.id.carin_charge_confirm:
                chargePass(App.serverurl);
                break;
            case R.id.carin_charge_free:

                break;
        }
    }

    private void judge() {
        switch (ctype) {
            case 1:
                cartype = getString(R.string.motorcycle);
                break;
            case 2:
                cartype = getString(R.string.compacts);
                break;
            case 3:
                cartype = getString(R.string.Intermediate);
                break;
            case 4:
                cartype = getString(R.string.large_vehicle);
                break;
            case 5:
                cartype = getString(R.string.transporter);
                break;
            case 6:
                cartype = getString(R.string.spare_car);
                break;
        }
    }

    private void chargePass(String url) {
        dialog1 = new ZLoadingDialog(CarinChargeActivity.this);
        dialog1.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();
        String cpjson = "{\"cmd\":\"141\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"sid\":\"" + sid + "\",\"io\":\"0\",\"rstat\":\"0\"," +
                "\"ftype\":\"" + cdtp + "\",\"sale\":\"0\",\"reas\":\"000\",\"spare\":\"0\",\"sign\":\"abcd\"}";

        Log.e("TAG", cpjson);
        OkHttpUtils.postString().url(url)
                .content(cpjson)
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
                    int rstat = datajson.getInt("rstat");
                    App.wmon = datajson.getDouble("emon");
                    //String sid=datajson.getString("sid");
                    if (rstat == 0) {
                        carin_start_voice();
                        Intent zdintent = new Intent(CarinChargeActivity.this, CarintoSuccessful.class);
                        zdintent.putExtra("carnum", carnum);
                        zdintent.putExtra("jfType", jfType);
                        zdintent.putExtra("ctype", ctype);
                        zdintent.putExtra("itime", itime);
                        startActivity(zdintent);
                        finish();
                    } else {
                        Toasty.error(CarinChargeActivity.this, getString(R.string.invalid_orders_need_to_be_reinitiated), Toast.LENGTH_SHORT, true).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void cancelPass(String url) {
        dialog = new ZLoadingDialog(CarinChargeActivity.this);
        dialog.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();

        String cpjson = "{\"cmd\":\"141\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"sid\":\"" + sid + "\",\"io\":\"0\",\"rstat\":\"1\"," +
                "\"ftype\":\"" + cdtp + "\",\"sale\":\"0\",\"reas\":\"000\",\"spare\":\"0\",\"sign\":\"abcd\"}";

        Log.e("TAG", cpjson);
        OkHttpUtils.postString().url(url)
                .content(cpjson)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("TAG", "?????????");
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG", response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String reasonjson = jsonObject.getString("reason");//????????????
                    JSONObject resultjson = jsonObject.getJSONObject("result");
                    JSONObject datajson = resultjson.getJSONObject("data");
                    int rstat = datajson.getInt("rstat");
                    //String sid=datajson.getString("sid");
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
