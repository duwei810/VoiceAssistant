package com.iflytek;

import java.util.List;
import java.net.*;
import java.io.*;

import org.json.JSONArray;
import org.json.JSONObject;
public class Translation {
 
    private static String translateURL = null;
    private static String api_key = null;
    private static String context = null;
    private static String from = null;
    private static String to = null;
    public Translation() {
    	translateURL = "http://openapi.baidu.com/public/2.0/bmt/translate";
    	api_key = "OaYbXBtre4GRfOMqf0fLnp5z";
    	context=JsonParse.getContent();
    	from = JsonParse.getSource();
    	to = JsonParse.getTarget();
	}
		
	
    public String getTransResult() throws Exception {
    	
    	translateURL=translateURL+"?client_id="+api_key+"&q="+context+"&from="+from+"&to="+to;
    	URL url;
		url = new URL(translateURL);
		URLConnection urlConnection=url.openConnection();
	    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    String result=bufferedReader.readLine();
	    JSONObject trans = new JSONObject(result);
	    JSONArray trans_result = trans.getJSONArray("trans_result");
	    JSONObject info=trans_result.getJSONObject(0);
	    String dst = info.getString("dst");
	    return dst;
		
    	
    }
    
}