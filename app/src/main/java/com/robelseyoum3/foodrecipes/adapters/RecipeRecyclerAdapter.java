package com.robelseyoum3.foodrecipes.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.robelseyoum3.foodrecipes.R;
import com.robelseyoum3.foodrecipes.models.Recipe;
import com.robelseyoum3.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ListPreloader.PreloadModelProvider<String> {

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;


    private List<Recipe> mRecipes;
    private OnRecipeListener onRecipeListener;
    private RequestManager requestManager;
    private ViewPreloadSizeProvider<String> preloadSizeProvider;


    public RecipeRecyclerAdapter(OnRecipeListener onRecipeListener, RequestManager requestManager, ViewPreloadSizeProvider<String> preloadSizeProvider) {
        this.onRecipeListener = onRecipeListener;
        this.requestManager = requestManager;
        this.preloadSizeProvider = preloadSizeProvider;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case LOADING_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            case CATEGORY_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new CategoryViewHolder(view, onRecipeListener, requestManager);
            }
            case EXHAUSTED_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_exhausted, parent, false);
                return new SearchExhaustedViewHolder(view);
            }
            case RECIPE_TYPE:
            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, onRecipeListener, requestManager, preloadSizeProvider);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemViewType =  getItemViewType(position);
        if(itemViewType == RECIPE_TYPE){
            ((RecipeViewHolder) holder).onBind(mRecipes.get(position));
        } else if (itemViewType == CATEGORY_TYPE) {
            ((CategoryViewHolder) holder).onBind(mRecipes.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mRecipes.get(position).getSocial_rank() == -1) {
            return CATEGORY_TYPE;
        } else if (mRecipes.get(position).getTitle().equals("LOADING...")) {
            return LOADING_TYPE;
        } else if (mRecipes.get(position).getTitle().equals("EXHAUSTED...")) {
            return EXHAUSTED_TYPE;
        } else {
            return RECIPE_TYPE;
        }
    }

    //display loading during search request
    public void displayOnlyLoading() {
        clearRecipesList();
        Recipe recipe = new Recipe();
        recipe.setTitle("LOADING...");
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    public void clearRecipesList() {
        if (mRecipes == null) {
            mRecipes = new ArrayList<>();
        } else {
            mRecipes.clear();
        }
        notifyDataSetChanged();
    }

    public void setQueryExhausted() {
        hideLoading();
        Recipe exhaustedRecipe = new Recipe();
        exhaustedRecipe.setTitle("EXHAUSTED...");
        mRecipes.add(exhaustedRecipe);
        notifyDataSetChanged();
    }

    public void hideLoading() {
        if (isLoading()) {
            if (mRecipes.get(0).getTitle().equals("LOADING...")) {
                mRecipes.remove(0);
            } else if (mRecipes.get(mRecipes.size() - 1).equals("LOADING...")) {
                mRecipes.remove(mRecipes.size() - 1);
            }
            notifyDataSetChanged();
        }
    }

    //pagination
    public void displayLoading() {
        if (mRecipes == null) {
            mRecipes = new ArrayList<>();
        }
        if(!isLoading()){
            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING...");
            mRecipes.add(recipe);
            notifyDataSetChanged();
        }
    }

    public void displaySearchCategories() {
        List<Recipe> categories = new ArrayList<>();
        for (int i = 0; i < Constants.DEFAULT_SEARCH_CATEGORIES.length; i++) {
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        mRecipes = categories;
        notifyDataSetChanged();
    }

    private boolean isLoading(){
        if(mRecipes != null) {
            if(mRecipes.size() > 0){
                if(mRecipes.get(mRecipes.size() - 1).getTitle().equals("LOADING...")){
                    return true;
                }
            }
        }
        return  false;
    }

    @Override
    public int getItemCount() {
        if (mRecipes != null) {
            return mRecipes.size();
        }
        return 0;
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public Recipe getSelectedRecipe(int position) {
        if (mRecipes != null) {
            if (mRecipes.size() > 0) {
                return mRecipes.get(position);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        String url = mRecipes.get(position).getImage_url();
        if (TextUtils.isEmpty(url)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return requestManager.load(item);
    }
}
