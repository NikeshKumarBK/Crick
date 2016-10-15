package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class Login extends AppCompatActivity {

    Button btnLogin,btnRegister;
    EditText edtUsername,edtPwd;

    private static final String TAG = Login.class.getSimpleName();

    String reg,pwd;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    // Server user login url
    public static String URL_LOGIN = "http://crickon.esy.es/php_files/login.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername=(EditText)findViewById(R.id.edtLoginUser);
        edtPwd=(EditText)findViewById(R.id.edtLoginpwd);
        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnRegister=(Button)findViewById(R.id.btnRegister);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());


        // Check if user is already logged in or not
        if (session.isLoggedIn()) {

            SQLiteHandler repo=new SQLiteHandler(this);
            String role=repo.getRole();

            if (role.matches("Player"))
            {
                // Launch main activity
                Intent intent = new Intent(Login.this,PlayerDash.class);
                startActivity(intent);
                finish();
            }
            else if (role.matches("Captain"))
            {
                // Launch main activity
                Intent intent = new Intent(Login.this,CaptainDash.class);
                startActivity(intent);
                finish();
            }
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"Login",Toast.LENGTH_SHORT).show();

                reg = edtUsername.getText().toString().trim();
                pwd = edtPwd.getText().toString().trim();

                if(CheckNetwork.isInternetAvailable(Login.this)) //returns true if internet available
                {
                    // Check for empty data in the form
                    if (!reg.isEmpty() && !pwd.isEmpty()) {
                        // login user
                        checkLogin(reg, pwd);
                    } else {
                        // Prompt user to enter credentials
                        Toast.makeText(getApplicationContext(),"Please enter the credentials!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(Login.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void checkLogin(final String regid, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_LOGIN, new Response.Listener<String>() {

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
                        session.setLogin(true);

                        JSONObject user = jObj.getJSONObject("user");
                        String PlayerId=user.getString("PlayerId");
                        String Name = user.getString("Name");
                        String Phno = user.getString("Phno");
                        String pincode = user.getString("Pincode");
                        String Teamid = user.getString("Teamid");
                        //String desig = user.getString("desig");
                        String Role = user.getString("Role");

                        // Inserting row in users table
                        db.addPlayer(PlayerId, Name, Phno, Teamid, pincode, Role);

                        if (Role.matches("Player"))
                        {
                            // Launch main activity
                            Intent intent = new Intent(Login.this,PlayerDash.class);
                            startActivity(intent);
                            finish();
                        }
                        else if (Role.matches("Captain"))
                        {
                            // Launch main activity
                            Intent intent = new Intent(Login.this,CaptainDash.class);
                            startActivity(intent);
                            finish();
                        }

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
                params.put("phno", regid);
                params.put("password", password);

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

