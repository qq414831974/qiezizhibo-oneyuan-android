package com.qiezitv.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qiezitv.R;
import com.qiezitv.fragment.BaseFragment;
import com.qiezitv.fragment.MainFindFragment;
import com.qiezitv.fragment.MainLeagueFragment;
import com.qiezitv.fragment.MainMyFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private ImageView ivHome;
    private TextView tvHome;
    private ImageView ivFind;
    private TextView tvFind;
    private ImageView ivMy;
    private TextView tvMy;

    private BaseFragment[] tabsFragment;
    private List<Fragment> mTabs;
    private FragmentPagerAdapter pagerAdapter;

    private long exitTime = 0;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_home:
                    viewPager.setCurrentItem(0, true);
                    break;
                case R.id.layout_find:
                    viewPager.setCurrentItem(1, true);
                    break;
                case R.id.layout_my:
                    viewPager.setCurrentItem(2, true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initUI() {
        viewPager = findViewById(R.id.view_pager_main);
        ivHome = findViewById(R.id.iv_home);
        tvHome = findViewById(R.id.tv_home);
        ivFind = findViewById(R.id.iv_find);
        tvFind = findViewById(R.id.tv_find);
        ivMy = findViewById(R.id.iv_my);
        tvMy = findViewById(R.id.tv_my);

        findViewById(R.id.layout_home).setOnClickListener(clickListener);
        findViewById(R.id.layout_find).setOnClickListener(clickListener);
        findViewById(R.id.layout_my).setOnClickListener(clickListener);

        //init fragment
        tabsFragment = new BaseFragment[3];
        tabsFragment[0] = new MainLeagueFragment();
        tabsFragment[1] = new MainFindFragment();
        tabsFragment[2] = new MainMyFragment();

        mTabs = new ArrayList<>();
        mTabs.add(tabsFragment[0]);
        mTabs.add(tabsFragment[1]);
        mTabs.add(tabsFragment[2]);

        //init adapter
        pagerAdapter = new FragmentPagerAdapter(mFManager) {
            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }
        };

        //set viewPager
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setCurrentItem(0);
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            resetBottomView();
            int clickColor = ContextCompat.getColor(getBaseContext(), R.color.base_title_background_color);
            switch (position) {
                case 0:
                    ivHome.setImageResource(R.drawable.icon_btmbar_home);
                    tvHome.setTextColor(clickColor);
                    break;
                case 1:
                    ivFind.setImageResource(R.drawable.icon_btmbar_find);
                    tvFind.setTextColor(clickColor);
                    break;
                case 2:
                    ivMy.setImageResource(R.drawable.icon_btmbar_me);
                    tvMy.setTextColor(clickColor);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 重写返回按钮
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重设底部tab栏
     */
    private void resetBottomView() {
        int defaultColor = this.getResources().getColor(R.color.activity_main_bottom_text_color);
        ivHome.setImageResource(R.drawable.icon_btmbar_home_be);
        tvHome.setTextColor(defaultColor);
        ivFind.setImageResource(R.drawable.icon_btmbar_find_be);
        tvFind.setTextColor(defaultColor);
        ivMy.setImageResource(R.drawable.icon_btmbar_me_be);
        tvMy.setTextColor(defaultColor);
    }

}
