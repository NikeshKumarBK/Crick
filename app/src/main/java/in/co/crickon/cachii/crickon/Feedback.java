package in.co.crickon.cachii.crickon;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Feedback extends AppCompatActivity {

    EditText edtName,edtSub,edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        edtMessage=(EditText)findViewById(R.id.edtMsg);
        edtName=(EditText)findViewById(R.id.edtName);
        edtSub=(EditText)findViewById(R.id.edtSub);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to="crickoncachii@gmail.com";
                String subject=edtSub.getText().toString();
                String message=edtMessage.getText().toString();
                String name=edtName.getText().toString();


                Intent email = new Intent(Intent.ACTION_SEND);

                //email.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, "Hi my name is "+name+"."+message);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));


            }
        });
    }
}
