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

public class TeamMemberFrag extends SwipeDownToRefresh {

    EditText edtPincode;
    Button btnSendReq;

    // Progress Dialog
    private ProgressDialog pDialog,pDialog1;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> teamList;

    //ArrayList<HashMap<String, String>> WarningproductsList;
    // url to get all products list
    private static String url_team_member = "http://crickon.co.in/php/TeamMember.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "Success";
    private static final String TAG_TEAM = "team";
    private static final String TAG_PLAYERID = "PlayerId";
    private static final String TAG_PLAYERNAME = "Name";
    private static final String TAG_PHNO = "Phno";

    // products JSONArray
    JSONArray teams = null;

    String pincode,teamid,team,teamCaptainId,playerId;
    int flag=0;


    private static final String LOG_TAG = TeamMemberFrag.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQLiteHandler repo=new SQLiteHandler(getContext());
        playerId=repo.getPlayerID();


        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hashmap for ListView
        teamList = new ArrayList<HashMap<String, String>>();
        //WarningproductsList = new ArrayList<HashMap<String, String>>();

        new LoadAllMembers().execute();

        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                initiateRefresh();
            }
        });


    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TextView child = (TextView)v.findViewById(R.id.playerId);
        playerId=child.getText().toString();
        Intent intent=new Intent(getActivity(),MyProfile.class);
        intent.putExtra("PlayerId", playerId);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

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
        new LoadAllMembers().execute();
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllMembers extends AsyncTask<String, String, String> {

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
            params.add(new BasicNameValuePair(TAG_PLAYERID, playerId));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_team_member, "GET", params);

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
                        String playerId = c.getString(TAG_PLAYERID);
                        String playerName = c.getString(TAG_PLAYERNAME);
                        String Phno= c.getString(TAG_PHNO);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_PLAYERID, playerId);
                        map.put(TAG_PLAYERNAME, playerName);
                        map.put(TAG_PHNO, Phno);

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
                Toast.makeText(getActivity(),"No Teams found in this Area!!",Toast.LENGTH_SHORT).show();
            }

            ListAdapter adapter1 = new SimpleAdapter(
                    getActivity(), teamList,
                    R.layout.single_row_team_member, new String[] { TAG_PLAYERID,
                    TAG_PLAYERNAME,TAG_PHNO},
                    new int[] { R.id.playerId, R.id.txtPlayerName, R.id.txtPhno});
            // updating listview
            setListAdapter(adapter1);

            // Stop the refreshing indicator
            setRefreshing(false);
        }
    }

}
