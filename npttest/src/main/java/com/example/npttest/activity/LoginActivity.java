package com.example.npttest.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.adapter.PopAdapter;
import com.example.npttest.constant.Constant;
import com.example.npttest.manager.ActivityManager;
import com.example.npttest.util.LogUtils;
import com.example.npttest.util.MD5Utils;
import com.example.npttest.util.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.npttest.constant.Constant.TABLE_UNAME;
import static com.example.npttest.constant.Constant.UTIME;


/**
 * Created by Administrator on 2017/7/26.
 */

public class LoginActivity extends NoStatusbarActivity implements View.OnClickListener, OnItemSwipeListener {
    private EditText loginEdtId;
    private TextView loginVerification,loginDomeLogin;
    private Button loginBtnClean1;
    private EditText loginEdtPwd;
    private Button loginBtnClean2;
    private Button loginBtnEye;
    private boolean pwdflag = true;//??????????????????flag
    private Button loginBtnLogin;
    private String id,pwd;
    //???????????????????????????????????????
    private long firsttime=0;
    private ZLoadingDialog dialog;
    private Button popDown;
    private PopupWindow popupWindow;
    private PopAdapter popAdapter;
    private LinearLayout lin_ver;//????????????????????????
    private LinearLayout lin_pwd;//????????????????????????
    private LinearLayout parent,login_main;//????????????????????????
    private CloudPushService mPushService;

