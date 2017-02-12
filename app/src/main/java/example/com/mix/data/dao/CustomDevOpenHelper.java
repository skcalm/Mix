package example.com.mix.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import example.com.mix.data.dao.gen.DaoMaster;

/**
 * Created by gordon on 2017/2/12.
 */

public class CustomDevOpenHelper extends DaoMaster.DevOpenHelper {
    public CustomDevOpenHelper(Context context, String name) {
        super(context, name);
    }

    public CustomDevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
