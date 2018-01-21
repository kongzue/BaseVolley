import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangChao on 2017/12/28.
 * ver:3.1
 */

public class HttpRequest {

    private RequestQueue mQueue;            //Volley请求队列

    //单例
    private static HttpRequest httpRequest;

    private HttpRequest() {
    }

    public static HttpRequest getInstance(Context context) {
        if (httpRequest == null) {
            synchronized (HttpRequest.class) {
                if (httpRequest == null) {
                    httpRequest = new HttpRequest();
                    httpRequest.mQueue = Volley.newRequestQueue(context);         //创建请求队列
                }
            }
        }
        return httpRequest;
    }

    public void postRequest(String partUrl, final Parameter parameter,
                            final ResponseListener listener) {
        doRequest(partUrl, parameter, listener, Request.Method.POST);
    }

    public void getRequest(String partUrl, final Parameter parameter,
                           final ResponseListener listener) {
        doRequest(partUrl, parameter, listener, Request.Method.GET);
    }

    private void doRequest(String partUrl, final Parameter parameter, final ResponseListener listener, final int method) {

        final String finalUrl = partUrl ;

        BaseRequest baseRequest = new BaseRequest(method, finalUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (BuildConfig.DEBUG)
                    Log.i(">>>", "request:" + finalUrl + "\nparameter:" + parameter.toParameterString() + "\nresponse:" + response.toString());
                listener.onResponse(response, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                listener.onResponse(null, error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return parameter;
            }
        };
        mQueue.add(baseRequest);
    }

    class BaseRequest extends JsonObjectRequest {

        private Map<String, String> params;

        public BaseRequest(int method, String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, requestBody, listener, errorListener);
        }

        public BaseRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        public BaseRequest(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            this.params = params;
        }

        public BaseRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public BaseRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        protected Map<String, String> getParams()
                throws com.android.volley.AuthFailureError {
            return params;
        };

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Charset", "UTF-8");
            headers.put("Content-Type", "application/json");
            headers.put("Accept-Encoding", "gzip,deflate");
            return headers;
        }
    }
}
