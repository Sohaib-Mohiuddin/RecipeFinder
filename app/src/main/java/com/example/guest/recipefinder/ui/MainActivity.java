package com.example.guest.recipefinder.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.guest.recipefinder.Constants;
import com.example.guest.recipefinder.R;
import com.example.guest.recipefinder.models.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.findRecipesButton) Button mFindRecipesButton;
    @Bind(R.id.ingredient1EditText) EditText mIngredient1EditText;
    @Bind(R.id.ingredient2EditText) EditText mIngredient2EditText;

    private ValueEventListener mUserRefListener;
    private Firebase mUserRef;
    private String mUId;
    private SharedPreferences mSharedPreferences;
    @Bind(R.id.introTextView)TextView mWelcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFindRecipesButton.setOnClickListener(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUId = mSharedPreferences.getString(Constants.KEY_UID, null);
        mUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mUId);

        mUserRefListener = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mWelcomeTextView.setText("Welcome, " + user.getName() + "!");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.findRecipesButton:
                String ingredient1 = mIngredient1EditText.getText().toString();
                String ingredient2 = mIngredient2EditText.getText().toString();
                Intent recipesIntent = new Intent(MainActivity.this, RecipeListActivity.class);
                recipesIntent.putExtra("ingredient1", ingredient1);
                recipesIntent.putExtra("ingredient2", ingredient2);
                startActivity(recipesIntent);
                break;
            default:
                break;
        }
    }
}
