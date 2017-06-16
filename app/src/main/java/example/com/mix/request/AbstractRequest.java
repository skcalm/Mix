package example.com.mix.request;

import android.os.AsyncTask;

import java.security.Policy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/13.
 */

public abstract class AbstractRequest<T> {
    private volatile Response response;
    private volatile Call call;
    private AsyncTask asyncTask;
    private static final ThreadPoolExecutor THREAD_EXECUTOR = new ThreadPoolExecutor(
            10, 10, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(128));

    public T request(){
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder reqBuilder = new Request.Builder();
            buildRequest(reqBuilder);
            call = okHttpClient.newCall(reqBuilder.build());
            response = call.execute();
            return parseResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void asyncRequest(final ResponseCallback<T> callback){
        try {
            asyncTask = new AsyncTask<Void, Void, T>() {
                @Override
                protected T doInBackground(Void... voids) {
                    return request();
                }

                @Override
                protected void onPostExecute(T t) {
                    if (callback != null) {
                        callback.onResponse(t);
                    }
                }
            }.executeOnExecutor(THREAD_EXECUTOR);
        }catch (Exception e){
            e.printStackTrace();
            if (callback != null) {
                callback.onResponse(null);
            }
        }
    }

    public Response response(){
        return response;
    }

    public void cancel(){
        if(call != null) call.cancel();
        if(asyncTask != null) asyncTask.cancel(true);
    }

    protected abstract void buildRequest(Request.Builder requestBuilder);
    protected abstract T parseResponse(Response response);
}
