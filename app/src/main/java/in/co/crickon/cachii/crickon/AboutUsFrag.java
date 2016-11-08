package in.co.crickon.cachii.crickon;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class AboutUsFrag extends Fragment {
    public AboutUsFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_about_us, container, false);

        WebView webView = (WebView) view.findViewById(R.id.txtAbout);
        String text;
        text = "<html><body><p align=\"justify\">";
        text+= "Crickon is an app for cricket teams and players to connect to different teams. Register with crickon and create your own team; add your players to the team's profile. Play match with different teams and update the team score and detailed score card in the application to get your team in the leaderboard. Improve the team's performance using the statistics also have fun to see your team and players at the top rankings and many more.\n" +
                "<br>Crickon is a mobile app that helps you enhance your tennis-ball-cricket skills and connects you to others who would love to enhance theirs too. Using Crickon, you can organise one-off matches with other teams and tournaments involving multiple teams. Crickon adds fun to your non-professional cricketing, and makes sure the fun goes on and on and on.";
        text+= "</p></body></html>";
        webView.loadData(text, "text/html", "utf-8");

        return view;
    }
}
