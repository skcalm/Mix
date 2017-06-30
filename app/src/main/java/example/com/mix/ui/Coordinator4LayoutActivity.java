package example.com.mix.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.mix.R;
import example.com.mix.utils.CommonUtil;

public class Coordinator4LayoutActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private LinearLayout headerLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ActionBar actionBar;

    private int headerHeight;
    private int scrimColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator4_layout);

        initVal();
        initView();
    }

    private void initVal() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        headerLayout = (LinearLayout) findViewById(R.id.headerLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        headerHeight = (int)(getResources().getDisplayMetrics().density*250 + 0.5f);
        scrimColor = ContextCompat.getColor(this, R.color.colorPrimary);
    }

    private void initView() {
        setTransparentForWindow();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.starry_sky);
        }

        appBarLayout.setPadding(appBarLayout.getPaddingLeft(), CommonUtil.getStatusBarHeight(this),
                appBarLayout.getPaddingRight(), appBarLayout.getPaddingBottom());

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alpha = 1f - (float)(verticalOffset + headerHeight)/headerHeight;
                if(alpha < 0) alpha = 0;
                if(alpha > 1) alpha = 1;

                Log.d("Test", "verticalOffset=" + verticalOffset + " alpha=" + alpha
                + " headerHeight=" + headerHeight + " transparent=" + ((float)(verticalOffset + headerHeight)/headerHeight));

                headerLayout.setBackgroundColor(ColorUtils.setAlphaComponent(scrimColor, (int)(alpha*255)));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerViewAdapter(getData()));
    }

    public ArrayList<String> getData() {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < 100; ++i) {
            list.add("this is test data " + (i + 1));
        }

        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTransparentForWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<String> dataList;

        public RecyclerViewAdapter(ArrayList<String> list) {
            dataList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Coordinator4LayoutActivity.this)
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.txtTitle.setText(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

        private class ItemHolder extends RecyclerView.ViewHolder {
            public TextView txtTitle;

            public ItemHolder(View itemView) {
                super(itemView);

                txtTitle = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
