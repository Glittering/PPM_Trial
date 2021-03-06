package com.example.npttest.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.adapter.ReleaseRemarksAdapter;
import com.example.npttest.constant.Constant;
import com.example.npttest.tool.DateTools;
import com.example.npttest.util.TimeUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.example.npttest.activity.CaroutChargeActivity.caroutactivity;
import static com.example.npttest.constant.Constant.TABLE_USER;

/**
 * Created by liuji on 2017/9/12.
 */

public class ReleaseRemarks extends NoStatusbarActivity implements OnItemDragListener, OnItemSwipeListener {


    @Bind(R.id.release_return)
    ImageView releaseReturn;
    @Bind(R.id.release_add)
    ImageView releaseAdd;
    @Bind(R.id.release_RecyclerView)
    RecyclerView releaseRecyclerView;
    @Bind(R.id.release_sweipeLayout)
    SwipeRefreshLayout releaseSweipeLayout;
    private ReleaseRemarksAdapter remarksAdapter;
    private Paint paint;
    private List<String> list = new ArrayList<>();
    private Boolean aBoolean, pvrefresh;
    private String carnum, cartype, jfType, comfirmYy, sid, pktime,snmon,ssmon,srmon;
    private long itime, ctime;
    private int ctype, cdtp;
    private ZLoadingDialog dialog1, dialog;
    SynthesizerListener mSynListener;

