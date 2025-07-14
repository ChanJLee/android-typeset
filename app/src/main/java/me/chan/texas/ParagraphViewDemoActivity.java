package me.chan.texas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.text.Paragraph;

import java.util.ArrayList;
import java.util.List;

public class ParagraphViewDemoActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(me.chan.texas.debug.R.layout.activity_paragraph_view_demo);

		RecyclerView recyclerView = findViewById(me.chan.texas.debug.R.id.recycler_view);
		List<ParagraphView.ParagraphSource> sources = new ArrayList<>();

		for (int i = 0; i < 10; ++i) {
			sources.add(new ParagraphSourceKt(i));
		}

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new Adapter(this, sources));
	}

	private static class MySource extends ParagraphView.ParagraphSource {

		private final String mText;

		public MySource(String msg) {
			mText = msg;
		}

		@Override
		protected Paragraph onRead(TexasOption option) {
			return Paragraph.Builder.newBuilder(option)
					.text(mText)
					.build();
		}
	}

	private static class Adapter extends RecyclerView.Adapter<MyViewHolder> {
		private final List<ParagraphView.ParagraphSource> mSources;
		private final LayoutInflater mLayoutInflater;

		public Adapter(Context context, List<ParagraphView.ParagraphSource> sources) {
			mSources = sources;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@NonNull
		@Override
		public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new MyViewHolder(mLayoutInflater.inflate(me.chan.texas.debug.R.layout.item_paragraph_view, parent, false));
		}

		@Override
		public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
			holder.mParagraphView.setSource(mSources.get(position));
			String msg = "idx: " + (position + 1);
			holder.mLabel.setText(msg);
			holder.mParagraphView.setTag(msg);
		}

		@Override
		public int getItemCount() {
			return mSources.size();
		}
	}

	private static class MyViewHolder extends RecyclerView.ViewHolder {

		private final ParagraphView mParagraphView;
		private final TextView mLabel;

		public MyViewHolder(@NonNull View itemView) {
			super(itemView);

			mParagraphView = itemView.findViewById(me.chan.texas.debug.R.id.paragraph);
			mLabel = itemView.findViewById(me.chan.texas.debug.R.id.label);
		}
	}
}