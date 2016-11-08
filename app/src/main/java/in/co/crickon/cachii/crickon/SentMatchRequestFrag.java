package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SentMatchRequestFrag extends SwipeDownToRefresh {

    EditText edtPincode;
    Button btnSendReq;

    // Progress Dialog
    private ProgressDialog pDialog,pDialog1;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> teamList;

    //ArrayList<HashMap<String, String>> WarningproductsList;
    // url to get all products list
    private static String url_all_teams = "http://crickon.co.in/php/sentrequest.php";

    private static String url_delete_req = "http://crickon.co.in/php/DeleteMatchRequest.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_REQ = "request";
    private static final String TAG_TEAMID = "TeamId";
    private static final String TAG_TEAMNAME = "TeamName";
    private static final String TAG_CAPTAINID = "CaptainId";
    private static final String TAG_MATCHID = "MatchId";
    // products JSONArray
    JSONArray teams = null;

    String pincode,teamid,team,captainId,playerId,matchid;
    int flag=0;


    private static final String LOG_TAG = SentMatchRequestFrag.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQLiteHandler repo=new SQLiteHandler(getContext());
        pincode=repo.getPincode();

        playerId=repo.getPlayerID();


        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inflate the layout for this fragment

        // Hashmap for ListView
        teamList = new ArrayList<HashMap<String, String>>();

        new LoadAllTeams().execute();

        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                initiateRefresh();
            }
        });


    }



    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        TextView child = (TextView)v.findViewById(R.id.matchId);
        matchid=child.getText().toString();

        new AlertDialog.Builder(getActivity())
                .setTitle("Delete request")
                .setMessage("Do you want to delete the request?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        new DeleteRequest().execute();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.main_menu, menu);
    }


    /**
     * By abstracting the refresh process to a single method, the app allows both the
     * SwipeGestureLayout onRefresh() method and the Refresh action item to refresh the content.
     */
    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");

        /**
         * Execute the background task, which uses {@link AsyncTask} to load the data.
         */
        teamList = new ArrayList<HashMap<String, String>>();
        new LoadAllTeams().execute();
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
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            SQLiteHandler repo=new SQLiteHandler(getActivity());
            captainId=repo.getPlayerID();
            params.add(new BasicNameValuePair(TAG_CAPTAINID, captainId));

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
                    teams = json.getJSONArray(TAG_REQ);

                    // looping through All Products
                    for (int i = 0; i < teams.length(); i++) {
                        JSONObject c = teams.getJSONObject(i);

                        // Storing each json item in variable
                        String macthid = c.getString(TAG_MATCHID);
                        String teamname = c.getString(TAG_TEAMNAME);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_MATCHID, macthid);
                        map.put(TAG_TEAMNAME, teamname);

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
            if(flag==1)
            {
                Toast.makeText(getActivity(),"No request sent!!",Toast.LENGTH_SHORT).show();
            }

            ListAdapter adapter1 = new SimpleAdapter(
                    getActivity(), teamList,
                    R.layout.single_row_match_request, new String[] { TAG_TEAMNAME,
                    TAG_MATCHID},
                    new int[] { R.id.oppName, R.id.matchId});
            // updating listview
            setListAdapter(adapter1);

            // Stop the refreshing indicator
            setRefreshing(false);
        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class DeleteRequest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            SQLiteHandler repo=new SQLiteHandler(getActivity());
            params.add(new BasicNameValuePair(TAG_MATCHID, matchid));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_delete_req, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    initiateRefresh();
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
                Toast.makeText(getActivity(),"Request cancelled!!",Toast.LENGTH_SHORT).show();
            }

            // Stop the refreshing indicator
            setRefreshing(false);
        }
    }

}
