package com.robelseyoum3.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.robelseyoum3.foodrecipes.models.Recipe;
import com.robelseyoum3.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private boolean mIsViewingRecipes;
    private boolean mIsPerformingQuery;

    public RecipeListViewModel() {
        mIsViewingRecipes = false;
        mRecipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipeRepository.getRecipes();
    }

    public void searchRecipesApi(String query, int pageNumber) {
        mIsViewingRecipes = true;
        mIsPerformingQuery = true;
        mRecipeRepository.searchRecipesApi(query, pageNumber);
    }

    public boolean isPerformingQuery() {
        return mIsPerformingQuery;
    }

    public void setPerformingQuery(Boolean isPerformingQuery) {
        mIsPerformingQuery = isPerformingQuery;
    }

    public boolean isIsViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void isIsViewingRecipes(Boolean isViewingRecipes) {
        mIsViewingRecipes = isViewingRecipes;
    }

    public void searchNextPage() {
        if (!mIsPerformingQuery && mIsViewingRecipes) {
            mRecipeRepository.searchNextPage();
        }
    }

    public boolean onBackPressed() {
        if (isPerformingQuery()) {
            //cancel the request query send message to repo
            mRecipeRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        if (mIsViewingRecipes) {
            mIsViewingRecipes = false;
            return false;
        }
        return true;
    }
}
