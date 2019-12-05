package tw.org.iii.appps.h_18_volley_open_data;
//缺點:1.必須包在執行緒 //2.可能要透過handler
//目的:android Volley :是一個HTtp的 可以讓我們的網路在網頁可以執行非常快,執行緒跟ui一次就處理掉

//1.先在檔案管理加入int網路權限,跟明碼傳送
//2.build.gradle=>Module,加上 implementation 'com.android.volley:volley:1.1.1'
//3.Send a simple request:幫我們徘程,就是取代之前要用執行緒去排任務的事情,會幫我們自動管理任務的排程
//3-1.Use newRequestQueue:我們會跟Queue來自動排程,對app來說一組一個就好,所以如果跳頁面也用同一個就好,這邊另外寫類別繼承Apiction
//3-2.寫一個class類別,我習慣叫MainApp,去繼承Application,這時去檔案總管Application內容,加上android:name=".MainApp",作為主要的管理動作
//因為頁面頁面之間的傳遞,用這招省去傳參數,比較方便
//3-3.設置on Creat在創建時, 取得Reqquer物件queue = Volley.newRequestQueue(this);//從Volley物件取得RequestQueue(回傳到RequestQueue)
//3-4.回到主程式從applictaion宣告呼叫來玩

//最大寬度,如果不指定就0,0
//Bitmap.Config.ARGB_8888,//影像要如何做組態解碼
// null //錯誤訊息

//newRequestQueue(Context context):從Volley物件取得RequestQueue(回傳到RequestQueue)
//getApplication()://取得application (回傳到Application )
// StringRequest:(
//            int method,////1.使用的方法post/get
//            String url,//2.要連接的網址
//            Listener<String> listener,//3.傳呼回來的Rseponse回應監聽者
//            @Nullable ErrorListener errorListener)//4.這邊回傳犯行設定的String訊息
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    private MainApp mainApp; //宣告寫好的application,這邊已經把RequestQueue物件實體化,好處是以後直接叫不用再用帶參數傳遞
    private TextView tv; //抓到url頁面呈現的地方
    private ImageView img;
    private boolean isWriteSDCard = true;
    private File sdcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //6.讀寫外部檔案權限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        } else {
            isWriteSDCard = true;
        }

        //1.這邊已經把RequestQueue物件實體化,好處是以後直接叫不用再用帶參數傳遞
        mainApp = (MainApp) getApplication();//這邊已經把RequestQueue物件實體化,好處是以後直接叫不用再用帶參數傳遞

        tv = findViewById(R.id.tv);
        img = findViewById(R.id.img);
        sdcard = Environment.getExternalStorageDirectory();//取得外部儲存路徑
    }

    //7.回應權限,如果問了室友權限,就為true給他玩
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isWriteSDCard = true;
        }
    }

    //2.按下按鈕抓取資策會抓url資料,利用Volley的queue的方法取得
    public void text1(View view) {
        StringRequest request = new StringRequest(
                Request.Method.GET,//1.使用的方法post/get
                "https://www.iii.org.tw",//2.要連接的網址
                new Response.Listener<String>() { //3.傳呼回來的Rseponse回應監聽者
                    @Override
                    public void onResponse(String response) {//這邊回傳url<String>的訊息,到response
                        Log.v("brad", "回傳Response<String>的call back資訊:" + response);
                        tv.setText(response);//直接抓到訊息,顯示出來
                    }
                },
                null//4.出現錯誤訊息時的監聽者
        );
        mainApp.queue.add(request);//把Request.queue掛上我們要要求的回應送出去(尾)
    }

    //2.按下按鈕抓取opendata資料,回傳到Log上
    public void text2(View view) {

        StringRequest request = new StringRequest(
                Request.Method.GET,//1.使用的方法post/get
                " http://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx",//2.要連接的網址
                new Response.Listener<String>() { //3.傳呼回來的Rseponse回應監聽者
                    @Override
                    public void onResponse(String response) {//這邊回傳url<String>的訊息,到response
                        parseJSON(response);

                    }
                },
                null//4.出現錯誤訊息時的監聽者
        );
        mainApp.queue.add(request);//把Request.queue掛上我們要要求的回應送出去(尾)
    }

    private void parseJSON(String json) {
        try {
            JSONArray root = new JSONArray(json);
            for (int i = 0; i < root.length(); i++) {
                JSONObject row = root.getJSONObject(i);
                Log.v("brad", row.getString("Name") + ":" + row.getString("Tel"));
            }
        } catch (Exception e) {

        }

    }
