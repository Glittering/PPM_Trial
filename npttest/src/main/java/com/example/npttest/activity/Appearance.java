package com.example.npttest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.npttest.util.FileUtils.SDPATH;

/**
 * Created by liuji on 2017/8/1.
 */

public class Appearance extends NoStatusbarActivity {

    @Bind(R.id.appearance_return)
    ImageView appearanceReturn;
    @Bind(R.id.appearance_img)
    ImageView appearanceImg;
    @Bind(R.id.appearance_motoimg)
    ImageView appearanceMotoimg;
    @Bind(R.id.appearance_mototext)
    TextView appearanceMototext;
    @Bind(R.id.appearance_moto)
    LinearLayout appearanceMoto;
    @Bind(R.id.appearance_smallcarimg)
    ImageView appearanceSmallcarimg;
    @Bind(R.id.appearance_smallcartext)
    TextView appearanceSmallcartext;
    @Bind(R.id.appearance_smallcar)
    LinearLayout appearanceSmallcar;
    @Bind(R.id.appearance_middleimg)
    ImageView appearanceMiddleimg;
    @Bind(R.id.appearance_middletext)
    TextView appearanceMiddletext;
    @Bind(R.id.appearance_middle)
    LinearLayout appearanceMiddle;
    @Bind(R.id.appearance_bigcarimg)
    ImageView appearanceBigcarimg;
    @Bind(R.id.appearance_bigcartext)
    TextView appearanceBigcartext;
    @Bind(R.id.appearance_bigcar)
    LinearLayout appearanceBigcar;
    @Bind(R.id.et_car_license_inputbox1_out)
    EditText etCarLicenseInputbox1Out;
    @Bind(R.id.et_car_license_inputbox2_out)
    EditText etCarLicenseInputbox2Out;
    @Bind(R.id.et_car_license_inputbox3_out)
    EditText etCarLicenseInputbox3Out;
    @Bind(R.id.et_car_license_inputbox4_out)
    EditText etCarLicenseInputbox4Out;
    @Bind(R.id.et_car_license_inputbox5_out)
    EditText etCarLicenseInputbox5Out;
    @Bind(R.id.et_car_license_inputbox6_out)
    EditText etCarLicenseInputbox6Out;
    @Bind(R.id.et_car_license_inputbox7_out)
    EditText etCarLicenseInputbox7Out;
    @Bind(R.id.et_car_license_inputbox8_out)
    EditText etCarLicenseInputbox8Out;
    @Bind(R.id.ll_license_input_boxes_content_out)
    LinearLayout llLicenseInputBoxesContentOut;
    @Bind(R.id.appearance_Cancel_admission)
    Button appearanceCancelAdmission;
    @Bind(R.id.appearance_Confirm_admission)
    Button appearanceConfirmAdmission;
    @Bind(R.id.keyboard_view)
    KeyboardView keyboardView;
    @Bind(R.id.appearance_error)
    LinearLayout appearanceError;
    private String bitmapPath, putbitmappath;
    private Bitmap bitmap = null;
    public static final String INPUT_LICENSE_COMPLETE = "me.kevingo.licensekeyboard.input.comp";
    public static final String INPUT_LICENSE_KEY = "LICENSE";
    private LicenseKeyboardUtil_cario keyboardUtil;
    private EditText edits[];
    private String s, editS;
    private String s1;
    public static Activity activity;
    private int carType = 2;
    private String car_type;
    private int rstat, ptype, ctype, cdtp, preson;
    private String jfType, carnum;//????????????
    private String comfirmYy;//????????????
    private ZLoadingDialog dialog, dialog1;
    SynthesizerListener mSynListener;
    private OSS oss;
    private boolean putimg = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appearance);
        activity = this;
        ButterKnife.bind(this);
        if (App.serverurl != null) {
            getosskey(App.serverurl);
        }
        SpeechUtility.createUtility(Appearance.this, SpeechConstant.APPID + "=59df2c0c");
        Intent intent = getIntent();
        s = intent.getStringExtra("number");
        s1 = intent.getStringExtra("color");
        bitmapPath = intent.getStringExtra("path").toString();
        if (bitmapPath != null && !bitmapPath.equals("")) {
           /* bitmap = BitmapFactory.decodeFile(bitmapPath);
            //???????????????????????????????????????????????????   ??????????????????????????????
           *//* bitmap = Bitmap.createBitmap(bitmap, left, top, w, h);
            if (bitmap != null) {
                appearanceImg.setImageBitmap(bitmap);
            }*//*
            Bitmap bitmap1 = ImageCrop(bitmap, 125, 300, true);
            appearanceImg.setImageBitmap(bitmap1);*/
            bitmap = BitmapFactory.decodeFile(bitmapPath);
            Bitmap bitmap1 = ImageCrop(bitmap, 125, 300, true);
            appearanceImg.setImageBitmap(bitmap1);
            Bitmap newbitmap = PictureUtil.getSmallBitmap(bitmapPath, 480, 800);
            FileUtils.saveBitmap(newbitmap, pictureName());
            putbitmappath = SDPATH + pictureName() + ".JPEG";
            Log.e("TAG", putbitmappath);
        }
        char[] carnumber = s.toCharArray();
        if (carnumber.length == 7) {
            LicenseKeyboardUtil_cario.etsize_cario = 6;
            etCarLicenseInputbox8Out.setVisibility(View.GONE);
            etCarLicenseInputbox1Out.setText(String.valueOf(carnumber[0]));
            etCarLicenseInputbox2Out.setText(String.valueOf(carnumber[1]));
            etCarLicenseInputbox3Out.setText(String.valueOf(carnumber[2]));
            etCarLicenseInputbox4Out.setText(String.valueOf(carnumber[3]));
            etCarLicenseInputbox5Out.setText(String.valueOf(carnumber[4]));
            etCarLicenseInputbox6Out.setText(String.valueOf(carnumber[5]));
            etCarLicenseInputbox7Out.setText(String.valueOf(carnumber[6]));
        } else if (carnumber.length == 8) {
            LicenseKeyboardUtil_cario.etsize_cario = 7;
            etCarLicenseInputbox1Out.setText(String.valueOf(carnumber[0]));
            etCarLicenseInputbox2Out.setText(String.valueOf(carnumber[1]));
            etCarLicenseInputbox3Out.setText(String.valueOf(carnumber[2]));
            etCarLicenseInputbox4Out.setText(String.valueOf(carnumber[3]));
            etCarLicenseInputbox5Out.setText(String.valueOf(carnumber[4]));
            etCarLicenseInputbox6Out.setText(String.valueOf(carnumber[5]));
            etCarLicenseInputbox7Out.setText(String.valueOf(carnumber[6]));
            etCarLicenseInputbox8Out.setText(String.valueOf(carnumber[7]));
        }

        edits = new EditText[]{etCarLicenseInputbox1Out, etCarLicenseInputbox2Out, etCarLicenseInputbox3Out,
                etCarLicenseInputbox4Out, etCarLicenseInputbox5Out, etCarLicenseInputbox6Out,
                etCarLicenseInputbox7Out, etCarLicenseInputbox8Out};
        //????????????????????????intent?????????
        IntentFilter finishFilter = new IntentFilter(INPUT_LICENSE_COMPLETE);
        keyboardUtil = new LicenseKeyboardUtil_cario(Appearance.this, edits);
        querycartype(App.serverurl);
        ActivityManager.getInstance().addActivity(this);
    }

    //????????????
    private void querycartype(String url) {
        String str = "{\"cmd\":\"143\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                "\"dsv\":\"" + Constant.DSV + "\",\"ptype\":\"0\",\"num\":\"" + s + "\",\"spare\":\"0\",\"sign\":\"abcd\"}";
        Log.e("TAG", str);
        OkHttpUtils.postString()
                .url(url).content(str)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG", "????????????????????????" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject resultjsonobject = jsonObject.getJSONObject("result");
                    int code=jsonObject.getInt("code");
                    if (code==100) {
                        JSONObject datajsonobject = resultjsonobject.getJSONObject("data");
                        int ctype = datajsonobject.getInt("ctype");
                        if (ctype==0){
                            appearanceError.setVisibility(View.VISIBLE);
                        }else {
                            appearanceError.setVisibility(View.GONE);
                            carType = ctype;
                            switch (ctype) {
                                case 1:
                                    appearanceMoto.setBackgroundColor(Color.parseColor("#1e7db4"));
                                    appearanceMotoimg.setImageResource(R.mipmap.ic_moto_bike_w);
                                    appearanceMototext.setTextColor(Color.parseColor("#FFFFFF"));
                                    break;
                                case 2:
                                    appearanceSmallcar.setBackgroundColor(Color.parseColor("#1e7db4"));
                                    appearanceSmallcarimg.setImageResource(R.mipmap.ic_small_car_w);
                                    appearanceSmallcartext.setTextColor(Color.parseColor("#FFFFFF"));
                                    break;
                                case 3:
                                    appearanceMiddle.setBackgroundColor(Color.parseColor("#1e7db4"));
                                    appearanceMiddleimg.setImageResource(R.mipmap.ic_mid_truck_w);
                                    appearanceMiddletext.setTextColor(Color.parseColor("#FFFFFF"));
                                    break;
                                case 4:
                                    appearanceBigcar.setBackgroundColor(Color.parseColor("#1e7db4"));
                                    appearanceBigcarimg.setImageResource(R.mipmap.ic_big_truck_w);
                                    appearanceBigcartext.setTextColor(Color.parseColor("#FFFFFF"));
                                    break;
                            }
                        }
                    } else {
                        //Toast.makeText(Appearance.this, "????????????", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
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
                Toast.makeText(Appearance.this, R.string.please_check_the_network, Toast.LENGTH_SHORT).show();
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

    //??????????????????
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return output;
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

    //????????????????????????
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

    @OnClick({R.id.appearance_return, R.id.et_car_license_inputbox1_out, R.id.et_car_license_inputbox2_out,
            R.id.et_car_license_inputbox3_out, R.id.et_car_license_inputbox4_out, R.id.et_car_license_inputbox5_out,
            R.id.et_car_license_inputbox6_out, R.id.et_car_license_inputbox7_out, R.id.et_car_license_inputbox8_out,
            R.id.appearance_Cancel_admission, R.id.appearance_Confirm_admission, R.id.appearance_moto,
            R.id.appearance_smallcar, R.id.appearance_middle, R.id.appearance_bigcar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.appearance_return:
                finish();
                break;
            case R.id.et_car_license_inputbox1_out:
                LicenseKeyboardUtil_cario.currentEditText_cario = 0;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox2_out:
                LicenseKeyboardUtil_cario.currentEditText_cario = 1;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox3_out:
                LicenseKeyboardUtil_cario.currentEditText_cario = 2;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox4_out:
                LicenseKeyboardUtil_cario.currentEditText_cario = 3;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox5_out:
                LicenseKeyboardUtil_cario.currentEditText_cario = 4;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox6_out:
                LicenseKeyboardUtil_cario.currentEditText_cario = 5;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox7_out:
                LicenseKeyboardUtil_cario.currentEditText_cario = 6;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.et_car_license_inputbox8_out:
                LicenseKeyboardUtil_cario.currentEditText_cario = 7;
                initcolor1();
                edits[LicenseKeyboardUtil_cario.currentEditText_cario].setBackgroundResource(R.drawable.keyboard_bg_red);
                keyboardUtil.showKeyboard();
                break;
            case R.id.appearance_Cancel_admission:
                finish();
                break;
            case R.id.appearance_Confirm_admission:
                editS = etCarLicenseInputbox1Out.getText().toString() +
                        etCarLicenseInputbox2Out.getText().toString() +
                        etCarLicenseInputbox3Out.getText().toString() +
                        etCarLicenseInputbox4Out.getText().toString() +
                        etCarLicenseInputbox5Out.getText().toString() +
                        etCarLicenseInputbox6Out.getText().toString() +
                        etCarLicenseInputbox7Out.getText().toString() +
                        etCarLicenseInputbox8Out.getText().toString();
                if (TextUtils.isEmpty(etCarLicenseInputbox1Out.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox2Out.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox3Out.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox4Out.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox5Out.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox6Out.getText().toString()) ||
                        TextUtils.isEmpty(etCarLicenseInputbox7Out.getText().toString())) {
                    Toasty.error(Appearance.this, getString(R.string.enter_correct_license_plate_number), Toast.LENGTH_SHORT, true).show();
                } else {
                    if (getConnectedType(this) == ConnectivityManager.TYPE_WIFI) {
                        carout(App.serverurl);
                    } else {
                        //Toast.makeText(this, "????????????wifi", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder normalDialog = new AlertDialog.Builder(Appearance.this);
                        normalDialog.setTitle(getString(R.string.reminder));
                        normalDialog.setMessage(getString(R.string.is_upload_pic));
                        normalDialog.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                putimg = true;
                                carout(App.serverurl);
                            }
                        });
                        normalDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                putimg = false;
                                carout(App.serverurl);
                            }
                        });
                        // ??????
                        normalDialog.show();
                    }
                }
                break;
            case R.id.appearance_moto:
                initcolor();
                carType = 1;
                appearanceMoto.setBackgroundColor(Color.parseColor("#1e7db4"));
                appearanceMotoimg.setImageResource(R.mipmap.ic_moto_bike_w);
                appearanceMototext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.appearance_smallcar:
                carType = 2;
                initcolor();
                appearanceSmallcar.setBackgroundColor(Color.parseColor("#1e7db4"));
                appearanceSmallcarimg.setImageResource(R.mipmap.ic_small_car_w);
                appearanceSmallcartext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.appearance_middle:
                carType = 3;
                initcolor();
                appearanceMiddle.setBackgroundColor(Color.parseColor("#1e7db4"));
                appearanceMiddleimg.setImageResource(R.mipmap.ic_mid_truck_w);
                appearanceMiddletext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case R.id.appearance_bigcar:
                carType = 4;
                initcolor();
                appearanceBigcar.setBackgroundColor(Color.parseColor("#1e7db4"));
                appearanceBigcarimg.setImageResource(R.mipmap.ic_big_truck_w);
                appearanceBigcartext.setTextColor(Color.parseColor("#FFFFFF"));
                break;
        }
    }

    //????????????
    private void carout(String url) {
        dialog = new ZLoadingDialog(Appearance.this);
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

        String intocar_jS = "{\"cmd\":\"140\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\",\"dsv\":\""
                + Constant.DSV + "\",\"ptype\":\"0\",\"io\":\"1\",\"num\":\"" + editS + "\",\"ctype\":\"" + carType +
                "\",\"muna\":\"0\",\"spare\":\"0\",\"sign\":\"abcd\"}";
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
                    Log.e("TAG", "sid*****:" + sid);
                    //????????????
                    if (sid != null && putimg && putbitmappath != null) {
                        String ppmbucket = Constant.ppmBucket;
                        //String ppmimgurl=Constant.CODE+"/"+sid+".jpg";
                        String ppmimgurl = MD5Util.MD5Encode(Constant.CODE +"1"+ sid) + ".jpg";
                        String ppmfileurl = putbitmappath;
                        Log.e("TAG", sid);
                        putdate(ppmbucket, ppmimgurl, ppmfileurl);
                    }
                    //??????
                    jfjudge();
                    if (rstat == 0) {
                        //????????????
                        carout_start_voice();
                        Intent zdintent = new Intent(Appearance.this, CaroutSuccessful.class);
                        zdintent.putExtra("carnum", carnum);
                        zdintent.putExtra("jfType", jfType);
                        zdintent.putExtra("ctype", ctype);
                        zdintent.putExtra("ctime", ctime);
                        zdintent.putExtra("itime", itime);
                        startActivity(zdintent);
                        finish();
                        //Toast.makeText(InputCarnum.this, "????????????", Toast.LENGTH_SHORT).show();
                    } else if (rstat == 1) {
                        //????????????
                        confirmjudge();
                        Intent qrintent = new Intent(Appearance.this, CaroutConfirmPass.class);
                        qrintent.putExtra("carnum", carnum);
                        qrintent.putExtra("jfType", jfType);
                        qrintent.putExtra("ctype", ctype);
                        qrintent.putExtra("itime", itime);
                        qrintent.putExtra("ctime", ctime);
                        qrintent.putExtra("comfirmYy", comfirmYy);
                        qrintent.putExtra("sid", sid);
                        qrintent.putExtra("cdtp", cdtp);
                        startActivity(qrintent);

                    } else if (rstat == 2) {
                        //????????????
                        chargejudge();
                        Intent sfintent = new Intent(Appearance.this, CaroutChargeActivity.class);
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
                        startActivity(sfintent);
                    } else if (rstat == 3) {
                        //????????????
                        prohibitjudge();
                        Intent jzintent = new Intent(Appearance.this, ProhibitPass.class);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        keyboardUtil.hideKeyboard();
        return super.onTouchEvent(event);
    }

    //???????????????
    private void initcolor() {
        appearanceMoto.setBackgroundColor(Color.parseColor("#FFFFFF"));
        appearanceMotoimg.setImageResource(R.mipmap.ic_moto_bike);
        appearanceMototext.setTextColor(Color.parseColor("#48495f"));
        appearanceSmallcar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        appearanceSmallcarimg.setImageResource(R.mipmap.ic_small_car);
        appearanceSmallcartext.setTextColor(Color.parseColor("#48495f"));
        appearanceMiddle.setBackgroundColor(Color.parseColor("#FFFFFF"));
        appearanceMiddleimg.setImageResource(R.mipmap.ic_mid_truck);
        appearanceMiddletext.setTextColor(Color.parseColor("#48495f"));
        appearanceBigcar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        appearanceBigcarimg.setImageResource(R.mipmap.ic_big_truck);
        appearanceBigcartext.setTextColor(Color.parseColor("#48495f"));
    }
}
