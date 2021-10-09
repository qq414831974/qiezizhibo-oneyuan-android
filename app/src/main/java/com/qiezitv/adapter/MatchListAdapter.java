package com.qiezitv.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiezitv.R;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.common.LayoutUtil;
import com.qiezitv.pojo.OneyuanEvent;
import com.qiezitv.model.oneyuan.MatchVO;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiezitv.pojo.MatchAgainstVO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MatchListAdapter extends BaseAdapter {

    private List<MatchVO> mData;
    private LayoutInflater mInflater;
    private Context mContext;

    public MatchListAdapter(Context context, List<MatchVO> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //如果view未被实例化过，缓存池中没有对应的缓存
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_match, null);
            viewHolder = new ViewHolder(convertView);

            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 取出bean对象
        MatchVO match = mData.get(position);

        viewHolder.tvName.setText(match.getName());
        LinearLayout againstTeamsContainer = viewHolder.llAgainstTeams;
        againstTeamsContainer.removeAllViews();
        if (match.getAgainstTeams() != null && match.getAgainstTeams().size() > 0) {
            for (Integer key : match.getAgainstTeams().keySet()) {
                MatchAgainstVO againstTeam = match.getAgainstTeams().get(key);
                LinearLayout againstTeamLayout = new LinearLayout(mContext);
                LinearLayout.LayoutParams layoutParam_againstTeam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                againstTeamLayout.setLayoutParams(layoutParam_againstTeam);
                againstTeamLayout.setGravity(Gravity.CENTER);
                if (againstTeam != null && againstTeam.getHostTeam() != null) {
                    String name = againstTeam.getHostTeam().getName();
                    if (againstTeam.getHostTeam().getShortName() != null) {
                        name = againstTeam.getHostTeam().getShortName();
                    }
                    //主队名
                    TextView tv_hostTeamName = new TextView(mContext);
                    LinearLayout.LayoutParams layoutParam_hostTeamName = new LinearLayout.LayoutParams(LayoutUtil.dip2px(mContext, 80), ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParam_hostTeamName.rightMargin = LayoutUtil.dip2px(mContext, 10);
                    tv_hostTeamName.setLayoutParams(layoutParam_hostTeamName);
                    tv_hostTeamName.setGravity(Gravity.CENTER);
                    tv_hostTeamName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tv_hostTeamName.setText(name);
                    againstTeamLayout.addView(tv_hostTeamName);
                    //主队头像
                    ImageView iv_hostTeamHeadImg = new ImageView(mContext);
                    LinearLayout.LayoutParams layout_hostTeamHeadImg = new LinearLayout.LayoutParams(LayoutUtil.dip2px(mContext, 30), LayoutUtil.dip2px(mContext, 30));
                    layout_hostTeamHeadImg.rightMargin = LayoutUtil.dip2px(mContext, 10);
                    iv_hostTeamHeadImg.setLayoutParams(layout_hostTeamHeadImg);
                    iv_hostTeamHeadImg.setImageResource(R.drawable.ic_result_ball);
                    ImageLoader.getInstance().displayImage(againstTeam.getHostTeam().getHeadImg(), iv_hostTeamHeadImg, ImageLoaderUtil.getOptions());
                    againstTeamLayout.addView(iv_hostTeamHeadImg);
                }
                if (match.getStatus() != null && match.getStatus().getScore() != null) {
                    String score = match.getStatus().getScore().get(key);
                    TextView tv_score = new TextView(mContext);
                    LinearLayout.LayoutParams layout_score = new LinearLayout.LayoutParams(LayoutUtil.dip2px(mContext, 50), ViewGroup.LayoutParams.WRAP_CONTENT);
                    layout_score.rightMargin = LayoutUtil.dip2px(mContext, 10);
                    tv_score.setLayoutParams(layout_score);
                    tv_score.setGravity(Gravity.CENTER);
                    tv_score.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tv_score.setText(score);
                    againstTeamLayout.addView(tv_score);
                }
                if (againstTeam != null && againstTeam.getGuestTeam() != null) {
                    String name = againstTeam.getGuestTeam().getName();
                    if (againstTeam.getGuestTeam().getShortName() != null) {
                        name = againstTeam.getGuestTeam().getShortName();
                    }
                    //客队头像
                    ImageView iv_guestTeamHeadImg = new ImageView(mContext);
                    LinearLayout.LayoutParams layout_guestTeamHeadImg = new LinearLayout.LayoutParams(LayoutUtil.dip2px(mContext, 30), LayoutUtil.dip2px(mContext, 30));
                    layout_guestTeamHeadImg.leftMargin = LayoutUtil.dip2px(mContext, 10);
                    iv_guestTeamHeadImg.setLayoutParams(layout_guestTeamHeadImg);
                    iv_guestTeamHeadImg.setImageResource(R.drawable.ic_result_ball);
                    ImageLoader.getInstance().displayImage(againstTeam.getGuestTeam().getHeadImg(), iv_guestTeamHeadImg, ImageLoaderUtil.getOptions());
                    againstTeamLayout.addView(iv_guestTeamHeadImg);
                    //客队名
                    TextView tv_guestTeamName = new TextView(mContext);
                    LinearLayout.LayoutParams layoutParam_guestTeamName = new LinearLayout.LayoutParams(LayoutUtil.dip2px(mContext, 80), ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParam_guestTeamName.leftMargin = LayoutUtil.dip2px(mContext, 10);
                    tv_guestTeamName.setLayoutParams(layoutParam_guestTeamName);
                    tv_guestTeamName.setGravity(Gravity.CENTER);
                    tv_guestTeamName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tv_guestTeamName.setText(name);
                    againstTeamLayout.addView(tv_guestTeamName);
                }
                againstTeamsContainer.addView(againstTeamLayout);
            }
        } else {
            LinearLayout matchNameLayout = new LinearLayout(mContext);
            TextView matchName = new TextView(mContext);
            LinearLayout.LayoutParams nameLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            matchName.setLayoutParams(nameLayout);
            matchName.setGravity(Gravity.CENTER);
            matchName.setTextSize(14);
            matchName.setText(match.getName());
            matchNameLayout.addView(matchName);
            againstTeamsContainer.addView(matchNameLayout);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        viewHolder.tvTime.setText(simpleDateFormat.format(match.getStartTime()));
        viewHolder.tvLocation.setText(match.getPlace());
        if (match.getStatus() != null) {
            String statusString = "";
            String status = OneyuanEvent.translateStatus(match.getStatus().getStatus());
            statusString = statusString + status;
            if (match.getAgainstTeams() != null && match.getAgainstTeams().size() > 1) {
                statusString = statusString + "  对阵" + match.getStatus().getAgainstIndex();
            }
            statusString = statusString + "  第" + match.getStatus().getSection() + "小节";
            viewHolder.tvStatus.setText(statusString);
        }
        return convertView;
    }

    public List<MatchVO> getData() {
        return mData;
    }

    public void setData(List<MatchVO> mData) {
        this.mData = mData;
    }

    static class ViewHolder {
        TextView tvName, tvTime, tvLocation, tvStatus;
        ImageView ivHostTeamImg, ivGuestTeamImg;
        LinearLayout llAgainstTeams;

        public ViewHolder(View view) {
            tvName = view.findViewById(R.id.tv_name);
            tvTime = view.findViewById(R.id.tv_time);
            tvLocation = view.findViewById(R.id.tv_location);
            tvStatus = view.findViewById(R.id.tv_status);
            ivHostTeamImg = view.findViewById(R.id.iv_host_team_img);
            ivGuestTeamImg = view.findViewById(R.id.iv_guest_team_img);
            llAgainstTeams = view.findViewById(R.id.ll_against_teams);
        }
    }
}
