package id.arv.dechilent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pixplicity.fontview.FontAppCompatTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;
import id.arv.dechilent.activity.games.MatematikaActivity;
import id.arv.dechilent.model.GamesModel;
import id.arv.dechilent.model.ReportModel;
import id.arv.dechilent.service.DateUtils;

import static id.arv.dechilent.BaseApp.mAuth;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<ReportModel> items;

    public ReportAdapter(ArrayList<ReportModel> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ReportModel item = items.get(position);
        holder.tv_game.setText(item.getName());
        holder.tv_tanggal.setText(DateUtils.formatDate(item.getCreated_at()));
        holder.tv_score.setText(item.getScore()+"");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FontAppCompatTextView tv_game, tv_tanggal, tv_score;

        public ViewHolder(View v) {
            super(v);
            tv_game = v.findViewById(R.id.tv_games);
            tv_tanggal = v.findViewById(R.id.tv_tanggal);
            tv_score = v.findViewById(R.id.tv_score);
        }
    }
}
