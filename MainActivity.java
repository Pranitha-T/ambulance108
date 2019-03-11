package com.example.ngouthamkumar.abulance108;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    LinearLayout bt1,bt2;
    //FirebaseAuth mAuth;
    //DatabaseReference mDatabase;
    //Map<String, String> map;
    //String uid,type;
    //FirebaseAuth.AuthStateListener mAuthListener;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = findViewById(R.id.button);
        bt2 = findViewById(R.id.button2);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainActivity.this, UserSignIn.class);
                startActivity(user);

            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent driver = new Intent(MainActivity.this, DriverLogin.class);
                startActivity(driver);

            }
        });
    }
   /* public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseAuth.getInstance().signOut();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null)

        {
            startActivity(getIntent());

            }
        else
        {
             uid = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child(uid);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    map = (Map<String, String>) dataSnapshot.getValue();
                    type = map.get("type");
                    if(type.equals("1"))
                    {
                        Intent intent = new Intent(Choose.this,MapsDriver.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(type.equals("2"))
                    {
                        Intent intent = new Intent(Choose.this, MapsUser.class);
                        startActivity(intent);
                        finish();
                        //Toast.makeText(First_page.this, ""+user_type, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }*/
}

