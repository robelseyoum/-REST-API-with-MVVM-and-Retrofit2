package com.robelseyoum3.foodrecipes.util;

import android.util.Log;

import com.robelseyoum3.foodrecipes.models.Recipe;

import java.util.List;

public class Testing {

    public static void printRecipes(List<Recipe> list, String tag) {
        int i = 0;
        for (Recipe recipe : list) {
            i++;
            Log.d(tag, "onChanged " + i + " " + recipe.getTitle());
        }
    }

}
