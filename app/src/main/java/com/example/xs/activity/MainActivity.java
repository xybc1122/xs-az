package com.example.xs.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.example.xs.R;
import com.example.xs.fragment.TwoFragment;
import com.example.xs.fragment.VideoListFragment;
import com.hjm.bottomtabbar.BottomTabBar;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private BottomTabBar mBottomBar;
    private final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar);
        mBottomBar.init(getSupportFragmentManager(), 720, 1280)
//                .setImgSize(70, 70)
//                .setFontSize(14)
//                .setTabPadding(5, 0, 5)
//                .setChangeColor(Color.parseColor("#FF00F0"),Color.parseColor("#CCCCCC"))
                .addTabItem("实时预览", R.mipmap.video_red, R.mipmap.video, VideoListFragment.class)
                .addTabItem("软件操作", R.mipmap.sorcket_red, R.mipmap.socket, TwoFragment.class)
//                .isShowDivider(true)
//                .setDividerColor(Color.parseColor("#FF0000"))
//                .setTabBarBackgroundColor(Color.parseColor("#00FF0000"))
                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name, View view) {
                        System.out.println(position);

                        System.out.println(name);

                        System.out.println(view);
                    }
                });
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