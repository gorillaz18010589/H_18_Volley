package tw.org.iii.appps.h_18_volley_open_data;
//1.繼承Request<這邊看你要的東西是什麼>
//2.建構式跟實作方法呼叫
//3.在建構式中加上response.listen
//4.實作方法稍微寫一下
import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

public class UrlInputStreamRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> listener;
    private Map<String,String> responseHeaders; //檔頭資料通常用Map資料結溝
    private Map<String,String> params;//post參數
    public UrlInputStreamRequest(int method,//1.方法
                                 String url,//2.網址
                                 Response.Listener<byte[]>listen,   //3.這個第三個參數,是回呼的參數類別要給自己家
                                 Map<String,String>params,//4.post參數自己定義的
                                 @Nullable Response.ErrorListener listener) {//5.錯誤訊息
        super(method, url, listener);
        this.listener = listen;
        this.params =params; //外部參數帶入存取
    }
    //自己呼叫ovver rider參數方法
    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        responseHeaders = response.headers;
        // <T> Response<T> success(T result, Cache.Entry cacheEntry)
        return Response.success(response.data, //回傳的資料結構
                HttpHeaderParser.parseCacheHeaders(response));//回傳的資料標頭檔
    }

    //分发响应的结果，我们可以通过将这个response放到监听器里来获取响应结果。
    @Override
    protected void deliverResponse(byte[] response) {
    listener.onResponse(response);
    }
}
