package example.com.mix.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import example.com.mix.R;

public class ToolbarActivity extends BaseActivity{
    /*views*/
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        initVal();
        initView();
    }

    private void initVal(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initView(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.mipmap.list_white));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.search:
                        break;
                    case R.id.setting:
                        break;
                }
                return true;
            }
        });
    }
}
