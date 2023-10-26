package org.deafop.srhr_signlanguage.fragments;

import static org.deafop.srhr_signlanguage.utils.Constant.VIDEO_LIST_COMPACT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.activities.ActivityVideoDetail;
import org.deafop.srhr_signlanguage.activities.MainActivity;
import org.deafop.srhr_signlanguage.adapters.AdapterRecent;
import org.deafop.srhr_signlanguage.callbacks.CallbackListVideo;
import org.deafop.srhr_signlanguage.config.AppConfig;
import org.deafop.srhr_signlanguage.models.Video;
import org.deafop.srhr_signlanguage.rests.ApiInterface;
import org.deafop.srhr_signlanguage.rests.RestAdapter;
import org.deafop.srhr_signlanguage.utils.Constant;
import org.deafop.srhr_signlanguage.utils.EqualSpacingItemDecoration;
import org.deafop.srhr_signlanguage.utils.SharedPref;
import org.deafop.srhr_signlanguage.utils.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentRecent extends Fragment {

    View root_view, parent_view;
    private RecyclerView recyclerView;
    private AdapterRecent adapterRecent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackListVideo> callbackCall = null;
    SharedPref sharedPref;
    private ShimmerFrameLayout lyt_shimmer;
    private int post_total = 0;
    private int failed_page = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_recent, container, false);
        parent_view = getActivity().findViewById(R.id.lyt_content);

        sharedPref = new SharedPref(getActivity());
        sharedPref.setDefaultSortHome();

        setHasOptionsMenu(true);

        lyt_shimmer = root_view.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout = root_view.findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerView = root_view.findViewById(R.id.recyclerView);

        if (sharedPref.getVideoViewType() == VIDEO_LIST_COMPACT) {
            recyclerView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.spacing_small), 0, getResources().getDimensionPixelOffset(R.dimen.spacing_small));
        }

        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(0));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        adapterRecent = new AdapterRecent(getActivity(), recyclerView, new ArrayList<Video>());
        recyclerView.setAdapter(adapterRecent);

        // on item list clicked
        adapterRecent.setOnItemClickListener((v, obj, position) -> {

            Intent intent = new Intent(getActivity(), ActivityVideoDetail.class);
            intent.putExtra(Constant.EXTRA_OBJC, obj);
            startActivity(intent);

        });

        // detect when scroll reach bottom
        adapterRecent.setOnLoadMoreListener(current_page -> {
            if (post_total > adapterRecent.getItemCount() && current_page != 0) {
                int next_page = current_page + 1;
                requestAction(next_page);
            } else {
                adapterRecent.setLoaded();
            }
        });

        // on swipe list
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
            adapterRecent.resetListData();
            requestAction(1);
        });

        requestAction(1);
        initShimmerLayout();
        onSortButtonClickListener();

        return root_view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void displayApiResult(final List<Video> videos) {
        adapterRecent.insertData(videos);
        swipeProgress(false);
        if (videos.size() == 0) {
            showNoItemView(true);
        }
    }

    private void requestListPostApi(final int page_no) {

        ApiInterface apiInterface = RestAdapter.createAPI(sharedPref.getApiUrl());

        if (sharedPref.getCurrentSortHome() == 0) {
            callbackCall = apiInterface.getVideos(page_no, AppConfig.LOAD_MORE, Constant.MOST_POPULAR, AppConfig.REST_API_KEY);
        } else if (sharedPref.getCurrentSortHome() == 1) {
            callbackCall = apiInterface.getVideos(page_no, AppConfig.LOAD_MORE, Constant.ADDED_OLDEST, AppConfig.REST_API_KEY);
        } else if (sharedPref.getCurrentSortHome() == 2) {
            callbackCall = apiInterface.getVideos(page_no, AppConfig.LOAD_MORE, Constant.ADDED_NEWEST, AppConfig.REST_API_KEY);
        } else {
            callbackCall = apiInterface.getVideos(page_no, AppConfig.LOAD_MORE, Constant.ADDED_NEWEST, AppConfig.REST_API_KEY);
        }

        callbackCall.enqueue(new Callback<CallbackListVideo>() {
            @Override
            public void onResponse(Call<CallbackListVideo> call, Response<CallbackListVideo> response) {
                CallbackListVideo resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    post_total = resp.count_total;
                    displayApiResult(resp.posts);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<CallbackListVideo> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        adapterRecent.setLoaded();
        swipeProgress(false);
        if (Tools.isConnect(getActivity())) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            adapterRecent.setLoading();
        }
        new Handler().postDelayed(() -> requestListPostApi(page_no), Constant.DELAY_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
        lyt_shimmer.stopShimmer();
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = root_view.findViewById(R.id.lyt_failed_home);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        root_view.findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction(failed_page));
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_home);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.msg_no_item);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            lyt_shimmer.setVisibility(View.GONE);
            lyt_shimmer.stopShimmer();
            return;
        }
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(show);
            lyt_shimmer.setVisibility(View.VISIBLE);
            lyt_shimmer.startShimmer();
        });
    }

    private void initShimmerLayout() {
        View lyt_shimmer_default = root_view.findViewById(R.id.lyt_shimmer_default);
        View lyt_shimmer_compact = root_view.findViewById(R.id.lyt_shimmer_compact);
        if (sharedPref.getVideoViewType() == VIDEO_LIST_COMPACT) {
            lyt_shimmer_default.setVisibility(View.GONE);
            lyt_shimmer_compact.setVisibility(View.VISIBLE);
        } else {
            lyt_shimmer_default.setVisibility(View.VISIBLE);
            lyt_shimmer_compact.setVisibility(View.GONE);
        }
    }

    private void onSortButtonClickListener() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.btn_sort.setOnClickListener(view -> {
                String[] items = getResources().getStringArray(R.array.dialog_single_choice_array);
                int itemSelected = sharedPref.getCurrentSortHome();
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_sort)
                        .setSingleChoiceItems(items, itemSelected, (dialogInterface, position) -> {
                            if (callbackCall != null && callbackCall.isExecuted())
                                callbackCall.cancel();
                            adapterRecent.resetListData();
                            requestAction(1);
                            sharedPref.updateSortHome(position);
                            dialogInterface.dismiss();
                        })
                        .show();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
