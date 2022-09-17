package com.example.smartmedicationmanager;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MedicRegisterRequest extends StringRequest {
    final static private String URL = "http://medichelper.dothome.co.kr/medicregister.php";
    private Map<String, String> map;

    public MedicRegisterRequest(String uID, String mName, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("uID", uID);
        map.put("mName", mName);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
