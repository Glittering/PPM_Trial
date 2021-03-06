package com.example.npttest.activity;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npttest.R;
import com.example.npttest.util.MPermissionHelper;
import com.example.npttest.util.SPUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ice.iceplate.ActivateService;

import java.io.File;
import java.util.Hashtable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by liuji on 2017/10/17.
 */

public class GuideActivity extends BaseActivity implements MPermissionHelper.PermissionCallBack {
    @Bind(R.id.guide_img)
    ImageView guideImg;
    @Bind(R.id.guide_tv)
    TextView guideTv;
    @Bind(R.id.guide_btn)
    Button guideBtn;
    @Bind(R.id.guide_et)
    EditText guideEt;
    private MPermissionHelper permissionHelper;
    public ActivateService.ActivateBinder acBinder;
    public ServiceConnection acConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            acConnection = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            acBinder = (ActivateService.ActivateBinder) service;
        }

    };
    private String scode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);
        ButterKnife.bind(this);
        Intent actiIntent = new Intent(GuideActivity.this, ActivateService.class);
        bindService(actiIntent, acConnection, Service.BIND_AUTO_CREATE);
        permissionHelper = new MPermissionHelper(this);
        //????????????
        permissionHelper.requestPermission(this,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String szImei = TelephonyMgr.getDeviceId();
        scode = "PPM" + szImei;
        guideTv.setText(scode);
        SPUtils.put(GuideActivity.this, "code", scode);
        //Content.CODE= (String) SPUtils.get(GuideActivity.this,"code","");
        try {
            Bitmap bitmap = CreateCode("PPM" + szImei, BarcodeFormat.QR_CODE, 256, 256);
            guideImg.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        //Bitmap bitmap = null;
        /*try {
            bitmap = BitmapUtils.create2DCode("ppm" + szImei);//???????????????????????????
            guideImg.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }*/
        File file = getFilesDir();
        String path = file.getAbsolutePath();
        System.out.println("path = " + path);
        Log.i("wu", "path = " + path);

        guideTv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            //????????????????????????????????? ??????????????????
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //??????????????????????????????
                GuideActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //?????????????????????
                int screenHeight = GuideActivity.this.getWindow().getDecorView().getRootView().getHeight();
                //????????????????????????????????????????????? ?????????????????????????????? ????????????0 ????????????????????????????????????
                int heightDifference = screenHeight - r.bottom;
                //Log.e("TAG", "Size: " + heightDifference+"???????????????"+screenHeight);

                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

        });
    }


    @OnClick(R.id.guide_btn)
    public void onViewClicked() {
        if (TextUtils.isEmpty(guideEt.getText().toString().trim())) {
            Toast.makeText(this, R.string.authorization_code_cannot_be_empty, Toast.LENGTH_SHORT).show();
        } else {
            activateSN();
        }
    }

    //????????????
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.handleRequestPermissionsResult(requestCode, this, grantResults);
    }

    @Override
    public void permissionRegisterSuccess(String... permissions) {
        //Toast.makeText(this, "??????", Toast.LENGTH_SHORT).show();
        Log.e("TAG", "????????????");
    }

    @Override
    public void permissionRegisterError(String... permissions) {
        permissionHelper.showGoSettingPermissionsDialog(getString(R.string.positioning));
    }

    protected void onDestroy() {
        super.onDestroy();
        permissionHelper.destroy();
        if (acBinder != null) {
            unbindService(acConnection);
        }
    }

    private void activateSN() {
        int code = acBinder.login(guideEt.getText().toString().trim());
        if (code == 0) {
            SPUtils.put(GuideActivity.this, "frist", false);
            //SPUtils.put(GuideActivity.this, "sn", false);
            //new AlertDialog.Builder(SpalshActivity.this).setMessage("????????????!").show();
            Toasty.success(this, getString(R.string.camera_authorized_successfully), Toast.LENGTH_SHORT, true).show();
            //Toast.makeText(this, "????????????!", Toast.LENGTH_SHORT).show();

        } else if (code == 1795) {
            //new AlertDialog.Builder(SpalshActivity.this).setMessage("???????????????????????????????????????????????????????????????????????????????????????????????????").show();
            Toast.makeText(this, R.string.the_number_of_activated_machines_has_reached_the_limit, Toast.LENGTH_SHORT).show();
        } else if (code == 1793) {
            //new AlertDialog.Builder(SpalshActivity.this).setMessage("??????????????????").show();
            Toast.makeText(this, R.string.authorization_code_has_expired, Toast.LENGTH_SHORT).show();
        } else if (code == 276) {
           //Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            Log.e("TAG","????????????????????????code???"+code);
        } else if (code == 284) {
            //new AlertDialog.Builder(SpalshActivity.this).setMessage("?????????????????????").show();
            //Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
        } else {
            //new AlertDialog.Builder(SpalshActivity.this).setMessage("????????????" + code).show();
            Toast.makeText(this, getString(R.string.error_code) + code, Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(GuideActivity.this, SpalshActivity.class));
        finish();
    }

    public Bitmap CreateCode(String str, BarcodeFormat type, int bmpWidth, int bmpHeight) throws WriterException {
        Hashtable<EncodeHintType, String> mHashtable = new Hashtable<EncodeHintType, String>();
        mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // ??????????????????,????????????????????????,??????????????????????????????????????????,??????????????????????????????
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // ????????????????????????????????????????????????????????????
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // ????????????????????????bitmap,????????????????api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}