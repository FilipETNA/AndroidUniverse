package etna.androiduniverse.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import etna.androiduniverse.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient extends AsyncTask<String, Void, String> {

    private static final String TAG = "HttpClient";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;
    private String baseUrl;

    public HttpClient() {
        client = new OkHttpClient();
    }

    public HttpClient(String url) {
        client = new OkHttpClient();
        baseUrl = url;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    protected String doInBackground(String... params) {
        String method = params[0].toLowerCase();
        String urlParams = params[1];
        String response = null;

        if (method.equals("get")) {
            try {
                response = httpGet(urlParams);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        else if (method.equals("post")) {
            try {
                response = httpPost(urlParams);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return response;
    }

    private String httpGet(String urlParams) throws IOException{

        Log.i(TAG, "GET REQUEST TO : " + baseUrl + urlParams);

        Request request = new Request.Builder().url(baseUrl + urlParams).build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    private String httpPost(String jsonParams) throws IOException {

        RequestBody requestBody = RequestBody.create(JSON, jsonParams);
        Request request = new Request.Builder().url(baseUrl).post(requestBody).build();

        Response response = client.newCall(request).execute();

        return response.body().toString();
    }

}
