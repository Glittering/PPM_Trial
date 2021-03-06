package com.example.npttest.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.example.npttest.App;
import com.example.npttest.R;
import com.example.npttest.adapter.CaroutAdapter;
import com.example.npttest.constant.Constant;
import com.example.npttest.entity.Carout;
import com.example.npttest.manager.LinearLayoutManagerWrapper;
import com.example.npttest.util.TimeUtils;
import com.example.npttest.view.CustomLoadMoreView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by liuji on 2017/10/20.
 */

public class CaroutRecord extends NoStatusbarActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @Bind(R.id.record_carout_return)
    ImageView recordCaroutReturn;
    @Bind(R.id.record_carout_search)
    ImageView recordCaroutSearch;
    @Bind(R.id.record_carout_tv_time1)
    TextView recordCaroutTvTime1;
    @Bind(R.id.record_carout_time1)
    LinearLayout recordCaroutTime1;
    @Bind(R.id.record_carout_tv_time2)
    TextView recordCaroutTvTime2;
    @Bind(R.id.record_carout_time2)
    LinearLayout recordCaroutTime2;
    @Bind(R.id.record_carout_rv)
    RecyclerView recordCaroutRv;
    @Bind(R.id.record_carout_swipeLayout)
    SwipeRefreshLayout recordCaroutSwipeLayout;
    private TimePickerView pvCustomTime, pvCustomTime2;
    private List<Carout> list = new ArrayList<>();
    private CaroutAdapter caroutAdapter;
    private static final int TOTAL_COUNTER = 0;
    private static final int PAGE_SIZE = 6;
    private int delayMillis = 1000;
    private int mCurrentCounter = 0;
    private boolean isErr;
    private boolean mLoadMoreEndGone = false;
    private ZLoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carout_record);
        ButterKnife.bind(this);
        recordCaroutTvTime1.setText(TimeUtils.getStrTime(String.valueOf(gettime() - 86400)));
        recordCaroutTvTime2.setText(TimeUtils.getStrTime(String.valueOf(gettime())));
        initCustomTimePicker();
        list.clear();
        if (App.serverurl != null) {
            getrecord(App.serverurl);
            initAdapter();
        }
        recordCaroutSwipeLayout.setOnRefreshListener(this);
        recordCaroutSwipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        //initAdapter();
        //rvListFg1.setLayoutManager(new LinearLayoutManager(getActivity()));
        recordCaroutRv.setLayoutManager(new LinearLayoutManagerWrapper(this, LinearLayoutManager.VERTICAL, false));
    }

    //??????????????????
    private long gettime() {
        Date date = new Date();
        long time = (date.getTime() / 1000);
        //Log.e("TAG",date.getTime()/1000+"");
        return time;
    }

    //???????????????
    private void initAdapter() {
        caroutAdapter = new CaroutAdapter(list);
        caroutAdapter.setOnLoadMoreListener(this, recordCaroutRv);
        caroutAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        caroutAdapter.setLoadMoreView(new CustomLoadMoreView());
        caroutAdapter.isFirstOnly(false);
        recordCaroutRv.setAdapter(caroutAdapter);
        mCurrentCounter = caroutAdapter.getData().size();

       /* rvListFg1.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_LONG).show();
            }
        } );*/

        /**
         * ??????????????????
         */
        recordCaroutRv.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                Intent caroutInfo_intent = new Intent(CaroutRecord.this, CaroutDetailedInfo.class);
                caroutInfo_intent.putExtra("carnum", caroutAdapter.getData().get(position).getPnum());
                caroutInfo_intent.putExtra("cartype", caroutAdapter.getData().get(position).getCtype());
                caroutInfo_intent.putExtra("pztype", caroutAdapter.getData().get(position).getCdtp());
                caroutInfo_intent.putExtra("ctime", caroutAdapter.getData().get(position).getEtime());
                startActivity(caroutInfo_intent);
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_img:
                        final AlertDialog dialog = new AlertDialog.Builder(CaroutRecord.this).create();
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                        ImageView imageView = new ImageView(CaroutRecord.this);
                        Glide.with(CaroutRecord.this).load(caroutAdapter.getData().get(position).getEurl())
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

    //????????????????????????
    public void getrecord(String url) {
        if (!isFinishing()) {
            dialog = new ZLoadingDialog(this);
            dialog.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//????????????STAR_LOADING ?????????
                    .setLoadingColor(Color.parseColor("#55BEB7"))//??????
                    .setHintText("Loading...")
                    .setHintTextColor(Color.parseColor("#55BEB7"))
                    .setHintTextSize(16) // ?????????????????? dp
                    .setHintTextColor(Color.GRAY)  // ??????????????????
                    .show();
        }
        String stime = String.valueOf(TimeUtils.getTimeStamp(String.valueOf(recordCaroutTvTime1.getText()), "yyyy-MM-dd HH:mm:ss"));
        String etime = String.valueOf(TimeUtils.getTimeStamp(String.valueOf(recordCaroutTvTime2.getText()), "yyyy-MM-dd HH:mm:ss"));

        String getrcord = "{\"cmd\":\"156\",\"type\":\"" + Constant.TYPE + "\",\"code\":\"" + Constant.CODE +
                "\",\"dsv\":\"" + Constant.DSV + "\",\"qtype\":\"0\",\"num\":\"\",\"stime\":\"" + stime + "\"," +
                "\"etime\":\"" + etime + "\",\"spare\":\" \",\"sign\":\"abcd\"}";
        Log.e("TAG", getrcord);
        OkHttpUtils.postString().url(url)
                .content(getrcord)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(CaroutRecord.this, getString(R.string.please_check_the_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                if (dialog != null) {
                    dialog.cancel();
                }
                try {
                    JSONObject rjsonObject = new JSONObject(response);
                    JSONObject resultjsOb = rjsonObject.getJSONObject("result");
                    JSONArray listjsAr = resultjsOb.getJSONArray("list");
                    if (listjsAr.length() > 0) {
                        for (int i = 0; i < listjsAr.length(); i++) {
                            JSONObject jsonObject = listjsAr.getJSONObject(i);
                            Carout carout = new Carout();
                            carout.setCnum(jsonObject.getString("cnum"));
                            carout.setPnum(jsonObject.getString("pnum"));
                            carout.setEtime(jsonObject.getInt("etime"));
                            carout.setEurl(jsonObject.getString("eurl"));
                            carout.setSid(jsonObject.getString("sid"));
                            carout.setCtype(jsonObject.getString("ctype"));
                            carout.setCdtp(jsonObject.getString("cdtp"));
                            list.add(carout);
                            handler.sendEmptyMessage(0x123);
                        }
                    } else {
                        Toasty.error(CaroutRecord.this, getString(R.string.did_not_find_out_the_record), Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                if (caroutAdapter != null && recordCaroutRv != null) {
                    caroutAdapter.notifyDataSetChanged();
                    recordCaroutRv.setAdapter(caroutAdapter);
                }
            }
        }
    };


    @OnClick({R.id.record_carout_return, R.id.record_carout_time1, R.id.record_carout_time2, R.id.record_carout_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.record_carout_return:
                finish();
                break;
            case R.id.record_carout_time1:
                pvCustomTime.show();
                break;
            case R.id.record_carout_time2:
                pvCustomTime2.show();
                break;
            case R.id.record_carout_search:
                startActivity(new Intent(CaroutRecord.this, QueryCaroutRecord.class));
                break;
        }
    }

    private void initCustomTimePicker() {

        /**
         * @description
         *
         * ???????????????
         * 1.?????????????????????id??? optionspicker ?????? timepicker ???????????????????????????????????????????????????????????????.
         * ???????????????demo ????????????????????????layout?????????
         * 2.????????????Calendar???????????????0-11???,?????????????????????Calendar???set?????????????????????,???????????????????????????0-11
         * setRangDate??????????????????????????????(?????????????????????????????????????????????1900-2100???????????????????????????)
         */
        Calendar selectedDate = Calendar.getInstance();//??????????????????
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //??????????????? ??????????????????
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//??????????????????
                recordCaroutTvTime1.setText(getTime(date));
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//????????????????????????
                .setTextColorCenter(Color.LTGRAY)//????????????????????????
                .setLineSpacingMultiplier(1.6f)//????????????????????????????????????
                .setTitleBgColor(Color.DKGRAY)//?????????????????? Night mode
                .setBgColor(Color.BLACK)//?????????????????? Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
               /*.gravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                list.clear();
                                if (App.serverurl != null) {
                                    getrecord(App.serverurl);
                                }
                                //rvListFg1.setLayoutManager(new LinearLayoutManager(getActivity()));
                                //carintoAdapter.setNewData(list);
                                caroutAdapter.notifyDataSetChanged();
                                pvCustomTime.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, true})
                .setLabel(getString(R.string.year), getString(R.string.month), getString(R.string.day), getString(R.string.hour), getString(R.string.minute), getString(R.string.second))
                .isCenterLabel(false) //?????????????????????????????????label?????????false?????????item???????????????label???
                .setDividerColor(0xFF24AD9D)
                .build();

        pvCustomTime2 = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//??????????????????
                recordCaroutTvTime2.setText(getTime(date));
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//????????????????????????
                .setTextColorCenter(Color.LTGRAY)//????????????????????????
                .setLineSpacingMultiplier(1.6f)//????????????????????????????????????
                .setTitleBgColor(Color.DKGRAY)//?????????????????? Night mode
                .setBgColor(Color.BLACK)//?????????????????? Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
               /*.gravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit1 = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel1 = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime2.returnData();
                                list.clear();
                                if (App.serverurl != null) {
                                    getrecord(App.serverurl);
                                }
                                //rvListFg1.setLayoutManager(new LinearLayoutManager(getActivity()));
                                //carintoAdapter.setNewData(list);
                                caroutAdapter.notifyDataSetChanged();
                                pvCustomTime2.dismiss();
                            }
                        });
                        ivCancel1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime2.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, true})
                .setLabel(getString(R.string.year), getString(R.string.month), getString(R.string.day), getString(R.string.hour), getString(R.string.minute), getString(R.string.second))
                .isCenterLabel(false) //?????????????????????????????????label?????????false?????????item???????????????label???
                .setDividerColor(0xFF24AD9D)
                .build();

    }

    private String getTime(Date date) {//???????????????????????????????????????
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onRefresh() {
        caroutAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list.clear();
                if (App.serverurl != null) {
                    getrecord(App.serverurl);
                }
                //rvListFg1.setLayoutManager(new LinearLayoutManager(getActivity()));
                //carintoAdapter.setNewData(list);
                caroutAdapter.notifyDataSetChanged();
                isErr = false;
                mCurrentCounter = PAGE_SIZE;
                if (recordCaroutSwipeLayout != null) {
                    recordCaroutSwipeLayout.setRefreshing(false);
                }
                caroutAdapter.setEnableLoadMore(true);
            }
        }, delayMillis);
    }

    @Override
    public void onLoadMoreRequested() {
        recordCaroutSwipeLayout.setEnabled(false);
        if (caroutAdapter.getData().size() < PAGE_SIZE) {
            caroutAdapter.loadMoreEnd(true);//????????????????????????
        } else {
            if (mCurrentCounter >= TOTAL_COUNTER) {
                caroutAdapter.loadMoreEnd();//default visible
                //????????????????????????
                caroutAdapter.loadMoreEnd(mLoadMoreEndGone);//true is gone,false is visible
            } else {
                if (isErr) {
                    caroutAdapter.addData(list);
                    mCurrentCounter = caroutAdapter.getData().size();
                    caroutAdapter.loadMoreComplete();
                } else {
                    isErr = true;
                    caroutAdapter.loadMoreFail();
                }
            }
            recordCaroutSwipeLayout.setEnabled(true);
        }
    }

}
