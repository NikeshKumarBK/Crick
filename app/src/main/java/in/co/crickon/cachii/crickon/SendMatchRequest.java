package in.co.crickon.cachii.crickon;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SendMatchRequest extends AppCompatActivity implements View.OnClickListener {

    // Button btnAdd,btnGetAll;


    EditText editTextDateBooking, edtvenue, edtovers,editTextTimeBooking;
    private ProgressDialog pDialog;
    private DatePickerDialog NewPurchaseDatePicker;
    private SimpleDateFormat dateFormatter;


    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> teamList;

    //ArrayList<HashMap<String, String>> WarningproductsList;
    // url to get all products list
    private static String url_all_teams = "http://crickon.co.in/php/sendmatchrequest.php";

    // url to get all products list
    private static String url_send_request = "http://crickon.co.in/php/SendRequest.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "error_msg";
    private static final String TAG_TEAM = "TeamId";
    private static final String TAG_OPPTEAMID = "opponent";
    private static final String TAG_TEAMID = "team";
    private static final String TAG_DATE = "date";
    private static final String TAG_TIME = "time";
    private static final String TAG_VENUE = "venue";
    private static final String TAG_OVERS = "overs";
    private static final String TAG_REQUEST = "request";
    private static final String TAG_PINCODE="Pincode";
    // products JSONArray
    JSONArray teams = null;

    String date, time, venue, oppTeamid, teamid;
    int flag = 0;
    Button btnSendReq,btncancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_match_request);

        Intent intent = getIntent();
        oppTeamid = intent.getStringExtra(TAG_TEAM);
        SQLiteHandler repo=new SQLiteHandler(SendMatchRequest.this);
        teamid=repo.getTeamid();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        editTextDateBooking = (EditText) findViewById(R.id.editTextDateBooking);
        edtvenue = (EditText) findViewById(R.id.edtvenue);
        edtovers = (EditText) findViewById(R.id.edtovers);
        btnSendReq = (Button) findViewById(R.id.btnsendrequest);
        editTextTimeBooking=(EditText)findViewById(R.id.editTextTimeBooking);
        long date = System.currentTimeMillis();
        long time=System.currentTimeMillis();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        editTextDateBooking.setText(dateString);
        setDateTimeField();

        btnSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadAllTeams().execute();
                Intent intent =new Intent(SendMatchRequest.this,Login.class);
                startActivity(intent);
                finish();
            }


        });

    }

    @Override
    public void onBackPressed() {
        SQLiteHandler repo=new SQLiteHandler(SendMatchRequest.this);
        String pincode=repo.getPincode();
        Intent intent=new Intent(SendMatchRequest.this,TeamRequest.class);

        intent.putExtra(TAG_PINCODE,pincode);
        startActivity(intent);
        finish();
    }

    private void setDateTimeField() {
        editTextDateBooking.setOnClickListener(this);



        Calendar newCalendar = Calendar.getInstance();
        NewPurchaseDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editTextDateBooking.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }
    @Override
    public void onClick(View view) {

        if (view == editTextDateBooking) {
            NewPurchaseDatePicker.show();
        }

    }



    class LoadAllTeams extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SendMatchRequest.this);
            pDialog.setMessage("Loading teams. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            //pDialog.show();
        }


        protected String doInBackground(String... args) {
            // Building Parameters
            String date, time, venue, overs;
            date = editTextDateBooking.getText().toString();
            time=editTextTimeBooking.getText().toString();
            venue = edtvenue.getText().toString();
            overs = edtovers.getText().toString();
            Log.e("sendmatch",date+" "+time+" "+venue+" "+overs+" "+teamid+" "+oppTeamid);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_TEAMID, teamid));
            params.add(new BasicNameValuePair(TAG_OPPTEAMID, oppTeamid));
            params.add(new BasicNameValuePair(TAG_DATE, date));
            params.add(new BasicNameValuePair(TAG_TIME, time));
            params.add(new BasicNameValuePair(TAG_VENUE, venue));
            params.add(new BasicNameValuePair(TAG_OVERS, overs));
            Log.e("Check",teamid+" "+oppTeamid+" "+date+" "+time+" "+venue+" "+overs);

            //params.add(new BasicNameValuePair(TAG_STATUS, status));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_teams, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found

                } else {
                    // no products found
                    // Launch Add New product Activity
                    flag = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            //pDialog.dismiss();
            if (flag == 1) {
                Toast.makeText(SendMatchRequest.this, "Request not sent!!", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(SendMatchRequest.this,"Request sent",Toast.LENGTH_SHORT).show();
        }
    }
}