package com.example.android.displaylistofcountries;

import com.example.android.displaylistofcountries.api.CountriesResponse;
import com.example.android.displaylistofcountries.api.CountryDetail;
import com.example.android.displaylistofcountries.api.DisbursementOption;
import com.example.android.displaylistofcountries.data.DataManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Lakshmanan on 9/11/18.
 */
public class DisplayCountriesPresenterTest {

    private static final int PAGE_SIZE = 100;
    private static final String DISBURSEMENT_MODE_ACTIVE = "active";
    private static final String DISBURSEMENT_MODE_INACTIVE = "inactive";
    private DisplayCountriesPresenter mSubject;
    private DisplayCountriesPresenter.PresenterFactory mPresenterFactory;
    private DisplayCountriesContract.View mView;
    private ServiceRepo mServiceRepo;
    private DataManager mDataManager;
    private BehaviorSubject<CountriesResponse> mCountriesResponseBehaviorSubject;

    @Before
    public void setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                return Schedulers.trampoline();
            }
        });

        mServiceRepo = mock(ServiceRepo.class);
        mDataManager = mock(DataManager.class);
        mPresenterFactory = new DisplayCountriesPresenter.PresenterFactory(mServiceRepo, mDataManager);
        mSubject = mPresenterFactory.newInstance();
        mView = mock(DisplayCountriesContract.View.class);
        mCountriesResponseBehaviorSubject = BehaviorSubject.create();
        when(mServiceRepo.getCountriesList(PAGE_SIZE)).thenReturn(mCountriesResponseBehaviorSubject);
    }

    @After
    public void tearDown() {
        mCountriesResponseBehaviorSubject = null;
        Mockito.reset(mDataManager, mServiceRepo, mView);
        RxAndroidPlugins.reset();
    }

    @Test
    public void onAttachView_verifyGetCountriesListCalled() {
        mSubject.attachView(mView);

        verify(mServiceRepo).getCountriesList(PAGE_SIZE);
        assertTrue(mCountriesResponseBehaviorSubject.hasObservers());
    }

    @Test
    public void onGetCountriesList_verifyShowLoadingCalledOnView() {
        mSubject.attachView(mView);

        verify(mView).showLoading();
    }

    @Test
    public void onGetCountriesList_success_returnsNoItems_verifyNoCallsToView() {
        CountriesResponse countriesResponse = mock(CountriesResponse.class);
        when(countriesResponse.getItems()).thenReturn(null);
        mSubject.attachView(mView);

        mCountriesResponseBehaviorSubject.onNext(countriesResponse);
        verify(mView, never()).showErrorMessage();
        verify(mView, never()).populateList(ArgumentMatchers.<Country>anyList());
        verify(mView, never()).showErrorMessage();
    }

    @Test
    public void onGetCountriesList_success_returnsItems_verifyPopulateListCallToView() {
        CountriesResponse countriesResponse = mock(CountriesResponse.class);
        ArrayList<CountryDetail> countryDetailArrayList = mockCountriesDetail();
        when(countriesResponse.getItems()).thenReturn(countryDetailArrayList);
        mSubject.attachView(mView);

        mCountriesResponseBehaviorSubject.onNext(countriesResponse);

        ArgumentCaptor<List<Country>> countryArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(mView).populateList(countryArgumentCaptor.capture());
        List<Country> countries = countryArgumentCaptor.getValue();
        assertEquals(countries.size(), countryDetailArrayList.size());
    }

    @Test
    public void onGetCountriesList_success_returnsItems_verifyRegisterToFavoriteClickEventsCalled() {
        CountriesResponse countriesResponse = mock(CountriesResponse.class);
        ArrayList<CountryDetail> countryDetailArrayList = mockCountriesDetail();
        when(countriesResponse.getItems()).thenReturn(countryDetailArrayList);
        mSubject.attachView(mView);

        mCountriesResponseBehaviorSubject.onNext(countriesResponse);

        verify(mView).registerToFavoriteClickEvents();
    }

    @Test
    public void onGetCountriesList_success_returnsItemsAlsoWithNonActiveDisbursementCountries_verifyOnlyActiveDisbursementCountriesPassedToView() {
        CountriesResponse countriesResponse = mock(CountriesResponse.class);
        ArrayList<CountryDetail> countryDetailArrayListWithFewNonActiveDisbursementCountries = mockCountriesDetailWithFewNonActiveDisbursementCountries();
        when(countriesResponse.getItems()).thenReturn(countryDetailArrayListWithFewNonActiveDisbursementCountries);
        mSubject.attachView(mView);

        mCountriesResponseBehaviorSubject.onNext(countriesResponse);

        ArgumentCaptor<List<Country>> countryArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(mView).populateList(countryArgumentCaptor.capture());
        List<Country> countries = countryArgumentCaptor.getValue();
        assertEquals(countries.size(), 4);
    }

    @Test
    public void onGetCountriesList_complete_verifyHideLoadingCalledOnView() {
        BehaviorSubject<CountriesResponse> responseBehaviorSubject = BehaviorSubject.create();
        when(mServiceRepo.getCountriesList(PAGE_SIZE)).thenReturn(responseBehaviorSubject);

        CountriesResponse countriesResponse = mock(CountriesResponse.class);
        ArrayList<CountryDetail> countryDetailArrayList = mockCountriesDetail();
        when(countriesResponse.getItems()).thenReturn(countryDetailArrayList);
        mSubject.attachView(mView);

        responseBehaviorSubject.onNext(countriesResponse);
        responseBehaviorSubject.onComplete();

        verify(mView).hideLoading();
    }

    @Test
    public void onGetCountriesList_error_verifyShowErrorMessageCalledOnView() {
        mSubject.attachView(mView);

        mCountriesResponseBehaviorSubject.onError(new Throwable());

        verify(mView).showErrorMessage();
    }

    @Test
    public void onCountryFavoriteButtonClicked_alreadyFavoriteCountry_verifyCountryRemovedFromPreferenceUsingDataManager() {
        Country country = mock(Country.class);
        when(country.getIsFavorite()).thenReturn(true);
        when(country.getCountryCode()).thenReturn("US");
        when(mDataManager.getCountries()).thenReturn("US,UK,IN,CN");
        mSubject.countryFavoriteButtonClicked(country);

        verify(mDataManager).saveCountries("UK,IN,CN");
    }

    @Test
    public void onCountryFavoriteButtonClicked_unFavoriteCountry_verifyCountryAddedToPreferenceUsingDataManager() {
        Country country = mock(Country.class);
        when(country.getIsFavorite()).thenReturn(false);
        when(country.getCountryCode()).thenReturn("US");
        when(mDataManager.getCountries()).thenReturn("UK,IN,CN");
        mSubject.countryFavoriteButtonClicked(country);

        verify(mDataManager).saveCountries("UK,IN,CN,US");
    }

    //region Helper methods

    private ArrayList<CountryDetail> mockCountriesDetail() {
        ArrayList<CountryDetail> countryDetailArrayList = new ArrayList<>();
        countryDetailArrayList.add(provideMockCountryDetailWithActiveDisbursementCountry("US", "United States"));
        countryDetailArrayList.add(provideMockCountryDetailWithActiveDisbursementCountry("UK", "United Kingdom"));
        countryDetailArrayList.add(provideMockCountryDetailWithActiveDisbursementCountry("IN", "India"));
        countryDetailArrayList.add(provideMockCountryDetailWithActiveDisbursementCountry("CN", "China"));
        return countryDetailArrayList;
    }

    private ArrayList<CountryDetail> mockCountriesDetailWithFewNonActiveDisbursementCountries() {
        ArrayList<CountryDetail> countryDetailArrayList = new ArrayList<>();
        countryDetailArrayList.add(provideMockCountryDetailWithActiveDisbursementCountry("US", "United States"));
        countryDetailArrayList.add(provideMockCountryDetailWithActiveDisbursementCountry("UK", "United Kingdom"));
        countryDetailArrayList.add(provideMockCountryDetailWithActiveDisbursementCountry("IN", "India"));
        countryDetailArrayList.add(provideMockCountryDetailWithActiveDisbursementCountry("CN", "China"));
        countryDetailArrayList.add(provideMockCountryDetailWithNonActiveDisbursementCountry("SU", "SUDAN"));
        return countryDetailArrayList;
    }

    private CountryDetail provideMockCountryDetailWithActiveDisbursementCountry(String code, String name) {
        CountryDetail countryDetail = mock(CountryDetail.class);
        when(countryDetail.getCode()).thenReturn(code);
        when(countryDetail.getName()).thenReturn(name);
        ArrayList<DisbursementOption> disbursementOptionArrayList = provideMockWithActiveDisbursementOptionList();
        when(countryDetail.getDisbursementOptions()).thenReturn(disbursementOptionArrayList);
        return countryDetail;
    }

    private CountryDetail provideMockCountryDetailWithNonActiveDisbursementCountry(String code, String name) {
        CountryDetail countryDetail = mock(CountryDetail.class);
        when(countryDetail.getCode()).thenReturn(code);
        when(countryDetail.getName()).thenReturn(name);
        ArrayList<DisbursementOption> disbursementOptionArrayList = provideMockWithNonActiveDisbursementOptionList();
        when(countryDetail.getDisbursementOptions()).thenReturn(disbursementOptionArrayList);
        return countryDetail;
    }

    private Country provideMockCountry(String code, String name, boolean isFavorite) {
        Country country = mock(Country.class);
        when(country.getCountryCode()).thenReturn(code);
        when(country.getCountryName()).thenReturn(name);
        when(country.getIsFavorite()).thenReturn(isFavorite);
        return country;
    }

    private ArrayList<DisbursementOption> provideMockWithActiveDisbursementOptionList() {
        DisbursementOption disbursementOption = mock(DisbursementOption.class);
        when(disbursementOption.getMode()).thenReturn(DISBURSEMENT_MODE_ACTIVE);
        ArrayList<DisbursementOption> disbursementOptionArrayList = new ArrayList<>();
        disbursementOptionArrayList.add(disbursementOption);
        return disbursementOptionArrayList;
    }

    private ArrayList<DisbursementOption> provideMockWithNonActiveDisbursementOptionList() {
        DisbursementOption disbursementOption = mock(DisbursementOption.class);
        when(disbursementOption.getMode()).thenReturn(DISBURSEMENT_MODE_INACTIVE);
        ArrayList<DisbursementOption> disbursementOptionArrayList = new ArrayList<>();
        disbursementOptionArrayList.add(disbursementOption);
        return disbursementOptionArrayList;
    }

    //endregion
}