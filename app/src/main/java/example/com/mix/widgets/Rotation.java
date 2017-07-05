package example.com.mix.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/7/5.
 */

public class Rotation extends Transition {
    private static final String PROPNAME_ROTATION = "mix:rotation:rotation";
    private static final String PROPNAME_ROTATIONY = "mix:rotation:rotation_y";

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        if(startValues == null || endValues == null){
            return null;
        }

        final View view = endValues.view;
        final float startRotation = (float) startValues.values.get(PROPNAME_ROTATION);
        final float endRotation = (float) endValues.values.get(PROPNAME_ROTATION);
        final float startRotationY = (float) startValues.values.get(PROPNAME_ROTATIONY);
        final float endRotationY = (float) endValues.values.get(PROPNAME_ROTATIONY);

        if(startRotation != endRotation){
            return ObjectAnimator.ofFloat(view, "rotation", startRotation, endRotation);
        }

        if(startRotationY != endRotationY){
            return ObjectAnimator.ofFloat(view, "rotationY", startRotationY, endRotationY);
        }

        return null;
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(@NonNull TransitionValues transitionValues){
        transitionValues.values.put(PROPNAME_ROTATION, transitionValues.view.getRotation());
        transitionValues.values.put(PROPNAME_ROTATIONY, transitionValues.view.getRotationY());
    }
}
