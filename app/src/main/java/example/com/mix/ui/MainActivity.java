package example.com.mix.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import example.com.mix.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_greendao_test:
                startActivity(new Intent(this, GreenDaoTestActivity.class));
                break;
            default: break;
        }
    }
}
