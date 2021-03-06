package com.example.npttest.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.OSSLogToFileUtils;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.camera.CameraActivity;
import com.example.npttest.constant.Constant;
import com.example.npttest.manager.ActivityManager;
import com.example.npttest.util.FileUtils;
import com.example.npttest.util.LicenseKeyboardUtil_input;
import com.example.npttest.util.MD5Util;
import com.example.npttest.util.PictureUtil;
import com.example.npttest.util.SPUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.npttest.util.FileUtils.SDPATH;
import static com.example.npttest.util.LicenseKeyboardUtil_input.currentEditText_input;

/**
 * Created by liuji on 2017/8/22.
 */

public class InputCarnum extends NoStatusbarActivity implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.inputcar_return)
    ImageView inputcarReturn;
    @Bind(R.id.textView7)
    TextView textView7;
    @Bind(R.id.inputcar_camera)
    ImageView inputcarCamera;
    @Bind(R.id.inputcar_inputbox1)
    EditText inputcarInputbox1;
    @Bind(R.id.inputcar_inputbox2)
    EditText inputcarInputbox2;
    @Bind(R.id.inputcar_inputbox3)
    EditText inputcarInputbox3;
    @Bind(R.id.inputcar_inputbox4)
    EditText inputcarInputbox4;
    @Bind(R.id.inputcar_inputbox5)
    EditText inputcarInputbox5;
    @Bind(R.id.inputcar_inputbox6)
    EditText inputcarInputbox6;
    @Bind(R.id.inputcar_inputbox7)
    EditText inputcarInputbox7;
    @Bind(R.id.inputcar_inputbox8)
    EditText inputcarInputbox8;
    @Bind(R.id.inputcar_lin_input)
    LinearLayout inputcarLinInput;
    @Bind(R.id.input_sbtn)
    SwitchButton inputSbtn;
    @Bind(R.id.inputcar_img)
    ImageView inputcarImg;
    @Bind(R.id.inputcar_motoimg)
    ImageView inputcarMotoimg;
    @Bind(R.id.inputcar_mototext)
    TextView inputcarMototext;
    @Bind(R.id.inputcar_moto)
    LinearLayout inputcarMoto;
    @Bind(R.id.inputcar_smallcarimg)
    ImageView inputcarSmallcarimg;
    @Bind(R.id.inputcar_smallcartext)
    TextView inputcarSmallcartext;
    @Bind(R.id.inputcar_smallcar)
    LinearLayout inputcarSmallcar;
    @Bind(R.id.inputcar_middleimg)
    ImageView inputcarMiddleimg;
    @Bind(R.id.inputcar_middletext)
    TextView inputcarMiddletext;
    @Bind(R.id.inputcar_middle)
    LinearLayout inputcarMiddle;
    @Bind(R.id.inputcar_bigcarimg)
    ImageView inputcarBigcarimg;
    @Bind(R.id.inputcar_bigcartextt)
    TextView inputcarBigcartextt;
    @Bind(R.id.inputcar_bigcar)
    LinearLayout inputcarBigcar;
    @Bind(R.id.inputcarinto_btn)
    Button inputcarintoBtn;
    @Bind(R.id.inputcarout_btn)
    Button inputcaroutBtn;
    @Bind(R.id.keyboard_view)
    KeyboardView keyboardView;
    private String carnumb, color, bitmapPath;
    private LicenseKeyboardUtil_input keyboardUtil;
    private EditText edits[];
    private String editS;
    private int carType = 2;
    private Bitmap bitmap = null;
    private long carintotime;
    private String bitPath;
    private ZLoadingDialog dialog1, dialog;
    private String comCity;
    private String car_type;
    private int rstat, ptype, ctype, cdtp, preson;
    private String jfType, carnum;//????????????
    private String comfirmYy, putbitmappath;//????????????
    SynthesizerListener mSynListener;
    private OSS oss;
    MyHandler handler;
    private long itime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputcar);
        ButterKnife.bind(this);
        if (App.serverurl != null) {
            getosskey(App.serverurl);
        }
        SpeechUtility.createUtility(InputCarnum.this, SpeechConstant.APPID + "=59df2c0c");
        edits = new EditText[]{inputcarInputbox1, inputcarInputbox2, inputcarInputbox3,
                inputcarInputbox4, inputcarInputbox5, inputcarInputbox6,
                inputcarInputbox7, inputcarInputbox8};
        keyboardUtil = new LicenseKeyboardUtil_input(InputCarnum.this, edits);
        inputcarSmallcar.setBackgroundColor(Color.parseColor("#1e7db4"));
        inputcarSmallcarimg.setImageResource(R.mipmap.ic_small_car_w);
        inputcarSmallcartext.setTextColor(Color.parseColor("#FFFFFF"));
        comCity = (String) SPUtils.get(InputCarnum.this, Constant.COM_CITY, "");
        char[] chars = comCity.toCharArray();
        if (TextUtils.isEmpty(comCity)) {

        } else {
            inputcarInputbox1.setText(String.valueOf(chars[0]));
            inputcarInputbox2.setText(String.valueOf(chars[1]));
        }
        Boolean aBoolean = (Boolean) SPUtils.get(InputCarnum.this, "open_new_car", false);
        inputSbtn.setCheckedImmediately(aBoolean);
        inputSbtn.setOnCheckedChangeListener(this);
        if (aBoolean) {
            inputcarInputbox8.setVisibility(View.VISIBLE);
            LicenseKeyboardUtil_input.etsize = 7;
        } else {
            inputcarInputbox8.setVisibility(View.GONE);
            LicenseKeyboardUtil_input.etsize = 6;
        }
        ActivityManager.getInstance().addActivity(this);
        handler=new MyHandler();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0123:
                    Log.e("TAG", "????????????");
                    break;
                case 0x0124:
                    Log.e("TAG", "???????????????????????????");
                    break;
                case 0x0125:
                    Log.e("TAG", "???????????????????????????");
                    break;
            }
        }
    }

    private void initoss() {
        /**
         * ?????????oss
         */
        String endpoint = Constant.EndPoint;
        // ????????????????????????STS???????????????OSSClient???
        // ?????????????????????sample ??? sts ????????????(https://github.com/aliyun/aliyun-oss-android-sdk/tree/master/app/src/main/java/com/alibaba/sdk/android/oss/app)
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(Constant.AccessKeyId, Constant.AccessKeySecret, Constant.SecurityToken);
        //?????????????????????????????????????????????????????????????????????
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // ?????????????????????15???
        conf.setSocketTimeout(15 * 1000); // socket???????????????15???
        conf.setMaxConcurrentRequest(5); // ??????????????????????????????5???
        conf.setMaxErrorRetry(2); // ????????????????????????????????????2???
        //??????????????????????????????????????????????????????????????????sd????????????????????????????????????SDCard_path\OSSLog\logs.csv  ???????????????
        //???????????????oss????????????????????????????????????????????????????????????
        //??????requestId,response header???
        //android_version???5.1  android??????
        //mobile_model???XT1085  android????????????
        //network_state???connected  ????????????
        //network_type???WIFI ??????????????????
        //???????????????????????????:
        //[2017-09-05 16:54:52] - Encounter local execpiton: //java.lang.IllegalArgumentException: The bucket name is invalid.
        //A bucket name must:
        //1) be comprised of lower-case characters, numbers or dash(-);
        //2) start with lower case or numbers;
        //3) be between 3-63 characters long.
        //------>end of log
        OSSLog.enableLog();
        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }

    private void getosskey(String url) {

        String ossjs = "{\"cmd\":\"162\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"sign\":\"abcd\"}";
        Log.e("TAG", ossjs);
        OkHttpUtils.postString().url(url)
                .content(ossjs)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(InputCarnum.this, getString(R.string.please_check_the_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String reasonjson = jsonObject.getString("reason");//????????????
                    JSONObject resultjson = jsonObject.getJSONObject("result");
                    JSONObject datajson = resultjson.getJSONObject("data");
                    int qrs = datajson.getInt("qrs");
                    if (qrs == 1) {
                        Constant.AccessKeyId = datajson.getString("accessKeyId");
                        Constant.AccessKeySecret = datajson.getString("accessKeySecret");
                        Constant.SecurityToken = datajson.getString("securityToken");
                        Log.e("TAG", "AccessKeyId:" + Constant.AccessKeyId + "\n" + "AccessKeySecret:" + Constant.AccessKeySecret + "\n" + "SecurityToken:" + Constant.SecurityToken);
                        initoss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void putdate(String bucket, String imgurl, String fileurl) {
        ///storage/emulated/0/DCIM/PlatePic/plateID_20171018_204458null.jpg
        OSSLogToFileUtils.reset();
        PutObjectRequest put = new PutObjectRequest(bucket, imgurl, fileurl);
        //Toast.makeText(this, put.getBucketName(), Toast.LENGTH_SHORT).show();
        // ???????????????????????????????????????
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //Log.e("TAG", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.e("TAG", "UploadSuccess");
                handler.sendEmptyMessage(0x123);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // ????????????
                if (clientExcepion != null) {
                    // ??????????????????????????????
                    clientExcepion.printStackTrace();
                    handler.sendEmptyMessage(0x0124);
                }
                if (serviceException != null) {
                    // ????????????
                    Log.e("TAG", serviceException.getErrorCode());
                    Log.e("TAG", serviceException.getRequestId());
                    Log.e("TAG", serviceException.getHostId());
                    Log.e("TAG", serviceException.getRawMessage());
                    handler.sendEmptyMessage(0x0125);
                }
            }
        });
        // task.cancel(); // ??????????????????
        //task.waitUntilFinished(); // ??????????????????????????????
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
        char[] carnumber = carnum.toCharArray();
        if (carnumber.length == 7) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + "???" + "???????????????", mSynListener);
            Log.e("TAG", String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + "???" + "???????????????");
        } else if (carnumber.length == 8) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + " " + String.valueOf(carnumber[7]) + "???" + "???????????????", mSynListener);
        }

        Log.e("TAG", String.valueOf(carnumber[0]) + String.valueOf(carnumber[1]) + String.valueOf(carnumber[2])
                + String.valueOf(carnumber[3]) + String.valueOf(carnumber[4]) + String.valueOf(carnumber[5]) + String.valueOf(carnumber[6]) + "");
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

    //????????? ??????????????????
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.inputcar_return, R.id.inputcar_camera, R.id.inputcar_inputbox1, R.id.inputcar_inputbox2,
            R.id.inputcar_inputbox3, R.id.inputcar_inputbox4, R.id.inputcar_inputbox5, R.id.inputcar_inputbox6,
            R.id.inputcar_inputbox7, R.id.inputcar_inputbox8, R.id.inputcarinto_btn, R.id.inputcarout_btn,
            R.id.inputcar_moto, R.id.inputcar_smallcar, R.id.inputcar_middle, R.id.inputcar_bigcar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.inputcar_return:
                finish();
                break;
            case R.id.inputcar_camera:
                //jumpVideoRecog();
                Intent intent = new Intent(InputCarnum.this, CameraActivity.class);
                intent.putExtra("camera", false);
                //startActivity(intent);
                startActivityForResult(intent, 0x11);
                break;
            case R.id.inputcar_inputbox1:
                currentEditText_input = 0;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.inputcar_inputbox2:
                currentEditText_input = 1;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.inputcar_inputbox3:
                currentEditText_input = 2;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();

                break;
            case R.id.inputcar_inputbox4:
                currentEditText_input = 3;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();

                break;
            case R.id.inputcar_inputbox5:
                currentEditText_input = 4;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();

                break;
            case R.id.inputcar_inputbox6:
                currentEditText_input = 5;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();

                break;
            case R.id.inputcar_inputbox7:
                currentEditText_input = 6;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();

                break;
            case R.id.inputcar_inputbox8:
                currentEditText_input = 7;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            //????????????
            case R.id.inputcarinto_btn:
                carintotime = gettime();
                Log.e("TAG", carintotime + "");
                switch (carType) {
                    case 1:
                        car_type = getString(R.string.motorcycle);
                        break;
                    case 2:
                        car_type = getString(R.string.compacts);
                        break;
                    case 3:
                        car_type = getString(R.string.Intermediate);
                        break;
                    case 4:
                        car_type = getString(R.string.large_vehicle);
                        break;
                }
                editS = inputcarInputbox1.getText().toString() +
                        inputcarInputbox2.getText().toString() +
                        inputcarInputbox3.getText().toString() +
                        inputcarInputbox4.getText().toString() +
                        inputcarInputbox5.getText().toString() +
                        inputcarInputbox6.getText().toString() +
                        inputcarInputbox7.getText().toString() +
                        inputcarInputbox8.getText().toString();

                if (TextUtils.isEmpty(inputcarInputbox1.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox2.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox3.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox4.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox5.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox6.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox7.getText().toString())) {
                    Toasty.error(this, getString(R.string.enter_correct_license_plate_number), Toast.LENGTH_SHORT, true).show();
                } else {
                    carinto(App.serverurl);
                }
                break;
            //????????????
            case R.id.inputcarout_btn:
                switch (carType) {
                    case 1:
                        car_type = getString(R.string.motorcycle);
                        break;
                    case 2:
                        car_type = getString(R.string.compacts);
                        break;
                    case 3:
                        car_type = getString(R.string.Intermediate);
                        break;
                    case 4:
                        car_type = getString(R.string.large_vehicle);
                        break;
                }
                editS = inputcarInputbox1.getText().toString() +
                        inputcarInputbox2.getText().toString() +
                        inputcarInputbox3.getText().toString() +
                        inputcarInputbox4.getText().toString() +
                        inputcarInputbox5.getText().toString() +
                        inputcarInputbox6.getText().toString() +
                        inputcarInputbox7.getText().toString() +
                        inputcarInputbox8.getText().toString();

                if (TextUtils.isEmpty(inputcarInputbox1.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox2.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox3.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox4.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox5.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox6.getText().toString()) ||
                        TextUtils.isEmpty(inputcarInputbox7.getText().toString())) {
                    Toasty.error(this, getString(R.string.enter_correct_license_plate_number), Toast.LENGTH_SHORT, true).show();
                } else {
                    carout(App.serverurl);

                }
                break;
            case R.id.inputcar_moto:
                initcolor();
                carType = 1;
                inputcarMoto.setBackgroundColor(Color.parseColor("#1e7db4"));
                inputcarMotoimg.setImageResource(R.mipmap.ic_moto_bike_w);
                inputcarMototext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.inputcar_smallcar:
                carType = 2;
                initcolor();
                inputcarSmallcar.setBackgroundColor(Color.parseColor("#1e7db4"));
                inputcarSmallcarimg.setImageResource(R.mipmap.ic_small_car_w);
                inputcarSmallcartext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.inputcar_middle:
                carType = 3;
                initcolor();
                inputcarMiddle.setBackgroundColor(Color.parseColor("#1e7db4"));
                inputcarMiddleimg.setImageResource(R.mipmap.ic_mid_truck_w);
                inputcarMiddletext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.inputcar_bigcar:
                carType = 4;
                initcolor();
                inputcarBigcar.setBackgroundColor(Color.parseColor("#1e7db4"));
                inputcarBigcarimg.setImageResource(R.mipmap.ic_big_truck_w);
                inputcarBigcartextt.setTextColor(Color.parseColor("#FFFFFF"));
                break;
        }
    }

    static final String[] PERMISSION = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// ????????????
            Manifest.permission.READ_EXTERNAL_STORAGE, // ????????????
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE, Manifest.permission.INTERNET};

    //?????????????????????????????????
  /*  public void jumpVideoRecog() {
        Intent video_intent = new Intent();
        video_intent.putExtra("camera", true);
        RecogService.recogModel = false;//true  ???????????? ????????????  false:????????????  ????????????
        video_intent = new Intent(InputCarnum.this, MemoryCameraActivity.class);
        if (Build.VERSION.SDK_INT >= 23) {
            CheckPermission checkPermission = new CheckPermission(InputCarnum.this);
            if (checkPermission.permissionSet(PERMISSION)) {
                PermissionActivity.startActivityForResult(InputCarnum.this, 0, "true", PERMISSION);
                startActivityForResult(video_intent, 0x11);
            } else {
                video_intent.setClass(InputCarnum.this.getApplication(), MemoryCameraActivity.class);
                video_intent.putExtra("camera", true);
                startActivityForResult(video_intent, 0x11);
            }
        } else {
            video_intent.setClass(InputCarnum.this, MemoryCameraActivity.class);
            video_intent.putExtra("camera", true);
            startActivityForResult(video_intent, 0x11);
        }
        //finish();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        carnumb = data.getStringExtra("number").toString();
        color = data.getStringExtra("color").toString();
        bitmapPath = data.getStringExtra("path").toString();
        if (carnumb.equals("null")) {

        } else {
            //????????????edit??????
            char[] carnumber = carnumb.toCharArray();
            if (carnumber.length == 7) {
                inputcarInputbox8.setVisibility(View.GONE);
                inputcarInputbox1.setText(String.valueOf(carnumber[0]));
                inputcarInputbox2.setText(String.valueOf(carnumber[1]));
                inputcarInputbox3.setText(String.valueOf(carnumber[2]));
                inputcarInputbox4.setText(String.valueOf(carnumber[3]));
                inputcarInputbox5.setText(String.valueOf(carnumber[4]));
                inputcarInputbox6.setText(String.valueOf(carnumber[5]));
                inputcarInputbox7.setText(String.valueOf(carnumber[6]));
            } else if (carnumber.length == 8) {
                inputcarInputbox1.setText(String.valueOf(carnumber[0]));
                inputcarInputbox2.setText(String.valueOf(carnumber[1]));
                inputcarInputbox3.setText(String.valueOf(carnumber[2]));
                inputcarInputbox4.setText(String.valueOf(carnumber[3]));
                inputcarInputbox5.setText(String.valueOf(carnumber[4]));
                inputcarInputbox6.setText(String.valueOf(carnumber[5]));
                inputcarInputbox7.setText(String.valueOf(carnumber[6]));
                inputcarInputbox8.setText(String.valueOf(carnumber[7]));
            }
            if (bitmapPath != null && !bitmapPath.equals("")) {
               /* bitmap = BitmapFactory.decodeFile(bitmapPath);
                //???????????????????????????????????????????????????   ??????????????????????????????
            *//*bitmap = Bitmap.createBitmap(bitmap, left, top, w, h);
            if (bitmap != null) {
                admissionImg.setImageBitmap(bitmap);
            }*//*
                inputcarImg.setVisibility(View.VISIBLE);
                Bitmap bitmap1 = ImageCrop(bitmap, 125, 300, true);
                inputcarImg.setImageBitmap(bitmap1);*/
                Bitmap newbitmap = PictureUtil.getSmallBitmap(bitmapPath, 480, 800);
                FileUtils.saveBitmap(newbitmap, pictureName());
                putbitmappath = SDPATH + pictureName() + ".JPEG";
                Log.e("TAG", putbitmappath);
                bitmap = BitmapFactory.decodeFile(putbitmappath);
                inputcarImg.setVisibility(View.VISIBLE);
                Bitmap bitmap1 = ImageCrop(bitmap, 125, 300, true);
                inputcarImg.setImageBitmap(bitmap1);
            }
        }

    }

    public static String pictureName() {
        String str = "";
        Time t = new Time();
        t.setToNow(); // ????????
        int year = t.year;
        int month = t.month + 1;
        int date = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        int second = t.second;
        if (month < 10)
            str = String.valueOf(year) + "0" + String.valueOf(month);
        else {
            str = String.valueOf(year) + String.valueOf(month);
        }
        if (date < 10)
            str = str + "0" + String.valueOf(date + "_");
        else {
            str = str + String.valueOf(date + "_");
        }
        if (hour < 10)
            str = str + "0" + String.valueOf(hour);
        else {
            str = str + String.valueOf(hour);
        }
        if (minute < 10)
            str = str + "0" + String.valueOf(minute);
        else {
            str = str + String.valueOf(minute);
        }
        if (second < 10)
            str = str + "0" + String.valueOf(second);
        else {
            str = str + String.valueOf(second);
        }
        return str;
    }

    //????????????????????????
    public static Bitmap ImageCrop(Bitmap bitmap, int num1, int num2, boolean isRecycled) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth(); // ????????????????????????
        int h = bitmap.getHeight();
        int retX, retY;
        int nw, nh;
        if (w > h) {
            if (h > w * num2 / num1) {
                nw = w;
                nh = w * num2 / num1;
                retX = 0;
                retY = (h - nh) / 2;
            } else {
                nw = h * num1 / num2;
                nh = h;
                retX = (w - nw) / 2;
                retY = 0;
            }
        } else {
            if (w > h * num2 / num1) {
                nh = h;
                nw = h * num2 / num1;
                retY = 0;
                retX = (w - nw) / 2;
            } else {
                nh = w * num1 / num2;
                nw = w;
                retY = (h - nh) / 2;
                retX = 0;
            }
        }
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
                false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp)
                && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }

    //???????????????????????????
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        keyboardUtil.hideKeyboard();
        return super.onTouchEvent(event);
    }

    //????????????
    private void carinto(String url) {
        dialog1 = new ZLoadingDialog(InputCarnum.this);
        dialog1.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();
        //{"cmd":"140","type":"2","code":"17083B3DE","dsv":"110","ptype":"0","io":"0",
        // "num":"???B1FL39","ctype":"2","spare":"0","sign":"abcd"}
       /* String intocar_jS = "{\"cmd\":\"140\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"ptype\":\"0\",\"io\":\"0\",\"num\":\"" + editS + "\"," +
                "\"ctype\":\"" + carType + "\",\"spare\":\"0\",\"sign\":\"abcd\"}";*/
        String intocar_jS ="{\"cmd\":\"140\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+ Constant.CODE+"\",\"dsv\":\""
                + Constant.DSV+"\",\"ptype\":\"0\",\"io\":\"0\",\"num\":\""+editS+"\",\"ctype\":\""+carType+
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
                    carnum = datajson.getString("num");//?????????
                    rstat = datajson.getInt("rstat");//????????????
                    //rstat=1;
                    ptype = datajson.getInt("ptype");//??????????????????????????????
                    ctype = datajson.getInt("ctype");//????????????
                    cdtp = datajson.getInt("cdtp");//???????????????????????????
                    long ctime = datajson.getLong("ctime");//????????????
                    preson = datajson.getInt("preson");//??????
                    itime = datajson.getLong("itime");//????????????
                    double nmon = datajson.getDouble("nmon");//????????????
                    double rmon = datajson.getDouble("rmon");//????????????
                    double smon = datajson.getDouble("smon");//????????????
                    String sid = datajson.getString("sid");
                    if (sid != null && putbitmappath != null) {
                        Log.e("TAG", putbitmappath);
                        String ppmbucket = Constant.ppmBucket;
                        //String ppmimgurl=Constant.CODE+"/"+sid+".jpg";
                        String ppmimgurl = MD5Util.MD5Encode(Constant.CODE +"0"+ sid) + ".jpg";
                        String ppmfileurl = putbitmappath;
                        Log.e("TAG", sid);
                        putdate(ppmbucket, ppmimgurl, ppmfileurl);
                    }
                    //??????
                    jfjudge();
                    if (rstat == 0) {
                        //????????????
                            carin_start_voice();
                            Intent zdintent = new Intent(InputCarnum.this, CarintoSuccessful.class);
                            zdintent.putExtra("carnum", carnum);
                            zdintent.putExtra("jfType", jfType);
                            zdintent.putExtra("ctype", ctype);
                            zdintent.putExtra("itime", itime);
                            zdintent.putExtra("cdtp", cdtp);
                            startActivity(zdintent);
                        //Toast.makeText(InputCarnum.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else if (rstat == 1) {
                        //????????????
                        confirmjudge();
                        Intent qrintent = new Intent(InputCarnum.this, CarinConfirmPass.class);
                        qrintent.putExtra("carnum", carnum);
                        qrintent.putExtra("jfType", jfType);
                        qrintent.putExtra("ctype", ctype);
                        qrintent.putExtra("itime", itime);
                        qrintent.putExtra("comfirmYy", comfirmYy);
                        qrintent.putExtra("sid", sid);
                        qrintent.putExtra("cdtp", cdtp);
                        startActivity(qrintent);

                    } else if (rstat == 2) {
                        //????????????
                        chargejudge();
                        Intent sfintent = new Intent(InputCarnum.this, CarinChargeActivity.class);
                        sfintent.putExtra("carnum", carnum);
                        sfintent.putExtra("jfType", jfType);
                        sfintent.putExtra("ctype", ctype);
                        sfintent.putExtra("itime", itime);
                        sfintent.putExtra("comfirmYy", comfirmYy);
                        sfintent.putExtra("nmon", nmon);
                        sfintent.putExtra("rmon", rmon);
                        sfintent.putExtra("smon", smon);
                        sfintent.putExtra("sid", sid);
                        sfintent.putExtra("cdtp", cdtp);
                        startActivity(sfintent);
                    } else if (rstat == 3) {
                        //????????????
                        prohibitjudge();
                        Intent jzintent = new Intent(InputCarnum.this, ProhibitPass.class);
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

    //????????????
    private void carout(String url) {
        dialog = new ZLoadingDialog(InputCarnum.this);
        dialog.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();
        //{"cmd":"140","type":"2","code":"17083B3DE","dsv":"110","ptype":"0","io":"0",
        // "num":"???B1FL39","ctype":"2","spare":"0","sign":"abcd"}
        /*String intocar_jS = "{\"cmd\":\"140\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"ptype\":\"0\",\"io\":\"1\",\"num\":\"" + editS + "\"," +
                "\"ctype\":\"" + carType + "\",\"spare\":\"0\",\"sign\":\"abcd\"}";*/

        String intocar_jS ="{\"cmd\":\"140\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+ Constant.CODE+"\",\"dsv\":\""
                + Constant.DSV+"\",\"ptype\":\"0\",\"io\":\"1\",\"num\":\""+editS+"\",\"ctype\":\""+carType+
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
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String reasonjson = jsonObject.getString("reason");//????????????
                    JSONObject resultjson = jsonObject.getJSONObject("result");
                    JSONObject datajson = resultjson.getJSONObject("data");
                    carnum = datajson.getString("num");//?????????
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
                    if (sid != null && bitmapPath != null) {
                        String ppmbucket = Constant.ppmBucket;
                        //String ppmimgurl=Constant.CODE+"/"+sid+".jpg";
                        String ppmimgurl = MD5Util.MD5Encode(Constant.CODE +"1"+ sid) + ".jpg";
                        String ppmfileurl = bitmapPath;
                        Log.e("TAG", sid);
                        putdate(ppmbucket, ppmimgurl, ppmfileurl);
                    }
                    //??????
                    jfjudge();
                    if (rstat == 0) {
                        //????????????
                        carout_start_voice();
                        Intent zdintent = new Intent(InputCarnum.this, CaroutSuccessful.class);
                        zdintent.putExtra("carnum", carnum);
                        zdintent.putExtra("jfType", jfType);
                        zdintent.putExtra("ctype", ctype);
                        zdintent.putExtra("ctime", ctime);
                        zdintent.putExtra("itime", itime);
                        startActivity(zdintent);
                        //Toast.makeText(InputCarnum.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else if (rstat == 1) {
                        //????????????
                        confirmjudge();
                        Intent qrintent = new Intent(InputCarnum.this, CaroutConfirmPass.class);
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
                        Intent sfintent = new Intent(InputCarnum.this, CaroutChargeActivity.class);
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
                        Intent jzintent = new Intent(InputCarnum.this, ProhibitPass.class);
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

    //????????????
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

    //??????????????????
    private long gettime() {
        Date date = new Date();
        long time = (date.getTime() / 1000);
        //Log.e("TAG",date.getTime()/1000+"");
        return time;
    }

    //???????????????
    private void initcolor() {
        inputcarMoto.setBackgroundColor(Color.parseColor("#FFFFFF"));
        inputcarMotoimg.setImageResource(R.mipmap.ic_moto_bike);
        inputcarMototext.setTextColor(Color.parseColor("#48495f"));
        inputcarSmallcar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        inputcarSmallcarimg.setImageResource(R.mipmap.ic_small_car);
        inputcarSmallcartext.setTextColor(Color.parseColor("#48495f"));
        inputcarMiddle.setBackgroundColor(Color.parseColor("#FFFFFF"));
        inputcarMiddleimg.setImageResource(R.mipmap.ic_mid_truck);
        inputcarMiddletext.setTextColor(Color.parseColor("#48495f"));
        inputcarBigcar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        inputcarBigcarimg.setImageResource(R.mipmap.ic_big_truck);
        inputcarBigcartextt.setTextColor(Color.parseColor("#48495f"));
    }

    //???????????????????????????
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            SPUtils.put(InputCarnum.this, "open_new_car", true);
            inputcarInputbox8.setVisibility(View.VISIBLE);
            LicenseKeyboardUtil_input.etsize = 7;
            if (!TextUtils.isEmpty(inputcarInputbox7.getText())) {
                currentEditText_input = 7;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
            }

        } else {
            SPUtils.put(InputCarnum.this, "open_new_car", false);
            inputcarInputbox8.setVisibility(View.GONE);
            LicenseKeyboardUtil_input.etsize = 6;
            inputcarInputbox8.setText("");
            if (!TextUtils.isEmpty(inputcarInputbox6.getText())) {
                currentEditText_input = 6;
                initcolor1();
                edits[LicenseKeyboardUtil_input.currentEditText_input].setBackgroundResource(R.drawable.keyboard_bg_red);
            }

        }
    }

}
