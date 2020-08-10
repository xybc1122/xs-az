package com.example.xs.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.xs.R;
import com.example.xs.views.PlaySurfaceView;

public class VideoListFragment extends Fragment implements View.OnClickListener {
    private PlaySurfaceView[] playSurfaceViews = new PlaySurfaceView[4];

    private TextView[] textViews = new TextView[4];

    private int m_iLogID = -1;
    private int m_iStartChan = 0; // start channel number

    public VideoListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_list_fragment, container, false);
        RelativeLayout relativeLayout = view.findViewById(R.id.id_relativeLayout);
        setFragmentContentView(relativeLayout);
        return view;
    }

    private void setFragmentContentView(RelativeLayout view) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        for (int i = 1; i <= 4; i++) {
            int index = i - 1;
            if (playSurfaceViews[index] == null) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                FrameLayout frameLayout = new FrameLayout(getActivity());
                frameLayout.setId(i);
                frameLayout.setOnClickListener(this);
                textViews[index] = new TextView(getActivity());
                //540
                playSurfaceViews[index] = new PlaySurfaceView(getActivity());
                playSurfaceViews[index].setParam(metrics.widthPixels, metrics.heightPixels / 2);
                playSurfaceViews[index].setBackgroundColor(getResources().getColor(R.color.btn_filled_blue_bg_disabled));
                if (i > 1) {
                    lp.addRule(RelativeLayout.BELOW, index);
                    lp.topMargin = 5;
                }
                frameLayout.addView(playSurfaceViews[index]);
                frameLayout.addView(textViews[index]);
                view.addView(frameLayout, lp);
            }
        }
    }

    private void startMultiPreview() {
        //one by one
        for (int i = 0; i < playSurfaceViews.length; i++) {
            int res = playSurfaceViews[i].startPreview(0, 33 + i);
            if (res == -1) {
                textViews[i].setText("通道暂无连接....");
                textViews[i].setGravity(Gravity.CENTER);
                textViews[i].setTextColor(getResources().getColor(R.color.qmui_drawable_color_list_pressed));
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                System.out.println("111111");
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
