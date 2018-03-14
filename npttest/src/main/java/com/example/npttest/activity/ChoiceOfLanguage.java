package com.example.npttest.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.npttest.R;
import com.example.npttest.constant.Constant;
import com.example.npttest.util.SPUtils;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoiceOfLanguage extends NoStatusbarActivity {

    @Bind(R.id.choice_of_language_return)
    ImageView choiceOfLanguageReturn;
    @Bind(R.id.choice_simplified_chinese)
    LinearLayout choiceSimplifiedChinese;
    @Bind(R.id.choice_traditional_chinese)
    LinearLayout choiceTraditionalChinese;
    @Bind(R.id.choice_traditional_chinese_hk)
    LinearLayout choiceTraditionalChineseHk;
    @Bind(R.id.choice_english_language)
    LinearLayout choiceEnglishLanguage;
    private int ZH=1;
    private int ZH_TW=2;
    private int ZH_HK=3;
    private int EN=4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_of_language);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.choice_of_language_return, R.id.choice_simplified_chinese, R.id.choice_traditional_chinese, R.id.choice_traditional_chinese_hk, R.id.choice_english_language})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.choice_of_language_return:
                finish();
                break;
            case R.id.choice_simplified_chinese:
                switchLanguage(ZH);
                break;
            case R.id.choice_traditional_chinese:
                switchLanguage(ZH_TW);
                break;
            case R.id.choice_traditional_chinese_hk:
                switchLanguage(ZH_HK);
                break;
            case R.id.choice_english_language:
                switchLanguage(EN);
                break;
        }
    }

    /**
     * 切换语言
     *
     * @param language
     */

    private void switchLanguage(int language) {

        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language==ZH) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            //保存设置语言的类型
            SPUtils.put(this, Constant.LANGUAGE,ZH);
        } else if (language==EN) {
            config.locale = Locale.ENGLISH;
            SPUtils.put(this, Constant.LANGUAGE,EN);
        } else if (language==ZH_HK){
            config.locale = Locale.TRADITIONAL_CHINESE;
            SPUtils.put(this, Constant.LANGUAGE,ZH_HK);
        }else if (language==ZH_TW){
            SPUtils.put(this, Constant.LANGUAGE,ZH_TW);
            config.locale = Locale.TRADITIONAL_CHINESE;
        }else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
        Log.e("TAG","选择语言："+language);
        /*SharedPreferences sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.commit();*/
        //更新语言后，destroy当前页面，重新绘制
        finish();
        Intent it = new Intent(ChoiceOfLanguage.this, FristActivity.class);
        //清空任务栈确保当前打开activit为前台任务栈栈顶
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }
}
