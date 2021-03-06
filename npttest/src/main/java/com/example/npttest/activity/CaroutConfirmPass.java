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

import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.constant.Constant;
import com.example.npttest.tool.DateTools;
import com.example.npttest.tool.TimeDifferTools;
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
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by liuji on 2017/9/25.
 */

public class CaroutConfirmPass extends NoStatusbarActivity {

    @Bind(R.id.co_conf_pass_return)
    ImageView coConfPassReturn;
    @Bind(R.id.co_conf_pass_title)
    LinearLayout coConfPassTitle;
    @Bind(R.id.co_conf_pass_carnum)
    TextView coConfPassCarnum;
    @Bind(R.id.co_conf_pass_cartype)
    TextView coConfPassCartype;
    @Bind(R.id.co_conf_pass_pztype)
    TextView coConfPassPztype;
    @Bind(R.id.co_conf_pass_yy)
    TextView coConfPassYy;
    @Bind(R.id.co_conf_pass_ptime)
    TextView coConfPassPtime;
    @Bind(R.id.co_conf_pass_itime)
    TextView coConfPassItime;
    @Bind(R.id.co_conf_pass_ctime)
    TextView coConfPassCtime;
    @Bind(R.id.co_conf_pass_cancel)
    Button coConfPassCancel;
    @Bind(R.id.co_conf_pass_submit)
    Button coConfPassSubmit;
    private String carnum, cartype, jfType, comfirmYy, sid, pktime;
    private int ctype, cdtp;
    private long itime, ctime, ptime;
    private ZLoadingDialog dialog1, dialog;
    private Boolean pvrefresh;
    SynthesizerListener mSynListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carout_confirm_pass);
        ButterKnife.bind(this);
        SpeechUtility.createUtility(CaroutConfirmPass.this, SpeechConstant.APPID + "=59df2c0c");
        Intent intent = getIntent();
        carnum = intent.getStringExtra("carnum");
        ctype = intent.getIntExtra("ctype", 0);
        jfType = intent.getStringExtra("jfType");
        itime = intent.getLongExtra("itime", 0);
        ctime = intent.getLongExtra("ctime", 0);
        comfirmYy = intent.getStringExtra("comfirmYy");
        sid = intent.getStringExtra("sid");
        cdtp = intent.getIntExtra("cdtp", 0);
        pvrefresh = intent.getBooleanExtra("pvrefresh", false);
        pktime =new TimeDifferTools(this).getDistanceTime(itime * 1000, ctime * 1000);
        judge();
        //coConfPassPtime.setText(pktime);
        coConfPassCarnum.setText(carnum);
        coConfPassCartype.setText(cartype);
        coConfPassPztype.setText(jfType);
       // coConfPassItime.setText(DateTools.getDate(itime * 1000) + "");
        coConfPassCtime.setText(DateTools.getDate(ctime * 1000) + "");
        coConfPassYy.setText(comfirmYy);
    }

    @OnClick({R.id.co_conf_pass_return, R.id.co_conf_pass_cancel, R.id.co_conf_pass_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.co_conf_pass_return:
                finish();
                break;
            case R.id.co_conf_pass_cancel:
                cancelPass(App.serverurl);
                break;
            case R.id.co_conf_pass_submit:
                confirmPass(App.serverurl);
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

    //????????????
    private void confirmPass(String url) {
        dialog1 = new ZLoadingDialog(CaroutConfirmPass.this);
        dialog1.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();
        String cpjson = "{\"cmd\":\"141\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"sid\":\"" + sid + "\",\"io\":\"1\",\"rstat\":\"0\"," +
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
                    //String sid=datajson.getString("sid");
                    if (rstat == 0) {
                        carout_start_voice();
                        Intent zdintent = new Intent(CaroutConfirmPass.this, CaroutSuccessful.class);
                        zdintent.putExtra("carnum", carnum);
                        zdintent.putExtra("jfType", jfType);
                        zdintent.putExtra("ctype", ctype);
                        zdintent.putExtra("ctime", ctime);
                        zdintent.putExtra("itime", itime);
                        zdintent.putExtra("pvrefresh", pvrefresh);
                        //zdintent.putExtra("pktime",pktime);
                        startActivity(zdintent);
                        finish();
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

    //??????
    private void cancelPass(String url) {
        dialog = new ZLoadingDialog(CaroutConfirmPass.this);
        dialog.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();

        String cpjson = "{\"cmd\":\"141\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"sid\":\"" + sid + "\",\"io\":\"1\",\"rstat\":\"1\"," +
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
