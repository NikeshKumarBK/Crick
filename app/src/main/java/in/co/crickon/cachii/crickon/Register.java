package in.co.crickon.cachii.crickon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Register extends AppCompatActivity {

    Button btnPlayerRegister,btnCaptainRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnPlayerRegister=(Button)findViewById(R.id.btnPlayerRegister);
        btnCaptainRegister=(Button)findViewById(R.id.btnCaptainRegister);

        btnPlayerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Register.this,PlayerRegister.class);
                intent.putExtra("Role","Player");
                startActivity(intent);
                finish();
            }
        });

        btnCaptainRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Register.this,PlayerRegister.class);
                intent.putExtra("Role","Captain");
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Register.this,Login.class);
        startActivity(intent);
        finish();
    }
}
