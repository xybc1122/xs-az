package com.example.xs.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.xs.R;
import com.example.xs.utils.GlobalUtil;
import com.example.xs.views.PlaySurfaceView;

public class VideoListFragment extends Fragment implements View.OnClickListener {
    private PlaySurfaceView[] playSurfaceViews = new PlaySurfaceView[4];
    private FrameLayout[] frameLayouts = new FrameLayout[4];
    private TextView[] textViews = new TextView[4];
    private final int frameIdMax = 10000;

    private ImageButton imageButtonPlay = null;

    public VideoListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_list_fragment, container, false);
        imageButtonPlay = view.findViewById(R.id.play);
        imageButtonPlay.setOnClickListener(this);
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
                TextView textView = textViews[index] = new TextView(getActivity());
                FrameLayout frameLayout = frameLayouts[index] = new FrameLayout(getActivity());
                PlaySurfaceView playSurfaceView = playSurfaceViews[index] = new PlaySurfaceView(getActivity());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                frameLayout.setId(frameIdMax + i);
                frameLayout.setOnClickListener(this);
                //540
                playSurfaceView.setParam(metrics.widthPixels, metrics.heightPixels / 2);
                playSurfaceView.setBackgroundColor(getResources().getColor(R.color.btn_filled_blue_bg_disabled));
                if (i > 1) {
                    lp.addRule(RelativeLayout.BELOW, frameIdMax + index);
                    lp.topMargin = 5;
                }
                frameLayout.addView(playSurfaceView);
                frameLayout.addView(textView);
                view.addView(frameLayout, lp);
                frameLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void startMultiPreview() {
        imageButtonPlay.setVisibility(View.GONE);
        //one by one
        for (int i = 0; i < 4; i++) {
            frameLayouts[i].setVisibility(View.VISIBLE);
            int res = playSurfaceViews[i].startPreview(GlobalUtil.loginInfo.getLoginId(), GlobalUtil.loginInfo.getM_iStartChan() + i);
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
            case R.id.play:
                startMultiPreview();
                System.out.println("play");
                break;
            case frameIdMax + 1:
                System.out.println("111111");
                break;
            case frameIdMax + 2:
                System.out.println("111");
                break;
            case frameIdMax + 3:
                System.out.println("222");
                break;
            case frameIdMax + 4:
                System.out.println("333");
                break;
        }
    }
}
