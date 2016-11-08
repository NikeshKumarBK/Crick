package in.co.crickon.cachii.crickon;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpcomingMatchesDel extends ListActivity {
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> playerRequest;

    //ArrayList<HashMap<String, String>> WarningproductsList;
    // url to get all products list
    private static String url_all_player_request = "http://crickon.esy.es/php_files/CheckRequest.php";

    // url to create new product
    private static String url_update_player_request = "http://crickon.esy.es/php_files/UpdateRequest.php";

    // JSON Node names
    private static final String TAG_OPPONENTID = "oppid";
    private static final String TAG_TEAMID = "teamId";

    private static final String TAG_OPPONENTNAME= "oppname";
    private static final String TAG_SUCCESS= "success";
    private static final String TAG_REQUEST= "request";
    private static final String TAG_DATE= "date";
    private static final String TAG_TIME= "time";


    // products JSONArray
    JSONArray players = null;

    SQLiteHandler repo=new SQLiteHandler(UpcomingMatchesDel.this);
    String teamid,date;



    int flag=0;

    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_matches_del);




        // Hashmap for ListView
        playerRequest = new ArrayList<HashMap<String, String>>();


        //WarningproductsList = new ArrayList<HashMap<String, String>>();

        date=repo.getTeamid();

        long date = System.currentTimeMillis();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        String dateString = sdf.format(date);

        Log.e("Check",dateString);


        // Get listview
        ListView lv = getListView();

        // launching Edit Product Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.teamid)).getText()
                        .toString();
            }
        });
    }


        /*public void onDateSet (DatePicker view,int year, int monthOfYear, int dayOfMonth){
            Calendar newDate = Calendar.getInstance();
            newDate.set(dayOfMonth, monthOfYear, year);
            Log.e("check", newDate);

    }
*/




    class RequestResponse extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpcomingMatchesDel.this);
            pDialog.setMessage("Request response...");
            pDialog.setIndeterminate(false);
            //pDialog.setCancelable(true);
            //pDialog.show();
        }

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_DATE,date));
            params.add(new BasicNameValuePair(TAG_TEAMID,teamid));
            ;




            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_update_player_request,
                    "GET", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                } else {
                    // failed to update product
                    //Toast.makeText(CheckRequest.this,"Response already sent",Toast.LENGTH_SHORT).show();
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
            // dismiss the dialog once product uupdated
            pDialog.dismiss();

        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllPlayerRequest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpcomingMatchesDel.this);
            pDialog.setMessage("Loading request. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_TEAMID, teamid));
            params.add(new BasicNameValuePair(TAG_DATE, date));
            //params.add(new BasicNameValuePair(TAG_STATUS, status));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_player_request, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    players = json.getJSONArray(TAG_REQUEST);

                    // looping through All Products
                    for (int i = 0; i < players.length(); i++) {
                        JSONObject c = players.getJSONObject(i);

                        // Storing each json item in variable
                        String teamid = c.getString(TAG_TEAMID);
                        String date = c.getString(TAG_DATE);


                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_TEAMID, teamid);
                        map.put(TAG_DATE,date);
                        //map.put(TAG_CAPTAINID, captainid);

                        // adding HashList to ArrayList
                        playerRequest.add(map);
                        Log.e("Check", "Added to requestlist");
                    }
                } else {
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
            if(flag==1)
            {
                Toast.makeText(UpcomingMatchesDel.this,"No Player request!!",Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();


            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter1 = new SimpleAdapter(
                            UpcomingMatchesDel.this, playerRequest,
                            R.layout.single_row_upcoming_match, new String[] { TAG_TEAMID,TAG_OPPONENTID,TAG_OPPONENTNAME,TAG_DATE,TAG_TIME
                    },
                            new int[]{R.id.teamid,R.id.oppid,R.id.oppname,R.id.txtMonth,R.id.time});
                    // updating listview
                    //setListAdapter(adapter1)

                    //setListAdapter(adapter);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(UpcomingMatchesDel.this,Login.class);
        startActivity(intent);
        finish();
    }


}