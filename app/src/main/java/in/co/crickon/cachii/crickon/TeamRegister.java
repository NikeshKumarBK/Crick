package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TeamRegister extends AppCompatActivity {



    EditText edt_teamname,edt_area,edt_desc,edt_pincode;

    Spinner team_type,team_nature;
    String[] Team={"Team Type","Corporate","Street Cricket"};
    String[] nat={"Ball Type","Tennis ball","Stitch Ball"};

    String selectedType,selectedNature,teamname, area, pincode, desc,capPhNo, capId;

    public static String URL_REGISTER="http://crickon.esy.es/php_files/RegisterTeam.php";

    //Registration declaration
    private static final String TAG = PlayerRegister.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle=getIntent().getExtras();
        capPhNo=bundle.getString("capPhNo");
        capId=bundle.getString("playerId");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edt_teamname = (EditText)findViewById(R.id.team_register_name);
        edt_area = (EditText)findViewById(R.id.team_register_area);
        edt_desc = (EditText)findViewById(R.id.team_register_desc);
        edt_pincode = (EditText)findViewById(R.id.team_register_pincode);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(TeamRegister.this,
                    Login.class);
            startActivity(intent);
            finish();
        }

        team_type =(Spinner)findViewById(R.id.teamtype);
        ArrayAdapter<String> team=new ArrayAdapter<String>(TeamRegister.this,android.R.layout.simple_spinner_item,Team);
        team_type.setAdapter(team);
        team_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        team_nature =(Spinner)findViewById(R.id.nature);
        ArrayAdapter<String> nature=new ArrayAdapter<String>(TeamRegister.this,android.R.layout.simple_spinner_item,nat);
        team_nature.setAdapter(nature);
        team_nature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNature = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamname = edt_teamname.getText().toString().trim();
                area = edt_area.getText().toString().trim();
                desc = edt_desc.getText().toString().trim();
                pincode = edt_pincode.getText().toString().trim();

                if (CheckNetwork.isInternetAvailable(TeamRegister.this)) //returns true if internet available
                {
                    if (!teamname.isEmpty() && !area.isEmpty() && !desc.isEmpty() && !pincode.isEmpty() ) {
                        Log.e("Register", "Not empty");

                        if (selectedType.matches("Team"))
                            selectedType="No";
                        if (selectedNature.matches("nat"))
                            selectedNature="No";

                        registerTeam(teamname, area, desc, pincode, selectedType, selectedNature);
                    } else {
                        Toast.makeText(getApplicationContext(),"Please enter your details!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TeamRegister.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerTeam(final String teamName, final String teamarea,
                               final String teamdesc, final String teampinc, final String selectedteamtype,
                               final String selectedteamnature) {

        Log.e("Register", "Inside registerUser");
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        Log.e("Register", "After ShowDialog");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response1: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        //String uid = jObj.getString("RegNo");

                        JSONObject user = jObj.getJSONObject("user");
                        String Name = user.getString("TeamName");
                        String pincode = user.getString("Pincode");
                        // Inserting row in users table
                        db.addTeam( Name,pincode);

                        Toast.makeText(getApplicationContext(), "Team successfully created!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(TeamRegister.this,Login.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(TeamRegister.this,"Faculty id already exists",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),
                        //      errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "No Internet Connection", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("TeamName", teamName);
                params.put("Pincode", teampinc);
                params.put("Area", teamarea);
                params.put("Description", teamdesc);
                params.put("TeamType", selectedteamtype);
                params.put("Nature", selectedteamnature);
                params.put("CaptainId", capId);
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
        Intent intent=new Intent(TeamRegister.this,Register.class);
        startActivity(intent);
        finish();
    }
}
