package in.co.crickon.cachii.crickon;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchRequest extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog, pDialog1;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> teamList;

    //ArrayList<HashMap<String, String>> WarningproductsList;
    // url to get all products list
    private static String url_match_request = "http://crickon.co.in/php/loadmatchrequest.php";


    // JSON Node names
    private static final String TAG_SUCCESS = "Success";
    private static final String TAG_ERROR = "error";
    private static final String TAG_TEAM = "team";
    private static final String TAG_TEAMNAME = "Teamname";
    private static final String TAG_COLNAME = "Colname";
    private static final String TAG_DATE = "Date";
    private static final String TAG_TIME = "Time";
    private static final String TAG_VENUE = "Venue";
    private static final String TAG_OVERS = "Overs";
    private static final String TAG_OPPONENTNAME = "Opponent";
    private static final String TAG_STATUS = "Status";
    private static final String TAG_MATCHID = "Matchid";

    // products JSONArray
    JSONArray teams = null;

    String teamid, colname, status, time, venue, overs, matchId;
    int flag = 0;

    SQLiteHandler repo = new SQLiteHandler(MatchRequest.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_request);
        teamid = repo.getTeamid();
        teamList = new ArrayList<HashMap<String, String>>();

        Bundle bundle=getIntent().getExtras();
        colname=bundle.getString(TAG_COLNAME);
        status=bundle.getString(TAG_STATUS);

        new LoadAllTeams().execute();
        ListView lv = getListView();

        // launching Edit Product Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.matchId)).getText()
                        .toString();
                Intent intent=new Intent(MatchRequest.this,TeamMatchRequest.class);
                intent.putExtra("MatchId",pid);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MatchRequest.this, Login.class);
        startActivity(intent);
        finish();
    }

    class LoadAllTeams extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MatchRequest.this);
            pDialog.setMessage("Loading teams. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_OPPONENTNAME, teamid));
            //params.add(new BasicNameValuePair(TAG_COLNAME, colname));
            params.add(new BasicNameValuePair(TAG_STATUS, status));

            JSONObject json = jParser.makeHttpRequest(url_match_request, "GET", params);

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
                        String opponent = c.getString(TAG_TEAMNAME);
                        String date= c.getString(TAG_DATE);
                        String time= c.getString(TAG_TIME);
                        String venue= c.getString(TAG_VENUE);
                        String overs= c.getString(TAG_OVERS);
                        String matchid= c.getString(TAG_MATCHID);
                        //String teamid= c.getString(TAG_TEAMID);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_TEAMNAME, opponent);
                        map.put(TAG_DATE, date);
                        map.put(TAG_TIME, time);
                        map.put(TAG_VENUE,venue);
                        map.put(TAG_OVERS,overs);
                        map.put(TAG_MATCHID,matchid);
                        //map.put(TAG_TEAMID,teamid);


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

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(flag==1)
            {
                Toast.makeText(MatchRequest.this,"No match request!!",Toast.LENGTH_SHORT).show();
            }

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter1 = new SimpleAdapter(
                            MatchRequest.this, teamList,
                            R.layout.single_row_match_request, new String[] { TAG_TEAMNAME,
                            TAG_MATCHID},
                            new int[] { R.id.oppName, R.id.matchId});
                    // updating listview
                    setListAdapter(adapter1);
                    //setListAdapter(adapter);
                }
            });
        }
    }
}
