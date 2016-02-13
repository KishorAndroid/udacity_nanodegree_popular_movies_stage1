package com.appdevelopkar.stage1.movies.popularmoviesstage1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appdevelopkar.stage1.movies.popularmoviesstage1.adapter.MovieGridAdapter;
import com.appdevelopkar.stage1.movies.popularmoviesstage1.asynctask.FetchMovieList;
import com.appdevelopkar.stage1.movies.popularmoviesstage1.model.Movie;
import com.appdevelopkar.stage1.movies.popularmoviesstage1.util.Constants;
import com.appdevelopkar.stage1.movies.popularmoviesstage1.util.SpacesItemDecoration;

import java.util.ArrayList;
/**
 * A MovieGridFragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {

    private MovieGridAdapter movieGridAdapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView movieGridView;
    private ArrayList<Movie> movies;
    private String filterName;
    private ProgressBar progressBar;

    public MovieGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMovieListReceiver,
                new IntentFilter("movies-fetched"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMovieListReceiver);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.content_loading);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        movieGridView = (RecyclerView) view.findViewById(R.id.movies_grid);
        movieGridView.setHasFixedSize(true);
        movieGridView.setLayoutManager(gridLayoutManager);
        movieGridView.addItemDecoration(new SpacesItemDecoration(0));
        if(savedInstanceState != null && savedInstanceState.containsKey("FILTER_NAME")){
            filterName = savedInstanceState.getString("FILTER_NAME");
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(filterName);
            movies = savedInstanceState.getParcelableArrayList("MOVIE_LIST");
            movieGridAdapter = new MovieGridAdapter(getActivity(), movies);
            movieGridView.setAdapter(movieGridAdapter);
        }else {
            filterName = Constants.SORT_BY_MOST_POPULAR;
            progressBar.setVisibility(View.VISIBLE);
            new FetchMovieList(getActivity()).execute(Constants.SORT_BY_MOST_POPULAR);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(filterName != null && movies != null) {
            outState.putString("FILTER_NAME", filterName);
            outState.putParcelableArrayList("MOVIE_LIST", movies);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sort_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);
        switch (id){
            case R.id.most_popular:
                changeSortOrder(Constants.SORT_BY_MOST_POPULAR);
                break;
            case R.id.most_rated:
                changeSortOrder(Constants.SORT_BY_MOST_RATED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(getFilterString(filterName).equalsIgnoreCase(Constants.SORT_BY_MOST_POPULAR)){menu.findItem(R.id.most_popular).setChecked(true);}
        if(getFilterString(filterName).equalsIgnoreCase(Constants.SORT_BY_MOST_RATED)){menu.findItem(R.id.most_rated).setChecked(true);}
    }

    private void changeSortOrder(String SORT_BY_MOST_POPULAR) {
        movieGridView.setAdapter(null);
        progressBar.setVisibility(View.VISIBLE);
        new FetchMovieList(getActivity()).execute(SORT_BY_MOST_POPULAR);
    }

    private BroadcastReceiver mMovieListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            movies = intent.getParcelableArrayListExtra("movieList");
            progressBar.setVisibility(View.GONE);
            if(movies != null && movies.size() > 0){
                movieGridAdapter = new MovieGridAdapter(getActivity(), movies);
                filterName = getFilterName(intent.getStringExtra("FILTER_NAME"));
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(filterName);
                movieGridView.setAdapter(movieGridAdapter);
            }else{
                Toast.makeText(getActivity(), "Connection Problem!", Toast.LENGTH_LONG).show();
            }
         }
    };

    private String getFilterName(String filter_name) {
        if(filter_name.equalsIgnoreCase(Constants.SORT_BY_MOST_POPULAR)){return "Most Popular";}
        if(filter_name.equalsIgnoreCase(Constants.SORT_BY_MOST_RATED)){return "Highest Rated";}
        return "";
    }

    private String getFilterString(String filterName) {
        if(filterName.equalsIgnoreCase("Most Popular")){return Constants.SORT_BY_MOST_POPULAR;}
        if(filterName.equalsIgnoreCase("Highest Rated")){return Constants.SORT_BY_MOST_RATED;}
        return "";
    }
}