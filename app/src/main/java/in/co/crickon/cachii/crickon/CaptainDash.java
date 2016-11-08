package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

public class CaptainDash extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtPlayerName, txtAge, txtbatsman;
    TextView txtbowler, txtwicketkeeper, txtPhonenumber, txtpincode, txtTeamName,txtCapName,txtTeamWin,txtTeamLost;


    private static final String TAG = MyProfile.class.getSimpleName();

    String playerId;
    private ProgressDialog pDialog;
    private SessionManager session;


    // Server user login url
    public static String URL_MYPROFILE = "http://crickon.co.in/php/PlayerProfile.php";



    TextView txtcapName,txtPhno;
    private static final String TAG_CAPTAINID = "CaptainId";
    private static final String TAG_PINCODE = "Pincode";
    Button btnCheckReq,btnSendMatchReq,btnCheckMatchReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_dash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        SQLiteHandler repo=new SQLiteHandler(CaptainDash.this);
        playerId=repo.getPlayerID();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        profile(playerId);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
/*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        TextView name = (TextView)header.findViewById(R.id.txtCapName);
        TextView phno = (TextView)header.findViewById(R.id.txtcapPhno);

        String capname=repo.getPlayerName();
        String capPincode=repo.getPincode();
        name.setText(capname);
        phno.setText(capPincode);
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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_player_request) {
            // Handle the camera action
            SQLiteHandler repo=new SQLiteHandler(CaptainDash.this);
            String captainId=repo.getPlayerID();
            Intent intent=new Intent(CaptainDash.this,CheckRequest.class);
            intent.putExtra(TAG_CAPTAINID, captainId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_my_profile) {
            Intent intent=new Intent(CaptainDash.this,Profiles.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_matches) {
            Intent intent=new Intent(CaptainDash.this,Matches.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_sign_out) {
            SessionManager session = new SessionManager(CaptainDash.this);
            session.setLogin(false);
            SQLiteHandler repo=new SQLiteHandler(getApplicationContext());
            repo.deleteUsers();
            Intent intent=new Intent(CaptainDash.this,Login.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_match_request) {
            Intent intent=new Intent(CaptainDash.this,MatchRequests.class);
            startActivity(intent);
            finish();
        }  else if (id == R.id.nav_about_us) {
            Intent intent=new Intent(CaptainDash.this,Aboutuss.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_feedback) {
            Intent intent=new Intent(CaptainDash.this,Feedback.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_leaderboard) {
            Intent intent=new Intent(CaptainDash.this,Leaderboard.class);
            startActivity(intent);
            finish();
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
