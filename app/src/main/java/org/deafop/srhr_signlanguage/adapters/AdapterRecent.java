package org.deafop.srhr_signlanguage.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.multidex.BuildConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.config.AppConfig;
import org.deafop.srhr_signlanguage.databases.DatabaseHandlerFavorite;
import org.deafop.srhr_signlanguage.models.Video;
import org.deafop.srhr_signlanguage.utils.AdsPref;
import org.deafop.srhr_signlanguage.utils.Constant;
import org.deafop.srhr_signlanguage.utils.NativeTemplateStyle;
import org.deafop.srhr_signlanguage.utils.SharedPref;
import org.deafop.srhr_signlanguage.utils.TemplateView;
import org.deafop.srhr_signlanguage.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterRecent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_AD = 2;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Video> items;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private Video pos;
    private CharSequence charSequence = null;
    private DatabaseHandlerFavorite databaseHandler;

    private StartAppNativeAd startAppNativeAd;
    private NativeAdDetails nativeAdDetails = null;

    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterRecent(Context context, RecyclerView view, List<Video> items) {
        this.items = items;
        this.context = context;
        lastItemViewDetector(view);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public TextView video_title;
        public TextView video_duration;
        public TextView total_views;
        public TextView date_time;
        public LinearLayout lyt_view;
        public LinearLayout lyt_date;
        public ImageView video_thumbnail;
        public MaterialRippleLayout lyt_parent;
        public ImageButton overflow;

        public OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            video_title = v.findViewById(R.id.video_title);
            video_duration = v.findViewById(R.id.video_duration);
            date_time = v.findViewById(R.id.date_time);
            total_views = v.findViewById(R.id.total_views);
            lyt_view = v.findViewById(R.id.lyt_view_count);
            lyt_date = v.findViewById(R.id.lyt_date);
            video_thumbnail = v.findViewById(R.id.video_thumbnail);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            overflow = v.findViewById(R.id.overflow);

        }

    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public TextView video_title;
        public TextView video_duration;
        public TextView total_views;
        public TextView date_time;
        public LinearLayout lyt_view;
        public LinearLayout lyt_date;
        public ImageView video_thumbnail;
        public MaterialRippleLayout lyt_parent;
        public ImageButton overflow;

        TemplateView admob_native_ad_container;
        MediaView admob_media_view;

        private NativeAd nativeAd;
        private NativeAdLayout fan_native_ad_container;
        private LinearLayout nativeAdView;

        View startapp_native_ad_container;
        ImageView startapp_native_image;
        TextView startapp_native_title;
        TextView startapp_native_description;
        Button startapp_native_button;

        public AdViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            video_title = v.findViewById(R.id.video_title);
            video_duration = v.findViewById(R.id.video_duration);
            date_time = v.findViewById(R.id.date_time);
            total_views = v.findViewById(R.id.total_views);
            lyt_view = v.findViewById(R.id.lyt_view_count);
            lyt_date = v.findViewById(R.id.lyt_date);
            video_thumbnail = v.findViewById(R.id.video_thumbnail);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            overflow = v.findViewById(R.id.overflow);

            //admob native ad
            admob_native_ad_container = v.findViewById(R.id.admob_native_ad_container);
            admob_media_view = v.findViewById(R.id.media_view);

            //fan native ad
            fan_native_ad_container = v.findViewById(R.id.fan_native_ad_container);

            //startapp native ad
            startapp_native_ad_container = v.findViewById(R.id.startapp_native_ad_container);
            startapp_native_image = v.findViewById(R.id.startapp_native_image);
            startapp_native_title = v.findViewById(R.id.startapp_native_title);
            startapp_native_description = v.findViewById(R.id.startapp_native_description);
            startapp_native_button = v.findViewById(R.id.startapp_native_button);
            startapp_native_button.setOnClickListener(v1 -> itemView.performClick());

        }

        private void bindNativeAd() {

            final SharedPref sharedPref = new SharedPref(context);
            final AdsPref adsPref = new AdsPref(context);

            if (adsPref.getAdStatus().equals(Constant.AD_STATUS_ON)) {
                switch (adsPref.getAdType()) {
                    case Constant.ADMOB:
                        AdLoader adLoader = new AdLoader.Builder(context, adsPref.getAdMobNativeId())
                                .forNativeAd(nativeAd -> {
                                    if (sharedPref.getIsDarkTheme()) {
                                        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorBackgroundDark));
                                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                        admob_native_ad_container.setStyles(styles);
                                    } else {
                                        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorBackgroundLight));
                                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                        admob_native_ad_container.setStyles(styles);
                                    }
                                    admob_media_view.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                    admob_native_ad_container.setNativeAd(nativeAd);
                                }).withAdListener(new AdListener() {
                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                        admob_native_ad_container.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                        admob_native_ad_container.setVisibility(View.GONE);
                                    }
                                })
                                .build();
                        adLoader.loadAd(Tools.getAdRequest((Activity) context));
                        break;
                    case Constant.FAN:
                        if (BuildConfig.DEBUG) {
                            nativeAd = new NativeAd(context, "IMG_16_9_APP_INSTALL#" + adsPref.getFanNativeUnitId());
                        } else {
                            nativeAd = new NativeAd(context, adsPref.getFanNativeUnitId());
                        }
                        NativeAdListener nativeAdListener = new NativeAdListener() {
                            @Override
                            public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onError(com.facebook.ads.Ad ad, AdError adError) {

                            }

                            @Override
                            public void onAdLoaded(com.facebook.ads.Ad ad) {
                                // Race condition, load() called again before last ad was displayed
                                fan_native_ad_container.setVisibility(View.VISIBLE);
                                if (nativeAd == null || nativeAd != ad) {
                                    return;
                                }
                                // Inflate Native Ad into Container
                                //inflateAd(nativeAd);
                                nativeAd.unregisterView();
                                // Add the Ad view into the ad container.
                                LayoutInflater inflater = LayoutInflater.from(context);
                                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.

                                if (sharedPref.getVideoViewType() == Constant.VIDEO_LIST_COMPACT) {
                                    nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_small_template, fan_native_ad_container, false);
                                } else {
                                    nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_medium_template, fan_native_ad_container, false);
                                }
                                fan_native_ad_container.addView(nativeAdView);

                                // Add the AdOptionsView
                                LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
                                AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, fan_native_ad_container);
                                adChoicesContainer.removeAllViews();
                                adChoicesContainer.addView(adOptionsView, 0);

                                // Create native UI using the ad metadata.
                                TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                                com.facebook.ads.MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
                                TextView nativeAdSocialContext = nativeAdView.findViewById(R.id.native_ad_social_context);
                                TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                                TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
                                Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);
                                LinearLayout ad_unit = nativeAdView.findViewById(R.id.fan_unit);

                                // Set the Text.
                                nativeAdTitle.setText(nativeAd.getAdvertiserName());
                                nativeAdBody.setText(nativeAd.getAdBodyText());
                                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                                nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                                sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                                // Create a list of clickable views
                                List<View> clickableViews = new ArrayList<>();
                                clickableViews.add(nativeAdTitle);
                                clickableViews.add(ad_unit);
                                clickableViews.add(nativeAdCallToAction);

                                // Register the Title and CTA button to listen for clicks.
                                nativeAd.registerViewForInteraction(nativeAdView, nativeAdMedia, clickableViews);
                            }

                            @Override
                            public void onAdClicked(com.facebook.ads.Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            }
                        };

                        NativeAd.NativeLoadAdConfig loadAdConfig = nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build();
                        nativeAd.loadAd(loadAdConfig);
                        break;
                    case Constant.STARTAPP:
                        startAppNativeAd = new StartAppNativeAd(context);
                        NativeAdPreferences nativePrefs = new NativeAdPreferences()
                                .setAdsNumber(1)
                                .setAutoBitmapDownload(true)
                                .setPrimaryImageSize(Constant.STARTAPP_IMAGE_MEDIUM);
                        AdEventListener adListener = new AdEventListener() {
                            @Override
                            public void onReceiveAd(Ad arg0) {
                                ArrayList<NativeAdDetails> nativeAdsList = startAppNativeAd.getNativeAds();
                                if (nativeAdsList.size() > 0) {
                                    nativeAdDetails = nativeAdsList.get(0);
                                }
                                if (nativeAdDetails != null) {
                                    startapp_native_image.setImageBitmap(nativeAdDetails.getImageBitmap());
                                    startapp_native_title.setText(nativeAdDetails.getTitle());
                                    startapp_native_description.setText(nativeAdDetails.getDescription());
                                    startapp_native_button.setText(nativeAdDetails.isApp() ? "Install" : "Open");
                                    nativeAdDetails.registerViewForInteraction(itemView);
                                }
                                startapp_native_ad_container.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailedToReceiveAd(Ad arg0) {
                                startapp_native_ad_container.setVisibility(View.GONE);
                            }
                        };
                        startAppNativeAd.loadAd(nativePrefs, adListener);
                        break;
                }
            }

        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.load_more);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            SharedPref sharedPref = new SharedPref(context);
            if (sharedPref.getVideoViewType() == Constant.VIDEO_LIST_COMPACT) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_compact, parent, false);
                vh = new OriginalViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_default, parent, false);
                vh = new OriginalViewHolder(v);
            }
        } else if (viewType == VIEW_AD) {
            SharedPref sharedPref = new SharedPref(context);
            if (sharedPref.getVideoViewType() == Constant.VIDEO_LIST_COMPACT) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_compact_native, parent, false);
                vh = new AdViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_default_native, parent, false);
                vh = new AdViewHolder(v);
            }
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        SharedPref sharedPref = new SharedPref(context);
        if (holder instanceof OriginalViewHolder) {
            final Video p = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.category_name.setText(p.category_name);
            vItem.video_title.setText(p.video_title);
            vItem.video_duration.setText(p.video_duration);
            if (AppConfig.ENABLE_VIEW_COUNT) {
                vItem.total_views.setText(Tools.withSuffix(p.total_views) + " " + context.getResources().getString(R.string.views_count));
            } else {
                vItem.lyt_view.setVisibility(View.GONE);
            }

            if (AppConfig.ENABLE_DATE_DISPLAY && AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
                PrettyTime prettyTime = new PrettyTime();
                long timeAgo = Tools.timeStringtoMilis(p.date_time);
                vItem.date_time.setText(prettyTime.format(new Date(timeAgo)));
            } else if (AppConfig.ENABLE_DATE_DISPLAY && !AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
                vItem.date_time.setText(Tools.getFormatedDateSimple(p.date_time));
            } else {
                vItem.lyt_date.setVisibility(View.GONE);
            }

            if (p.video_type != null && p.video_type.equals("youtube")) {
                if (sharedPref.getVideoViewType() == Constant.VIDEO_LIST_COMPACT) {
                    Picasso.get()
                            .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK_MQ)
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(vItem.video_thumbnail);
                } else {
                    Picasso.get()
                            .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK_HQ)
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(vItem.video_thumbnail);
                }
            } else {
                Picasso.get()
                        .load(sharedPref.getApiUrl() + "/upload/" + p.video_thumbnail)
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(vItem.video_thumbnail);
            }

            vItem.lyt_parent.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, p, position);
                }
            });

            vItem.overflow.setOnClickListener(view -> {
                pos = items.get(position);
                showPopupMenu(vItem.overflow);
            });

        } else if (holder instanceof AdViewHolder) {
            final Video p = items.get(position);
            final AdViewHolder vItem = (AdViewHolder) holder;

            if (vItem.admob_native_ad_container.getVisibility() == View.VISIBLE ||
                    vItem.fan_native_ad_container.getVisibility() == View.VISIBLE ||
                    vItem.startapp_native_ad_container.getVisibility() == View.VISIBLE) {
                Log.d("LAYOUT_VISIBILITY", "layout is visible");
            } else {
                vItem.bindNativeAd();
                Log.d("LAYOUT_VISIBILITY", "layout is not visible");
            }

            vItem.category_name.setText(p.category_name);
            vItem.video_title.setText(p.video_title);
            vItem.video_duration.setText(p.video_duration);
            if (AppConfig.ENABLE_VIEW_COUNT) {
                vItem.total_views.setText(Tools.withSuffix(p.total_views) + " " + context.getResources().getString(R.string.views_count));
            } else {
                vItem.lyt_view.setVisibility(View.GONE);
            }

            if (AppConfig.ENABLE_DATE_DISPLAY && AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
                PrettyTime prettyTime = new PrettyTime();
                long timeAgo = Tools.timeStringtoMilis(p.date_time);
                vItem.date_time.setText(prettyTime.format(new Date(timeAgo)));
            } else if (AppConfig.ENABLE_DATE_DISPLAY && !AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
                vItem.date_time.setText(Tools.getFormatedDateSimple(p.date_time));
            } else {
                vItem.lyt_date.setVisibility(View.GONE);
            }

            if (p.video_type != null && p.video_type.equals("youtube")) {
                if (sharedPref.getVideoViewType() == Constant.VIDEO_LIST_COMPACT) {
                    Picasso.get()
                            .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK_MQ)
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(vItem.video_thumbnail);
                } else {
                    Picasso.get()
                            .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK_HQ)
                            .placeholder(R.drawable.ic_thumbnail)
                            .into(vItem.video_thumbnail);
                }
            } else {
                Picasso.get()
                        .load(sharedPref.getApiUrl() + "/upload/" + p.video_thumbnail)
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(vItem.video_thumbnail);
            }

            vItem.lyt_parent.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, p, position);
                }
            });

            vItem.overflow.setOnClickListener(view -> {
                pos = items.get(position);
                showPopupMenu(vItem.overflow);
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) != null) {
            final AdsPref adsPref = new AdsPref(context);
            int LIMIT_NATIVE_AD = (Constant.MAX_NUMBER_OF_NATIVE_AD_DISPLAYED * adsPref.getNativeAdInterval()) + adsPref.getNativeAdIndex();
            for (int i = adsPref.getNativeAdIndex(); i < LIMIT_NATIVE_AD; i += adsPref.getNativeAdInterval()) {
                if (position == i) {
                    return VIEW_AD;
                }
            }
            return VIEW_ITEM;
        } else {
            return VIEW_PROG;
        }
    }

    public void insertData(List<Video> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / AppConfig.LOAD_MORE;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();

        databaseHandler = new DatabaseHandlerFavorite(context);
        List<Video> data = databaseHandler.getFavRow(pos.vid);
        if (data.size() == 0) {
            popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.favorite_add);
            charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
        } else {
            if (data.get(0).getVid().equals(pos.vid)) {
                popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.favorite_remove);
                charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
            }
        }
    }

    public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.menu_context_favorite:
                    if (charSequence.equals(context.getString(R.string.favorite_add))) {
                        databaseHandler.AddtoFavorite(new Video(
                                pos.category_name,
                                pos.vid,
                                pos.video_title,
                                pos.video_url,
                                pos.video_id,
                                pos.video_thumbnail,
                                pos.video_duration,
                                pos.video_description,
                                pos.video_type,
                                pos.total_views,
                                pos.date_time
                        ));
                        Toast.makeText(context, context.getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();

                    } else if (charSequence.equals(context.getString(R.string.favorite_remove))) {
                        databaseHandler.RemoveFav(new Video(pos.vid));
                        Toast.makeText(context, context.getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                    }
                    return true;

                case R.id.menu_context_share:

                    String share_title = android.text.Html.fromHtml(pos.video_title).toString();
                    String share_content = android.text.Html.fromHtml(context.getResources().getString(R.string.share_text)).toString();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" + share_content + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                    return true;

                default:
            }
            return false;
        }
    }

}