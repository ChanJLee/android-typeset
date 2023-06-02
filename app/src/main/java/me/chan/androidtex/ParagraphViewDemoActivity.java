package me.chan.androidtex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shanbay.lib.texas.TexasOption;
import com.shanbay.lib.texas.renderer.OnSpanClickedPredicate;
import com.shanbay.lib.texas.renderer.ui.text.ParagraphView;
import com.shanbay.lib.texas.source.SourceCloseException;
import com.shanbay.lib.texas.text.Paragraph;

import java.util.ArrayList;
import java.util.List;

public class ParagraphViewDemoActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph_view_demo);

		RecyclerView recyclerView = findViewById(R.id.recycler_view);
		List<ParagraphView.ParagraphSource> sources = new ArrayList<>();

		sources.add(new MySource("1 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("2 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("3 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("4 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("5 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("6 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("7 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("8 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("9 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("10 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("11 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("12 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("13 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("14 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("15 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("16 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("17 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("18 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("19 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));
		sources.add(new MySource("20 Mr. and Mrs. Dursley, of number four, Privet Drive, were proud to say that they were perfectly normal, thank you very much. They were the last people you'd expect to be involved in anything strange or mysterious, because they just didn't hold with such nonsense.\n"));


		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new Adapter(this, sources));
	}

	private static class MySource extends ParagraphView.ParagraphSource {

		private final String mText;

		public MySource(String msg) {
			mText = msg;
		}

		@Override
		protected Paragraph open(TexasOption option) {
			return Paragraph.Builder.newBuilder(option)
					.text(mText)
					.build();
		}

		@Override
		public void close() throws SourceCloseException {
			/* do nothing */
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
			return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_paragraph_view, parent, false));
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

			mParagraphView = itemView.findViewById(R.id.paragraph);
			mParagraphView.setOnSpanClickedPredicate(new OnSpanClickedPredicate() {
				@Override
				public boolean apply(@Nullable Object clickedTag, @Nullable Object tag) {
					return false;
				}
			});

			mLabel = itemView.findViewById(R.id.label);
		}
	}
}