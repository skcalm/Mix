package example.com.mix.widgets;

import android.widget.Toast;

import example.com.mix.App;

/**
 * Created by gordon on 2017/2/12.
 */

public class ToastCover {
    private static Toast toast;

    public static void showShort(String text){
        if(toast == null){
            toast = Toast.makeText(App.context(), text, Toast.LENGTH_SHORT);
        }else{
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showLong(String text){
        if(toast == null){
            toast = Toast.makeText(App.context(), text, Toast.LENGTH_LONG);
        }else{
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    public static void showShort(int resId){
        showShort(App.context().getString(resId));
    }

    public static void showLong(int resId){
        showLong(App.context().getString(resId));
    }
}
