package example.com.mix.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.util.List;

import example.com.mix.App;
import example.com.mix.R;
import example.com.mix.data.dao.gen.User;
import example.com.mix.widgets.ToastEx;

public class GreenDaoTestActivity extends BaseActivity {
    /*views*/
    private EditText editName;
    private EditText editTel;
    private Spinner spinnerGender;
    private ListView listView;

    /*variants*/
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_dao_test);

        initVal();
        initView();
    }

    private void initVal() {
        editName = (EditText) findViewById(R.id.edit_name);
        editTel = (EditText) findViewById(R.id.edit_tel);
        spinnerGender = (Spinner) findViewById(R.id.spinner_gender);
        listView = (ListView) findViewById(R.id.list);
    }

    private void initView(){
        getSupportActionBar().setTitle("GreenDaoTest");

        List<User> users = App.daoHelper().users();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, users);
        listView.setAdapter(arrayAdapter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_user:
                createUser();
                break;
            case R.id.btn_show_users:
                showUsers();
                break;
            default:
                break;
        }
    }

    private void createUser() {
        String name = editName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastEx.showShort("name can not be empty");
            return;
        }
        String tel = editTel.getText().toString();
        if(TextUtils.isEmpty(tel)){
            ToastEx.showShort("tel can not be empty");
            return;
        }
        App.daoHelper().session().insertOrReplace(new User(null, name,
                tel, spinnerGender.getSelectedItemPosition()));
    }

    private void showUsers() {
        List<User> users = App.daoHelper().users();
        arrayAdapter.clear();
        arrayAdapter.addAll(users);
        arrayAdapter.notifyDataSetChanged();
    }
}
