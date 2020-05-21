package com.robelseyoum3.foodrecipes;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robelseyoum3.foodrecipes.adapters.OnRecipeListener;
import com.robelseyoum3.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.robelseyoum3.foodrecipes.models.Recipe;
import com.robelseyoum3.foodrecipes.util.Testing;
import com.robelseyoum3.foodrecipes.util.VerticalSpacingItemDecorator;
import com.robelseyoum3.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {
    private static  final String  TAG = "RecipeListActivity";
    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mRecipeRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);
        mRecyclerView = findViewById(R.id.recycler_recipe_list);
        initRecyclerView();
        initSearchView();
        subscribeObservers();
        if (!mRecipeListViewModel.isIsViewingRecipes()) {
            //display search categories
            displaySearchCategories();
        }
    }

    private void initRecyclerView() {
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
    }

    private void subscribeObservers() {
        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null) {
                    if (mRecipeListViewModel.isIsViewingRecipes()) {
                        Testing.printRecipes(recipes, "TestingSearchingRecipe");
                        mRecipeRecyclerAdapter.setRecipes(recipes);
                    }
                }
            }
        });
    }

    private void initSearchView() {
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mRecipeRecyclerAdapter.displayLoading();
                mRecipeListViewModel.searchRecipesApi(query, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Log.d(TAG, "onRecipeClick: clicked. " + position);
    }

    @Override
    public void onCategoryClick(String category) {
        mRecipeRecyclerAdapter.displayLoading();
        mRecipeListViewModel.searchRecipesApi(category, 1);
    }

    private void displaySearchCategories() {
        mRecipeListViewModel.isIsViewingRecipes(false);
        mRecipeRecyclerAdapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.onBackPressed()) {
            super.onBackPressed();
        } else {
            displaySearchCategories();
        }
    }
}
