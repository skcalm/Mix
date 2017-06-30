package example.com.mix.utils;

import android.content.Context;

/**
 * Created by Administrator on 2017/6/27.
 */

public class CommonUtil {
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
