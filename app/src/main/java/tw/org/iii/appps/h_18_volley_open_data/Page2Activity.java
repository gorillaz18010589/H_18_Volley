package tw.org.iii.appps.h_18_volley_open_data;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Page2Activity extends AppCompatActivity {
    private  MainApp mainApp;
    private EditText cname,tel,addr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        mainApp = (MainApp) getApplication();
        cname = findViewById(R.id.cname);
        tel = findViewById(R.id.tel);
        addr = findViewById(R.id.addr);
    }
    //按下按鈕送書參數到url,讓他post接收參數寫入資料庫
    public void test1(View view) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                "https://www.bradchao.com/autumn/addCust.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("brad",response);
                    }
                },null
        ){  //這邊要Overrrider取得參數方法
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>  params = new HashMap<>();
                params.put("cname",cname.getText().toString()); //設置key:cname ,取得這個使用者輸入的cname直,掛上去傳遞出去
                params.put("tel",tel.getText().toString());
                params.put("addr",addr.getText().toString());
                return params; //把掛好的職回傳出去
            }
        };
        mainApp.queue.add(request);
    }
}
