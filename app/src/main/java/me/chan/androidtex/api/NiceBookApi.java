package me.chan.androidtex.api;

import java.util.Map;

import io.reactivex.Observable;
import me.chan.androidtex.Section;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NiceBookApi {

    @GET("/material/meta.json")
    Observable<Map<String, Object>> fetchBooks();

    @GET("/material/{bookId}/{sectionId}/content.json")
    Observable<Section> fetchSection(@Path("bookId") String bookId, @Path("sectionId") String sectionId);
}
