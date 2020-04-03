package org.haobtc.wallet.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.utils.Daemon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * splash
 * */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private List<View> viewList = new ArrayList<>();
    private ImageView[] dots;
    private int[] ids = {R.id.iv1, R.id.iv2};
    private SharedPreferences.Editor edit;

    @Override
    public int getLayoutId() {
        return R.layout.boot_page;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        edit = preferences.edit();
        edit.putBoolean("JumpOr", true);
        edit.apply();

        ViewPager viewPager = findViewById(R.id.vp);
        LayoutInflater inflater = LayoutInflater.from(this);
        viewList.add(inflater.inflate(R.layout.boot_page_item1, null));
        viewList.add(inflater.inflate(R.layout.boot_page_item2, null));
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(viewList.get(position));
            }
        });
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void initData() {
        initDots();
        currency();
    }

    private void currency() {
        try {
            Daemon.commands.callAttr("set_currency", "CNY");
            Daemon.commands.callAttr("set_base_uint", "mBTC");
            Daemon.commands.callAttr("set_rbf", true);
            Daemon.commands.callAttr("set_unconf", true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        edit.putBoolean("set_rbf", true);
        edit.putBoolean("set_unconf", true);
        edit.putString("base_unit", "mBTC");
        edit.apply();
    }

    private void initDots() {
        dots = new ImageView[viewList.size()];
        for (int i = 0; i < viewList.size(); i++) {
            dots[i] = findViewById(ids[i]);
        }
    }

    private void startNewPage() {
        Intent intent = new Intent(this, CreateWalletActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < viewList.size(); i++) {
            if (position == i) {
                // The currently sliding page set point is the blue point
                dots[i].setImageResource(R.drawable.boot_page_bluedot);
            } else {
                // Gray dots when not selected
                dots[i].setImageResource(R.drawable.boot_page_greydot);
            }
        }
        new Handler().postDelayed(this::startNewPage, 3000);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}