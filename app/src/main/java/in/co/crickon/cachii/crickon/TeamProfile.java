package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TeamProfile extends AppCompatActivity {

    TextView txtteamname, txtPincode, txtcaptainname,txtdescription, txtwin, txtloss;


    private static final String TAG = TeamProfile.class.getSimpleName();
    private static final String TAG_TEAMID = "teamId";
    String teamid;
    private ProgressDialog pDialog;
    private SessionManager session;


    // Server user login url
    public static String URL_MYPROFILE = "http://crickon.co.in/php/TeamProfile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_profile);

        txtteamname = (TextView) findViewById(R.id.tvtn);
        //txtcaptainname = (TextView) findViewById(R.id.capnam);
        txtdescription = (TextView) findViewById(R.id.tvd);
        txtwin = (TextView) findViewById(R.id.tvw);
        txtloss = (TextView) findViewById(R.id.tvl);
        txtPincode = (TextView) findViewById(R.id.tvpc);

        Intent intent=getIntent();
        teamid=intent.getStringExtra(TAG_TEAMID);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        profile(teamid);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    private void profile(final String playerId) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Loading...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_MYPROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session


                        JSONObject user = jObj.getJSONObject("user");
                        String teamname=user.getString("Teamname");
                        //String captainname = user.getString("CaptainName");
                        String win=user.getString("Win");
                        String loss = user.getString("Loss");
                        String Pincode = user.getString("Pincode");
                        String description = user.getString("Description");
                        // String bowler = user.getString("Bowler");
                        // String wk = user.getString("WK");
                        //String Teamid = user.getString("Teamid");
                        //String Role = user.getString("Role");

                        // Log.e("check", PlayerId+""+Name+""+Phno+""+Pincode+""+batsman+""+bowler+""+wk +""+Age);

                        txtteamname.setText(teamname);
                        txtPincode.setText(Pincode);
                        //txtcaptainname.setText(captainname);
                        txtwin.setText(win);
                        txtloss.setText(loss);
                        txtdescription.setText(description);

                        //txtAge.setText();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),"No Internet connection", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("TeamId", teamid);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}



