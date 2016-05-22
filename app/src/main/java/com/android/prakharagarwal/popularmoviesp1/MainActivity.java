package com.android.prakharagarwal.popularmoviesp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MovieDisplayFragment.Callback{
boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(CheckNetwork.isInternetAvailable(this)){

 if(findViewById(R.id.movie_detail_container)!=null){
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailsFragment(), "DETAILFRAGMEN_TAG")
                        .commit();
            }
        }else mTwoPane=false;

    }else{
            Toast.makeText(this, "Please check connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(Movie currentMovie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();

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

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, "DetailActivityFragment.TAG")
                    .commit();
        } else {
           String image=currentMovie.image;
                String title=currentMovie.original_title;
                String release_date=currentMovie.release_date;
                String user_rating=currentMovie.user_rating;
                String synopsis=currentMovie.synopsis;
                String movieid=currentMovie.id;

                //Create new Intent and add a String Array of data for the Item Clicked
                Intent intent= new Intent(this,MovieDetails.class).putExtra("image",image)
                                                                            .putExtra("title", title)
                                                                            .putExtra("release", release_date)
                                                                            .putExtra("rating",user_rating)
                                                                            .putExtra("synopsis", synopsis)
                                                                            .putExtra("id",movieid);
                startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

