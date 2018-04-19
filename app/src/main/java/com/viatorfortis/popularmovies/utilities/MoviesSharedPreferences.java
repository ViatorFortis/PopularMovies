package com.viatorfortis.popularmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.viatorfortis.popularmovies.R;


public class MoviesSharedPreferences {

    public static String getPreferredSortType (Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortTypePrefKey = context.getString(R.string.sort_type_pref_key);
        String sortTypePrefdefaultVaue = context.getString(R.string.movies_popularity_sorting);

        return prefs.getString(sortTypePrefKey, sortTypePrefdefaultVaue);
    }

    public static void setPreferredSortType (Context context, String sortType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = prefs.edit();
        String sortTypePrefKey = context.getString(R.string.sort_type_pref_key);
        prefEditor.putString(sortTypePrefKey, sortType);
        prefEditor.apply();
    }
}
