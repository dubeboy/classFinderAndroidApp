package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.Book;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by divine on 2/5/17.
 */
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksViewHolder>{
    private static final String TAG = AccomAdapter.class.getSimpleName();
    List<Book> books;
    private int rowLayout;
    private Context context;
    private final BooksAdapter.OnItemClickListener listener;

    public BooksAdapter(ArrayList<Book> books, int rowLayout, Context context, OnItemClickListener listener) {
        this.books = books;
        this.rowLayout = rowLayout;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(rowLayout, parent, false);
        return new BooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BooksViewHolder holder, int position) {
        holder.bind(books.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public interface OnItemClickListener {
    }

    public class BooksViewHolder extends RecyclerView.ViewHolder  {

        private final ImageView booksIcon;
        private final TextView booksTitle;
        private final TextView booksInfo;
        private final TextView booksDesc;
        private final TextView booksPrice;

        public BooksViewHolder(View view) {
            super(view);
            booksIcon = (ImageView) view.findViewById(R.id.booksIcon);
            booksTitle = (TextView) view.findViewById(R.id.booksTitle);
            booksInfo = (TextView) view.findViewById(R.id.booksInfo);
            booksDesc = (TextView) view.findViewById(R.id.booksDesc);
            booksPrice = (TextView) view.findViewById(R.id.booksPrice);
        }

        void bind(final Book book, final BooksAdapter.OnItemClickListener listener) {
            Picasso.with(itemView.getContext())
                    .load(ApiClient.DEV_HOST + book.getThumb()).into(booksIcon);
            booksTitle.setText(book.getName());
            booksInfo.setText(book .getPhone() + "-" + book.getEmail());
            booksDesc.setText(book.getDesc());
            booksPrice.setText(book.getPrice());
        }
    }
}
