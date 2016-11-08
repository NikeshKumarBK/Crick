package in.co.crickon.cachii.crickon;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;


public class TeamRequest extends ListActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText edtPincode;
    Button btnSearch;

    // Progress Dialog
    private ProgressDialog pDialog,pDialog1;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> teamList;

    //ArrayList<HashMap<String, String>> WarningproductsList;
    // url to get all products list
    private static String url_all_teams = "http://crickon.co.in/php/TeamList.php";

    // url to get all products list
    private static String url_send_request = "http://crickon.co.in/php/SendRequest.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ERROR = "error";
    private static final String TAG_TEAM = "team";
    private static final String TAG_TEAMID = "TeamId";
    private static final String TAG_TEAMID1 = "Teamid";
    private static final String TAG_TEAMNAME = "TeamName";
    private static final String TAG_CAPTAINID = "CaptainId";
    private static final String TAG_CAPTAINNAME = "CaptainName";
    private static final String TAG_PLAYERID = "PlayerId";
    private static final String TAG_PINCODE = "Pincode";
    private static final String TAG_WIN = "Win";
    private static final String TAG_LOSS = "Loss";

    // products JSONArray
    JSONArray teams = null;

    String pincode,teamid,team,teamCaptainId,playerId;
    int flag=0;

    SQLiteHandler repo=new SQLiteHandler(TeamRequest.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_dash);

        Intent intent=getIntent();
        pincode=intent.getStringExtra(TAG_PINCODE);

        playerId=repo.getPlayerID();

        edtPincode=(EditText)findViewById(R.id.edtPincode);
        btnSearch=(Button)findViewById(R.id.btnTeamSearch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


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


        edtPincode.setText(pincode);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TeamRequest.this,TeamRequest.class);
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
                String pid = ((TextView) view.findViewById(R.id.txtteamId)).getText()
                        .toString();
                Intent intent=new Intent(TeamRequest.this,SendMatchRequest.class);
                intent.putExtra(TAG_TEAMID, pid);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(TeamRequest.this,Login.class);
        startActivity(intent);
        finish();
    }

    public void btnSendRequest(View v)
    {

        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        //TextView teamchild = (TextView)vwParentRow.getChildAt(1);
        //TextView captainId = (TextView)vwParentRow.getChildAt(2);
        //Button btnChild = (Button)vwParentRow.getChildAt(3);
        //btnChild.setText(child.getText());

        teamid=child.getText().toString();
        //teamname=teamchild.getText().toString();
        //teamCaptainId=captainId.getText().toString();
        vwParentRow.refreshDrawableState();

        //new SendRequest().execute();
        //btnChild.setText("Team Request sent");
        //btnChild.setEnabled(false);

        Intent intent=new Intent(TeamRequest.this,SendMatchRequest.class);
        intent.putExtra(TAG_TEAMID, teamid);
        Log.e("teamID",teamid);
        startActivity(intent);
        finish();

        //btnChild.setText("Solved");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_player_request) {
            // Handle the camera action
            SQLiteHandler repo=new SQLiteHandler(TeamRequest.this);
            String captainId=repo.getPlayerID();
            Intent intent=new Intent(TeamRequest.this,CheckRequest.class);
            intent.putExtra(TAG_CAPTAINID, captainId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_my_profile) {
            Intent intent=new Intent(TeamRequest.this,Profiles.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_matches) {
            Intent intent=new Intent(TeamRequest.this,Matches.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_sign_out) {
            SessionManager session = new SessionManager(TeamRequest.this);
            session.setLogin(false);
            SQLiteHandler repo=new SQLiteHandler(getApplicationContext());
            repo.deleteUsers();
            Intent intent=new Intent(TeamRequest.this,Login.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_match_request) {
            Intent intent=new Intent(TeamRequest.this,MatchRequests.class);
            startActivity(intent);
            finish();
        }  else if (id == R.id.nav_about_us) {
            Intent intent=new Intent(TeamRequest.this,Aboutuss.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_feedback) {
            Intent intent=new Intent(TeamRequest.this,Feedback.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_leaderboard) {
            Intent intent=new Intent(TeamRequest.this,Leaderboard.class);
            startActivity(intent);
            finish();
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class SendRequest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = new ProgressDialog(TeamRequest.this);
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
                    Toast.makeText(TeamRequest.this,"Request already sent",Toast.LENGTH_SHORT).show();
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
            pDialog = new ProgressDialog(TeamRequest.this);
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
            SQLiteHandler repo=new SQLiteHandler(TeamRequest.this);
            team=repo.getTeamid();
            params.add(new BasicNameValuePair(TAG_PINCODE, pincode));
            params.add(new BasicNameValuePair(TAG_TEAMID1, team));

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
                Toast.makeText(TeamRequest.this,"No Teams found in this Area!!",Toast.LENGTH_SHORT).show();
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
                            TeamRequest.this, teamList,
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
