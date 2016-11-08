package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


public class TeamProfileFrag extends Fragment {
    TextView txtPlayerName, txtAge, txtbatsman;
    ImageView imgBat,imgBowl,imgWk;
    TextView txtbowler, txtwicketkeeper, txtPhonenumber, txtpincode, txtTeamName,txtCapName,txtTeamWin,txtTeamLost;


    private static final String TAG = MyProfile.class.getSimpleName();

    String playerId;
    private ProgressDialog pDialog;
    private SessionManager session;


    // Server user login url
    public static String URL_MYPROFILE = "http://crickon.co.in/php/PlayerProfile.php";


    public TeamProfileFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_team_profile, container, false);

        SQLiteHandler repo=new SQLiteHandler(getActivity());
        playerId=repo.getPlayerID();

        txtTeamName=(TextView)view.findViewById(R.id.txtProfileTeamName);
        txtCapName=(TextView)view.findViewById(R.id.txtProfileCapName);
        txtTeamWin=(TextView)view.findViewById(R.id.txtProfileWon);
        txtTeamLost=(TextView)view.findViewById(R.id.txtProfileLost);

        profile(playerId);

        return view;
    }

    private void profile(final String playerId) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";



        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_MYPROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());


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



                        txtTeamName.setText(teamname);
                        txtCapName.setText(capname);
                        txtTeamWin.setText(teamwin);
                        txtTeamLost.setText(teamlost);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(),"No Internet connection", Toast.LENGTH_LONG).show();

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

}
