package com.example.xs.activity;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.example.xs.R;
import com.example.xs.fragment.TwoFragment;
import com.example.xs.fragment.VideoListFragment;
import com.hjm.bottomtabbar.BottomTabBar;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private BottomTabBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar);
        mBottomBar.init(getSupportFragmentManager(), 720, 1280)
//                .setImgSize(70, 70)
//                .setFontSize(14)
//                .setTabPadding(5, 0, 5)
//                .setChangeColor(Color.parseColor("#FF00F0"),Color.parseColor("#CCCCCC"))
                .addTabItem("视频", R.mipmap.add, R.mipmap.del, VideoListFragment.class)
                .addTabItem("热点", R.mipmap.left, R.mipmap.right_down, TwoFragment.class)
//                .isShowDivider(true)
//                .setDividerColor(Color.parseColor("#FF0000"))
//                .setTabBarBackgroundColor(Color.parseColor("#00FF0000"))
                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name, View view) {
                        if (position == 1)
                            mBottomBar.setSpot(0, false);
                    }
                })
                .setSpot(0, true);
    }

    public void setShowTabBar(boolean isShow) {
        if (isShow) {
            mBottomBar.getTabBar().setVisibility(View.VISIBLE);
        } else {
            mBottomBar.getTabBar().setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

    }
}