    private int pwidth;// ?????????????????????
    private boolean init_flag = false;// ???????????????????????????
    private List<String> list=new ArrayList<>();
    private Boolean vg_flag=true;
    private  RecyclerView recyclerView;
    private String username;
    private String uname,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActivityManager.getInstance().addActivity(this);
        mPushService = PushServiceFactory.getCloudPushService();
        Constant.CODE = (String) SPUtils.get(LoginActivity.this, "code", "");
        //??????
        /*String sql_del="delete from "+TABLE_UNAME+" where "+UNAME+"= '"+"?????????"+"'";
        //???????????????????????????
        Log.e("TAG","?????????"+sql_del);
        SQLiteDatabase db=App.dbHelper.getWritableDatabase();
        db.execSQL(sql_del);//??????sql??????
        Toasty.success(LoginActivity.this,"????????????",Toast.LENGTH_SHORT,true).show();
        db.close();*/
        //query_DB();
        //????????????
    }

    private void query_DB() {
        String sql_user="select * from "+ TABLE_UNAME +" order by " + UTIME +" desc ";
        getdata(sql_user);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e("TAG","onWindowFocusChanged");
       while (!init_flag){
           Log.e("TAG","*********");
           initView();
           initPopuWindow();
            init_flag = true;
        }
    }

    //??????sql??????(??????)
    private void getdata(String sql_user) {
        list.clear();
        // String sql_user="select * from "+ Constant.TABLE_USER;
        SQLiteDatabase sdb = App.dbHelper.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(sql_user, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String s = cursor.getString(cursor.getColumnIndex(Constant.UNAME));
                Log.e("TAG", s);
                list.add(s);
            }
            cursor.close();
            sdb.close();
        }
    }


    //??????sql??????????????????
    private void getdata_insert(String sql_user) {
        //list.clear();
        // String sql_user="select * from "+ Constant.TABLE_USER;
        SQLiteDatabase sdb = App.dbHelper.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(sql_user, null);

        if (cursor.getCount() != 0) {
            String sql_update="update "+ Constant.TABLE_UNAME+" set "+ Constant.UNAME+"='"
                    +loginEdtId.getText().toString().trim()+"',"+ Constant.UTIME+"="
                    +gettime()+" where "+ Constant.UNAME+"="+"'"+loginEdtId.getText().toString().trim()+"'";
            Log.e("TAG","?????????"+sql_update);
            SQLiteDatabase db=App.dbHelper.getWritableDatabase();
            db.execSQL(sql_update);
            db.close();
            //Toasty.success(LoginActivity.this,"????????????",Toast.LENGTH_SHORT,true).show();
        }else {
            //String sql="insert into "+  Constant.TABLE_UNAME+"("+Constant.RRSTRING+") values('"+remags.getText().toString()+"')";
            String sql="insert into "+  Constant.TABLE_UNAME+"("+ Constant.UNAME+","+ Constant.UTIME+") values('"+loginEdtId.getText().toString().trim()+"',"+gettime()+")";
            //???????????????????????????
            Log.e("TAG","?????????"+sql);
            SQLiteDatabase db=App.dbHelper.getWritableDatabase();
            db.execSQL(sql);//??????sql??????
            //Toasty.success(LoginActivity.this,"????????????",Toast.LENGTH_SHORT,true).show();
            db.close();
        }
    }


    //??????????????????
    private long gettime(){
        Date date=new Date();
        long time = (date.getTime()/1000);
        //Log.e("TAG",date.getTime()/1000+"");
        return time;
    }

    /**
     * ?????????????????????
     */
    public void initPopuWindow() {
        // ?????????????????????
        View loginwindow = getLayoutInflater().inflate(R.layout.popup_window, null);
        SwipeRefreshLayout swipeRefreshLayout= loginwindow.findViewById(R.id.pop_sweipeLayout);
        recyclerView= loginwindow.findViewById(R.id.pop_RecyclerView);
        swipeRefreshLayout.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //item ????????????
        recyclerView.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //Toast.makeText(LoginActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                //popupWindow.isShowing();
                //init_flag=true;
                //loginEdtId.setText(list.get(position));
                loginEdtId.setText(popAdapter.getData().get(position));
                String sql_update1="update "+ Constant.TABLE_UNAME+" set "+ Constant.UNAME+"='"
                        +loginEdtId.getText().toString().trim()+"',"+ Constant.UTIME+"="
                        +gettime()+" where "+ Constant.UNAME+"="+"'"+loginEdtId.getText().toString().trim()+"'";
                Log.e("TAG","?????????"+sql_update1);
                SQLiteDatabase db=App.dbHelper.getWritableDatabase();
                db.execSQL(sql_update1);
                db.close();
                //query_DB();
                //popAdapter.notifyDataSetChanged();
                //init_flag=false;
                //popupWindow.dismiss();
                //Toasty.success(LoginActivity.this,"????????????",Toast.LENGTH_SHORT,true).show();
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        // ??????????????????
        popAdapter=new PopAdapter(list);
        recyclerView.setAdapter(popAdapter);
        //recyclerView.setAdapter(popAdapter);
        ItemDragAndSwipeCallback swipeCallback = new ItemDragAndSwipeCallback(popAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);

        //??????????????????
        popAdapter.enableSwipeItem();
        swipeCallback.setSwipeMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        popAdapter.setOnItemSwipeListener(this);
        // ????????????????????????????????????
        popupWindow = new PopupWindow(loginwindow, pwidth, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.e("TAG","onDismisspopwindow");
                popDown.setBackgroundResource(R.mipmap.ic_down);
                lin_pwd.setVisibility(View.VISIBLE);
                lin_ver.setVisibility(View.VISIBLE);
                loginBtnLogin.setVisibility(View.VISIBLE);
                query_DB();
            }
        });
        // ??????????????????
        //popAdapter=new PopAdapter(list);
        query_DB();
        if (list.size()!=0){
            loginEdtId.setText(list.get(0));
        }
       /* if (popAdapter.getData().get(0)!=null){
            loginEdtId.setText(popAdapter.getData().get(0));
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //?????????????????????
        /*View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);*/
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.hide();*/
    }

    //???????????????
    private void initView() {
        loginEdtId = (EditText) findViewById(R.id.login_edt_id);
        loginBtnClean1 = (Button) findViewById(R.id.login_btn_clean1);
        loginEdtPwd = (EditText) findViewById(R.id.login_edt_pwd);
        loginBtnClean2 = (Button) findViewById(R.id.login_btn_clean2);
        loginBtnEye = (Button) findViewById(R.id.login_btn_eye);
        loginBtnLogin = (Button) findViewById(R.id.login_btn_login);
        loginVerification= (TextView) findViewById(R.id.login_tv_verification);
        popDown= (Button) findViewById(R.id.login_btn_down);
        parent= (LinearLayout) findViewById(R.id.login_lin_username);
        lin_pwd= (LinearLayout) findViewById(R.id.login_lin_pwd);
        lin_ver= (LinearLayout) findViewById(R.id.login_lin_ver);
        loginDomeLogin=findViewById(R.id.login_tv_domelogin);
        login_main=findViewById(R.id.login_lin_main);

        loginBtnLogin.setOnClickListener(this);
        loginBtnClean1.setOnClickListener(this);
        loginBtnClean2.setOnClickListener(this);
        loginBtnEye.setOnClickListener(this);
        loginVerification.setOnClickListener(this);
        popDown.setOnClickListener(this);
        loginDomeLogin.setOnClickListener(this);
        login_main.setOnClickListener(this);

/*        loginBtnClean1.setVisibility(View.GONE);
        loginBtnClean2.setVisibility(View.GONE);
        loginBtnEye.setVisibility(View.GONE);*/

        //????????????????????????????????????
        loginEdtId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Toast.makeText(LoginActivity.this, "???????????????beforeTextChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //popupWindow.dismiss();
               // Toast.makeText(LoginActivity.this, "???????????????onTextChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(LoginActivity.this, "???????????????afterTextChanged", Toast.LENGTH_SHORT).show();
                //popupWindow.dismiss();
                if (loginEdtId.getSelectionEnd()>0){
                    loginBtnClean1.setVisibility(View.VISIBLE);
                }else {
                    //loginBtnClean1.setVisibility(View.GONE);
                }
            }
        });

        //???????????????????????????
        loginEdtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (loginEdtPwd.getSelectionEnd()>0){
                    loginBtnClean2.setVisibility(View.VISIBLE);
                }else {
                    //loginBtnClean2.setVisibility(View.GONE);
                }
            }
        });

        //???????????????????????????
        loginEdtPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    loginBtnEye.setVisibility(View.VISIBLE);
                }else {
                    //loginBtnEye.setVisibility(View.GONE);
                }
            }
        });

        // ??????????????????????????????????????????????????????????????????
        int w = parent.getWidth();
        pwidth = w;

        login_main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            //????????????????????????????????? ??????????????????
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //??????????????????????????????
                LoginActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //?????????????????????
                int screenHeight = LoginActivity.this.getWindow().getDecorView().getRootView().getHeight();
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


    //???????????????
    public boolean CheckId(){
        if (TextUtils.isEmpty(loginEdtId.getText().toString().trim())){
            loginEdtId.requestFocus();
            loginEdtId.setError(getString(R.string.please_enter_user_name));
            return false;
        }else {
            id = loginEdtId.getText().toString().trim();
            return true;
        }
    }

    //????????????
    public boolean CheckPwd(){
        if (TextUtils.isEmpty(loginEdtPwd.getText().toString())){
            loginEdtPwd.requestFocus();
            loginEdtPwd.setError(getString(R.string.please_enter_the_password));
            return false;
        }else {
            pwd = loginEdtPwd.getText().toString();
            return true;
        }
    }

    /**
     * ??????????????????:CloudPushService.bindAccount????????????
     * 1. ???????????????,??????????????????????????????????????????
     * 2. ????????????????????????????????????
     */
    private void bindAccount() {
        final String account = Constant.CODE;
        if (account.length() > 0) {
            mPushService.bindAccount(account, new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    Log.e("TAG", "????????????");
                }

                @Override
                public void onFailed(String errorCode, String errorMsg) {
                    Log.e("TAG", "????????????");
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn_login:
                if (CheckId()&&CheckPwd()){
                    getParkosUrl();
                }
                break;
            case R.id.login_btn_clean1:
                loginEdtId.setText("");
                break;
            case R.id.login_btn_clean2:
                loginEdtPwd.setText("");
                break;
            case R.id.login_btn_eye:
                loginBtnEye.setBackgroundResource(pwdflag?R.mipmap.ic_password_hide_white:R.mipmap.ic_password_display_white);
                pwdflag = !pwdflag;
                loginEdtPwd.setInputType(!pwdflag?144:129);
                loginEdtPwd.setSelection(loginEdtPwd.getText().length());
                break;
            case R.id.login_tv_verification:
                startActivity(new Intent(LoginActivity.this,VerificationLogin.class));
                //Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_btn_down:
                if (list.size()!=0){
                    Log.e("TAG","onclick");
                    popDown.setBackgroundResource(R.mipmap.ic_up);
                    lin_pwd.setVisibility(View.INVISIBLE);
                    lin_ver.setVisibility(View.INVISIBLE);
                    loginBtnLogin.setVisibility(View.INVISIBLE);
                    vg_flag=!vg_flag;
                    if (init_flag) {
                        // ??????????????????
                        popupWindow.showAsDropDown(parent, 0, -5);
                        init_flag=false;
                    }
                    //popupWindow.showAsDropDown(parent, 0, -3);
                }

                break;

            case R.id.login_tv_domelogin:
                SPUtils.put(LoginActivity.this,"domeloginboo",true);
                Constant.domeLoginBoo=true;
                getParkosUrl();
                break;

            case R.id.login_lin_main:
                popDown.setBackgroundResource(R.mipmap.ic_down);
                lin_pwd.setVisibility(View.VISIBLE);
                lin_ver.setVisibility(View.VISIBLE);
                loginBtnLogin.setVisibility(View.VISIBLE);
                break;
        }
    }

    //??????????????????
    private void login(String url,String jsons){

        Log.e("TAG","???????????????"+jsons);
        OkHttpUtils.postString().url(url)
                .content(jsons)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toasty.error(LoginActivity.this, getString(R.string.please_check_the_network), Toast.LENGTH_SHORT, true).show();
                if (dialog!=null){
                    dialog.dismiss();
                }
                Log.e("TAG","????????????");
            }

            @Override
            public void onResponse(String response, int id) {
                if (dialog!=null){
                    dialog.dismiss();
                }
                Log.e("TAG","??????????????????"+response);
                JSONObject rpjson = null;
                try {
                    rpjson = new JSONObject(response);
                    int code=rpjson.getInt("code");
                    if (code==100){
                        JSONObject resultjson=rpjson.getJSONObject("result");
                        JSONObject datajson = resultjson.getJSONObject("data");
                        int lrs=datajson.getInt("lrs");
                        username=datajson.getString("nname");
                        App.wmon=datajson.getDouble("wmon");
                        Constant.wtime=datajson.getLong("wtime");
                        Constant.enfree=datajson.getInt("enFree");
                        Log.e("TAG","?????????????????????***"+Constant.enfree);
                        if (lrs==0){
                            Log.e("TAG","???????????????code???"+Constant.CODE);
                            bindAccount();
                            if (!Constant.domeLoginBoo){
                                String sql_user="select * from "+ TABLE_UNAME +" where "+ Constant.UNAME+" = "+"'"+loginEdtId.getText().toString().trim()+"'" ;
                                Log.e("TAG","???????????????"+sql_user);
                                getdata_insert(sql_user);
                            }
                            Constant.logintype=0;
                            SPUtils.put(LoginActivity.this, Constant.LOGINTYPE,0);
                            SPUtils.put(LoginActivity.this, Constant.ID,loginEdtId.getText().toString().trim());
                            SPUtils.put(LoginActivity.this, Constant.PASS,MD5Utils.encode(loginEdtPwd.getText().toString().trim()));
                            SPUtils.put(LoginActivity.this, Constant.USERNAME,username);
                            startActivity(new Intent(LoginActivity.this,IndexActivity.class));
                            Toasty.success(LoginActivity.this, "????????????", Toast.LENGTH_SHORT, true).show();
                            finish();
                        }else {
                            Toasty.error(LoginActivity.this, getString(R.string.check_user_name_and_password), Toast.LENGTH_SHORT, true).show();
                        }
                    }else {
                        Toasty.error(LoginActivity.this, getString(R.string.check_if_the_device_is_registered), Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getParkosUrl(){
        if (!isFinishing()){
            dialog = new ZLoadingDialog(LoginActivity.this);
            dialog.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                    .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                    .setHintText("Loading...")
                    .setHintTextColor(Color.parseColor("#55BEB7"))
                    .setHintTextSize(16) // ?????????????????? dp
                    .setHintTextColor(Color.GRAY)  // ??????????????????
                    .show();
        }
        Log.e("TAG","????????????COde???"+Constant.CODE);
        //???????????????
        String parkosObject = "{\"cmd\":\"10\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE
                + "\",\"pid\":\"1\",\"dsv\":\"" + Constant.DSV + "\",\"dhv\":\"121\",\"spare\":\"0\",\"sign\":\"abcd\"}";
        LogUtils.e( parkosObject);
        OkHttpUtils.postString().url(Constant.OSURL)
                .content(parkosObject)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toasty.error(LoginActivity.this,getString(R.string.please_check_the_network),Toast.LENGTH_SHORT,true);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("TAG","??????parkos???????????????"+response);
                try {
                    JSONObject osjson = new JSONObject(response);
                    String reasonjson = osjson.getString("reason");
                    int code=osjson.getInt("code");
                    if (code==100) {
                        JSONObject resultjson = osjson.getJSONObject("result");
                        String tourl = resultjson.getString("url");
                        String domeUrl = "http://" + tourl + "/jcont";
                        Log.e("TAG","????????????"+domeUrl);
                        if (domeUrl!=null){
                            Log.e("TAG","jjjjjjjjjjj"+Constant.domeLoginBoo);
                            if (Constant.domeLoginBoo){
                                App.serverurl=Constant.DOMEURL;
                                SPUtils.put(LoginActivity.this, Constant.URL, Constant.DOMEURL);
                                Constant.CODE=Constant.DOMECODE;
                                Log.e("TAG","????????????");
                                //????????????
                                String s="{\"cmd\":\"149\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+ Constant.CODE+"\",\"dsv\":\""+ Constant.DSV+"\"," +
                                        "\"ltype\":\"0\",\"user\":\""+Constant.testusername+"\"," +
                                        "\"pass\":\""+ MD5Utils.encode(Constant.testuserpwd)+"\"," +
                                        "\"sign\":\"abcd\"}";
                                Log.e("TAG","????????????"+s+"??????code"+Constant.CODE);
                                login(App.serverurl,s);
                            }else {
                                SPUtils.put(LoginActivity.this, Constant.URL, domeUrl);
                                App.serverurl=domeUrl;
                                Log.e("TAG","???????????????");
                                String s="{\"cmd\":\"149\",\"type\":\""+ Constant.TYPE+"\",\"code\":\""+ Constant.CODE+"\",\"dsv\":\""+ Constant.DSV+"\"," +
                                        "\"ltype\":\"0\",\"user\":\""+loginEdtId.getText().toString().trim()+"\"," +
                                        "\"pass\":\""+ MD5Utils.encode(loginEdtPwd.getText().toString().trim())+"\"," +
                                        "\"sign\":\"abcd\"}";
                                Log.e("TAG","????????????"+s+"??????code"+Constant.CODE);
                                login(domeUrl,s);
                            }

                        }
                    } else {
                        Toasty.error(LoginActivity.this, getString(R.string.the_server_requested_failed), Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //???????????????????????????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            long secondTime=System.currentTimeMillis();//????????????????????????
            if (secondTime-firsttime>2000){
                Toasty.info(LoginActivity.this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT, true).show();
                firsttime=System.currentTimeMillis();//???????????????????????????
            }else {
                //finish();
                ActivityManager.getInstance().exit();
                SPUtils.remove(LoginActivity.this, Constant.ID);
                SPUtils.remove(LoginActivity.this, Constant.PASS);
                finish();
            }
        }
        return false;
    }

    //???????????????????????????
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        popDown.setBackgroundResource(R.mipmap.ic_down);
        lin_pwd.setVisibility(View.VISIBLE);
        lin_ver.setVisibility(View.VISIBLE);
        loginBtnLogin.setVisibility(View.VISIBLE);
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

    }

    @Override
    public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
        String sql_del="delete from "+ Constant.TABLE_UNAME+" where "+ Constant.UNAME+"="+"'"+list.get(pos)+"'";
        Log.e("TAG",sql_del);
        SQLiteDatabase db=App.dbHelper.getWritableDatabase();
        db.execSQL(sql_del);//??????sql??????
        //Toast.makeText(LoginActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        /*//????????????list??????
                        list.remove(position);
                        //???????????????
                        popAdapter.notifyDataSetChanged();*/
                        //query_DB();
       /* popupWindow.dismiss();
        if (init_flag) {
            // ??????????????????
            popupWindow.showAsDropDown(parent, 0, -5);
            init_flag=false;
        }*/
        db.close();

    }

    @Override
    public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

    }
}
