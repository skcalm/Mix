package example.com.mix.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import example.com.mix.R;
import example.com.mix.widgets.Rotation;

public class SceneTransitionActivity extends BaseActivity {
    /*views*/
    private ViewGroup sceneRoot;
    private View viewScene1;
    private Scene sceneStart;
    private Scene sceneEnd;
    private Scene scenePersonalInfo;
    private TransitionSet expandTransitionSet;
    private TransitionSet collapsedTransitionSet;
    private TransitionSet personalInfoTransitionSet;

    /*variants*/
    private boolean isCollapsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_transition);

        initVal();
        initView();
    }

    private void initVal(){
        sceneRoot = (ViewGroup) findViewById(R.id.scene_root);
        viewScene1 = findViewById(R.id.scene_1);

        sceneStart = new Scene(sceneRoot, viewScene1);
        sceneEnd = Scene.getSceneForLayout(sceneRoot, R.layout.layout_scene_2, this);
        scenePersonalInfo = Scene.getSceneForLayout(sceneRoot, R.layout.layout_scene_3, this);


        collapsedTransitionSet = new TransitionSet();
        Transition transition1 = new ChangeBounds();
        transition1.addTarget(R.id.layoutBottom);
        collapsedTransitionSet.addTransition(transition1);
        Transition transition2 = new ChangeBounds();
        transition2.addTarget(R.id.layoutTop);
        transition2.setStartDelay(100);
        collapsedTransitionSet.addTransition(transition2);
        collapsedTransitionSet.addTransition(new Rotation().setDuration(500));

        expandTransitionSet = new TransitionSet();
        Transition transition3 = new ChangeBounds();
        transition3.addTarget(R.id.layoutBottom);
        transition3.setStartDelay(100);
        expandTransitionSet.addTransition(transition3);
        Transition transition4 = new ChangeBounds();
        transition4.addTarget(R.id.layoutTop);
        expandTransitionSet.addTransition(transition4);
        expandTransitionSet.addTransition(new Rotation().setDuration(500));

        personalInfoTransitionSet = new TransitionSet();
        personalInfoTransitionSet.addTransition(new ChangeBounds());
        personalInfoTransitionSet.addTransition(new Rotation().setDuration(400).setStartDelay(200));
        personalInfoTransitionSet.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            SceneTransitionActivity.this,
                            new Pair<View, String>(findViewById(R.id.menu), "avatar"));
                    startActivity(new Intent(SceneTransitionActivity.this, PersonalInfoActivity.class),
                            optionsCompat.toBundle());
                }else{
                    startActivity(new Intent(SceneTransitionActivity.this, PersonalInfoActivity.class));
                }
            }

            @Override
            public void onTransitionCancel(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionPause(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionResume(@NonNull Transition transition) {

            }
        });
    }

    private void initView(){

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.arrow:
                TransitionManager.go(isCollapsed ? sceneStart : sceneEnd,
                        isCollapsed ? expandTransitionSet : collapsedTransitionSet);
                isCollapsed = !isCollapsed;
                break;
            case R.id.personInfo:
                TransitionManager.go(scenePersonalInfo, personalInfoTransitionSet);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
