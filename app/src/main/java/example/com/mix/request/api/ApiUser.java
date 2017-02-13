package example.com.mix.request.api;

import com.google.gson.Gson;

import java.net.HttpURLConnection;

import example.com.mix.request.AbstractRequest;
import example.com.mix.request.Address;
import example.com.mix.request.responsedata.RUser;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/13.
 */

public class ApiUser extends AbstractRequest<RUser> {
    private String id;

    public ApiUser(String id) {
        this.id = id;
    }

    @Override
    protected void buildRequest(Request.Builder requestBuilder) {
        requestBuilder.url(Address.GET_USER);
    }

    @Override
    protected RUser parseResponse(Response response) {
        RUser user = null;
        if (response.code() == HttpURLConnection.HTTP_OK) {
            String data = response.body().toString();
            user = new Gson().fromJson(data, RUser.class);
        }
        return user;
    }
}
