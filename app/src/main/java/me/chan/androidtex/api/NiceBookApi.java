package me.chan.androidtex.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface NiceBookApi {

    @GET("/material/meta.json")
    Observable<Map<String, Object>> fetchBooks();
}
