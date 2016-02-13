package com.appdevelopkar.stage1.movies.popularmoviesstage1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appdevelopkar.stage1.movies.popularmoviesstage1.model.Movie;
import com.appdevelopkar.stage1.movies.popularmoviesstage1.util.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by kishor on 8/2/16.
 */
public class MovieDetailFragment extends Fragment {

    private Movie movie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_details, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView movieTitle = (TextView) view.findViewById(R.id.movie_title);
        ImageView moviePoster= (ImageView) view.findViewById(R.id.movie_poster);
        TextView movieOverview = (TextView) view.findViewById(R.id.overview);
        TextView releaseDate = (TextView) view.findViewById(R.id.release_date);
        TextView vote = (TextView) view.findViewById(R.id.vote_average);
        if(savedInstanceState != null){
            movie = savedInstanceState.getParcelable("MOVIE");
        }else{
            Bundle bundle = getArguments();
            movie = bundle.getParcelable("MOVIE_DATA");
        }
        movieTitle.setText(movie.getTitle());
        Picasso.with(getActivity()).load(Constants.THUMBNAIL_BASE_URL + movie.getPoster_path()).into(moviePoster);
        movieOverview.setText(movie.getOverview());
        releaseDate.setText(movie.getRelease_date());
        vote.setText(movie.getVote_average() + " / " + movie.getVote_count());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.most_popular).setVisible(false);
        menu.findItem(R.id.most_rated).setVisible(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("MOVIE", movie);
    }
}
