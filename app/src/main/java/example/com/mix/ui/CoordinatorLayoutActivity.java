package example.com.mix.ui;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.mix.R;

public class CoordinatorLayoutActivity extends BaseActivity {
    /*views*/
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private TextView txtTabNestedScrollView;
    private TextView txtTabRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_layout);

        initVal();
        initView();
    }

    private void initVal() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        txtTabNestedScrollView = (TextView) findViewById(R.id.tab_nested_scroll);
        txtTabRecyclerView = (TextView) findViewById(R.id.tab_recycler_view);
    }

    private void initView() {
        tabSelectedNestedScroll(true);
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

    public void onTabClick(View v) {
        switch (v.getId()) {
            case R.id.tab_nested_scroll:
                tabSelectedNestedScroll(true);
                break;
            case R.id.tab_recycler_view:
                tabSelectedNestedScroll(false);
                break;
        }
    }

    private void tabSelectedNestedScroll(boolean isSelected) {
        txtTabNestedScrollView.setSelected(isSelected);
        txtTabRecyclerView.setSelected(!isSelected);
        nestedScrollView.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isSelected ? View.GONE : View.VISIBLE);
    }

    public void onBack(View v) {
        finish();
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<String> dataList;

        public RecyclerViewAdapter(ArrayList<String> list) {
            dataList = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CoordinatorLayoutActivity.this)
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.txtTitle.setText(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

        private class ItemHolder extends ViewHolder {
            public TextView txtTitle;

            public ItemHolder(View itemView) {
                super(itemView);

                txtTitle = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
