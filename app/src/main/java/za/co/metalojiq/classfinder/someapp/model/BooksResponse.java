package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by divine on 2/7/17.
 */
public class BooksResponse {


    //TODO BAD  BOOK SHOULD HAVE THE STATUS NOT THE BOOK it self what!!!


    @SerializedName("books")
    private ArrayList<Book> books;

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }
}
