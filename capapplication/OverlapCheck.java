package com.example.capapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class OverlapCheck extends StringRequest{
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://capgroup2.dothome.co.kr/idOverlap.php";
    private Map<String, String> map;

    public OverlapCheck(String id, Response.Listener<String> listener){
        super(Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("id", id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
