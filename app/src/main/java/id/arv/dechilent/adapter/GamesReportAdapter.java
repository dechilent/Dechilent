package id.arv.dechilent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.OrderBy;
import com.pixplicity.fontview.FontAppCompatTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;
import id.arv.dechilent.activity.RecommendationActivity;
import id.arv.dechilent.activity.games.MatematikaActivity;
import id.arv.dechilent.model.GamesModel;
import id.arv.dechilent.model.ReportModel;

import static id.arv.dechilent.BaseApp.mAuth;

public class GamesReportAdapter extends RecyclerView.Adapter<GamesReportAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<GamesModel> items;

    public GamesReportAdapter(ArrayList<GamesModel> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GamesModel item = items.get(position);
        loadAllScores(item.getName(), holder);
        holder.tv_game.setText(item.getName());
        holder.cv_row.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, RecommendationActivity.class);
            intent.putExtra("games", item.getName());
            mContext.startActivity(intent);
        });
    }

    int scores = 0;

    private void loadAllScores(String names, ViewHolder holder) {
        holder.pb_load.setVisibility(View.VISIBLE);
        holder.tv_score.setVisibility(View.GONE);

        BaseApp.db
                .collection("scores")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .whereEqualTo("name", names.toLowerCase())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    scores = 0;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String name = documentSnapshot.getData().get("name").toString();
                        String email = documentSnapshot.getData().get("email").toString();
                        int score = Integer.parseInt(documentSnapshot.getData().get("score").toString());
                        long created_at = Long.parseLong(documentSnapshot.getData().get("created_at").toString());
                        scores += score;
                    }

                    holder.pb_score.setProgress(scores);
                    holder.tv_score.setText(scores + "");
                    holder.pb_load.setVisibility(View.GONE);
                    holder.tv_score.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FontAppCompatTextView tv_game, tv_score;
        public ProgressBar pb_score;
        public CardView cv_row;
        public ProgressBar pb_load;

        public ViewHolder(View v) {
            super(v);
            pb_load = v.findViewById(R.id.pb_load);
            pb_score = v.findViewById(R.id.pb_score);
            tv_game = v.findViewById(R.id.tv_game);
            tv_score = v.findViewById(R.id.tv_score);
            cv_row = v.findViewById(R.id.cv_row);
        }
    }
}
