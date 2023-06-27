package me.chan.androidtex.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class NiceBookApiService {
    private final NiceBookApi mApi;
    private static volatile NiceBookApiService sInstance;

    private NiceBookApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.107:8080/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mApi = retrofit.create(NiceBookApi.class);
    }

    public synchronized static NiceBookApiService getInstance() {
        if (sInstance == null) {
            sInstance = new NiceBookApiService();
        }
        return sInstance;
    }

    public Observable<Map<String, Object>> fetchBooks() {
        return mApi.fetchBooks();
    }
}
