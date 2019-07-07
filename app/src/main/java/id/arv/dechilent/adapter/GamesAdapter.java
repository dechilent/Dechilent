package id.arv.dechilent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pixplicity.fontview.FontAppCompatTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.arv.dechilent.R;
import id.arv.dechilent.activity.games.MatematikaActivity;
import id.arv.dechilent.model.GamesModel;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<GamesModel> items;

    public GamesAdapter(ArrayList<GamesModel> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_games, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GamesModel item = items.get(position);
        holder.tvNama.setText(item.getName());
        holder.tvCategory.setText(item.getCategory().toUpperCase());
        Picasso.get().load(item.getImage_url()).into(holder.img_cover);
        holder.rl_games.setOnClickListener(v -> {
            Intent intent = null;
            if(item.getCategory().equals("matematika")) {
                intent = new Intent(mContext, MatematikaActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FontAppCompatTextView tvNama, tvCategory;
        public ImageView img_cover;
        public RelativeLayout rl_games;

        public ViewHolder(View v) {
            super(v);
            tvNama = v.findViewById(R.id.tv_games);
            tvCategory = v.findViewById(R.id.tv_category);
            img_cover = v.findViewById(R.id.img_cover);
            rl_games = v.findViewById(R.id.rl_games);
        }
    }
}
