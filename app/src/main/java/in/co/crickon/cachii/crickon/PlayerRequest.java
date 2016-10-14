package in.co.crickon.cachii.crickon;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

public class PlayerRequest extends ListActivity {

    EditText edtPincode;
    Button btnSearch;

    // Progress Dialog
    private ProgressDialog pDialog,pDialog1;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

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
    private static final String TAG_TEAMNAME = "TeamName";
    private static final String TAG_CAPTAINID = "CaptainId";
    private static final String TAG_CAPTAINNAME = "CaptainName";
    private static final String TAG_PLAYERID = "PlayerId";
    private static final String TAG_PINCODE = "Pincode";
    private static final String TAG_WIN = "Win";
    private static final String TAG_LOSS = "Loss";

    // products JSONArray
    JSONArray teams = null;

    String pincode,teamid,teamname,teamCaptainId,playerId;
    int flag=0;

    SQLiteHandler repo=new SQLiteHandler(PlayerRequest.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_request);

        Intent intent=getIntent();
        pincode=intent.getStringExtra(TAG_PINCODE);

        playerId=repo.getPlayerID();

        edtPincode=(EditText)findViewById(R.id.edtPincode);
        btnSearch=(Button)findViewById(R.id.btnSearch);


        edtPincode.setText(pincode);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PlayerRequest.this,PlayerRequest.class);
                pincode=edtPincode.getText().toString().trim();
                intent.putExtra(TAG_PINCODE,pincode);
                startActivity(intent);
                finish();
            }
        });

        // Hashmap for ListView
        teamList = new ArrayList<HashMap<String, String>>();
        //WarningproductsList = new ArrayList<HashMap<String, String>>();

        new LoadAllTeams().execute();


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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(PlayerRequest.this,PlayerDash.class);
        startActivity(intent);
        finish();
    }

    public void btnSendRequest(View v)
    {

        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        //TextView teamchild = (TextView)vwParentRow.getChildAt(1);
        TextView captainId = (TextView)vwParentRow.getChildAt(1);
        //Button btnChild = (Button)vwParentRow.getChildAt(3);
        //btnChild.setText(child.getText());

        teamid=child.getText().toString();
        //teamname=teamchild.getText().toString();
        teamCaptainId=captainId.getText().toString();
        vwParentRow.refreshDrawableState();

        new SendRequest().execute();
        //btnChild.setText("Team Request sent");
        //btnChild.setEnabled(false);

        Intent intent=new Intent(PlayerRequest.this,PlayerRequest.class);
        intent.putExtra(TAG_PINCODE, pincode);

        startActivity(intent);
        finish();

        //btnChild.setText("Solved");
    }

    class SendRequest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = new ProgressDialog(PlayerRequest.this);
            pDialog1.setMessage("Sending Request...");
            pDialog1.setIndeterminate(false);
            //pDialog.setCancelable(true);
            //pDialog1.show();
        }

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {



            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_TEAMID, teamid));
            params.add(new BasicNameValuePair(TAG_PLAYERID, playerId));
            params.add(new BasicNameValuePair(TAG_CAPTAINID, teamCaptainId));


            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_send_request,
                    "POST", params);

            // check json success tag
            try {
                boolean success = json.getBoolean(TAG_ERROR);
                Log.e("Check",""+success);

                if (!success) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update product
                    Toast.makeText(PlayerRequest.this,"Request already sent",Toast.LENGTH_SHORT).show();
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
            //pDialog1.dismiss();

        }
    }


    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllTeams extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PlayerRequest.this);
            pDialog.setMessage("Loading teams. Please wait...");
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
            params.add(new BasicNameValuePair(TAG_PINCODE, pincode));
            //params.add(new BasicNameValuePair(TAG_STATUS, status));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_teams, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    teams = json.getJSONArray(TAG_TEAM);

                    // looping through All Products
                    for (int i = 0; i < teams.length(); i++) {
                        JSONObject c = teams.getJSONObject(i);

                        // Storing each json item in variable
                        String teamid = c.getString(TAG_TEAMID);
                        String teamname = c.getString(TAG_TEAMNAME);
                        String captainid= c.getString(TAG_CAPTAINID);
                        String captainname= c.getString(TAG_CAPTAINNAME);
                        String win = c.getString(TAG_WIN);
                        String loss= c.getString(TAG_LOSS);



                        HashMap<String, String> map = new HashMap<String, String>();


                        map.put(TAG_TEAMID, teamid);
                        map.put(TAG_TEAMNAME, teamname);
                        map.put(TAG_CAPTAINID, captainid);
                        map.put(TAG_CAPTAINNAME, captainname);
                        map.put(TAG_WIN,win);
                        map.put(TAG_LOSS,loss);

                        // adding HashList to ArrayList
                        teamList.add(map);
                        Log.e("Check", "Added to teamlist");

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
            pDialog.dismiss();
            if(flag==1)
            {
                Toast.makeText(PlayerRequest.this,"No Teams found in this Area!!",Toast.LENGTH_SHORT).show();
            }



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
                                PlayerRequest.this, teamList,
                                R.layout.single_team_list, new String[] { TAG_TEAMID,
                                TAG_TEAMNAME,TAG_CAPTAINID, TAG_CAPTAINNAME,TAG_WIN,TAG_LOSS},
                                new int[] { R.id.txtteamId, R.id.txtteamName, R.id.txtCaptainId, R.id.txtCaptainName, R.id.txtWin,R.id.txtLoss});
                        // updating listview
                        setListAdapter(adapter1);
                        //setListAdapter(adapter);
                    }
                });


        }
    }
}
