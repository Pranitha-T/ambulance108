package com.example.ngouthamkumar.abulance108;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;
public class DriverLogin extends AppCompatActivity {
    EditText un,ps;
    Button b;
    ProgressBar progressbar;
    CheckBox show_hide_password;
    FirebaseUser user_id;
    private FirebaseAuth mAuth;
    DatabaseReference mdata2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        mAuth = FirebaseAuth.getInstance();
        progressbar=findViewById(R.id.progressbar);
        un=findViewById(R.id.username);
        ps=findViewById(R.id.password);
        b=findViewById(R.id.login);
        show_hide_password =findViewById(R.id.show_hide_password);
        show_hide_password
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {
                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {
                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text
                           ps.setInputType(InputType.TYPE_CLASS_TEXT);
                           ps.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            ps.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            ps.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em=un.getText().toString().trim();
                String pss=ps.getText().toString().trim();
                if (em.isEmpty()) {
                    un.setError(" email required ");
                    un.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher((CharSequence) em).matches()) {
                    un.setError("plz enter a valid email");
                    un.requestFocus();
                    return;
                }

                if (pss.isEmpty()) {
                    ps.setError("valid password required ");
                    ps.requestFocus();
                    return;

                }
                if (pss.length() < 8) {
                    ps.setError("length should be atleast 8 characters");
                    ps.requestFocus();
                    return;

                }
                progressbar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(em, pss).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressbar.setVisibility(View.GONE);
                        // Toast.makeText(DriverLogin.this, "logcat", Toast.LENGTH_SHORT).show();
                        if(task.isSuccessful())
                        {
                            finish();
                            Toast.makeText(DriverLogin.this,"logged in", Toast.LENGTH_SHORT).show();
                            Intent next=new Intent(DriverLogin.this,MapsDriver.class);
                            startActivity(next);

                        }
                        else
                        {
                            Toast.makeText(DriverLogin.this, "login error", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        });
    }
}
