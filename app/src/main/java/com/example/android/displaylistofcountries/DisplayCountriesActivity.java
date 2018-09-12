package com.example.android.displaylistofcountries;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.displaylistofcountries.dagger.DisplayListOfCountriesApplication;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

public class DisplayCountriesActivity extends AppCompatActivity {

    @Inject
    DisplayCountriesPresenter.PresenterFactory mPresenterFactory;

    private ActivityComponent activityComponent;
    private ProgressBar mProgressBar;
    private RecyclerView mCountriesRecyclerView;
    private CountriesRecyclerViewAdapter mCountriesRecyclerViewAdapter;
    private DisplayCountriesContract.View mView = new DisplayCountriesView();
    private DisplayCountriesContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_countries);

        getActivityComponent().inject(this);

        bindViews();

        mPresenter = mPresenterFactory.newInstance();
        mPresenter.attachView(mView);
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule())
                    .applicationComponent(DisplayListOfCountriesApplication.get(this).getComponent())
                    .build();
        }
        return activityComponent;
    }

    private void bindViews() {
        mProgressBar = findViewById(R.id.progressBar);
        mCountriesRecyclerView = findViewById(R.id.countriesRecyclerView);
        mCountriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class DisplayCountriesView implements DisplayCountriesContract.View {

        @Override
        public void populateList(List<Country> countries) {
            mCountriesRecyclerViewAdapter = new CountriesRecyclerViewAdapter(DisplayCountriesActivity.this, countries);
            mCountriesRecyclerView.setAdapter(mCountriesRecyclerViewAdapter);
            mCountriesRecyclerView.addItemDecoration(new DividerItemDecoration(DisplayCountriesActivity.this,
                    DividerItemDecoration.VERTICAL));
        }

        @Override
        public void registerToFavoriteClickEvents() {
            mCountriesRecyclerViewAdapter.getPositionClicks().subscribeWith(new DisposableObserver<Country>() {
                @Override
                public void onNext(Country clickedCountry) {
                    mPresenter.countryFavoriteButtonClicked(clickedCountry);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {}
            });
        }

        @Override
        public void showErrorMessage() {
            Toast.makeText(DisplayCountriesActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void showLoading() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideLoading() {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
