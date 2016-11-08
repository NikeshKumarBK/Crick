package in.co.crickon.cachii.crickon;

/**
 * Created by nikeshkumarbk on 06/11/16.
 */

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UpcomingMatchFrag extends SwipeDownToRefresh{


    // Progress Dialog
    private ProgressDialog pDialog;

    String teamid;
    int flag=0;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> upcomingMatch;

    String[] month={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    // url to get upcoming matches
    private static String url_upcoming_match = "http://crickon.co.in/php/UpcomingMatch.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "Success";
    private static final String TAG_ERROR = "error";
    private static final String TAG_TEAM = "team";
    private static final String TAG_TEAMNAME = "Teamname";
    private static final String TAG_OPPONENT = "OppTeamname";
    private static final String TAG_DATE = "Date";
    private static final String TAG_TIME = "Time";
    private static final String TAG_VENUE = "Venue";
    private static final String TAG_OVERS = "Overs";
    private static final String TAG_OPPONENTNAME = "OppTeamname";
    private static final String TAG_MONTH = "month";
    private static final String TAG_MATCHID = "Matchid";


    // products JSONArray
    JSONArray teams = null;

    private static final String LOG_TAG = UpcomingMatchFrag.class.getSimpleName();

    private static final int LIST_ITEM_COUNT = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Create an ArrayAdapter to contain the data for the ListView. Each item in the ListView
         * uses the system-defined simple_list_item_1 layout that contains one TextView.
         */

        teamid = 1+"";
        upcomingMatch = new ArrayList<HashMap<String, String>>();
        new UpcomingMatches().execute();


        /**
         * Implement {@link SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
         * refresh" gesture, SwipeRefreshLayout invokes
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
         * refreshes the content. Call the same method in response to the Refresh action from the
         * action bar.
         */
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                upcomingMatch = new ArrayList<HashMap<String, String>>();
                initiateRefresh();
            }
        });


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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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
         * Execute the background task, which uses {@link android.os.AsyncTask} to load the data.
         */
        new UpcomingMatches().execute();
    }

    /**
     * When the AsyncTask finishes, it calls onRefreshComplete(), which updates the data in the
     * ListAdapter and turns off the progress bar.
     */
    private void onRefreshComplete(List<String> result) {
        Log.i(LOG_TAG, "onRefreshComplete");

        // Remove all items from the ListAdapter, and then replace them with the new items
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
        adapter.clear();
        for (String cheese : result) {
            adapter.add(cheese);
        }

        // Stop the refreshing indicator
        setRefreshing(false);
    }

    /**
     * Dummy {@link AsyncTask} which simulates a long running task to fetch new cheeses.
     */
    class UpcomingMatches extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading teams. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
//            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Opponent", teamid));

            JSONObject json = jParser.makeHttpRequest(url_upcoming_match, "GET", params);

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
                        String teamname = c.getString(TAG_TEAMNAME);
                        String opponent = c.getString(TAG_OPPONENTNAME);
                        String date= c.getString(TAG_DATE);
                        String months= c.getString(TAG_DATE);
                        String time= c.getString(TAG_TIME);
                        String venue= c.getString(TAG_VENUE);
                        String overs= c.getString(TAG_OVERS);
                        String matchid= c.getString(TAG_MATCHID);

                        DateFormat srcDf = new SimpleDateFormat("yyyy-mm-dd");
                        // parse the date string into Date object
                        Date parMonth = srcDf.parse(months);
                        Date parDate = srcDf.parse(date);
                        DateFormat destDf = new SimpleDateFormat("mm");
                        // format the date into another format
                        int mon = Integer.parseInt(destDf.format(parMonth));
                        System.out.println("Converted date is : " + date);
                        months=month[mon-1];

                        DateFormat destDay = new SimpleDateFormat("dd");
                        // format the date into another format
                        date=destDay.format(parDate);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_TEAMNAME, teamname);
                        map.put(TAG_OPPONENTNAME, opponent);
                        map.put(TAG_DATE, date);
                        map.put(TAG_MONTH, months);
                        map.put(TAG_TIME, time);
                        map.put(TAG_VENUE,venue);
                        map.put(TAG_OVERS,overs);
                        map.put(TAG_MATCHID,matchid);

                        // adding HashList to ArrayList
                        upcomingMatch.add(map);
                        Log.e("Check", "Added to teamlist");

                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    flag=1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            // pDialog.dismiss();
            if(flag==1)
            {
                Toast.makeText(getActivity(),"No upcoming matches!!",Toast.LENGTH_SHORT).show();
            }
            ListAdapter adapter1;

            adapter1 = new SimpleAdapter(
                    getActivity(), upcomingMatch,
                    R.layout.single_row_upcoming_match, new String[] { TAG_TEAMNAME, TAG_OPPONENTNAME, TAG_DATE, TAG_MONTH,TAG_TIME, TAG_VENUE},
                    new int[] { R.id.teamName, R.id.OppTeamName,R.id.txtDate,R.id.txtMonth, R.id.txtTime, R.id.txtLocation});
            // updating listview
            setListAdapter(adapter1);

            // Stop the refreshing indicator
            setRefreshing(false);
        }

    }

}
