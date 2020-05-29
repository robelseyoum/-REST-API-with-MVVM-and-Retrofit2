package com.robelseyoum3.foodrecipes;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.robelseyoum3.foodrecipes.adapters.OnRecipeListener;
import com.robelseyoum3.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.robelseyoum3.foodrecipes.models.Recipe;
import com.robelseyoum3.foodrecipes.util.Resource;
import com.robelseyoum3.foodrecipes.util.VerticalSpacingItemDecorator;
import com.robelseyoum3.foodrecipes.viewmodels.RecipeListViewModel;
import com.robelseyoum3.foodrecipes.viewmodels.RecipeListViewModelFactory;

import java.util.List;

import static com.robelseyoum3.foodrecipes.viewmodels.RecipeListViewModel.QUERY_EXHORTED;


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

        mRecipeListViewModel = new ViewModelProvider(this, new RecipeListViewModelFactory(this.getApplication())).get(RecipeListViewModel.class);

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

        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(Resource<List<Recipe>> listResource) {
                if (listResource != null) {
                    Log.d(TAG, "onChanged: status: " + listResource.status);
                    if (listResource.data != null) {
//                        Testing.printRecipes(listResource.data, "data");
//                        mRecipeRecyclerAdapter.setRecipes(listResource.data);
                        switch (listResource.status) {
                            case LOADING: {
                                if (mRecipeListViewModel.getPageNumber() > 1) {
                                    mRecipeRecyclerAdapter.displayLoading();
                                } else {
                                    mRecipeRecyclerAdapter.displayOnlyLoading();
                                }
                                break;
                            }
                            case ERROR: {
                                Log.e(TAG, "onChanged: cannot refresh the cach.");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.e(TAG, "onChanged: ERROR, #recipes: " + listResource.data.size());
                                mRecipeRecyclerAdapter.hideLoading();
                                mRecipeRecyclerAdapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();
                                if (listResource.message.equals(QUERY_EXHORTED)) {
                                    mRecipeRecyclerAdapter.setQueryExhausted();
                                }
                                break;
                            }
                            case SUCCESS: {
                                Log.e(TAG, "onChanged: cache has been refreshed.");
                                Log.e(TAG, "onChanged: SUCCESS, #recipes: " + listResource.data.size());
                                mRecipeRecyclerAdapter.hideLoading();
                                mRecipeRecyclerAdapter.setRecipes(listResource.data);
                                break;
                            }
                        }
                    }
                }
            }
        });

    }


    private void searchRecipeApi(String query) {
        mRecyclerView.smoothScrollToPosition(0);
        mRecipeListViewModel.searchRecipesApi(query, 1);
        mSearchView.clearFocus();
    }

    private void displayCategories() {
        mRecipeRecyclerAdapter.displaySearchCategories();
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreloadSizeProvider = new ViewPreloadSizeProvider<>();
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this, initGlide(), viewPreloadSizeProvider);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //assigning preloaded cache for how many images need ahead of time
        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(
                Glide.with(this),
                mRecipeRecyclerAdapter,
                viewPreloadSizeProvider,
                30);

        mRecyclerView.addOnScrollListener(preloader);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!mRecyclerView.canScrollVertically(1)
                        && mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.RECIPES) {
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });

        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
    }


    private void initSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipeApi(query);
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
        searchRecipeApi(category);
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.CATEGORIES) {
            super.onBackPressed();
        } else {
            mRecipeListViewModel.cancelSearchRequest();
            mRecipeListViewModel.setViewCategories();
        }
    }
}
