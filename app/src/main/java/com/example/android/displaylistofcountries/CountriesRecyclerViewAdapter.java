package com.example.android.displaylistofcountries;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Lakshmanan on 9/8/18.
 */

public class CountriesRecyclerViewAdapter extends RecyclerView.Adapter<CountriesRecyclerViewAdapter.CustomViewHolder> {
    private List<Country> countryList;
    private Context mContext;
    private final String placeholder = "%s";
    private final String imageUrl = "https://www.countryflags.io/"+placeholder+"/flat/64.png";
    private final PublishSubject<Country> onClickSubject = PublishSubject.create();

    public CountriesRecyclerViewAdapter(Context context, List<Country> countryList) {
        this.countryList = countryList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_list_row, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder customViewHolder, int i) {
        final Country country = countryList.get(i);
        if (StringUtils.isNotEmpty(country.getCountryCode())) {
            //Render image using Picasso library
                Picasso.with(mContext).load(StringUtils.replace(imageUrl, placeholder, country.getCountryCode())).fit().centerCrop()
                        .error(R.drawable.ic_error_icon)
                        .placeholder(R.drawable.ic_spinner)
                        .into(customViewHolder.flagImageView);
        }

        customViewHolder.countryNameText.setText(country.getCountryName());
        if(country.getIsFavorite()) {
            customViewHolder.favoriteIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_favourite_star_filled));
        } else {
            customViewHolder.favoriteIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_favourite_star_hollow));
        }

        customViewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(country.getIsFavorite()) {
                    customViewHolder.favoriteIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_favourite_star_hollow));
                } else {
                    customViewHolder.favoriteIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_favourite_star_filled));
                }
                onClickSubject.onNext(country);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != countryList ? countryList.size() : 0);
    }

    public Observable<Country> getPositionClicks() {
        return onClickSubject;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView flagImageView;
        private TextView countryNameText;
        private ImageButton favoriteIcon;

        CustomViewHolder(View view) {
            super(view);
            this.flagImageView = view.findViewById(R.id.flagImageView);
            this.countryNameText = view.findViewById(R.id.countryNameText);
            this.favoriteIcon = view.findViewById(R.id.favoriteIcon);
        }
    }
}