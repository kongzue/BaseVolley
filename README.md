# BaseVolley
因为觉得每次写Volley请求太麻烦而进行的二次封装

### 版本
3.0

废弃了ErrorListener监听器，合并请求成功和错误的返回监听器为同一个新的监听器：ResponseListener，请在ResponseListener中直接判断Exception是否为空，若为空即请求成功。

同时，在HttpRequest中提供了基础请求类BaseRequest可以直接重写其 getHeaders() 方法以实现请求头的修改。

### 原因
1) Volley的请求参数不支持连续添加，写法不够轻松简洁
2) 我们项目主要使用Json交互，所以对请求结果默认转Json进行处理
3) 提供统一返回监听器ResponseListener处理返回数据 //重新自定义了ErrorListener错误返回回调函数

### 食用方法
```
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
```
POST请求可以使用HttpRequest.getInstance(context).postRequest(...);方法；

GET请求可以使用HttpRequest.getInstance(context).getRequest(...);方法进行。

Parameter是有序参数，方便某些情况下对参数进行加密和校验。
