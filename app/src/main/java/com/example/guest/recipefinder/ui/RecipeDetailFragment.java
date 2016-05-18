package com.example.guest.recipefinder.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.recipefinder.Constants;
import com.example.guest.recipefinder.R;
import com.example.guest.recipefinder.models.Recipe;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener  {
    @Bind(R.id.recipeImageView) ImageView mImageLabel;
    @Bind(R.id.recipeNameTextView) TextView mNameLabel;
    @Bind(R.id.websiteTextView) TextView mWebsiteLabel;
    @Bind(R.id.ingredientListView) ListView mIngredientList;
    @Bind(R.id.saveRecipeButton) Button mSaveRecipeButton;
    private SharedPreferences mSharedPreferences;


    private Recipe mRecipe;

    public static RecipeDetailFragment newInstance(Recipe recipe) {
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("recipe", Parcels.wrap(recipe));
        recipeDetailFragment.setArguments(args);
        return recipeDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe = Parcels.unwrap(getArguments().getParcelable("recipe"));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);

        ArrayAdapter ingredientAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, mRecipe.getIngredients());
        mIngredientList.setAdapter(ingredientAdapter);
        mIngredientList.setOnItemClickListener(this);
        Picasso.with(view.getContext()).load(mRecipe.getImageUrl()).into(mImageLabel);
        mNameLabel.setText(mRecipe.getName());
        mWebsiteLabel.setOnClickListener(this);
        mSaveRecipeButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String ingredient = mRecipe.getIngredients()[i];
        saveIngredientToFirebase(ingredient);
    }

    public void saveIngredientToFirebase(String ingredient) {
        String uID = mSharedPreferences.getString(Constants.KEY_UID, null);
        Firebase ingredientRef = new Firebase(Constants.FIREBASE_URL_SHOPPING_LIST).child(uID);
        ingredientRef.push().setValue(ingredient);
        Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v == mWebsiteLabel) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRecipe.getSourceUrl()));
            startActivity(webIntent);
        }
        if (v == mSaveRecipeButton) {
            String userUid = mSharedPreferences.getString(Constants.KEY_UID, null);
            Firebase userRecipesFirebaseRef = new Firebase(Constants.FIREBASE_URL_RECIPES).child(userUid);
            Firebase pushRef = userRecipesFirebaseRef.push();
            String recipePushId = pushRef.getKey();
            mRecipe.setPushId(recipePushId);
            pushRef.setValue(mRecipe);
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
    }

}
