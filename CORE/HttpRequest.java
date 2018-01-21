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
 * ver:4.0
 */

public class HttpRequest {

    private RequestQueue mQueue;            //Volley请求队列

    //单例
    private static HttpRequest httpRequest;

    private Parameter headers;

    public Parameter getHeaders() {
        return headers;
    }

    public HttpRequest setHeaders(Parameter headers) {
        this.headers = headers;
        return this;
    }

    private HttpRequest() {
    }

    //默认请求创建方法
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

    //信任指定证书的Https请求
    public static HttpRequest getInstance(Context context, String SSLFileNameInAssets) {
        if (httpRequest == null) {
            synchronized (HttpRequest.class) {
                SSLSocketFactory sslSocketFactory = initSSLSocketFactory(context, SSLFileNameInAssets);
                HurlStack stack = new HurlStack(null, sslSocketFactory);
                httpRequest = new HttpRequest();
                httpRequest.mQueue = Volley.newRequestQueue(context, stack);         //创建请求队列
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

        final String finalUrl = partUrl;

        BaseRequest baseRequest = new BaseRequest(method, headers, finalUrl, new Response.Listener<JSONObject>() {
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

    //生成SSLSocketFactory
    private static SSLSocketFactory initSSLSocketFactory(Context context, String SSLFileNameInAssets) {
        //生成证书:Certificate
        CertificateFactory cf = null;
        SSLSocketFactory factory = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getAssets().open(SSLFileNameInAssets);
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                try {
                    caInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //初始化公钥:keyStore
            String keyType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            //初始化TrustManagerFactory
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory managerFactory = TrustManagerFactory.getInstance(algorithm);
            managerFactory.init(keyStore);

            //初始化sslContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, managerFactory.getTrustManagers(), null);
            factory = sslContext.getSocketFactory();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return factory;
    }

    class BaseRequest extends JsonObjectRequest {

        private Map<String, String> params;
        private Parameter headers;

        public BaseRequest(int method, String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, requestBody, listener, errorListener);
        }

        public BaseRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        public BaseRequest(int method, Parameter headers, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            this.params = params;
            this.headers = headers;
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
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
//            headers.put("Charset", "UTF-8");
//            headers.put("Content-Type", "application/json");
//            headers.put("Accept-Encoding", "gzip,deflate");
            if (headers == null || headers.isEmpty()) {
                return Collections.emptyMap();
            }
            return headers;
        }
    }
}
