package com.qiezitv.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiezitv.R;
import com.qiezitv.common.FinishActivityManager;
import com.qiezitv.common.StringUtil;
import com.qiezitv.common.http.AutoRefreshTokenCallback;
import com.qiezitv.common.http.RetrofitManager;
import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.fragment.MatchListFragment;
import com.qiezitv.http.provider.OneyuanServiceProvider;
import com.qiezitv.pojo.LeagueRound;
import com.qiezitv.model.oneyuan.LeagueVO;
import com.qiezitv.model.oneyuan.MatchVO;
import com.qiezitv.model.page.Page;
import com.qiezitv.view.WaitingDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class MatchActivity extends BaseActivity {
    private static final String TAG = MatchActivity.class.getSimpleName();
    public static final String LEAGUE_ID_BUNDLE_KEY = "league_id";

    private ViewPager viewPager;
    private LinearLayout llBottomTab;
    private HorizontalScrollView scrollView;
    private FragmentPagerAdapter pagerAdapter;
    private WaitingDialog waitingDialog;

    private List<MatchListFragment> tabsFragmentList = new ArrayList<>();
    private List<TextView> bottomTabsTextViewList = new ArrayList<>();

    private List<String> roundsList = new ArrayList<>();
    private List<MatchVO> matchVOList = new ArrayList<>();

    private long leagueId;
    private LeagueVO leagueVo;
    private boolean isFirstQuery = true;
    private int textViewWidth = 240;
    private String currentRound;

    private int lastPosition = 0;

    private View.OnClickListener onClickListener = v -> {
        int index = (int) v.getTag();
        viewPager.setCurrentItem(index);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        leagueId = bundle.getLong(LEAGUE_ID_BUNDLE_KEY);

        init();
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager_match);
        llBottomTab = findViewById(R.id.ll_bottom_tab);
        scrollView = findViewById(R.id.scroll_view);

        pagerAdapter = new FragmentPagerAdapter(mFManager) {
            @Override
            public Fragment getItem(int position) {
                return tabsFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return tabsFragmentList.size();
            }
        };

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                resetBottomTab();
                TextView textView = bottomTabsTextViewList.get(position);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.rgb(54, 156, 246));
                if (lastPosition < position && position > 2) {
                    scrollToPosition(textViewWidth * (position - 1), 0);
                } else if (lastPosition > position && position >= 1) {
                    scrollToPosition(textViewWidth * (position - 1), 0);
                }
                lastPosition = position;
                queryMatchList(textView.getText().toString());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryLeagueDetail();
    }

    private void queryLeagueDetail() {
        showWaitingDialog();
        OneyuanServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(OneyuanServiceProvider.class);
        Call<ResponseEntity<LeagueVO>> response = request.getLeagueById(leagueId);
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<LeagueVO>>() {
            @Override
            public void onRefreshTokenFail() {
                dismissWaitingDialog();
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<LeagueVO> result) {
                leagueVo = result.getData();
                dismissWaitingDialog();
                if (isFirstQuery) {
                    roundsList = getRoundString(result.getData().getRound());
                    currentRound = getCurrentRound();
                    createBottomTabAndTabFragment();
                } else {
//                    if (roundsList.size() != result.getData().getRound().getRounds().size()) {
//                        showToast("数据发生变化，请重新进入");
//                        finish();
//                    } else {
                    currentRound = getCurrentRound();
                    queryMatchList(currentRound);
//                    }
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<LeagueVO>> response, @Nullable Throwable t) {
                dismissWaitingDialog();
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
                finish();
            }
        });
    }

    private void queryMatchList(String round) {
        showWaitingDialog();
        OneyuanServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(OneyuanServiceProvider.class);
        Map<String, Object> queryMap = new ArrayMap<>();
        queryMap.put("pageSize", 1000);
        queryMap.put("pageNum", 1);
        queryMap.put("sortOrder", "desc");
        queryMap.put("sortField", "startTime");
        queryMap.put("leagueId", leagueId);
        queryMap.put("round", round);
        Call<ResponseEntity<Page<MatchVO>>> response = request.getMatchList(queryMap);
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Page<MatchVO>>>() {

            @Override
            public void onRefreshTokenFail() {
                dismissWaitingDialog();
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Page<MatchVO>> result) {
                dismissWaitingDialog();
                matchVOList = result.getData().getRecords();
                updateMatchFragment(round);
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Page<MatchVO>>> response, @Nullable Throwable t) {
                dismissWaitingDialog();
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
                finish();
            }
        });
    }

    private void updateMatchFragment(String round) {
        List<MatchVO> dataList = matchVOList;
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        for (MatchListFragment matchListFragment : tabsFragmentList) {
            if (matchListFragment.round.equalsIgnoreCase(round)) {
                matchListFragment.setMatchList(dataList);
                matchListFragment.updateView();
            }
        }
//        Map<String, List<MatchVo>> matchVoListMap = new HashMap<>();
//        for (MatchVo vo : matchVoList) {
//            List<MatchVo> list = matchVoListMap.get(vo.getRound());
//            if (list == null) {
//                list = new ArrayList<>();
//                list.add(vo);
//                matchVoListMap.put(vo.getRound(), list);
//            } else {
//                list.add(vo);
//            }
//        }
//
//        int size = roundsList.size();
//        for (int i = 0; i < size; i++) {
//            MatchListFragment fragment = tabsFragmentList.get(i);
//            List<MatchVo> dataList = matchVoListMap.get(roundsList.get(i));
//            if (dataList == null) {
//                dataList = new ArrayList<>();
//            }
//            fragment.setMatchVoList(dataList);
//            fragment.updateView();
//        }
    }

    private void createBottomTabAndTabFragment() {
        isFirstQuery = false;
        runOnUiThread(() -> {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(textViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            int textViewBgColor = ContextCompat.getColor(getBaseContext(), R.color.activity_main_bottom_text_color);

            int index = 0;
            int currentRoundIndex = 0;
            for (String rounds : roundsList) {
                if (rounds.equalsIgnoreCase(currentRound)) {
                    currentRoundIndex = index;
                }
                TextView textView = new TextView(MatchActivity.this);
                textView.setPadding(3, 0, 3, 0);
                textView.setText(rounds);
                textView.setTextSize(14);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(textViewBgColor);
                textView.setTag(index++);
                textView.setOnClickListener(onClickListener);
                bottomTabsTextViewList.add(textView);
                llBottomTab.addView(textView, layoutParams);

                MatchListFragment matchListFragment = new MatchListFragment();
                matchListFragment.round = rounds;
                tabsFragmentList.add(matchListFragment);
            }

            viewPager.setAdapter(pagerAdapter);
            viewPager.setOffscreenPageLimit(tabsFragmentList.size() - 1);
            viewPager.setCurrentItem(currentRoundIndex);

            TextView textView = bottomTabsTextViewList.get(currentRoundIndex);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.rgb(54, 156, 246));
            if (currentRound == null) {
                currentRound = getCurrentRound();
            }
            queryMatchList(currentRound);
        });
    }

    private void resetBottomTab() {
        int textViewBgColor = ContextCompat.getColor(getBaseContext(), R.color.activity_main_bottom_text_color);
        for (TextView textView : bottomTabsTextViewList) {
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundColor(textViewBgColor);
        }
    }

    private void showWaitingDialog() {
        if (waitingDialog == null) {
            waitingDialog = new WaitingDialog(this, "");
            waitingDialog.setCanceledOnTouchOutside(false);
        }
        runOnUiThread(() -> waitingDialog.show());
    }

    private void dismissWaitingDialog() {
        runOnUiThread(() -> {
            if (waitingDialog != null && !isDestroyed()) {
                waitingDialog.dismiss();
            }
        });
    }

    private void scrollToPosition(int x, int y) {
        ObjectAnimator xTranslate = ObjectAnimator.ofInt(scrollView, "scrollX", x);
        ObjectAnimator yTranslate = ObjectAnimator.ofInt(scrollView, "scrollY", y);

        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(500L);
        animators.playTogether(xTranslate, yTranslate);
        animators.start();
    }

    private void gotoLoginActivity() {
        runOnUiThread(() -> {
            showToast("授权过期，请重新登录");
            readyGo(LoginActivity.class);
            finish();
            FinishActivityManager.getManager().finishActivity(MainActivity.class);
        });
    }

    private String getCurrentRound() {
        String round = leagueVo.getCurrentRound();
        if (round == null) {
            if (leagueVo.getRound() != null && leagueVo.getRound().getRounds() != null && leagueVo.getRound().getRounds().size() > 0) {
                return leagueVo.getRound().getRounds().get(0);
            } else {
                return "第一轮";
            }
        }
        return round;
    }

    public List<String> getRoundString(LeagueRound rounds) {
        List<String> roundList = new ArrayList<>();
        boolean hasOpen = false;
        boolean hasClose = false;
        for (String round : rounds.getRounds()) {
            if (round.startsWith("z-")) {
                int num = Integer.parseInt(round.split("-")[1]);
                for (int i = 1; i <= num; i++) {
                    roundList.add("第" + StringUtil.getChinesNum(i) + "轮");
                }
            } else if (round.startsWith("x-")) {
                int num = Integer.parseInt(round.split("-")[1]);
                for (int i = 1; i <= num; i++) {
                    roundList.add("小组赛第" + StringUtil.getChinesNum(i) + "轮");
                }
            } else if (round.startsWith("t-")) {
                int num = Integer.parseInt(round.split("-")[1]);
                for (int i = 1; i <= num; i++) {
                    roundList.add("淘汰赛第" + StringUtil.getChinesNum(i) + "轮");
                }
            } else if (round.startsWith("j-")) {
//                int num = Integer.parseInt(round.split("-")[1]);
                roundList.add("决赛");
            } else if (round.equalsIgnoreCase("open")) {
                hasOpen = true;
            } else if (round.equalsIgnoreCase("close")) {
                hasClose = true;
            } else {
                roundList.add(round);
            }
        }
        if (hasOpen) {
            roundList.add(0, "开幕式");
        }
        if (hasClose) {
            roundList.add(roundList.size(), "闭幕式");
        }
        return roundList;
    }
}
