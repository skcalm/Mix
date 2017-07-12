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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_greendao_test:
                startActivity(new Intent(this, GreenDaoTestActivity.class));
                break;
            case R.id.btn_coordinator_layout:
                startActivity(new Intent(this, CoordinatorLayoutActivity.class));
                break;
            case R.id.btn_coordinator2_layout:
                startActivity(new Intent(this, Coordinator2LayoutActivity.class));
                break;
            case R.id.btn_coordinator3_layout:
                startActivity(new Intent(this, Coordinator3LayoutActivity.class));
                break;
            case R.id.btn_coordinator4_layout:
                startActivity(new Intent(this, Coordinator4LayoutActivity.class));
                break;
            case R.id.btn_coordinator_layout2:
                startActivity(new Intent(this, ToolbarActivity.class));
                break;
            case R.id.btn_scene_transition:
                startActivity(new Intent(this, SceneTransitionActivity.class));
                break;
            case R.id.drag_drop:
                startActivity(new Intent(this, DragDropActivity.class));
                break;
            default:
                break;
        }
    }
}
