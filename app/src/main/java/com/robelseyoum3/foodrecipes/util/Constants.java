package com.robelseyoum3.foodrecipes.util;

public class Constants {
    public static final String BASE_URL = "https://recipesapi.herokuapp.com";
    public static final int CONNECTION_TIMEOUT = 10; //10 seconds
    public static final int READ_TIMEOUT = 2; //2 seconds
    public static final int WRITE_TIMEOUT = 2; //2 seconds


    public static final int RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30; //SEC * MIN * 24HR/DAY == 30 DAYS IN SECONDS



    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
}
