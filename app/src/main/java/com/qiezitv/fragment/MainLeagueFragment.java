package com.qiezitv.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.qiezitv.R;
import com.qiezitv.activity.LoginActivity;
import com.qiezitv.activity.MatchActivity;
import com.qiezitv.adapter.LeagueAdapter;
import com.qiezitv.common.http.AutoRefreshTokenCallback;
import com.qiezitv.common.http.RetrofitManager;
import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.http.provider.OneyuanServiceProvider;
import com.qiezitv.model.oneyuan.LeagueVO;
import com.qiezitv.model.page.Page;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class MainLeagueFragment extends BaseFragment {
    private static final String TAG = MainLeagueFragment.class.getSimpleName();

    private RefreshLayout refreshLayout;
    private LeagueAdapter leagueAdapter;
    private EditText etName;
    private AppCompatSpinner spinnerLeagueStatus;

    private List<LeagueVO> data = new ArrayList<>();

    private long currentPageNum = 1;
    private long totalPages = 1;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_main_league;
    }

    @Override
    protected void onBindFragment(View view) {
        refreshLayout = view.findViewById(R.id.refresh_layout);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        leagueAdapter = new LeagueAdapter(data);
        recyclerView.setAdapter(leagueAdapter);
        etName = view.findViewById(R.id.et_name);
        spinnerLeagueStatus = view.findViewById(R.id.spinner_league_status);
        Button btnRefresh = view.findViewById(R.id.btn_refresh);

        refreshLayout.setOnRefreshListener(refreshLayout -> {
            Log.d(TAG, "onRefresh");
            requestData(1, true);
        });

        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            Log.d(TAG, "onLoadMore");
            if (currentPageNum < totalPages) {
                requestData(++currentPageNum, false);
            } else {
                refreshLayout.finishLoadMore();
                showToast("无更多数据");
            }
        });

        leagueAdapter.setOnItemClickListener(position -> {
            Long id = data.get(position).getId();
            Log.d(TAG, "league id:" + id);
            if (id == null) {
                showToast("league id is null!!!");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putLong(MatchActivity.LEAGUE_ID_BUNDLE_KEY, id);
            readyGo(MatchActivity.class, bundle);
        });

        btnRefresh.setOnClickListener(v -> refreshLayout.autoRefresh());

        etName.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                refreshLayout.autoRefresh();
            }
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshLayout.autoRefresh();
    }

    @Override
    protected void lazyLoad() {

    }

    private void requestData(long pageNum, boolean isFresh) {
        closeSoftKeyboard();
        OneyuanServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(OneyuanServiceProvider.class);
        String name = TextUtils.isEmpty(etName.getText()) ? null : etName.getText().toString();
        String status = spinnerLeagueStatus.getSelectedItem().toString();
        switch (status) {
            case "未开始":
                status = "unopen";
                break;
            case "进行中":
                status = "live";
                break;
            case "已结束":
                status = "finish";
                break;
            default:
                status = null;
                break;
        }
        Call<ResponseEntity<Page<LeagueVO>>> response = request.getLeagueList(createQueryMap(pageNum, name, status));
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Page<LeagueVO>>>() {
            @Override
            public void onRefreshTokenFail() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("授权失效，重新登录");
                        readyGo(LoginActivity.class);
                        getActivity().finish();
                    });
                }
            }

            @Override
            public void onSuccess(ResponseEntity<Page<LeagueVO>> result) {
                if (isFresh) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
                totalPages = result.getData().getPages();
                currentPageNum = result.getData().getCurrent();
                if (isFresh) {
                    data = result.getData().getRecords();
                } else {
                    data.addAll(result.getData().getRecords());
                }
                leagueAdapter.setData(data);
                leagueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Page<LeagueVO>>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });

    }

    private Map<String, Object> createQueryMap(long pageNum, String name, String status) {
        Map<String, Object> queryMap = new ArrayMap<>();
        queryMap.put("pageSize", (long) 10);
        queryMap.put("pageNum", pageNum);
        queryMap.put("sortOrder", "desc");
        queryMap.put("sortField", "sortIndex");
        queryMap.put("leagueType", 4);
        if (name != null) {
            queryMap.put("name", name);
        }
        if (status != null) {
            queryMap.put("status", status);
        }
        return queryMap;
    }

    private void closeSoftKeyboard() {
        etName.clearFocus();
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
        }
    }
}
