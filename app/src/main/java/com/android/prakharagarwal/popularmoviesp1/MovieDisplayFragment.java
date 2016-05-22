package com.android.prakharagarwal.popularmoviesp1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDisplayFragment extends Fragment {

    private PosterAdapter moviesAdapter; // The custom adapter for the Gridvew of movies
    private String sortType = "sort_by=popularity.desc";
    private String sort = "popular";
    private ArrayList<Movie> currentMovieList = new ArrayList<>(); //ArrayList containing all the movie objects from the Fetch Movies Task
    private DBAdapter dba;
    private ArrayList<Movie> mMovies;
    private Context context;
    public MovieDisplayFragment() {
    }

    public interface Callback {
        void onItemSelected(Movie movie);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sortType = "sort_by=popularity.desc";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMovies != null) {
            outState.putParcelableArrayList("MOVIES_KEY", mMovies);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        //Changes sortType to Fetch Movies by popularity
        if (id == R.id.action_sort_popularity) {
            sortType = "sort_by=popularity.desc";
            sort = "popular";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();
            return true;
        }

        //Changes sortType to Fetch Movies by rating
        if (id == R.id.action_sort_rated) {
            sortType = "sort_by=vote_average.desc";
            sort = "top_rated";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();
            return true;
        }
        if (id == R.id.action_sort_favourites) {
            sortType = "sort_by=vote_average.desc";
            FetchFavoriteMoviesTask moviesTask = new FetchFavoriteMoviesTask();
            moviesTask.execute();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        moviesAdapter = new PosterAdapter(getActivity(), currentMovieList);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movieGridView);
        dba= new DBAdapter(getActivity());
        if (savedInstanceState == null) {
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();

        }else{
            moviesAdapter.clear();
            mMovies=savedInstanceState.getParcelableArrayList("MOVIES_KEY");
            moviesAdapter.addAll(mMovies);
        }


        //Initializes our custom adapter for the Gridview with the current Movie ArrayList data and fetches id's to identify Gridview




        // Sets Custom Adapter to the Gridview
        gridView.setAdapter(moviesAdapter);

        //Create and Launch Intent on Item Click
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie currentMovie = moviesAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(currentMovie);

            }
        });
        return rootView;
    }





    public class FetchMovies extends AsyncTask<Void, Void,ArrayList<Movie>> {
        private final String LOG_TAG = FetchMovies.class.getName();

        //Method for parsing JSON data and creating an array of Movie Objects
        private ArrayList<Movie> getMoviesDatafromJson(String moviesJsonString)
                throws JSONException {
            final String OWM_RESULTS = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_SYNOPSIS = "overview";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_RATING = "vote_average";
            final String OWM_ID = "id";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);
            ArrayList<Movie> resultObjects = new ArrayList();

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                Movie model = new Movie("http://image.tmdb.org/t/p/w185/" + movieObject.getString(OWM_POSTER),movieObject.getString(OWM_ORIGINAL_TITLE),movieObject.getString(OWM_SYNOPSIS),movieObject.getString(OWM_RELEASE_DATE),movieObject.getString(OWM_RATING),movieObject.getString(OWM_ID));
                resultObjects.add(model);
            }
            return resultObjects;
        }

        //Fetches Movies from the Movie Database
        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonString = null;

            try {

                URL url = new URL("http://api.themoviedb.org/3/movie/"+sort+"?" + sortType+  "&api_key="+getString(R.string.api_key));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonString = buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDatafromJson(moviesJsonString); //Returns Array of Movie Objects parsed from the JSON String
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        //Clears and assigns results to the custom Adapter, thereby changing the Array List passed into it
        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                mMovies=new ArrayList<>();
                mMovies=result;
                moviesAdapter.clear();
                moviesAdapter.addAll(result);
                moviesAdapter.notifyDataSetChanged();

 if(getActivity().findViewById(R.id.movie_detail_container)!=null) {
     Bundle arguments = new Bundle();
Movie currentMovie=result.get(0);
     String image=currentMovie.image;
     String title=currentMovie.original_title;
     String release_date=currentMovie.release_date;
     String user_rating=currentMovie.user_rating;
     String synopsis=currentMovie.synopsis;
     String movieid=currentMovie.id;
     arguments.putString("title",title);
     arguments.putString("release",release_date);
     arguments.putString("rating",user_rating);
     arguments.putString("synopsis",synopsis);
     arguments.putString("id",movieid);
     arguments.putString("image", image);
     MovieDetailsFragment fragment = new MovieDetailsFragment();
     fragment.setArguments(arguments);

     getActivity().getSupportFragmentManager().beginTransaction()
             .replace(R.id.movie_detail_container, fragment, "DetailActivityFragment.TAG")
             .commit();

 }

            }
        }
    }
    public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<Movie>> {





        @Override
        protected List<Movie> doInBackground(Void... params) {

        try{
            dba.open();
        }catch (SQLException e){

        }
            return dba.getFavouriteMovies();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                 moviesAdapter.clear();
                    moviesAdapter.addAll(movies);
                if(getActivity().findViewById(R.id.movie_detail_container)!=null) {
                    Bundle arguments = new Bundle();
                    Movie currentMovie=movies.get(0);
                    String image=currentMovie.image;
                    String title=currentMovie.original_title;
                    String release_date=currentMovie.release_date;
                    String user_rating=currentMovie.user_rating;
                    String synopsis=currentMovie.synopsis;
                    String movieid=currentMovie.id;
                    arguments.putString("title",title);
                    arguments.putString("release",release_date);
                    arguments.putString("rating",user_rating);
                    arguments.putString("synopsis",synopsis);
                    arguments.putString("id",movieid);
                    arguments.putString("image",image);
                    MovieDetailsFragment fragment = new MovieDetailsFragment();
                    fragment.setArguments(arguments);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment, "DetailActivityFragment.TAG")
                            .commit();

                }

            }
        }
    }

}
