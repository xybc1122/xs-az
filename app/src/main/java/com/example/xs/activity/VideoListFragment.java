package com.example.xs.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.xs.R;
import com.example.xs.views.PlaySurfaceView;

public class VideoListFragment extends Fragment implements View.OnClickListener {
    private PlaySurfaceView[] playSurfaceViews = new PlaySurfaceView[4];

    private TextView[] textViews = new TextView[4];
    private RelativeLayout mContainer;

    private int m_iLogID = -1;
    private int m_iStartChan = 0; // start channel number

    public VideoListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_list_fragment, container, false);
//        setFragment();
        mContainer = (RelativeLayout) view;
        setFragmentContentView(mContainer);
        return mContainer;
    }


    private void setFragmentContentView(RelativeLayout view) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        for (int i = 1; i <= 4; i++) {
            int index = i - 1;
            if (playSurfaceViews[index] == null) {
                textViews[index] = new TextView(getActivity());

                //540
                playSurfaceViews[index] = new PlaySurfaceView(getActivity());
                int height = metrics.heightPixels / 4;
                playSurfaceViews[index].setParam(metrics.widthPixels, height - 35);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height - 35);
                playSurfaceViews[index].setId(i);
                playSurfaceViews[index].setOnClickListener(this);
                playSurfaceViews[index].setBackgroundColor(getResources().getColor(R.color.btn_filled_blue_bg_disabled));
                if (i > 1) {
                    lp.addRule(RelativeLayout.BELOW, index);
                    lp.topMargin = 5;
                }
                view.addView(playSurfaceViews[index], lp);
                view.addView(textViews[index], lp);
            }
        }
    }

    private void startMultiPreview() {
        //one by one
        for (int i = 0; i < 4; i++) {
            int res = playSurfaceViews[i].startPreview(0, 33 + i);
            if (res == -1) {
                textViews[i].setText("通道暂无连接....");
                textViews[i].setGravity(Gravity.CENTER);
                textViews[i].setTextColor(getResources().getColor(R.color.qmui_drawable_color_list_pressed));
            }
        }
    }

    private void setFragment() {
        //获取activity传的值
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //获取传过来的值
            m_iLogID = bundle.getInt("miLogID");
            //设置值
            m_iStartChan = bundle.getInt("miStartChan");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                startMultiPreview();
                break;
            case 2:
                System.out.println("111");
                break;
            case 3:
                System.out.println("222");
                break;
            case 4:
                System.out.println("333");
                break;
        }
    }
}
