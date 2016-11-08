package in.co.crickon.cachii.crickon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class SendMatchRequestFrag extends SwipeDownToRefresh {

    EditText edtPincode;
    Button btnSendReq;

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


    private static final String LOG_TAG = SendMatchRequestFrag.class.getSimpleName();

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



        /*edtPincode=(EditText)view.findViewById(R.id.edtPincode);
        btnSearch=(Button)view.findViewById(R.id.btnTeamSearch);


        edtPincode.setText(pincode);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincode=edtPincode.getText().toString().trim();
                initiateRefresh();
            }
        });*/

        // Hashmap for ListView
        teamList = new ArrayList<HashMap<String, String>>();
        //WarningproductsList = new ArrayList<HashMap<String, String>>();

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
    public void onListItemClick(ListView l, View v, int position, long id) {
        TextView child = (TextView)v.findViewById(R.id.txtteamId);
        teamid=child.getText().toString();

        Intent intent=new Intent(getActivity(),SendMatchRequest.class);
        intent.putExtra(TAG_TEAMID, teamid);
        Log.e("teamID",teamid);
        startActivity(intent);
        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    /**
     * Respond to the user's selection of the Refresh action item. Start the SwipeRefreshLayout
     * progress bar, then initiate the background task that refreshes the content.
     *
     * <p>A color scheme menu item used for demonstrating the use of SwipeRefreshLayout's color
     * scheme functionality. This kind of menu item should not be incorporated into your app,
     * it just to demonstrate the use of color. Instead you should choose a color scheme based
     * off of your application's branding.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:

                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.single_get_pincode, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText edtPincode = (EditText) promptsView.findViewById(R.id.edtPincode);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        pincode=edtPincode.getText().toString();
                                        // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                                        if (!isRefreshing()) {
                                            setRefreshing(true);
                                        }

                                        // Start our refresh background task
                                        initiateRefresh();

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

                return true;




        }

        return super.onOptionsItemSelected(item);
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
            if(flag==1)
            {
                Toast.makeText(getActivity(),"No Teams found in this Area!!",Toast.LENGTH_SHORT).show();
            }

            ListAdapter adapter1 = new SimpleAdapter(
                    getActivity(), teamList,
                    R.layout.single_team_list, new String[] { TAG_TEAMID,
                    TAG_TEAMNAME,TAG_CAPTAINID, TAG_CAPTAINNAME,TAG_WIN,TAG_LOSS},
                    new int[] { R.id.txtteamId, R.id.txtteamName, R.id.txtCaptainId, R.id.txtCaptainName, R.id.txtWin,R.id.txtLoss});
            // updating listview
            setListAdapter(adapter1);

            // Stop the refreshing indicator
            setRefreshing(false);
        }
    }

}
