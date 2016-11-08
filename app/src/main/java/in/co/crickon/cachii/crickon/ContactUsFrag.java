package in.co.crickon.cachii.crickon;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactUsFrag extends Fragment {

    TextView txtWeb;
    public ContactUsFrag() {
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
        View view= inflater.inflate(R.layout.fragment_contact_us, container, false);

        txtWeb=(TextView)view.findViewById(R.id.txtWeb);

        txtWeb.setClickable(true);
        txtWeb.setMovementMethod(LinkMovementMethod.getInstance());
        String text = " <font color='#757575'><a href='http://crickon.co.in/'>Crickon </a></font>";
        txtWeb.setText(Html.fromHtml(text));

        return view;
    }
}
