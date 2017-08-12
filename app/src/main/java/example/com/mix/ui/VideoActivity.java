package example.com.mix.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import example.com.mix.R;
import example.com.mix.widgets.VideoTextureView;

public class VideoActivity extends BaseActivity {
    private ViewGroup layoutContainer;
    private TextView txtVideoTitle;

    private String videoPath;
    private int videoCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        layoutContainer = (ViewGroup) findViewById(R.id.layoutContainer);
        txtVideoTitle = (TextView) findViewById(R.id.txtVideoTitle);

        videoPath = "http://pic.ibaotu.com/00/14/35/07X888piCj9A.mp4_10s.mp4";
        videoCount = 1;

        showVideo();
    }

    public void onNext(View v) {
        ++videoCount;
        showVideo();
    }

    private void showVideo() {
        txtVideoTitle.setText("video " + videoCount);
        removeVideo();
        VideoTextureView videoView = new VideoTextureView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutContainer.addView(videoView);
        videoView.setLayoutParams(params);
        videoView.setVideoPath(videoPath);
        videoView.start();
    }

    private void removeVideo() {
        if (layoutContainer.getChildCount() > 0) {
            VideoTextureView videoView = (VideoTextureView) layoutContainer.getChildAt(0);
            videoView.release();
            layoutContainer.removeAllViews();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeVideo();
    }
}
