package tw.org.iii.appps.h_18_volley_open_data;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MainApp extends Application {
    public RequestQueue queue; //queue,用公開部封裝,這樣很多程式都能玩到
    @Override
    public void onCreate() {
        super.onCreate();
        //RequestQueue newRequestQueue(Context context):從Volley物件取得RequestQueue(回傳到RequestQueue)
       queue = Volley.newRequestQueue(this);//從Volley物件取得RequestQueue(回傳到RequestQueue)
    }
}
