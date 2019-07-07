package id.arv.dechilent.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pixplicity.fontview.FontAppCompatTextView;

import net.crosp.libs.android.circletimeview.CircleTimeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;
import id.arv.dechilent.service.BroadcastService;

public class TimeControlActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_back)
    ImageView btn_back;
    @BindView(R.id.tv_title)
    FontAppCompatTextView tv_title;
    @BindView(R.id.img_setting)
    ImageView img_setting;
    @BindView(R.id.circle_timer_view)
    CircleTimeView circle_timer_view;
    @BindView(R.id.ll_start)
    LinearLayout ll_start;
    @BindView(R.id.btn_start)
    FontAppCompatTextView btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_control);
        ButterKnife.bind(this);
        setUI();
    }

    private void setUI() {
        tv_title.setText("Time Control");
        img_setting.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        ll_start.setOnClickListener(this);
        circle_timer_view.setCurrentTimeMode(CircleTimeView.MODE_MANUAL_SETUP);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.ll_start:
                if (btn_start.getText().toString().toLowerCase().trim().equals("mulai")) {
                    btn_start.setText("Berhenti");
                    BaseApp.timerValue = circle_timer_view.getCurrentTimeInSeconds() * 1000;
                    startService(new Intent(this, BroadcastService.class));
                    Log.i("countDown", "Started service");
                } else {
                    circle_timer_view.setCurrentTime(120, CircleTimeView.FORMAT_MINUTES_HOURS);
                    circle_timer_view.setCurrentTimeMode(CircleTimeView.MODE_MANUAL_SETUP);
                    btn_start.setText("Mulai");
                    BaseApp.timerValue = 0;
                    stopService(new Intent(this, BroadcastService.class));
                    Log.i("countDown", "Stopped service");
                }
                break;
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.i("countDown", "Registered broacast receiver");
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);

            if (millisUntilFinished > 0) {
                circle_timer_view.setCurrentTime(millisUntilFinished / 1000);
                circle_timer_view.setCurrentTimeMode(CircleTimeView.MODE_NORMAL);
                btn_start.setText("Berhenti");
                Log.i("countDown", "Countdown seconds remaining: " + millisUntilFinished / 1000);
            } else {
                circle_timer_view.setCurrentTime(120, CircleTimeView.FORMAT_MINUTES_HOURS);
                circle_timer_view.setCurrentTimeMode(CircleTimeView.MODE_MANUAL_SETUP);
                btn_start.setText("Mulai");
                BaseApp.timerValue = 0;
                stopService(new Intent(this, BroadcastService.class));
                Log.i("countDown", "Stopped service");
            }
        }
    }
}
