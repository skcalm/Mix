package example.com.mix.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.mix.R;

public class Coordinator3LayoutActivity extends BaseActivity {
    /*views*/
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View viewExpandHeader;
    private View viewCollapseHeader;

    /*variants*/
    private float headerSwitchOffset;
    private int toolBarColor;
    private View viewMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator3_layout);

        initVal();
        initView();
    }

    private void initVal(){
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        viewExpandHeader = LayoutInflater.from(this).inflate(
                R.layout.coordinatorlayout3_expand_header, toolbar, false);
        viewCollapseHeader = LayoutInflater.from(this).inflate(
                R.layout.coordinatorlayout3_collapse_header, toolbar, false);

        headerSwitchOffset = getResources().getDisplayMetrics().density*40 + 0.5f;
        toolBarColor = ContextCompat.getColor(this, R.color.colorPrimary);
    }

    private void initView(){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offset = Math.abs(verticalOffset);
                float alpha;
                if(offset >= headerSwitchOffset){
                    if(toolbar.indexOfChild(viewCollapseHeader) < 0){
                        toolbar.removeView(viewExpandHeader);
                        toolbar.addView(viewCollapseHeader);
                        viewMask = viewCollapseHeader.findViewById(R.id.mask);
                    }
                    alpha = 1 - (offset - headerSwitchOffset)/headerSwitchOffset;
                }else{
                    if(toolbar.indexOfChild(viewExpandHeader) < 0){
                        toolbar.removeView(viewCollapseHeader);
                        toolbar.addView(viewExpandHeader);
                        viewMask = viewExpandHeader.findViewById(R.id.mask);
                    }
                    alpha = 1 - (headerSwitchOffset - offset)/headerSwitchOffset;
                }
                if(alpha < 0) alpha = 0;
                if(alpha > 1) alpha = 1;
                viewMask.setBackgroundColor(ColorUtils.setAlphaComponent(toolBarColor,
                        (int)(alpha*255)));
                Log.d("Test", "verticalOffset=" + verticalOffset +
                        " headerSwitchOffset=" + headerSwitchOffset + " alpha=" + alpha
                + " transparent=" + (int)(alpha*255));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
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

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<String> dataList;

        public RecyclerViewAdapter(ArrayList<String> list) {
            dataList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Coordinator3LayoutActivity.this)
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
