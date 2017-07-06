package example.com.mix.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import example.com.mix.R;
import example.com.mix.utils.CommonUtil;

public class PersonalInfoActivity extends BaseActivity {
    private ImageView imageClose;
    private ViewGroup layoutRoot;
    private View viewAvatar;
    private View viewArc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        CommonUtil.setFullScreen(this);

        viewArc = findViewById(R.id.arc);
        viewAvatar = findViewById(R.id.avatar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layoutRoot = (ViewGroup) findViewById(R.id.layoutRoot);
            layoutRoot.setTransitionGroup(false);
            Transition enterTransition = TransitionInflater.from(this)
                    .inflateTransition(R.transition.personal_info_enter);
            getWindow().setEnterTransition(enterTransition);
            getWindow().setSharedElementReturnTransition(null);
            getWindow().setReturnTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.personal_info_exit));

            imageClose = (ImageView) findViewById(R.id.close);
            imageClose.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(imageClose, "rotation", 0f, 360f);
                    rotationAnimator.setRepeatCount(2);
                    rotationAnimator.setDuration(200);
                    ObjectAnimator xAnimator = ObjectAnimator.ofFloat(imageClose, "x",
                            imageClose.getRight(), imageClose.getLeft());
                    xAnimator.setDuration(400);
                    animatorSet.playTogether(rotationAnimator, xAnimator);
                    animatorSet.start();
                    imageClose.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        }
    }

    public void onClose(View v){
        closeActivity();
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layoutRoot.setTransitionGroup(true);
            viewArc.setTransitionName(null);
            viewAvatar.setTransitionName(null);
            finishAfterTransition();
        }else{
            finish();
        }
    }
}
