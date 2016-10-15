package in.co.crickon.cachii.crickon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class CaptainDash extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG_CAPTAINID = "CaptainId";
    private static final String TAG_PINCODE = "Pincode";
    Button btnCheckReq,btnSendMatchReq,btnCheckMatchReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_dash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnCheckReq=(Button)findViewById(R.id.btnCheckRequest);
        btnSendMatchReq=(Button)findViewById(R.id.btnSendMatchRequest);
        btnCheckMatchReq=(Button)findViewById(R.id.btnCheckMatchRequest);

        btnCheckReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteHandler repo=new SQLiteHandler(CaptainDash.this);
                String captainId=repo.getPlayerID();
                Intent intent=new Intent(CaptainDash.this,CheckRequest.class);
                intent.putExtra(TAG_CAPTAINID, captainId);
                startActivity(intent);
                finish();
            }
        });

        btnSendMatchReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteHandler repo=new SQLiteHandler(CaptainDash.this);
                String pincode=repo.getPincode();
                Intent intent=new Intent(CaptainDash.this,TeamRequest.class);
                intent.putExtra(TAG_PINCODE, pincode);
                startActivity(intent);
                finish();
            }
        });

        btnCheckMatchReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteHandler repo=new SQLiteHandler(CaptainDash.this);
                String captainId=repo.getPlayerID();
                Intent intent=new Intent(CaptainDash.this,CheckRequest.class);
                intent.putExtra(TAG_CAPTAINID, captainId);
                startActivity(intent);
                finish();
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.captain_dash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_player_request) {
            // Handle the camera action
            SQLiteHandler repo=new SQLiteHandler(CaptainDash.this);
            String captainId=repo.getPlayerID();
            Intent intent=new Intent(CaptainDash.this,CheckRequest.class);
            intent.putExtra(TAG_CAPTAINID, captainId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_match_request) {

        } else if (id == R.id.nav_send_match_request) {
            SQLiteHandler repo=new SQLiteHandler(CaptainDash.this);
            String pincode=repo.getPincode();
            Intent intent=new Intent(CaptainDash.this,TeamRequest.class);
            intent.putExtra(TAG_PINCODE, pincode);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_sign_out) {

            SessionManager session = new SessionManager(CaptainDash.this);
            session.setLogin(false);
            SQLiteHandler repo=new SQLiteHandler(getApplicationContext());
            repo.deleteUsers();
            Intent intent=new Intent(CaptainDash.this,Login.class);
            startActivity(intent);

        } else if (id == R.id.nav_about_us) {

            Intent intent = new Intent(CaptainDash.this, AboutUs.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
