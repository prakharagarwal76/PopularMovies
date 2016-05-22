package com.android.prakharagarwal.popularmoviesp1;

/**
 * Created by prakharagarwal on 17/04/16.
 */
import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    private String id;
    private String author;
    private String content;

    public Review() {

    }

    public Review(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.author = trailer.getString("author");
        this.content = trailer.getString("content");
    }

    public String getId() { return id; }

    public String getAuthor() { return author; }

    public String getContent() { return content; }
}
