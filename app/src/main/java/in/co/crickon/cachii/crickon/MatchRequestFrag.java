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

public class MatchRequestFrag extends SwipeDownToRefresh {

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

    private static final String LOG_TAG = MatchRequestFrag.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inflate the layout for this fragment

        SQLiteHandler repo=new SQLiteHandler(getContext());
        teamid = repo.getTeamid();
        teamList = new ArrayList<HashMap<String, String>>();

        colname="Opponent";
        status="1";

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
        TextView child = (TextView)v.findViewById(R.id.matchId);

        String pid = child.getText().toString();
        Intent intent=new Intent(getActivity(),TeamMatchRequest.class);
        intent.putExtra("MatchId",pid);
        startActivity(intent);
        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.main_menu, menu);
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
    /*@Override
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
                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!isRefreshing()) {
                    setRefreshing(true);
                }

                // Start our refresh background task
                initiateRefresh();
                return true;



        }

        return super.onOptionsItemSelected(item);
    }*/

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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

            if(flag==1)
            {
                Toast.makeText(getActivity(),"No match request!!",Toast.LENGTH_SHORT).show();
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

}
