package in.co.crickon.cachii.crickon;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.co.crickon.cachii.crickon.R.id.captainId;

public class TeamRequest extends ListActivity {

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
    private static final String TAG_APP= "success";
    private static final String TAG_VISIBLE= "Phno";
    private static final String  TAG_CSK= "PlayerId";
    private static final String TAG_PINCODE = "request";
    private static final String TAG_WN= "CaptainId";
    private static final String TAG_WIN= "TeamId";


    // products JSONArray
    JSONArray players = null;

    SQLiteHandler repo=new SQLiteHandler(TeamRequest.this);
    String captainId,status,playerId,teamId;

    int flag=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_request);

        // Hashmap for ListView
        playerRequest = new ArrayList<HashMap<String, String>>();

        Intent intent=getIntent();
        captainId=intent.getStringExtra(TAG_WN);
        //WarningproductsList = new ArrayList<HashMap<String, String>>();

        teamId=repo.getTeamid();

        new TeamRequest.LoadAllPlayerRequest().execute();
        // Get listview
        ListView lv = getListView();

        // launching Edit Product Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.playerId)).getText()
                        .toString();
            }
        });
    }


    public void btnConfirm(View v)
    {
        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        TextView childplayerid = (TextView)vwParentRow.getChildAt(0);
        //TextView childplayername= (TextView)vwParentRow.getChildAt(1);
        //TextView captainId = (TextView)vwParentRow.getChildAt(2);
//        Button btnChild = (Button)vwParentRow.getChildAt(2);
        //btnChild.setText(child.getText());

        playerId=childplayerid.getText().toString();
        status=""+1;

        vwParentRow.refreshDrawableState();

        new TeamRequest.RequestResponse().execute();

        Toast.makeText(TeamRequest.this,"Request updated",Toast.LENGTH_SHORT).show();
    }




    class RequestResponse extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TeamRequest.this);
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
            params.add(new BasicNameValuePair(TAG_CSK, playerId));
            params.add(new BasicNameValuePair(TAG_WIN, teamId));

            Log.e("Check",playerId+" "+teamId);

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_update_player_request,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt( TAG_APP);

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
            pDialog = new ProgressDialog(TeamRequest.this);
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
            params.add(new BasicNameValuePair(TAG_WN, captainId));
            //params.add(new BasicNameValuePair(TAG_STATUS, status));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_player_request, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS
                int success = json.getInt( TAG_APP);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    players = json.getJSONArray(TAG_PINCODE);

                    // looping through All Products
                    for (int i = 0; i < players.length(); i++) {
                        JSONObject c = players.getJSONObject(i);

                        // Storing each json item in variable
                        String csk = c.getString(TAG_CSK);

                        String pin = c.getString(TAG_PINCODE);
                        String wn= c.getString(TAG_WN);
                        String WN = c.getString(TAG_WIN);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_CSK,csk);
                        map.put(TAG_PINCODE,pin);
                        map.put(TAG_WN,wn);
                        map.put(TAG_WIN,WN);

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
                Toast.makeText(TeamRequest.this,"No Player request!!",Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();


            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                        /*SpecialAdapter adapter = new SpecialAdapter(AreaTstHome.this,WarningproductsList,R.layout.single_row_issue_solved,new String[] { TAG_TICKET_NO,
                                TAG_SITE_NAME,TAG_ISSUE,TAG_DATE,TAG_FIELD,TAG_STATUS},
                                new int[] { R.id.pid, R.id.allPlaceName, R.id.allLandMark,R.id.allProb,R.id.allAltRoute ,R.id.allSolved});*/

                    ListAdapter adapter1 = new SimpleAdapter(
                            TeamRequest.this, playerRequest,
                            R.layout.single_team_list, new String[] { TAG_APP,
                             TAG_VISIBLE, TAG_CSK, TAG_PINCODE,TAG_WN,TAG_WIN},
                            new int[] { R.id.app, R.id.visible, R.id.csk, R.id.pin, R.id.wn,R.id.WN});
                    // updating listview
                    setListAdapter(adapter1);
                    //setListAdapter(adapter);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(TeamRequest.this,CaptainDash.class);
        startActivity(intent);
        finish();
    }
}
