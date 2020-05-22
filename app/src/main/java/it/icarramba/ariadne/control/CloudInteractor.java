package it.icarramba.ariadne.control;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.icarramba.ariadne.constants.Constants;

public class CloudInteractor implements Response.ErrorListener, Response.Listener<String> {

    private List<String> headers;
    private String domain;
    private Context context;
    private CloudListener cl;

    public CloudInteractor(String domain, Context context, CloudListener cl){
        headers = new ArrayList<>();
        this.context = context;
        this.domain = domain;
        this.cl = cl;
    }

    public void addHeader(String headerName, String value){
        this.headers.add(headerName+","+value);
    }

    public void sendRequest(String lat, String lon, String time, String  transport){

        cl.beforeCall();

        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="http://"+this.domain+":"+Constants.Cloud.SERVER_PORT+"/?"+ Constants.Cloud.LAT+"="+lat+
                    "&"+Constants.Cloud.LON+"="+lon+"&"+Constants.Cloud.TIME+"="+time+
                    "&"+Constants.Cloud.TRA+"="+transport;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, this, this) {
            //Adding headers, if any.
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                for(String header : headers){
                    String[] par = header.split(",");
                    params.put(par[0], par[1]);
                }
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        System.out.println("Queued");
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        cl.onError();
    }

    @Override
    public void onResponse(String response) {
        System.out.println("Responded");
        cl.afterCall(response);
    }
}
