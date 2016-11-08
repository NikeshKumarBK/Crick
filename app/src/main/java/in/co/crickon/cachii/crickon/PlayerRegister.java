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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlayerRegister extends AppCompatActivity {

    //ui declaration

    EditText edt_name,edt_age,edt_phonenumber,edt_pincode,edt_password;

    //Registration declaration
    private static final String TAG = PlayerRegister.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    String facBatsman="",facBowler="",facWicketkeeper="";
    int flagBatsman,flagBowler,flagWicketkeeper;

    CheckBox chkbatsman, chkbowler , chkwicketkeeper;

    String name,age,phonenumber,pincode,password,message;

    public static String URL_REGISTER="http://crickon.co.in/php/RegisterPlayer.php";

    //server you type da


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle=getIntent().getExtras();
        message=bundle.getString("Role");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chkbatsman = (CheckBox)findViewById(R.id.chkBatsman);
        chkbowler = (CheckBox)findViewById(R.id.chkBowler);
        chkwicketkeeper = (CheckBox)findViewById(R.id.chkWicketkeeper);

        edt_name=(EditText)findViewById(R.id.register_name);
        edt_age=(EditText)findViewById(R.id.register_age);
        edt_phonenumber=(EditText)findViewById(R.id.register_phoneNumber);
        edt_pincode=(EditText)findViewById(R.id.register_pincode);
        edt_password=(EditText)findViewById(R.id.register_password);

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
            Intent intent = new Intent(PlayerRegister.this,
                    Login.class);
            startActivity(intent);
            finish();
        }


        chkbatsman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagBatsman==0)
                {
                    flagBatsman=1;
                    facBatsman = "Yes";
                }
                else
                {
                    flagBatsman=0;
                    facBatsman = "No";
                }
            }
        });
        chkbowler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagBowler==0)
                {
                    flagBowler=1;
                    facBowler = "Yes";
                }
                else
                {
                    flagBowler=0;
                    facBowler = "No";
                }
            }
        });
        chkwicketkeeper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagWicketkeeper==0)
                {
                    flagWicketkeeper=1;
                    facWicketkeeper = "Yes";
                }
                else
                {
                    flagWicketkeeper=0;
                    facWicketkeeper = "No";
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = edt_name.getText().toString().trim();
                age = edt_age.getText().toString().trim();
                phonenumber = edt_phonenumber.getText().toString().trim();
                pincode = edt_pincode.getText().toString().trim();
                password = edt_password.getText().toString().trim();

                if (CheckNetwork.isInternetAvailable(PlayerRegister.this)) //returns true if internet available
                {
                    if (!name.isEmpty() && !age.isEmpty() && !phonenumber.isEmpty() && !pincode.isEmpty() && !password.isEmpty() ) {
                        Log.e("Register", "Not empty");

                        if(facBatsman.matches(""))
                            facBatsman="No";
                        if(facBowler.matches(""))
                            facBowler="No";
                        if(facWicketkeeper.matches(""))
                            facWicketkeeper="No";

                        registerPlayer( name, age, phonenumber, pincode, password ,facBatsman,facBowler,facWicketkeeper);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter your details!", Toast.LENGTH_LONG)
                                .show();
                    }
                }
                else
                {
                    Toast.makeText(PlayerRegister.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void registerPlayer(final String name, final String age,final String phonenumber, final String pincode, final String password, final String facBatsman, final String facBowler, final String facWicketkeeper) {

        Log.e("Register", "Inside registerPlayer");
        // Tag used to cancel the request
        String tag_string_req = "req_registerStaff";

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

                        JSONObject Player = jObj.getJSONObject("user");
                        String playerId = Player.getString("PlayerId");
                        String name = Player.getString("Name");
                        String phonenumber = Player.getString("Phonenumber");
                        String pincode = Player.getString("Pincode");
                        Log.e("Check",name+""+phonenumber+""+pincode);

                        //db.addPlayer(name,phonenumber, pincode);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        if(message.matches("Player"))
                        {
                            Intent intent = new Intent(PlayerRegister.this,Login.class);
                            startActivity(intent);
                            finish();
                        }
                        if(message.matches("Captain"))
                        {
                            Intent intent = new Intent(PlayerRegister.this,TeamRegister.class);
                            intent.putExtra("capPhNo",phonenumber);
                            intent.putExtra("playerId",playerId);
                            startActivity(intent);
                            finish();
                        }

                        // Launch login activity

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        //Toast.makeText(getApplicationContext(),
                        //      errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

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
                params.put("name", name);
                params.put("age", age);
                params.put("phonenumber", phonenumber);
                params.put("Pincode", pincode);
                params.put("password", password);
                params.put("facbatsman",facBatsman);
                params.put("facbowler",facBowler);
                params.put("facwicketkeeper", facWicketkeeper);

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

