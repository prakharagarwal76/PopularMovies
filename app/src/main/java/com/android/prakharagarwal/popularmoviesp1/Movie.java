package com.android.prakharagarwal.popularmoviesp1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by prakharagarwal on 06/03/16.
 */
public class Movie implements Parcelable{
    String image;
    String original_title;
    String synopsis;
    String user_rating;
    String release_date;
    String id;
    int numOfParameters;

    //Constructors

    public Movie() {
        super();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(image);

        dest.writeString(synopsis);
        dest.writeString(user_rating);
        dest.writeString(release_date);
    }

    public Movie(String image, String original_title, String synopsis, String release_date, String user_rating, String id) {
        super();
        this.image = image;
        this.original_title = original_title;
        this.synopsis = synopsis;
        this.release_date = release_date;
        this.user_rating = user_rating;
        this.id = id;
        this.numOfParameters = 6;
    }

}
