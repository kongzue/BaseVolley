import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONObject;

public class DemoActivity extends AppCompatActivity {

    private DemoActivity me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        
        HttpRequest.getInstance(me).postRequest("http://www.xxx.com/test", new Parameter()
                        .add("key1", "value1")
                        .add("key2", "value3")
                        .add("key4", "value4"),
                new ResponseListener() {
                    @Override
                    public void onResponse(JSONObject main, Exception error) {
                        if (error == null) {
                            //请求成功处理
                            
                        } else {
                            //请求失败处理
                            toast("网络错误，请重试");
                        }
                    }
                });
    }
}
