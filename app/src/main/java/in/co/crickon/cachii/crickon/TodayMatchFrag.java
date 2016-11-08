package in.co.crickon.cachii.crickon;

/**
 * Created by nikeshkumarbk on 06/11/16.
 */

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

public class TodayMatchFrag extends SwipeDownToRefresh{

    // Progress Dialog
    private ProgressDialog pDialog;

    String teamid,dateString;
    String matchid,teamname,opponent;
    int flag=0;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> upcomingMatch;

    // url to get upcoming matches
    private static String url_today_match = "http://crickon.co.in/php/TodayMatch.php";

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

    private static final String LOG_TAG = TodayMatchFrag.class.getSimpleName();

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
        SQLiteHandler repo = new SQLiteHandler(getActivity());


        teamid = repo.getTeamid();
        upcomingMatch = new ArrayList<HashMap<String, String>>();

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateString = sdf.format(date);


        new TodayMatch().execute();

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
                initiateRefresh();
            }
        });


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TextView child = (TextView)v.findViewById(R.id.txtMatchId);
        matchid=child.getText().toString();

        Intent intent=new Intent(getActivity(),ScoreCard.class);
        intent.putExtra(TAG_MATCHID,matchid);
        intent.putExtra(TAG_TEAMNAME,teamname);
        intent.putExtra(TAG_OPPONENTNAME,opponent);
        startActivity(intent);

        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
        upcomingMatch = new ArrayList<HashMap<String, String>>();
        new TodayMatch().execute();
    }

    /**
     * When the AsyncTask finishes, it calls onRefreshComplete(), which updates the data in the
     * ListAdapter and turns off the progress bar.
     */


    /**
     * Dummy {@link AsyncTask} which simulates a long running task to fetch new cheeses.
     */
    class TodayMatch extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Opponent", teamid));
            params.add(new BasicNameValuePair("Date", dateString));
            JSONObject json = jParser.makeHttpRequest(url_today_match, "GET", params);

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
                        teamname = c.getString(TAG_TEAMNAME);
                        opponent = c.getString(TAG_OPPONENTNAME);
                        //String date= c.getString(TAG_DATE);
                        //String months= c.getString(TAG_DATE);
                        String time= c.getString(TAG_TIME);
                        String venue= c.getString(TAG_VENUE);
                        String overs= c.getString(TAG_OVERS);
                        matchid= c.getString(TAG_MATCHID);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_TEAMNAME, teamname);
                        map.put(TAG_OPPONENTNAME, opponent);
                        //map.put(TAG_DATE, date);
                        //map.put(TAG_MONTH, months);
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
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            if(flag==1)
            {
                Toast.makeText(getActivity(),"No matches today!!",Toast.LENGTH_SHORT).show();
            }

            ListAdapter adapter1 = new SimpleAdapter(
                    getActivity(), upcomingMatch,
                    R.layout.single_row_today_match, new String[] { TAG_TEAMNAME, TAG_OPPONENTNAME,TAG_TIME, TAG_VENUE,
                    TAG_MATCHID},
                    new int[] { R.id.teamName, R.id.OppTeamName, R.id.txtTime, R.id.txtLocation, R.id.txtMatchId});
            // updating listview
            setListAdapter(adapter1);

            // Stop the refreshing indicator
            setRefreshing(false);

        }
    }

}
