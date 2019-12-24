package tw.org.iii.appps.h_18_volley_open_data;
//自我練習方法
//目的自訂泛型byte陣列的方法
//1.宣告繼承Request<byte[]> :想要資料結構是byte,byte是基本型別加上陣列後便物件,才能在網路傳遞
//2.建構式最重要的參數是Response,所以加上這個參數
//3.檔頭部分要去處理,Http有分成Header(傳遞資料的頭),跟Body(Data部分)
//4.寫一個params參數,用來處理post的參數
import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

//1.宣告繼承Request<byte[]> :想要資料結構是byte,byte是基本型別加上陣列後便物件,才能在網路傳遞
class MyByteInputStream extends Request<byte[]> {
    private final  Response.Listener<byte[]> listen; //自己寫的response回乎回來參數物件
    private Map<String,String> responseHeader;   //檔頭,通常是Map資料結構
    private Map<String,String> params;  //post參數

    //2.建構式最重要的參數是Response,所以加上這個參數
    public MyByteInputStream(int method,
                                String url,
                                Response.Listener<byte[]> listen,  //4.自己加的Res.linten,byte[]泛型
                                @Nullable Response.ErrorListener listener,
                                Map<String,String> params //自己加的post參數
                             ) {
        super(method, url, listener);
        this.listen = listen; //建構式時init
        this.params = params;
    }
    //4.取得參數方法要改寫成回傳我們的params
    @Override
    public Map<String, String> getParams() {
        return params;
    }

    //responseMap.headers :Respons的檔頭物件(回傳值<String, String> )
    //response./success(T result, Cache.Entry cacheEntry):當回乎成功時傳送資料(1.hasmap的Data,2.檔頭)(回傳泛型Response<T> )
    //HttpHeaderParser.parseCacheHeaders(NetworkResponse response):在http檔頭parse,pqrse出一個CachHeaders(回傳Cache.Entry )
    //3.收到檔頭後初始化,且回乎資料結構回傳
    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        responseHeader = response.headers;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {

    }
}