    private final int PRINTVERSION = 5;
    ProgressDialog pgdialog;
    private ProgressDialog progressDialog;
    MyHandler handler;
    private static String printVersion;
    private String Result;
    private Boolean nopaper = false;
    private final int OVERHEAT = 12;
    private final int PRINTERR = 11;
    private final int CANCELPROMPT = 10;
    private final int NOPAPER = 3;
    private Boolean printok=false;
    private String print;


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case OVERHEAT:
                    android.app.AlertDialog.Builder overHeatDialog = new android.app.AlertDialog.Builder(ReleaseRemarks.this);
                    overHeatDialog.setTitle(getString(R.string.reminder));
                    overHeatDialog.setMessage(R.string.print_too_hot);
                    overHeatDialog.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            free_start_voice();
                            Intent zdintent = new Intent(ReleaseRemarks.this, CaroutSuccessful.class);
                            zdintent.putExtra("carnum", carnum);
                            zdintent.putExtra("jfType", jfType);
                            zdintent.putExtra("ctype", ctype);
                            zdintent.putExtra("ctime", ctime);
                            zdintent.putExtra("itime", itime);
                            zdintent.putExtra("pvrefresh", pvrefresh);
                            startActivity(zdintent);
                            finish();
                        }
                    });
                    overHeatDialog.show();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !ReleaseRemarks.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case 0x0123:
                    free_start_voice();
                    Intent zdintent = new Intent(ReleaseRemarks.this, CaroutSuccessful.class);
                    zdintent.putExtra("carnum", carnum);
                    zdintent.putExtra("jfType", jfType);
                    zdintent.putExtra("ctype", ctype);
                    zdintent.putExtra("ctime", ctime);
                    zdintent.putExtra("itime", itime);
                    zdintent.putExtra("pvrefresh", pvrefresh);
                    startActivity(zdintent);
                    finish();
                    break;
                case PRINTVERSION:
                    if (msg.obj.equals("1")) {
                        Log.e("TAG",printVersion);
                    } else{
                        //Toast.makeText(InputCarnum.this, "", Toast.LENGTH_LONG).show();
                        Log.e("TAG","??????????????????");
                    }
                    break;
                default:
                    Toast.makeText(ReleaseRemarks.this, getString(R.string.printer_is_abnormal), Toast.LENGTH_LONG).show();
                    free_start_voice();
                    Intent zintent = new Intent(ReleaseRemarks.this, CaroutSuccessful.class);
                    zintent.putExtra("carnum", carnum);
                    zintent.putExtra("jfType", jfType);
                    zintent.putExtra("ctype", ctype);
                    zintent.putExtra("ctime", ctime);
                    zintent.putExtra("itime", itime);
                    zintent.putExtra("pvrefresh", pvrefresh);
                    startActivity(zintent);
                    finish();
                    break;
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_remarks);
        ButterKnife.bind(this);
        SpeechUtility.createUtility(ReleaseRemarks.this, SpeechConstant.APPID + "=59df2c0c");
        Intent intent = getIntent();
        aBoolean = intent.getBooleanExtra("relBoo", false);
        if (aBoolean) {
            carnum = intent.getStringExtra("carnum");
            ctype = intent.getIntExtra("ctype", 0);
            cdtp = intent.getIntExtra("cdtp", 0);
            jfType = intent.getStringExtra("jfType");
            itime = intent.getLongExtra("itime", 0);
            ctime = intent.getLongExtra("ctime", 0);
            pvrefresh = intent.getBooleanExtra("pvrefresh", false);
            sid = intent.getStringExtra("sid");
            pktime=intent.getStringExtra("pktime");
            snmon=intent.getStringExtra("snmon");
            ssmon=intent.getStringExtra("ssmon");
            srmon=intent.getStringExtra("srmon");
            Log.e("TAG", "*****" + pvrefresh);
        }
        //carnumber=intent.getStringExtra("carnum");
        remarksAdapter = new ReleaseRemarksAdapter(list);
        query_DB();
        releaseSweipeLayout.setEnabled(false);
        releaseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemDragAndSwipeCallback swipeCallback = new ItemDragAndSwipeCallback(remarksAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(releaseRecyclerView);
        //????????????
        /*remarksAdapter.enableDragItem(touchHelper);
        remarksAdapter.setOnItemDragListener(this);*/

        //??????????????????
        remarksAdapter.enableSwipeItem();
        swipeCallback.setSwipeMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        remarksAdapter.setOnItemSwipeListener(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(45);
        paint.setColor(Color.WHITE);
        Log.e("TAG", "onstart");
        releaseRecyclerView.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (aBoolean) {
                    //Toast.makeText(ReleaseRemarks.this, ""+ list.get(position), Toast.LENGTH_SHORT).show();
                    String cpjson = "{\"cmd\":\"141\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE + "\"," +
                            "\"dsv\":\"" + Constant.DSV + "\",\"sid\":\"" + sid + "\",\"io\":\"1\",\"rstat\":\"3\"," +
                            "\"ftype\":\"\",\"sale\":\"0\",\"reas\":\"" + list.get(position) + "\",\"spare\":\"0\",\"sign\":\"abcd\"}";

                    chargePass(App.serverurl, cpjson);
                }
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                switch (view.getId()) {
                    case R.id.releaseItem_iv:
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseRemarks.this);
                        builder.setTitle(R.string.release_comment_modification);//????????????
                        //?????????????????????
                        final EditText updatetv = new EditText(ReleaseRemarks.this);
                        //?????????????????????
                        updatetv.setText(list.get(position));
                        //??????????????????????????????
                        LinearLayout linearLayout = new LinearLayout(ReleaseRemarks.this);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);//?????????????????????????????????
                        linearLayout.addView(updatetv);
                        builder.setView(linearLayout);//????????????????????????build
                        builder.setNegativeButton(getString(R.string.cancel), null);
                        builder.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(updatetv.getText())) {
                                    Toasty.error(ReleaseRemarks.this, getString(R.string.input_information_cannot_be_empty), Toast.LENGTH_SHORT, true).show();
                                } else {
                                    String sql_update = "update " + Constant.TABLE_USER + " set " + Constant.RRSTRING + "='"
                                            + updatetv.getText().toString() + "'" + " where " + Constant.RRSTRING + "=" + "'" + list.get(position) + "'";
                                    Log.e("TAG", sql_update);
                                    SQLiteDatabase db = App.dbHelper.getWritableDatabase();
                                    db.execSQL(sql_update);
                                    db.close();
                                    Toasty.success(ReleaseRemarks.this, getString(R.string.successfully_modified), Toast.LENGTH_SHORT, true).show();
                                    query_DB();
                                }
                            }
                        });
                        builder.show();
                        break;
                }
            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter adapter, View view, final int position) {

            }
        });

        handler=new MyHandler();
        //???????????????
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ThermalPrinter.start(ReleaseRemarks.this);
                    ThermalPrinter.reset();
                    printVersion = ThermalPrinter.getVersion();
                } catch (TelpoException e) {
                    e.printStackTrace();
                } finally {
                    if (printVersion != null) {
                        Message message = new Message();
                        message.what = PRINTVERSION;
                        message.obj = "1";
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = PRINTVERSION;
                        message.obj = "0";
                        handler.sendMessage(message);
                    }
                    ThermalPrinter.stop(ReleaseRemarks.this);
                }
            }
        }).start();
    }

    //????????????
    private class contentPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                ThermalPrinter.start(ReleaseRemarks.this);
                ThermalPrinter.reset();
                ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
                ThermalPrinter.setLeftIndent(0);
                ThermalPrinter.setLineSpace(32);
                ThermalPrinter.setFontSize(2);
               /* if (wordFont == 4) {
                    ThermalPrinter.setFontSize(2);
                    ThermalPrinter.enlargeFontSize(2, 2);
                } else if (wordFont == 3) {
                    ThermalPrinter.setFontSize(1);
                    ThermalPrinter.enlargeFontSize(2, 2);
                } else if (wordFont == 2) {
                    ThermalPrinter.setFontSize(2);
                } else if (wordFont == 1) {
                    ThermalPrinter.setFontSize(1);
                }*/
                String str="\n          ????????????"
                        + "\n----------------------------"
                        + "\n????????????"+carnum
                        + "\n????????????"+sid
                        + "\n?????????"+ TimeUtils.getStrTime(String.valueOf(gettime()))
                        + "\n----------------------------"
                        + "\n???????????????"+jfType
                        + "\n???????????????"+ DateTools.getDate(itime * 1000) + ""
                        + "\n???????????????"+DateTools.getDate(ctime * 1000) + ""
                        + "\n???????????????"+pktime
                        + "\n???????????????"+snmon+"???"
                        + "\n???????????????"+ssmon+"???"
                        + "\n???????????????0?????????????????????"
                        + "\n????????????"+ Constant.username
                        + "\n?????????"+ Constant.adds
                        + "\n----------------------------"
                        + "\n          ?????????????????????"
                        ;
                Bitmap bitmap = CreateCode(sid, BarcodeFormat.QR_CODE, 256, 256);
                if(bitmap != null){
                    ThermalPrinter.printLogo(bitmap);
                }
                ThermalPrinter.setGray(4);
                ThermalPrinter.addString(str);
                ThermalPrinter.printString();
                ThermalPrinter.walkPaper(100);
                printok=true;
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                printok=false;
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }else {
                    handler.sendEmptyMessage(0x0123);
                }
                ThermalPrinter.stop(ReleaseRemarks.this);
            }
        }
    }

    public Bitmap CreateCode(String str, com.google.zxing.BarcodeFormat type, int bmpWidth, int bmpHeight) throws WriterException {
        Hashtable<EncodeHintType,String> mHashtable = new Hashtable<EncodeHintType,String>();
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

    //??????
    private void noPaperDlg() {
        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(ReleaseRemarks.this);
        dlg.setTitle("????????????");
        dlg.setMessage("??????????????????????????????????????????");
        dlg.setCancelable(false);
        dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ThermalPrinter.stop(ReleaseRemarks.this);
                free_start_voice();
                Intent zdintent = new Intent(ReleaseRemarks.this, CaroutSuccessful.class);
                zdintent.putExtra("carnum", carnum);
                zdintent.putExtra("jfType", jfType);
                zdintent.putExtra("ctype", ctype);
                zdintent.putExtra("ctime", ctime);
                zdintent.putExtra("itime", itime);
                zdintent.putExtra("pvrefresh", pvrefresh);
                startActivity(zdintent);
                finish();
            }
        });
        dlg.show();
    }

    private void free_start_voice() {
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
        // mTts.startSpeaking(carnum+"?????????????????????", mSynListener);
        char[] carnumber = carnum.toCharArray();
        if (carnumber.length == 7) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + " " + "?????????????????????", mSynListener);
        } else if (carnumber.length == 8) {
            mTts.startSpeaking(String.valueOf(carnumber[0]) + " " + String.valueOf(carnumber[1]) + " " + String.valueOf(carnumber[2])
                    + " " + String.valueOf(carnumber[3]) + " " + String.valueOf(carnumber[4]) + " " + String.valueOf(carnumber[5]) + " " + String.valueOf(carnumber[6]) + " " + String.valueOf(carnumber[7]) + " " + "?????????????????????", mSynListener);
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


    @OnClick({R.id.release_return, R.id.release_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.release_return:
                finish();
                //Snackbar.make(releaseReturn,"nihao",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.release_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseRemarks.this);
                builder.setTitle(R.string.release_notes_added);//????????????
                //?????????????????????
                final EditText remags = new EditText(ReleaseRemarks.this);
                TextView eptv = new TextView(ReleaseRemarks.this);
                //??????????????????????????????
                LinearLayout linearLayout = new LinearLayout(ReleaseRemarks.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);//?????????????????????????????????
                linearLayout.addView(eptv);
                linearLayout.addView(remags);
                builder.setView(linearLayout);//????????????????????????build
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isEmpty(remags.getText())) {
                            Toasty.error(ReleaseRemarks.this, getString(R.string.input_information_cannot_be_empty), Toast.LENGTH_SHORT, true).show();
                        } else {
                            String sql = "insert into " + Constant.TABLE_USER + "(" + Constant.RRSTRING + ") values('" + remags.getText().toString() + "')";
                            //????????????
                            //String sql="insert into "+  Constant.TABLE_UNAME+"("+Constant.UNAME+","+Constant.UTIME+") values('"+remags.getText().toString()+"',"+gettime()+"')";
                            //Log.e("TAG",sql+"?????????"+gettime());
                            //???????????????????????????
                            SQLiteDatabase db = App.dbHelper.getWritableDatabase();
                            db.execSQL(sql);//??????sql??????
                            Toasty.success(ReleaseRemarks.this, getString(R.string.added_successfull), Toast.LENGTH_SHORT, true).show();
                            db.close();
                            query_DB();
                        }
                    }
                });
                builder.show();
                break;
        }
    }

    private void query_DB() {
        String sql_user = "select * from " + TABLE_USER;
        getdata(sql_user);
    }

    //??????????????????
    private long gettime() {
        Date date = new Date();
        long time = (date.getTime() / 1000);
        //Log.e("TAG",date.getTime()/1000+"");
        return time;
    }


    //??????sql??????
    private void getdata(String sql_user) {
        list.clear();
        // String sql_user="select * from "+ Constant.TABLE_USER;
        SQLiteDatabase sdb = App.dbHelper.getReadableDatabase();
        Cursor cursor = sdb.rawQuery(sql_user, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String s = cursor.getString(cursor.getColumnIndex(Constant.RRSTRING));
                Log.e("TAG", s);
                list.add(s);
            }
            releaseRecyclerView.setAdapter(remarksAdapter);
            cursor.close();
            sdb.close();
        }
    }

    //??????????????????
    @Override
    public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {

    }

    @Override
    public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
        Log.d("TAG", "move from: " + source.getAdapterPosition() + " to: " + target.getAdapterPosition());
    }

    @Override
    public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {

    }


    //????????????????????????
    @Override
    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
        Log.e("TAG", "?????????" + pos);
    }

    @Override
    public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
        Log.e("TAG", "??????1???" + pos);
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
        String sql = "delete from " + Constant.TABLE_USER + " where " + Constant.RRSTRING + "=" + "'" + list.get(pos) + "'";
        Log.e("TAG", sql);
        SQLiteDatabase db = App.dbHelper.getWritableDatabase();
        db.execSQL(sql);//??????sql??????
        Toasty.success(ReleaseRemarks.this, getString(R.string.successfully_deleted), Toast.LENGTH_SHORT, true).show();
        //????????????list??????
        //list.remove(pos);
        //???????????????
        //remarksAdapter.notifyDataSetChanged();
        db.close();
    }

    @Override
    public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
        canvas.drawColor(ContextCompat.getColor(ReleaseRemarks.this, R.color.colorPrimaryDark));
        canvas.drawText(getString(R.string.swipe_to_delete), 50, 90, paint);
        //Log.e("TAG","??????3???"+dX+"+"+dY+"+"+isCurrentlyActive);
    }

    private void chargePass(String url, String cpjson) {
        dialog1 = new ZLoadingDialog(ReleaseRemarks.this);
        dialog1.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                .setHintText("Loading...")
                .setHintTextColor(Color.parseColor("#55BEB7"))
                .setHintTextSize(16) // ?????????????????? dp
                .setHintTextColor(Color.GRAY)  // ??????????????????
                .show();
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
                            Constant.srmon=getString(R.string.free_0);
                            free_start_voice();
                            Intent zdintent = new Intent(ReleaseRemarks.this, CaroutSuccessful.class);
                            zdintent.putExtra("carnum", carnum);
                            zdintent.putExtra("jfType", jfType);
                            zdintent.putExtra("ctype", ctype);
                            zdintent.putExtra("ctime", ctime);
                            zdintent.putExtra("itime", itime);
                            zdintent.putExtra("pvrefresh", pvrefresh);
                            zdintent.putExtra("paytype", 4);
                            zdintent.putExtra("caroutprint", true);
                            startActivity(zdintent);
                            finish();
                            caroutactivity.finish();
                    } else {
                        Toasty.error(ReleaseRemarks.this, getString(R.string.invalid_orders_need_to_be_reinitiated), Toast.LENGTH_SHORT, true).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
