package com.example.xs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.example.xs.R;
import com.example.xs.bean.LoginInfo;
import com.hjm.bottomtabbar.BottomTabBar;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private BottomTabBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        LoginInfo loginInfo = (LoginInfo) intent.getSerializableExtra("loginInfo");
        if (loginInfo == null) {
            return;
        }
//
//        //这里需要注意一下：getChildFragmentManager所得到的是在fragment里面子容器的管理器 getFragmentManager()直接获取FragmentManager
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        VideoListFragment videoListFragment = new VideoListFragment();
//        // 步骤1:创建Bundle对象
//        // 作用:存储数据，并传递到Fragment中
//        Bundle bundle = new Bundle();
//        // 步骤2:往bundle中添加数据
//        bundle.putInt("miLogID", loginInfo.getLoginId());
//        bundle.putInt("miStartChan", loginInfo.getM_iStartChan());
//        // 步骤3:把数据设置到Fragment中
//        videoListFragment.setArguments(bundle);
//        transaction.commit();

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