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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class UpcomingMatch extends ListActivity {

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

    SQLiteHandler repo = new SQLiteHandler(UpcomingMatch.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_match);

        teamid = repo.getTeamid();
        upcomingMatch = new ArrayList<HashMap<String, String>>();
        new UpcomingMatches().execute();
        ListView lv = getListView();

        // launching Edit Product Screen
        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.matchId)).getText()
                        .toString();

            }
        });*/
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UpcomingMatch.this, Login.class);
        startActivity(intent);
        finish();
    }

    class UpcomingMatches extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpcomingMatch.this);
            pDialog.setMessage("Loading teams. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
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
            pDialog.dismiss();
            if(flag==1)
            {
                Toast.makeText(UpcomingMatch.this,"No upcoming matches!!",Toast.LENGTH_SHORT).show();
            }

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter1 = new SimpleAdapter(
                            UpcomingMatch.this, upcomingMatch,
                            R.layout.single_row_upcoming_match, new String[] { TAG_TEAMNAME, TAG_OPPONENTNAME, TAG_DATE, TAG_MONTH,TAG_TIME, TAG_VENUE,
                            TAG_MATCHID},
                            new int[] { R.id.teamName, R.id.OppTeamName,R.id.txtDate,R.id.txtMonth, R.id.txtTime, R.id.txtLocation, R.id.matchId});
                    // updating listview
                    setListAdapter(adapter1);
                    //setListAdapter(adapter);
                }
            });


        }
    }

}
