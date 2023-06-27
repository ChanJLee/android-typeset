package me.chan.androidtex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.chan.androidtex.api.NiceBookApiService;

public class ShowBooksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

        NiceBookApiService.getInstance().fetchBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String, Object>>() {
                    @Override
                    public void accept(Map<String, Object> stringObjectMap) throws Exception {
                        render(new ArrayList<>((Collection) stringObjectMap.values()));
                    }
                });
    }

    private void render(List<Map<String, Object>> list) {
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(0, 0, 0, 20);
            }
        });
        recyclerView.setAdapter(new Adapter(list));
    }

    private class Adapter extends RecyclerView.Adapter<MyHolder> {

        private List<Map<String, Object>> list;

        public Adapter(List<Map<String, Object>> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(10, 20, 10, 20);
            textView.setBackground(new ColorDrawable(Color.parseColor("#7f00ff00")));
            return new MyHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            Map<String, Object> item = list.get(position);
            String text = (String) item.get("raw_name");
            TextView textView = (TextView) holder.itemView;
            textView.setText(text);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // todo
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}