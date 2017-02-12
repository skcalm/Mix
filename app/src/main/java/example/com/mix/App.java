package example.com.mix;

import android.app.Application;
import android.content.Context;

import example.com.mix.data.dao.DaoHelper;

/**
 * Created by gordon on 2017/2/12.
 */

public class App extends Application {
    private static DaoHelper daoHelper;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        daoHelper = new DaoHelper(getApplicationContext());
        context = getApplicationContext();
    }

    public static DaoHelper daoHelper(){
        return daoHelper;
    }

    public static Context context(){
        return context;
    }
}
