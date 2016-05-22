package com.android.prakharagarwal.popularmoviesp1;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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


public class MovieDetailsFragment extends Fragment {
    public static final String TAG = MovieDetailsFragment.class.getSimpleName();
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    private TrailerAdapter mTrailerAdapter;

    private ListView mTrailersView;
    private ReviewAdapter mReviewAdapter;
    private ListView mReviewsView;

    private DBAdapter dba;
    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dba=new DBAdapter(getActivity());
        final View rootview=inflater.inflate(R.layout.fragment_movie_details, container, false);
        final String image ;
        final String title ;
        final String release ;
        final String rating ;
        final String synopsis;
        final String movieid ;
        Bundle arguments= getArguments();
        if(arguments!=null){
            image = arguments.getString("image");
             title = arguments.getString("title");
            release = arguments.getString("release");
            rating =arguments.getString("rating");
            synopsis = arguments.getString("synopsis");
            movieid = arguments.getString("id");
        }else {
            Intent intent = getActivity().getIntent();
            image = intent.getStringExtra("image");
             title = intent.getStringExtra("title");
             release = intent.getStringExtra("release");
            rating = intent.getStringExtra("rating");
           synopsis = intent.getStringExtra("synopsis");
             movieid = intent.getStringExtra("id");
        }
        ImageView imageview=(ImageView)rootview.findViewById(R.id.imageView2);
        Picasso.with(getContext()).load(image).into(imageview);
        TextView textview=(TextView)rootview.findViewById(R.id.movie_detail_title);
        textview.setText(" " + title);
        TextView textview1=(TextView)rootview.findViewById(R.id.release2);
        textview1.setText(release);
        TextView textview2=(TextView)rootview.findViewById(R.id.rating1);
        textview2.setText(rating);
        TextView textview3=(TextView)rootview.findViewById(R.id.synopsis);
        textview3.setText(synopsis);

        CheckBox star=(CheckBox)rootview.findViewById(R.id.star);
        try{
            dba.open();
        }catch (SQLException e){
            Log.e("SqlException",e.toString());
        }
        int isFavourite=dba.isFavourite(movieid);
        dba.close();
        if(isFavourite==1)
        star.setChecked(true);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    try {
                        dba.open();
                    } catch (SQLException e) {
                        Log.e("SqlException", e.toString());
                    }
                    dba.updateFavourite(movieid, title, image, synopsis, rating, release);
                    dba.close();
                    Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        dba.open();
                    } catch (SQLException e) {
                        Log.e("SqlException", e.toString());
                    }
                    dba.removeFavourite(movieid);
                    dba.close();
                    Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button trailerbtn= (Button)rootview.findViewById(R.id.Trailers_btn);
        trailerbtn.performClick();
        mTrailersView = (ListView) rootview.findViewById(R.id.detail_List_trailer_review);
     final ScrollView sv=(ScrollView)rootview.findViewById(R.id.Scrollview_detailsFragment);


        mReviewsView = (ListView) rootview.findViewById(R.id.detail_List_trailer_review);


        trailerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mReviewAdapter!=null)
                mReviewAdapter.clear();
                mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
                mTrailersView.setAdapter(mTrailerAdapter);
                mTrailersView.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        sv.requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                mTrailersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Trailer trailer = mTrailerAdapter.getItem(position);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                        startActivity(intent);
                    }
                });
                new FetchTrailersTask().execute(movieid);

            }
        });
        Button reviewbtn= (Button)rootview.findViewById(R.id.Reviews_btn);
        reviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTrailerAdapter!=null)
                mTrailerAdapter.clear();
                mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
                mReviewsView.setAdapter(mReviewAdapter);
                mReviewsView.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        sv.requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                new FetchReviewsTask().execute(movieid);

            }
        });


    return rootview;
    }


    public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailer>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private List<Trailer> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<Trailer> results = new ArrayList<>();

            for(int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                // Only show Trailers which are on Youtube
                if (trailer.getString("site").contentEquals("YouTube")) {
                    Trailer trailerModel = new Trailer(trailer);
                    results.add(trailerModel);
                }
            }

            return results;
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
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
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if (trailers != null) {
                if (trailers.size() > 0) {
                   // mTrailersCardview.setVisibility(View.VISIBLE);
                    if (mTrailerAdapter != null) {
                        mTrailerAdapter.clear();
                        for (Trailer trailer : trailers) {
                            mTrailerAdapter.add(trailer);
                        }
                    }

                  /*  mTrailer = trailers.get(0);
                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareMovieIntent());
                    }*/
                }
            }
        }
    }
    public class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private List<Review> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<Review> results = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new Review(review));
            }

            return results;
        }

        @Override
        protected List<Review> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
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
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews != null) {
                if (reviews.size() > 0) {

                    if (mReviewAdapter != null) {
                        mReviewAdapter.clear();
                        for (Review review : reviews) {
                            mReviewAdapter.add(review);
                        }
                    }
                }
            }
        }
    }
}
