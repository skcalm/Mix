package example.com.mix.data.dao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import example.com.mix.data.dao.gen.DaoMaster;
import example.com.mix.data.dao.gen.DaoSession;
import example.com.mix.data.dao.gen.User;

/**
 * Created by gordon on 2017/2/12.
 */

public class DaoHelper {
    protected final static String DB_NAME = "schema";
    protected DaoSession session;

    public DaoHelper(Context context){
        CustomDevOpenHelper devHelper = new CustomDevOpenHelper(context, DB_NAME);
        Database db = devHelper.getWritableDb();
        session = new DaoMaster(db).newSession();
    }

    public DaoSession session(){
        return session;
    }

    public List<User> users(){
        return session.getUserDao().queryBuilder().list();
    }
}
