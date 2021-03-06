package com.example.npttest.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
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
import com.example.npttest.constant.Constant;
import com.example.npttest.manager.ActivityManager;
import com.example.npttest.util.FileUtils;
import com.example.npttest.util.LicenseKeyboardUtil_cario;
import com.example.npttest.util.MD5Util;
import com.example.npttest.util.PictureUtil;
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

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.npttest.util.FileUtils.SDPATH;

/**
 * Created by liuji on 2017/7/31.
 */
public class Admission extends NoStatusbarActivity {

    @Bind(R.id.admission_return)
    ImageView admissionReturn;
    @Bind(R.id.admission_img)
    ImageView admissionImg;
    @Bind(R.id.admission_motoimg)
    ImageView admissionMotoimg;
    @Bind(R.id.admission_mototext)
    TextView admissionMototext;
    @Bind(R.id.admission_moto)
    LinearLayout admissionMoto;
    @Bind(R.id.admission_smallcarimg)
    ImageView admissionSmallcarimg;
    @Bind(R.id.admission_smallcartext)
    TextView admissionSmallcartext;
    @Bind(R.id.admission_smallcar)
    LinearLayout admissionSmallcar;
    @Bind(R.id.admission_middleimg)
    ImageView admissionMiddleimg;
    @Bind(R.id.admission_middletext)
    TextView admissionMiddletext;
    @Bind(R.id.admission_middle)
    LinearLayout admissionMiddle;
    @Bind(R.id.admission_bigcarimg)
    ImageView admissionBigcarimg;
    @Bind(R.id.admission_bigcartext)
    TextView admissionBigcartext;
    @Bind(R.id.admission_bigcar)
    LinearLayout admissionBigcar;
    @Bind(R.id.et_car_license_inputbox1)
    EditText etCarLicenseInputbox1;
    @Bind(R.id.et_car_license_inputbox2)
    EditText etCarLicenseInputbox2;
    @Bind(R.id.et_car_license_inputbox3)
    EditText etCarLicenseInputbox3;
    @Bind(R.id.et_car_license_inputbox4)
    EditText etCarLicenseInputbox4;
    @Bind(R.id.et_car_license_inputbox5)
    EditText etCarLicenseInputbox5;
    @Bind(R.id.et_car_license_inputbox6)
    EditText etCarLicenseInputbox6;
    @Bind(R.id.et_car_license_inputbox7)
    EditText etCarLicenseInputbox7;
    @Bind(R.id.et_car_license_inputbox8)
    EditText etCarLicenseInputbox8;
    @Bind(R.id.ll_license_input_boxes_content)
    LinearLayout llLicenseInputBoxesContent;
    @Bind(R.id.admission_Cancel_admission)
    Button admissionCancelAdmission;
    @Bind(R.id.admission_Confirm_admission)
    Button admissionConfirmAdmission;
    @Bind(R.id.keyboard_view)
    KeyboardView keyboardView;
    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;
    private String bitmapPath;
    private Bitmap bitmap = null;
    public static final String INPUT_LICENSE_COMPLETE = "me.kevingo.licensekeyboard.input.comp";
    public static final String INPUT_LICENSE_KEY = "LICENSE";
    private LicenseKeyboardUtil_cario keyboardUtil;
    private EditText edits[];
    private int carType = 2;
    private String cameraS, carnum;
    private String editS;
    private ZLoadingDialog dialog, dialog1;
    private String car_type;
    private int rstat, ptype, ctype, cdtp, preson;
    private String jfType, sid;//????????????
    private String comfirmYy;//????????????
    SynthesizerListener mSynListener;
    private OSS oss;
    private boolean putimg = true;
    private String putbitmappath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admission);
        ButterKnife.bind(this);
        if (App.serverurl != null) {
            getosskey(App.serverurl);
        }
        SpeechUtility.createUtility(Admission.this, SpeechConstant.APPID + "=59df2c0c");
        Intent intent = getIntent();
        cameraS = intent.getStringExtra("number");
        String s1 = intent.getStringExtra("color");
        bitmapPath = intent.getStringExtra("path").toString();
        Log.e("TAG", "????????????????????????:" + bitmapPath);
        if (bitmapPath != null && !bitmapPath.equals("")) {
            /*bitmap = BitmapFactory.decodeFile(bitmapPath);
            //???????????????????????????????????????????????????   ??????????????????????????????
            *//*bitmap = Bitmap.createBitmap(bitmap, left, top, w, h);
            if (bitmap != null) {
                admissionImg.setImageBitmap(bitmap);
            }*//*
            Bitmap bitmap1 = ImageCrop(bitmap, 125, 300, true);
            admissionImg.setImageBitmap(bitmap1);*/
            bitmap = BitmapFactory.decodeFile(bitmapPath);
            Bitmap bitmap1 = ImageCrop(bitmap, 125, 300, true);
            admissionImg.setImageBitmap(bitmap1);
            Bitmap newbitmap = PictureUtil.getSmallBitmap(bitmapPath, 480, 800);
            FileUtils.saveBitmap(newbitmap, pictureName());
            putbitmappath = SDPATH + pictureName() + ".JPEG";
            Log.e("TAG", "???????????????????????????"+putbitmappath);
        }
        //????????????edit??????
        char[] carnumber = cameraS.toCharArray();
        if (carnumber.length == 7) {
            LicenseKeyboardUtil_cario.etsize_cario = 6;
            etCarLicenseInputbox8.setVisibility(View.GONE);
            etCarLicenseInputbox1.setText(String.valueOf(carnumber[0]));
            etCarLicenseInputbox2.setText(String.valueOf(carnumber[1]));
            etCarLicenseInputbox3.setText(String.valueOf(carnumber[2]));
            etCarLicenseInputbox4.setText(String.valueOf(carnumber[3]));
            etCarLicenseInputbox5.setText(String.valueOf(carnumber[4]));
            etCarLicenseInputbox6.setText(String.valueOf(carnumber[5]));
            etCarLicenseInputbox7.setText(String.valueOf(carnumber[6]));
        } else if (carnumber.length == 8) {
            LicenseKeyboardUtil_cario.etsize_cario = 7;
            etCarLicenseInputbox1.setText(String.valueOf(carnumber[0]));
            etCarLicenseInputbox2.setText(String.valueOf(carnumber[1]));
            etCarLicenseInputbox3.setText(String.valueOf(carnumber[2]));
            etCarLicenseInputbox4.setText(String.valueOf(carnumber[3]));
            etCarLicenseInputbox5.setText(String.valueOf(carnumber[4]));
            etCarLicenseInputbox6.setText(String.valueOf(carnumber[5]));
            etCarLicenseInputbox7.setText(String.valueOf(carnumber[6]));
            etCarLicenseInputbox8.setText(String.valueOf(carnumber[7]));
        }

        edits = new EditText[]{etCarLicenseInputbox1, etCarLicenseInputbox2, etCarLicenseInputbox3,
                etCarLicenseInputbox4, etCarLicenseInputbox5, etCarLicenseInputbox6,
                etCarLicenseInputbox7, etCarLicenseInputbox8};
        //????????????????????????intent?????????
        IntentFilter finishFilter = new IntentFilter(INPUT_LICENSE_COMPLETE);
        keyboardUtil = new LicenseKeyboardUtil_cario(Admission.this, edits);
        admissionSmallcar.setBackgroundColor(Color.parseColor("#1e7db4"));
        admissionSmallcarimg.setImageResource(R.mipmap.ic_small_car_w);
        admissionSmallcartext.setTextColor(Color.parseColor("#FFFFFF"));
        //jump();
        ActivityManager.getInstance().addActivity(this);
        //Log.e("TAG",getConnectedType(Admission.this)+"");

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
                Toast.makeText(Admission.this,getString(R.string.please_check_the_network), Toast.LENGTH_SHORT).show();
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

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
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
        //mTts.startSpeaking(carnum+"???????????????", mSynListener);
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

    private void jump() {
        TranslateAnimation down = new TranslateAnimation(0, 0, -300, 0);//??????????????????button?????????300??????????????????
        down.setFillAfter(true);
        down.setInterpolator(new BounceInterpolator());//????????????,??????????????????????????????????????????????????????
        down.setDuration(2000);//????????????
        admissionCancelAdmission.startAnimation(down);//?????????????????????????????????
        admissionConfirmAdmission.startAnimation(down);//?????????????????????????????????
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

    //????????????
    @OnClick({R.id.admission_moto, R.id.admission_smallcar, R.id.admission_middle,
            R.id.admission_bigcar, R.id.et_car_license_inputbox1, R.id.et_car_license_inputbox2,
            R.id.et_car_license_inputbox3, R.id.et_car_license_inputbox4, R.id.et_car_license_inputbox5,
            R.id.et_car_license_inputbox6, R.id.et_car_license_inputbox7, R.id.et_car_license_inputbox8,
            R.id.admission_Cancel_admission, R.id.admission_Confirm_admission, R.id.admission_return})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //????????????
            case R.id.admission_moto:
                initcolor();
                carType = 1;
                admissionMoto.setBackgroundColor(Color.parseColor("#1e7db4"));
                admissionMotoimg.setImageResource(R.mipmap.ic_moto_bike_w);
                admissionMototext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.admission_smallcar:
                carType = 2;
                initcolor();
                admissionSmallcar.setBackgroundColor(Color.parseColor("#1e7db4"));
                admissionSmallcarimg.setImageResource(R.mipmap.ic_small_car_w);
                admissionSmallcartext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.admission_middle:
                carType = 3;
                initcolor();
                admissionMiddle.setBackgroundColor(Color.parseColor("#1e7db4"));
                admissionMiddleimg.setImageResource(R.mipmap.ic_mid_truck_w);
                admissionMiddletext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.admission_bigcar:
                carType = 4;
                initcolor();
                admissionBigcar.setBackgroundColor(Color.parseColor("#1e7db4"));
                admissionBigcarimg.setImageResource(R.mipmap.ic_big_truck_w);
                admissionBigcartext.setTextColor(Color.parseColor("#FFFFFF"));
                break;

            //??????edittext????????????
            case R.id.et_car_license_inputbox1:
                LicenseKeyboardUtil_cario.currentEditText_cario = 0;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox2:
                LicenseKeyboardUtil_cario.currentEditText_cario = 1;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox3:
                LicenseKeyboardUtil_cario.currentEditText_cario = 2;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox4:
                LicenseKeyboardUtil_cario.currentEditText_cario = 3;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox5:
                LicenseKeyboardUtil_cario.currentEditText_cario = 4;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox6:
                LicenseKeyboardUtil_cario.currentEditText_cario = 5;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox7:
                LicenseKeyboardUtil_cario.currentEditText_cario = 6;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox8:
                LicenseKeyboardUtil_cario.currentEditText_cario = 7;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.admission_Cancel_admission:
                //startActivity(new Intent(Admission.this, IndexActivity.class));
                finish();
                break;
            case R.id.admission_Confirm_admission:
                editS = etCarLicenseInputbox1.getText().toString() +
                        etCarLicenseInputbox2.getText().toString() +
                        etCarLicenseInputbox3.getText().toString() +
                        etCarLicenseInputbox4.getText().toString() +
                        etCarLicenseInputbox5.getText().toString() +
                        etCarLicenseInputbox6.getText().toString() +
                        etCarLicenseInputbox7.getText().toString() +
                        etCarLicenseInputbox8.getText().toString();
                if (TextUtils.isEmpty(etCarLicenseInputbox1.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox2.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox3.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox4.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox5.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox6.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox7.getText().toString())) {
                    Toasty.error(Admission.this, getString(R.string.enter_correct_license_plate_number), Toast.LENGTH_SHORT, true).show();
                } else {
                    if (getConnectedType(this) == ConnectivityManager.TYPE_WIFI) {
                        carinto(App.serverurl);
                    } else {
                        //Toast.makeText(this, "????????????wifi", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder normalDialog = new AlertDialog.Builder(Admission.this);
                        normalDialog.setTitle(getString(R.string.reminder));
                        normalDialog.setMessage(getString(R.string.is_upload_pic));
                        normalDialog.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                putimg = true;
                                carinto(App.serverurl);
                            }
                        });
                        normalDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                putimg = false;
                                carinto(App.serverurl);
                            }
                        });
                        // ??????
                        normalDialog.show();
                    }


                }
                break;
            //????????????
            case R.id.admission_return:
                //startActivity(new Intent(Admission.this, IndexActivity.class));
                finish();
                break;
        }
    }

    //???????????????
    private void initcolor() {
        admissionMoto.setBackgroundColor(Color.parseColor("#FFFFFF"));
        admissionMotoimg.setImageResource(R.mipmap.ic_moto_bike);
        admissionMototext.setTextColor(Color.parseColor("#48495f"));
        admissionSmallcar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        admissionSmallcarimg.setImageResource(R.mipmap.ic_small_car);
        admissionSmallcartext.setTextColor(Color.parseColor("#48495f"));
        admissionMiddle.setBackgroundColor(Color.parseColor("#FFFFFF"));
        admissionMiddleimg.setImageResource(R.mipmap.ic_mid_truck);
        admissionMiddletext.setTextColor(Color.parseColor("#48495f"));
        admissionBigcar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        admissionBigcarimg.setImageResource(R.mipmap.ic_big_truck);
        admissionBigcartext.setTextColor(Color.parseColor("#48495f"));
    }

    //???????????????????????????
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        keyboardUtil.hideKeyboard();
        return super.onTouchEvent(event);
    }

    //????????????
    private void carinto(String url) {
        dialog1 = new ZLoadingDialog(Admission.this);
        dialog1.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();
        //{"cmd":"140","type":"2","code":"17083B3DE","dsv":"110","ptype":"0","io":"0",
        // "num":"???B1FL39","ctype":"2","spare":"0","sign":"abcd"}
      /*  String intocar_jS = "{\"cmd\":\"140\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"ptype\":\"0\",\"io\":\"0\",\"num\":\"" + editS + "\"," +
                "\"ctype\":\"" + carType + "\",\"spare\":\"0\",\"sign\":\"abcd\"}";*/

        String intocar_jS ="{\"cmd\":\"140\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+ Constant.CODE+"\",\"dsv\":\""
                + Constant.DSV+"\",\"ptype\":\"0\",\"io\":\"0\",\"num\":\""+editS+"\",\"ctype\":\""+carType+
                "\",\"muna\":\"0\",\"spare\":\"0\",\"sign\":\"abcd\"}";
        Log.e("TAG", intocar_jS);
        OkHttpUtils.postString().url(url)
                .content(intocar_jS)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("TAG", "?????????");
                Toasty.error(Admission.this, getString(R.string.please_check_the_network), Toast.LENGTH_SHORT, true).show();
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
                    carnum = datajson.getString("num");//?????????
                    rstat = datajson.getInt("rstat");//????????????
                    //rstat=3;
                    ptype = datajson.getInt("ptype");//??????????????????????????????
                    ctype = datajson.getInt("ctype");//????????????
                    cdtp = datajson.getInt("cdtp");//???????????????????????????
                    long ctime = datajson.getLong("ctime");//????????????
                    preson = datajson.getInt("preson");//??????
                    long itime = datajson.getLong("itime");//????????????
                    int nmon = datajson.getInt("nmon");//????????????
                    int rmon = datajson.getInt("rmon");//????????????
                    int smon = datajson.getInt("smon");//????????????
                    sid = datajson.getString("sid");
                    Log.e("TAG", "sid*****:" + sid);
                    //????????????
                    if (sid != null && putimg&&putbitmappath!=null) {
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
                        Intent zdintent = new Intent(Admission.this, CarintoSuccessful.class);
                        zdintent.putExtra("carnum", carnum);
                        zdintent.putExtra("jfType", jfType);
                        zdintent.putExtra("ctype", ctype);
                        zdintent.putExtra("itime", itime);
                        zdintent.putExtra("cdtp", cdtp);
                        startActivity(zdintent);
                        finish();
                        //Toast.makeText(InputCarnum.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else if (rstat == 1) {
                        //????????????
                        confirmjudge();
                        Intent qrintent = new Intent(Admission.this, CarinConfirmPass.class);
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
                        Intent sfintent = new Intent(Admission.this, CarinChargeActivity.class);
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
                        Intent jzintent = new Intent(Admission.this, ProhibitPass.class);
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


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x0123) {
                //Toast.makeText(Admission.this, "????????????", Toast.LENGTH_LONG).show();
                Log.e("TAG", "????????????");
            } else if (msg.what == 0x0124) {
                //Toast.makeText(Admission.this, "???????????????????????????", Toast.LENGTH_LONG).show();
                Log.e("TAG", "???????????????????????????");
            } else if (msg.what == 0x0125) {
                //Toast.makeText(Admission.this, "???????????????????????????", Toast.LENGTH_LONG).show();
                Log.e("TAG", "???????????????????????????");
            }
        }
    };
}

 /*???????????????
           Bitmap dstBmp = null;
            if (bitmap.getWidth() >= bitmap.getHeight()){

                dstBmp = Bitmap.createBitmap(
                        bitmap,
                        bitmap.getWidth()/2 - bitmap.getHeight()/2,
                        0,
                        bitmap.getHeight(),
                        bitmap.getHeight()
                );

            }else{

                dstBmp = Bitmap.createBitmap(
                        bitmap,
                        0,
                        bitmap.getHeight()/2 - bitmap.getWidth()/2,
                        bitmap.getWidth(),
                        bitmap.getWidth()
                );
            }
            admissionImg.setImageBitmap(dstBmp);*/