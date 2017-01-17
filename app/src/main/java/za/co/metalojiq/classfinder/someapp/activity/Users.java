package za.co.metalojiq.classfinder.someapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import za.co.metalojiq.classfinder.someapp.R;



//currently the user is a runner so look in the runner activity
public class Users extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
    }
}
