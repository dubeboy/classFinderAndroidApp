package za.co.metalojiq.classfinder.someapp.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.BookSearchFaculty;
import za.co.metalojiq.classfinder.someapp.activity.fragment.Books;
import za.co.metalojiq.classfinder.someapp.activity.fragment.ListBottomSheet;
import za.co.metalojiq.classfinder.someapp.model.BooksResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;


import static za.co.metalojiq.classfinder.someapp.util.Utils.makeToast;

public class BooksList extends AppCompatActivity implements ListBottomSheet.OnSelectTime {

    private String TAG = BooksList.class.getSimpleName();
    private ProgressBar progressBar;
    private ListBottomSheet bookSearchFaculty;
    private String selectedFaculty;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_books_list, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_books_search));
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        //MenuItemCompat.collapseActionView(menu.findItem(R.id.action_books_search));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_books_search:
                Log.d(TAG, "In here");
                bookSearchFaculty = ListBottomSheet.newInstance("Select faculty to search", BookSearchFaculty.FACULTIES);
                bookSearchFaculty.setListener(this);
                bookSearchFaculty.show(getSupportFragmentManager(), "Books_faculty_btm_s");
                Log.d(TAG, "onOptionsItemSelected: its clicked");
                return true;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);
        progressBar = (ProgressBar) findViewById(R.id.booksLoad);
        Log.d(TAG, "onCreate: BEING CREATED");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), NewBooks.class);
               startActivity(intent);
            }
        });

        //this is where we get the data!!
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "onCreate: Search result's" + query);

            getInitBooks(query, selectedFaculty);
        } else {
            getInitBooks( null, null);
        }
    }

    // so this will take 1 for a search and 0 for default behavior
    private void getInitBooks(String query, String searchFaculty) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);


        if (query == null) {
            Call<BooksResponse> call = apiService.getBooks(1); //just get the first 6 elements
            getBooks(fragmentTransaction, call);

        } else {
            Call<BooksResponse> call = apiService.searchBooks(query, genIDForSelectedFaculty(searchFaculty)); //just get the first 6 elements
            getBooks(fragmentTransaction, call);
        }
    }

    private void getBooks(final FragmentTransaction fragmentTransaction, Call<BooksResponse> call) {
        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                fragmentTransaction.add(R.id.books_content_view, Books.newInstance(response.body().getBooks()), "BOOKS_FRAGMENT");
                fragmentTransaction.commit();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                makeToast("Error occured mate in onResponse of getting books", BooksList.this);
                Log.d(TAG, "onFailure: it failed" + t.toString());
                TextView tvError = (TextView) findViewById(R.id.tvBooksError);
                tvError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemSelected(String faculty) {
        Log.d(TAG, "onItemSelected: the faculty is " + faculty);
        selectedFaculty = faculty;
    }
    private int genIDForSelectedFaculty(String faculty) {
        Log.d(TAG, "genIDForSelectedFaculty: faculty" + faculty);
       for (int i = 0; i < BookSearchFaculty.FACULTIES.length; i++) {
           if(BookSearchFaculty.FACULTIES[i].equals(faculty))
               return i;
       }
       return -1;
    }
}
