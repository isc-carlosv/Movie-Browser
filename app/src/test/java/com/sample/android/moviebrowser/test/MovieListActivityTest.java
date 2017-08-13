package com.sample.android.moviebrowser.test;


import org.junit.Before;
import org.junit.Test;

import com.sample.android.moviebrowser.MovieListActivity;

public class MovieListActivityTest {
    public MovieListActivity movieListActivity;


    @Before
    public void setup(){

    }

    @Test
    public void deleteMovie_Test(){


        movieListActivity.deleteMovie(1);
    }


}