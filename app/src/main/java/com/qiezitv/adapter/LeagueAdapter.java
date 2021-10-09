package com.qiezitv.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiezitv.R;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.model.oneyuan.LeagueVO;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.ViewHolder> {

    private List<LeagueVO> data;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    private OnItemClickListener listener;

    public LeagueAdapter(List<LeagueVO> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_league, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LeagueVO leagueVo = data.get(position);
        String leagueName = leagueVo.getName();
        if(leagueVo.getShortName() != null){
            leagueName = leagueVo.getShortName();
        }
        holder.tvName.setText(leagueName);
        holder.tvCity.setText(leagueVo.getCity() != null ? leagueVo.getCity().trim() : "");
        String timeStr = simpleDateFormat.format(leagueVo.getDateBegin()) + " - " + simpleDateFormat.format(leagueVo.getDateEnd());
        holder.tvTime.setText(timeStr);
        ImageLoader.getInstance().displayImage(leagueVo.getHeadImg(), holder.ivLeague, ImageLoaderUtil.getOptions());
        long now = System.currentTimeMillis();
        if (leagueVo.getDateBegin().getTime() > now) {
            holder.tvStatus.setText("未开始");
        } else if (leagueVo.getDateEnd().getTime() > now) {
            holder.tvStatus.setText("进行中");
        } else {
            holder.tvStatus.setText("已结束");
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<LeagueVO> getData() {
        return data;
    }

    public void setData(List<LeagueVO> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView tvName, tvCity, tvStatus, tvTime;
        ImageView ivLeague;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvName = itemView.findViewById(R.id.tv_name);
            tvCity = itemView.findViewById(R.id.tv_city);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivLeague = itemView.findViewById(R.id.iv_league);
        }
    }
}
