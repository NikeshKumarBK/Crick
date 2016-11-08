package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ReconsiderMatchRequest extends AppCompatActivity {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    EditText edttimepicker,edtovers,edtvenue,editTextDateBooking;
    Button btnsendrequest;

    ArrayList<HashMap<String, String>> teamList;

    //ArrayList<HashMap<String, String>> WarningproductsList;
    // url to get all products list
    private static String url_all_teams = "http://crickon.esy.es/php_files/PlayerRequest.php";

    // url to get all products list
    private static String url_send_request = "http://crickon.esy.es/php_files/SendRequest.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ERROR = "error";
    private static final String TAG_TEAM = "team";
    private static final String TAG_TEAMID = "TeamId";
    private static final String TAG_DATE = "date";
    private static final String TAG_TIME = "time";
    private static final String TAG_VENUE = "venue";
    private static final String TAG_OVERS = "overs";
    private static final String TAG_REQUEST = "request";

    // products JSONArray
    JSONArray teams = null;

    String date, time, venue, overs;
    int flag = 0;




    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconsider_match_request);
        editTextDateBooking=(EditText)findViewById(R.id.editTextDateBooking);
        edttimepicker=(EditText)findViewById(R.id.editTextTimeBooking);
        edtovers=(EditText)findViewById(R.id.edtovers);
        edtvenue=(EditText)findViewById(R.id.edtvenue);
        btnsendrequest=(Button)findViewById(R.id.btnsendrequest);



        Intent intent=getIntent();
        date=intent.getStringExtra(TAG_DATE);
        time=intent.getStringExtra(TAG_TIME);
        venue=intent.getStringExtra(TAG_VENUE);
        overs=intent.getStringExtra(TAG_OVERS);


        edtovers.setText(overs);
        edtvenue.setText(venue);
        edttimepicker.setText(time);
        editTextDateBooking.setText(date);



        btnsendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadAllTeams().execute();
            }

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
        });}




    class LoadAllTeams extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReconsiderMatchRequest.this);
            pDialog.setMessage("Loading details. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters

            date=editTextDateBooking.getText().toString();
            time=edttimepicker.getText().toString();
            venue=edtvenue.getText().toString();
            overs=edtovers.getText().toString();


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_DATE, date));
            params.add(new BasicNameValuePair(TAG_TIME, time));
            params.add(new BasicNameValuePair(TAG_VENUE, venue));
            params.add(new BasicNameValuePair(TAG_OVERS, overs));

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

                    Log.e("Check", "Added to teamlist");

                }
                else {
                    // no products found
                    // Launch Add New product Activity
                    flag=1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(flag==1)
            {
                Toast.makeText(ReconsiderMatchRequest.this,"No Teams found in this Area!!",Toast.LENGTH_SHORT).show();
            }



            // updating UI from Background Thread



        }
    }
}