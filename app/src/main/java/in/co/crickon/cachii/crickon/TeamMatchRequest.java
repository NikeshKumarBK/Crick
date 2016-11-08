package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamMatchRequest extends AppCompatActivity {

    TextView txtTeamName,txtPincode,txtCaptainName,txtDesc,txtWin,txtLoss,txtDate,txtTime,txtLocation,txtOvers;
    Button btnConfirm,btnReject,btnReconsider;
    String Date,Time,Venue,Overs;

    private static final String TAG = TeamMatchRequest.class.getSimpleName();

    String matchId,status;
    private ProgressDialog pDialog;
    // team request url
    public static String URL_TEAMMATCHREQUEST = "http://crickon.co.in/php/TeamMatchRequest.php";

    // url update request
    private static String url_send_request = "http://crickon.co.in/php/updatematchrequest.php";

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_match_request);

        txtTeamName=(TextView)findViewById(R.id.txtTeamName);
        txtPincode=(TextView)findViewById(R.id.pincode);
        txtCaptainName=(TextView)findViewById(R.id.txtCapName);
        txtDesc=(TextView)findViewById(R.id.txtDesc);
        txtWin=(TextView)findViewById(R.id.txtWon);
        txtLoss=(TextView)findViewById(R.id.txtLoss);
        txtDate=(TextView)findViewById(R.id.txtDate);
        txtTime=(TextView)findViewById(R.id.txtTime);
        txtLocation=(TextView)findViewById(R.id.txtLocation);
        txtOvers=(TextView)findViewById(R.id.txtOvers);

        btnConfirm=(Button)findViewById(R.id.btnConfirm);
        btnReject=(Button)findViewById(R.id.btnReject);
        btnReconsider=(Button)findViewById(R.id.btnReconsider);

        Bundle bundle=getIntent().getExtras();
        matchId=bundle.getString("MatchId");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        requestDetails(matchId);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "accept";
                new UpdateRequest().execute();
                Toast.makeText(TeamMatchRequest.this, "Request updated", Toast.LENGTH_SHORT).show();
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "reject";
                new UpdateRequest().execute();
            }
        });

        btnReconsider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(TeamMatchRequest.this);
                View promptsView = li.inflate(R.layout.activity_reconsider_match_request, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        TeamMatchRequest.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText edtDate = (EditText) promptsView.findViewById(R.id.editTextDateBooking);
                final EditText edtTime = (EditText) promptsView.findViewById(R.id.editTextTimeBooking);
                final EditText edtOvers = (EditText) promptsView.findViewById(R.id.edtvenue);
                final EditText edtVenue = (EditText) promptsView.findViewById(R.id.edtovers);

                edtDate.setText(Date);
                edtTime.setText(Time);
                edtOvers.setText(Overs);
                edtVenue.setText(Venue);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        Date=edtDate.getText().toString();
                                        Time=edtTime.getText().toString();
                                        Venue=edtVenue.getText().toString();
                                        Overs=edtOvers.getText().toString();
                                        status = "consider";
                                        new UpdateRequest().execute();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }
        });
    }



    class UpdateRequest extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TeamMatchRequest.this);
            pDialog.setMessage("Sending Request...");
            pDialog.setIndeterminate(false);
            //pDialog.setCancelable(true);
            //pDialog1.show();
        }


        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("value", status));
            params.add(new BasicNameValuePair("matchid", matchId));
            params.add(new BasicNameValuePair("date", Date));
            params.add(new BasicNameValuePair("time", Time));
            params.add(new BasicNameValuePair("venue", Venue));
            params.add(new BasicNameValuePair("overs", Overs));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_send_request,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt("Success");
                Log.e("Check",""+success);

                if (success==1) {
                    // successfully updated
                    Intent intent=new Intent(TeamMatchRequest.this,Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    // failed to update product
                    Toast.makeText(TeamMatchRequest.this,"Request sent",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product uupdated
            //pDialog1.dismiss();

        }
    }


    private void requestDetails(final String matchId) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Fetching details...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_TEAMMATCHREQUEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {

                        JSONObject user = jObj.getJSONObject("user");
                        String TeamName=user.getString("TeamName");
                        String Pincode = user.getString("Pincode");
                        String CaptainName = user.getString("CaptainName");
                        String Description = user.getString("Description");
                        String Win = user.getString("Win");
                        String Lost = user.getString("Lost");
                        Date = user.getString("Date");
                        Time = user.getString("Time");
                        Venue= user.getString("Venue");
                        Overs= user.getString("Overs");

                        txtTeamName.setText(TeamName);
                        txtPincode.setText(Pincode);
                        txtCaptainName.setText(CaptainName);
                        txtDesc.setText(Description);
                        txtWin.setText(Win);
                        txtLoss.setText(Lost);
                        txtDate.setText(Date);
                        txtTime.setText(Time);
                        txtLocation.setText(Venue);
                        txtOvers.setText(Overs);
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
                params.put("MatchId", matchId);

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
        Intent intent=new Intent(TeamMatchRequest.this,Login.class);
        startActivity(intent);
        finish();
    }
}
