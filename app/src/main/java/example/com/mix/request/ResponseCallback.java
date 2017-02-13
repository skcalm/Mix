package example.com.mix.request;

/**
 * Created by Administrator on 2017/2/13.
 */

public interface ResponseCallback<T> {
    void onResponse(T result);
}
