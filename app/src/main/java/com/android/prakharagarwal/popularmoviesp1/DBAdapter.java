package com.android.prakharagarwal.popularmoviesp1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by prakharagarwal on 18/04/16.
 */
public class DBAdapter {
    private MovieDbHelper DBHelper;
    static SQLiteDatabase db;


    public DBAdapter(Context context)
    {
        DBHelper = new MovieDbHelper(context);
    }


    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public void updateFavourite(String movieid,String title,String image,String overview,String rating,String date){
        String qry="insert into movie(movie_id,title,image,overview,rating,date) values('"+movieid+"','"+title+"','"+image+"','"+overview+"','"+rating+"','"+date+"');";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDbHelper.COLUMN_MOVIE_ID, movieid);
        contentValues.put(MovieDbHelper.COLUMN_TITLE, title);
        contentValues.put(MovieDbHelper.COLUMN_IMAGE, image);
        contentValues.put(MovieDbHelper.COLUMN_OVERVIEW, overview);
        contentValues.put(MovieDbHelper.COLUMN_RATING, rating);
        contentValues.put(MovieDbHelper.COLUMN_DATE, date);

        db.insert(MovieDbHelper.TABLE_NAME, null, contentValues);
                }
    public int isFavourite(String movieid){
        String qry="select * from movie where movie_id='"+movieid+"';";
        int isFavourite=0;
        Cursor c=db.rawQuery(qry,null);
        while(c.moveToNext()){
            isFavourite=1;
        }
        return  isFavourite;
    }
public void removeFavourite(String movieid){
        String qry="delete from movie where movie_id='"+movieid+"';";
    db.execSQL(qry);
    }
public List<Movie> getFavouriteMovies(){
    String qry="select * from movie";
    Cursor c= db.rawQuery(qry, null);
    int count=c.getCount();
    Movie[] movies= new Movie[count];
    int i=0;
    while (c.moveToNext()){
       movies[i]=new Movie(c.getString(3),c.getString(2),c.getString(4),c.getString(6),c.getString(5),c.getString(1));
        i++;
    }
    List<Movie> moviesList= new ArrayList<Movie>(Arrays.asList(movies));
    return moviesList;

}
    public class MovieDbHelper extends SQLiteOpenHelper {

        public static final String TABLE_NAME = "movie";
        public static final String ID = "id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";

        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";
        private static final int DATABASE_VERSION = 1;

        static final String DATABASE_NAME = "movie.db";

        public MovieDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MOVIE_ID + " VARCHAR(25) NOT NULL, " +
                    COLUMN_TITLE + " VARCHAR(25) NOT NULL, " +
                    COLUMN_IMAGE + " VARCHAR(100), " +

                    COLUMN_OVERVIEW + " VARCHAR(500), " +
                    COLUMN_RATING + " VARCHAR(25), " +
                    COLUMN_DATE + " VARCHAR(25));";

            db.execSQL(SQL_CREATE_MOVIE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
