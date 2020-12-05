package com.example.final_application;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    TextView tv;
    EditText et;
    Button bt;

    JSONObject jsonObject;

    RequestQueue requestQueue;

    String searchText;
    String serviceKey = "EHOHinv8kC9YMj0tpx6CxoXkKI51jTHw1VDvBExBNLxuV3pD3n%2BsVdYd0BPBvtk9Ptdlkng2KRY1mwsHSMPkgA%3D%3D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = findViewById(R.id.et);
        bt = findViewById(R.id.bt);
        tv = findViewById(R.id.tv);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("");
                doSearchStation();
            }
        });
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    public void doSearchStation() {
        StringBuffer buffer=new StringBuffer();
        searchText = et.getText().toString();
        if (searchText.equals("")) {
            Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "success" + searchText);

        String url="http://openapi.tago.go.kr/openapi/service/SubwayInfoService/getKwrdFndSubwaySttnList?"
                +"subwayStationName="+searchText
                +"&ServiceKey="+serviceKey
                +"&_type=json&numOfRows=100&pageNo=1";

        StringRequest request = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = null;
                        try {
                            result = new String(response.getBytes("8859_1"),"utf-8");
                            Log.d(TAG, result);

                            JSONObject jsonObject = new JSONObject(result);
                            String jsonStr = jsonObject.getString("response");

                            JSONObject jsonObject2 = new JSONObject(jsonStr);
                            String jsonStr2 = jsonObject2.getString("body");

                            JSONObject jsonObject3 = new JSONObject(jsonStr2);
                            String jsonStr3 = jsonObject3.getString("items");

                            JSONObject jsonObject4 = new JSONObject(jsonStr3);
                            String jsonStr4 = jsonObject4.getString("item");

                            JSONArray jsonArray = new JSONArray(jsonStr4);

                            ArrayList<String> printList = new ArrayList<>();

                            for (int i=0; i < jsonArray.length(); i++) {
                                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                                String routeName = subJsonObject.getString("subwayRouteName");
                                String stationName = subJsonObject.getString("subwayStationName");
                                String print="호선 : "+routeName+", 이름 : "+stationName+"\n";

                                printList.add(print);
                            }

                            tv.setText(printList + "\n");

                            Log.d(TAG, "success");

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tv.append("error\n");
                        Log.d(TAG, "fail");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(TAG, "request");
        tv.append("processing"+"\n");
    }
}

