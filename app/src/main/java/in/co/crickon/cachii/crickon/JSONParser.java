package in.co.crickon.cachii.crickon;

/**
 * Created by nikeshkumarbk on 08/10/16.
 */
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
                Log.e("Check", "Entered POST method");
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                Log.e("Check","url initialized");
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                Log.e("Check", "Parameters encoded");

                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.e("Check","httppost executed");
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.e("Check","httpresponse got");
                is = httpEntity.getContent();
                Log.e("Check","set to input stream");

            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                //Log.e("Check","HttpClient initialized");
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
                //Log.e("Check","httpget initialized");

                HttpResponse httpResponse = httpClient.execute(httpGet);
                //Log.e("Check","httpget executed");
                HttpEntity httpEntity = httpResponse.getEntity();
                //Log.e("Check","httpresponse got");
                is = httpEntity.getContent();
                //Log.e("Check","set to input stream");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.e("Check",sb.toString());

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            Log.i("tagconvertstr", "["+json+"]");
            jObj = new JSONObject(json);
            Log.e("Check","jobj initialized");
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
}