//    ImageRequest(
//            String url,//1.url網址
//            Response.Listener<Bitmap> listener,//2.回乎回來的圖片訊息<Bitmap>
//            int maxWidth,//3.最大寬
//            int maxHeight,//4.最大高
//            ScaleType scaleType,//5.縮放方式
//            Config decodeConfig,//6.Bitmap的組態圖片設定方式
//            @Nullable Response.ErrorListener errorListener)//7.錯誤訊息

    //3.按下按鈕抓取opendata,圖片顯示出來
    public void text3(View view) {
        ImageRequest request = new ImageRequest(
                "https://ezgo.coa.gov.tw/Uploads/opendata/TainmaMain01/APPLY_D/20151007173924.jpg",//1.url網址
                new Response.Listener<Bitmap>() {//2.回乎回來的圖片訊息<Bitmap>
                    @Override
                    public void onResponse(Bitmap response) {
                        img.setImageBitmap(response);
                    }
                },
                0, 0,//3.最大寬,4.最大高
                null,//5.縮放方式
                Bitmap.Config.ARGB_8888,//6.Bitmap的組態圖片設定方式
                null//7.錯誤訊息
        );
        mainApp.queue.add(request);
    }

    //4.用volley自訂的解json方式取得資料,要先看你是陣列包還是物件包
//    JsonArrayRequest(
//            String url, Listener<JSONArray> listener, @Nullable ErrorListener errorListener)//1.url網址//2.回傳JsonArry陣列犯行//3.錯誤訊息
    public void text4(View view) {
        JsonArrayRequest request = new JsonArrayRequest(
                " http://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx",//1.url網址
                new Response.Listener<JSONArray>() {//2.回傳JsonArry陣列犯行
                    @Override
                    public void onResponse(JSONArray response) {
                        ParsejsonObject(response);
                    }
                }, null//3.錯誤訊息
        );
        mainApp.queue.add(request);
    }

    //4.搭配解新json物件
    private void ParsejsonObject(JSONArray root) {

        for (int i = 0; i < root.length(); i++) {
            try {
                JSONObject row = root.getJSONObject(i);
                Log.v("brad", row.getString("Name") + ":" + row.getString("Tel"));
            } catch (Exception e) {
                Log.v("brad", e.toString());
            }


        }
    }

    //5.處理檔案串流的方法,下載圖片,用自己寫好的UrlIputStream類別來處理
    //*因為要存檔所以權限跟詢問權限要做
    public void text5(View view) {
        if (!isWriteSDCard) return; //如果沒有權限,就不准讀取檔案,但其他按鈕還可以玩
        UrlInputStreamRequest request = new UrlInputStreamRequest(
                Request.Method.GET,
//                "https://ezgo.coa.gov.tw/Uploads/opendata/TainmaMain01/APPLY_D/20151007173924.jpg",//這是下載一張圖片
                "https://pdfmyurl.com/?url=https://www.pchome.com.tw", //這是用pdf帶參數下載pdf檔,那下面的檔案名稱也要改
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.v("brad", "檔案大小:" + response.length);
                        saveSDcard(response);//呼叫把這張照片存放的檔案位置
                    }
                },
                null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("brad", "下載圖片錯誤訊息:" + error.toString());
                    }
                }
        );
        //當volley延遲過久,設置重試方法
        request.setRetryPolicy(new DefaultRetryPolicy( //new 默認重試方法
                20*1000,//設定跑20秒
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mainApp.queue.add(request);

    }
    //7.自己寫的檔案存放位置
    private void saveSDcard(byte[] data) {
        File saveFile = new File(sdcard, "Download/pchome.pdf");
        try {
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(saveFile));
            bout.write(data);
            bout.flush();
            bout.close();
            Toast.makeText(this, "儲存成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.v("brad", "下載圖片到sdcard底下Download路徑失敗:" + e.toString());
        }

    }
    //8.按下按鈕連到第二頁,第二頁做的事情是輸入參數,把參數帶到你的url網址去新增資料庫
    public void text6(View view) {
        Intent intent = new Intent(this,Page2Activity.class);
        startActivity(intent);

    }
}



