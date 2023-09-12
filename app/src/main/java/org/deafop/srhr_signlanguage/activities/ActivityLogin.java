package org.deafop.srhr_signlanguage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.utils.AddUser;

public class ActivityLogin extends AppCompatActivity {
    EditText First_name;
    RadioGroup Gender;
    Button Login;
    EditText Phone;
    RadioButton Sex;
    Button Skip;
    AddUser addUser;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_login);
        this.Login = (Button) findViewById(R.id.login);
        this.Skip = (Button) findViewById(R.id.skip);
        this.Phone = (EditText) findViewById(R.id.first_phone);
        this.Gender = (RadioGroup) findViewById(R.id.gender);
        this.First_name = (EditText) findViewById(R.id.first_name);
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        this.firebaseDatabase = instance;
        this.databaseReference = instance.getReference("Users");
        this.addUser = new AddUser();
        this.Skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ActivityLogin.this.launchMainScreen();
            }
        });
        this.Login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int checkedRadioButtonId = ActivityLogin.this.Gender.getCheckedRadioButtonId();
                ActivityLogin activityLogin = ActivityLogin.this;
                activityLogin.Sex = (RadioButton) activityLogin.findViewById(checkedRadioButtonId);
                ActivityLogin activityLogin2 = ActivityLogin.this;
                Toast.makeText(activityLogin2, activityLogin2.Sex.getText(), Toast.LENGTH_SHORT).show();
                ActivityLogin.this.registerUser();
            }
        });
    }

    /* access modifiers changed from: private */
    public void registerUser() {
        String obj = this.First_name.getText().toString();
        String obj2 = this.Phone.getText().toString();
        String charSequence = this.Sex.getText().toString();
        if (!TextUtils.isEmpty(obj) || !TextUtils.isEmpty(obj2) || !TextUtils.isEmpty(charSequence)) {
            addDatatoFirebase(obj, obj2, charSequence);
        } else {
            Toast.makeText(this, "Please add some data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDatatoFirebase(String str, String str2, String str3) {
        this.addUser.setUserName(str);
        this.addUser.setEmployeeContactNumber(str2);
        this.addUser.setUserGender(str3);
        this.databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActivityLogin.this.databaseReference.setValue(ActivityLogin.this.addUser);
                Toast.makeText(ActivityLogin.this, "data added", Toast.LENGTH_SHORT).show();
            }

            public void onCancelled(DatabaseError databaseError) {
                ActivityLogin activityLogin = ActivityLogin.this;
                Toast.makeText(activityLogin, "Fail to add data " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* access modifiers changed from: private */
    public void launchMainScreen() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
