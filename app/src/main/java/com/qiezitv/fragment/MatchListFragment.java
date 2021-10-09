package com.qiezitv.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.qiezitv.R;
import com.qiezitv.activity.GameVideoActivity;
import com.qiezitv.adapter.MatchListAdapter;
import com.qiezitv.model.oneyuan.MatchVO;
import com.qiezitv.view.WaitingDialog;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MatchListFragment extends BaseFragment {
    private static final String TAG = MatchListFragment.class.getSimpleName();

    private TextView tvNotDataHint;
    private ListView lvMatchList;
    private MatchListAdapter adapter;
    private WaitingDialog waitingDialog;

    private List<MatchVO> matchList;
    private DialogPlus dialog;
    private TextView dialogTvName;
    private TextView dialogTvTime;
    private TextView dialogBtnLive;

    private MatchVO match;

    private Class<?> chooseClazz;

    public String round;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_match_list;
    }

    @Override
    protected void onBindFragment(View view) {
        tvNotDataHint = view.findViewById(R.id.tv_not_data_hint);
        lvMatchList = view.findViewById(R.id.lv_match_list);
    }

    @Override
    protected void lazyLoad() {
    }

    public void updateView() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            if (matchList == null || matchList.isEmpty()) {
                lvMatchList.setVisibility(View.GONE);
                tvNotDataHint.setVisibility(View.VISIBLE);
            } else {
                lvMatchList.setVisibility(View.VISIBLE);
                tvNotDataHint.setVisibility(View.GONE);

                if (adapter == null) {
                    adapter = new MatchListAdapter(getContext(), matchList);
                    lvMatchList.setAdapter(adapter);
                    lvMatchList.setOnItemClickListener((parent, view, position, id) -> {
                        match = matchList.get(position);
                        Log.d(TAG, "matchVo.id:" + match.getId());
                        showDialog();
                    });
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void setMatchList(List<MatchVO> matchList) {
        this.matchList = matchList;
    }

    private void showDialog() {
        chooseClazz = null;
        if (dialog == null) {
            dialog = DialogPlus.newDialog(getContext())
                    .setContentHolder(new ViewHolder(R.layout.dialog_match))
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnDismissListener(dialog -> {
                        if (chooseClazz != null) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Match", match);
                            if (chooseClazz == GameVideoActivity.class) {
                                readyGo(GameVideoActivity.class, bundle);
                            }
                        }
                    })
                    .setOnClickListener((dialog, view) -> {
                        dialog.dismiss();
                        if (view instanceof Button) {
                            switch (view.getId()) {
                                case R.id.btn_live:
                                    chooseClazz = GameVideoActivity.class;
                                    break;
                            }
                        }
                    })
                    .setExpanded(false)
                    .create();
            dialogTvName = (TextView) dialog.findViewById(R.id.tv_name);
            dialogTvTime = (TextView) dialog.findViewById(R.id.tv_time);
            dialogBtnLive = (Button) dialog.findViewById(R.id.btn_live);
        }
        if (!match.isAllowStreaming()) {
            dialogBtnLive.setVisibility(View.INVISIBLE);
        } else {
            dialogBtnLive.setVisibility(View.VISIBLE);
        }
        dialogTvName.setText(match.getName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        dialogTvTime.setText(simpleDateFormat.format(match.getStartTime()));
        dialog.show();
    }

    private void showWaitingDialog() {
        if (waitingDialog == null) {
            waitingDialog = new WaitingDialog(getActivity(), "");
            waitingDialog.setCanceledOnTouchOutside(false);
        }
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> waitingDialog.show());
    }

    private void dismissWaitingDialog() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            if (waitingDialog != null && !getActivity().isDestroyed()) {
                waitingDialog.dismiss();
            }
        });
    }

}
