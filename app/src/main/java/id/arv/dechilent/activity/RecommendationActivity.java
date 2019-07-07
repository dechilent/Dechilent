package id.arv.dechilent.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pixplicity.fontview.FontAppCompatTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;
import id.arv.dechilent.adapter.GamesReportAdapter;
import id.arv.dechilent.adapter.ReportAdapter;
import id.arv.dechilent.model.GamesModel;
import id.arv.dechilent.model.ReportModel;

import static id.arv.dechilent.BaseApp.mAuth;

public class RecommendationActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_back)
    ImageView btn_back;
    @BindView(R.id.img_setting)
    ImageView btn_setting;
    @BindView(R.id.tv_title)
    FontAppCompatTextView tv_title;
    @BindView(R.id.tv_rekomendasi)
    FontAppCompatTextView tv_rekomendasi;
    @BindView(R.id.rv_games)
    RecyclerView rv_games;
    @BindView(R.id.chart_record)
    LineChart chart_record;
    private ArrayList<Entry> scoreList = new ArrayList<>();
    private ArrayList<ReportModel> reportModels = new ArrayList<>();
    private ReportAdapter mAdapter;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        ButterKnife.bind(this);
        getExtrasIntent();
        setUI();
    }

    private void getExtrasIntent() {
        name = getIntent().getStringExtra("games");
    }

    private void setUI() {
        tv_title.setText("Recommendation");
        btn_setting.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_games.setLayoutManager(linearLayoutManager);
        mAdapter = new ReportAdapter(reportModels, this);
        rv_games.setAdapter(mAdapter);

        loadReport();
    }

    private void loadGeneralChart() {
        try {
            LineDataSet set1;

            XAxis xAxis = chart_record.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            //xAxis.setValueFormatter(new DateValueFormatter());

            if (chart_record.getData() != null &&
                    chart_record.getData().getDataSetCount() > 0) {
                try {
                    set1 = (LineDataSet) chart_record.getData().getDataSetByIndex(0);
                    set1.setValues(scoreList);
                    chart_record.getData().notifyDataChanged();
                    chart_record.invalidate();
                } catch (Exception ex) {
                }
            } else {
                try {
                    set1 = new LineDataSet(scoreList, "Kemajuan Nilai");
                    set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    set1.setCubicIntensity(0.2f);
                    set1.setDrawIcons(false);
                    set1.setDrawCircleHole(false);
                    set1.setValueTextSize(9f);
                    set1.setDrawFilled(true);
                    set1.enableDashedLine(10f, 5f, 0f);
                    set1.enableDashedHighlightLine(10f, 5f, 0f);
                    if (Utils.getSDKInt() >= 18) {
                        Drawable drawable = ContextCompat.getDrawable(RecommendationActivity.this, R.drawable.bg_gradient_green);
                        set1.setFillDrawable(drawable);
                    } else {
                        set1.setFillColor(Color.GREEN);
                    }
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);
                    LineData data = new LineData(dataSets);
                    chart_record.setData(data);
                    chart_record.invalidate();
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
        }
    }

    int step = 1;

    private void loadReport() {
        reportModels.clear();
        scoreList.clear();
        step = 1;

        BaseApp.db
                .collection("scores")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .whereEqualTo("name", name.toLowerCase())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    reportModels.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String email = documentSnapshot.getData().get("email").toString();
                        int score = Integer.parseInt(documentSnapshot.getData().get("score").toString());
                        long created_at = Long.parseLong(documentSnapshot.getData().get("created_at").toString());
                        Calendar cl = Calendar.getInstance();
                        cl.setTimeInMillis(created_at);

                        ReportModel games = new ReportModel(name, email, score, created_at);
                        reportModels.add(games);

                        scoreList.add(new Entry(step, score));
                        step++;
                        mAdapter.notifyDataSetChanged();
                        tv_rekomendasi.setText(Html.fromHtml("<b>1. Mengajak anak berbicara atau berdialog</b></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">Ajaklah anak berbicara dan berdialog, alasannya ialah dengan mengajak anak berdialog dan berbicara merupakan langkah awal melatih anak berbicara, yang merupakan unsur penting dalam berkomunikasi dan keterampilan sosial.</span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><b>2. Membacakan Cerita</b></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">Membacakan dongeng selain menarik bagi anak juga dapa merangsang kemampuan kecerdasan linguistik anak. Dengan membacakan dongeng juga akan melibatkan anak dalam aktivitas-aktivitas linguistik baik secara pribadi maupun tidak langsung.</span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <b><span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">3.&nbsp;</span><span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">Merangkai cerita</span></b></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">Berikan anak potongan-potongan gambar dan biarkan anak mengungkapkan apa yang ada dipikirannya perihal jalannya dongeng pada gambar tersebut. Ini menyenangkan alasannya ialah anak akan terlibat&nbsp;</span><span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">secara pribadi dalam dialog dan diskusi perihal gambarnya.</span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <b><span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">4.&nbsp;</span><span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">Bermain aksara dan angka</span></b></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">Seiring dengan pemahaman anak akan aksara dan angka serta penggunaannya, yaitu dengan bermain kartu bergambar berikut kosakatanya. Lakukan kegiatan dengan bermain menggunakan aksara dan angka tersebut.</span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><b>5. Berdiskusi</b></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">Memberikan kesempatan pada anak untuk memberikan pendapatnya, menghargai kata-katanya, membiarkan anak membicarakan perasaan, selain mengasah perkembangan bahasa juga melatih anak untuk mengendalikan emosi.</span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><b>6. Bermain Peran</b></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\"><br /></span></div> <div style=\"text-align: justify;\"> <span style=\"font-family: &quot;arial&quot; , &quot;helvetica&quot; , sans-serif;\">Dalam bermain tugas anak akan melaksanakan dialog atau berkomunikasi dengan sobat mainnya, hal ini dapat membantu membuatkan kemampuan anak dalam penggunaan kosakata menjadi suatu kalimat dan berkomunikasi dengan orang lain.</span>"));
                    }

                    if (scoreList.size() > 0)
                        loadGeneralChart();
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
