package example.com.mix.ui;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import example.com.mix.R;

public class PersonalInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.personal_info_enter));
            getWindow().setSharedElementReenterTransition(null);
        }
    }

    public void onClose(View v){
        finish();
    }
}
