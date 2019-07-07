package id.arv.dechilent.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.pixplicity.fontview.FontAppCompatTextView;

import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;
import id.arv.dechilent.adapter.GamesReportAdapter;
import id.arv.dechilent.adapter.ReportAdapter;
import id.arv.dechilent.model.GamesModel;
import id.arv.dechilent.model.ReportModel;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_back)
    ImageView btn_back;
    @BindView(R.id.tv_title)
    FontAppCompatTextView tv_title;
    @BindView(R.id.img_setting)
    ImageView img_setting;
    @BindView(R.id.rv_games)
    RecyclerView rv_games;
    private ArrayList<GamesModel> reportModels = new ArrayList<>();
    private GamesReportAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        setUI();
    }

    private void setUI() {
        tv_title.setText("Report");
        img_setting.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_games.setLayoutManager(linearLayoutManager);
        mAdapter = new GamesReportAdapter(reportModels, this);
        rv_games.setAdapter(mAdapter);

        loadReport();
    }

    private void loadReport() {
        BaseApp.db
                .collection("games")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    reportModels.clear();
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
                        String category = documentSnapshot.getData().get("category").toString();
                        String games_id = documentSnapshot.getData().get("games_id").toString();
                        String image_url = documentSnapshot.getData().get("image_url").toString();
                        String name = documentSnapshot.getData().get("name").toString();
                        GamesModel games = new GamesModel(games_id, image_url, name, category);
                        reportModels.add(games);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }
}
