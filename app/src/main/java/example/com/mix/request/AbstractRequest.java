package example.com.mix.request;

import android.os.AsyncTask;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/13.
 */

public abstract class AbstractRequest<T> {
    private Response response;
    private Call call;

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
        new AsyncTask<Void, Void, T>(){
            @Override
            protected T doInBackground(Void... voids) {
                return request();
            }

            @Override
            protected void onPostExecute(T t) {
                if(callback != null){
                    callback.onResponse(t);
                }
            }
        }.execute();
    }

    public Response response(){
        return response;
    }

    public void cancel(){
        if(call != null) call.cancel();
    }

    protected abstract void buildRequest(Request.Builder requestBuilder);
    protected abstract T parseResponse(Response response);
}
