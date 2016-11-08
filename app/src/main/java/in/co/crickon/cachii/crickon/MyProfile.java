package in.co.crickon.cachii.crickon;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class MyProfile extends AppCompatActivity {

    TextView txtPlayerName, txtAge, txtbatsman;
    TextView txtbowler, txtwicketkeeper, txtPhonenumber, txtpincode, txtTeamName,txtCapName,txtTeamWin,txtTeamLost;


    private static final String TAG = MyProfile.class.getSimpleName();

    String playerId;
    private ProgressDialog pDialog;
    private SessionManager session;


    // Server user login url
    public static String URL_MYPROFILE = "http://crickon.co.in/php/PlayerProfile.php";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile1);

        txtPlayerName = (TextView) findViewById(R.id.txtPlayerName);
        txtAge = (TextView) findViewById(R.id.txtAge);
        txtbatsman = (TextView) findViewById(R.id.txtbatsman);
        txtbowler = (TextView) findViewById(R.id.txtbowler);
        txtwicketkeeper = (TextView) findViewById(R.id.txtwicketkeeper);
        txtPhonenumber = (TextView) findViewById(R.id.txtPhonenumber);
        txtpincode = (TextView) findViewById(R.id.pincode);

        txtTeamName=(TextView)findViewById(R.id.txtProfileTeamName);
        txtCapName=(TextView)findViewById(R.id.txtProfileCapName);
        txtTeamWin=(TextView)findViewById(R.id.txtProfileWon);
        txtTeamLost=(TextView)findViewById(R.id.txtProfileLost);

        Bundle bundle=getIntent().getExtras();
        playerId=bundle.getString("PlayerId");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        profile(playerId);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(MyProfile.this,Login.class);
        startActivity(intent);
        finish();
    }

    private void profile(final String playerId) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Fetching details...");
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
                        String PlayerId=user.getString("PlayerId");
                        String Name = user.getString("Name");
                        String Age = user.getString("Age");
                        String Phno = user.getString("Phno");
                        String pincode = user.getString("Pincode");
                        String batsman = user.getString("Batsman");
                        String bowler = user.getString("Bowler");
                        String wk = user.getString("WK");
                        String teamname= user.getString("TeamName");
                        String capname= user.getString("CaptainName");
                        String teamwin= user.getString("Win");
                        String teamlost= user.getString("Lost");


                        txtPlayerName.setText(Name);
                        txtAge.setText(Age);
                        txtPhonenumber.setText(Phno);
                        txtbatsman.setText(batsman);
                        txtbowler.setText(bowler);
                        txtwicketkeeper.setText(wk);
                        txtpincode.setText(pincode);
                        txtTeamName.setText(teamname);
                        txtCapName.setText(capname);
                        txtTeamWin.setText(teamwin);
                        txtTeamLost.setText(teamlost);

                        Log.e("check", PlayerId+""+Name+""+Phno+""+pincode+""+"");

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
                params.put("playerId", playerId);

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




