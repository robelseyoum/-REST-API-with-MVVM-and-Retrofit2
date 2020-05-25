package com.robelseyoum3.foodrecipes;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robelseyoum3.foodrecipes.adapters.OnRecipeListener;
import com.robelseyoum3.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.robelseyoum3.foodrecipes.util.VerticalSpacingItemDecorator;
import com.robelseyoum3.foodrecipes.viewmodels.MyViewModelFactory;
import com.robelseyoum3.foodrecipes.viewmodels.RecipeListViewModel;


public class RecipeListActivity extends BaseActivity implements OnRecipeListener {
    private static  final String  TAG = "RecipeListActivity";
    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mRecipeRecyclerAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recycler_recipe_list);
        mSearchView = findViewById(R.id.search_view);
        mRecipeListViewModel = new ViewModelProvider(this, new MyViewModelFactory(this.getApplication())).get(RecipeListViewModel.class);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        initRecyclerView();
        initSearchView();
        subscribeObserver();
    }

    private void subscribeObserver() {
        mRecipeListViewModel.getViewState().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(RecipeListViewModel.ViewState viewState) {
                if (viewState != null) {
                    switch (viewState) {
                        case RECIPES: {
                            //recipes will show automatically from another observer
                            break;
                        }
                        case CATEGORIES:
                            displayCategories();
                            break;
                    }
                }
            }
        });
    }

    private void displayCategories() {
        mRecipeRecyclerAdapter.displaySearchCategories();
    }


    private void initRecyclerView() {
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void initSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mRecipeRecyclerAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {

    }



}
