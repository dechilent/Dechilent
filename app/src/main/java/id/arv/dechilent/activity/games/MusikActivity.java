package id.arv.dechilent.activity.games;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.pixplicity.fontview.FontAppCompatTextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;
import id.arv.dechilent.service.BroadcastService;

import static android.view.animation.Animation.INFINITE;
import static id.arv.dechilent.BaseApp.appName;
import static id.arv.dechilent.BaseApp.mAuth;

public class MusikActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_score)
    FontAppCompatTextView tv_score;
    @BindView(R.id.tv_time)
    FontAppCompatTextView tv_time;
    @BindView(R.id.btn_pause)
    ImageView btn_pause;
    @BindView(R.id.star)
    LottieAnimationView star;
    @BindView(R.id.cv_pause)
    CardView cv_pause;
    @BindView(R.id.btn_menu)
    FontAppCompatTextView btn_menu;
    @BindView(R.id.tone1)
    FontAppCompatTextView tone1;
    @BindView(R.id.tone2)
    FontAppCompatTextView tone2;
    @BindView(R.id.tone3)
    FontAppCompatTextView tone3;
    @BindView(R.id.tone4)
    FontAppCompatTextView tone4;
    @BindView(R.id.mask_layout)
    RelativeLayout mask_layout;
    @BindView(R.id.btn_resume)
    ImageView btn_resume;
    @BindView(R.id.ll_dialog_text)
    FontAppCompatTextView ll_dialog_text;
    @BindView(R.id.btn_replay)
    ImageButton btn_replay;
    @BindView(R.id.btn_replay_games)
    ImageButton btn_replay_games;
    @BindView(R.id.btn_hint)
    ImageButton btn_hint;
    private int interval = 60;
    private int score = 0;
    private SharedPreferences prefs = null;
    private int answer = 0;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musik);
        ButterKnife.bind(this);
        hideNavbar();
        setUI();
    }

    private void setUI() {
        btn_resume.setTag("play");
        tone1.setTag("0");
        tone2.setTag("1");
        tone3.setTag("2");
        tone4.setTag("3");
        star.setRepeatCount(INFINITE);
        btn_pause.setOnClickListener(this);
        btn_resume.setOnClickListener(this);
        btn_hint.setOnClickListener(this);
        btn_replay.setOnClickListener(this);
        btn_menu.setOnClickListener(this);
        btn_replay_games.setOnClickListener(this);
        tone1.setOnClickListener(this::toneClick);
        tone2.setOnClickListener(this::toneClick);
        tone3.setOnClickListener(this::toneClick);
        tone4.setOnClickListener(this::toneClick);

        startTimer();
        setUpGames();
    }

    public void toneClick(View v) {
        if (v.getTag().equals(answer + "")) {
            score += 5;
            setScore(score);
            showStar();
        }
    }

    private void setUpGames() {
        answer = new Random().nextInt(4);
        playTones(0);
    }

    int index = 0;
    private void playTones(int i) {
        if (answer == i) {
            play(R.raw.do_tone);
        } else {
            play(R.raw.la_tone);
        }
    }

    public void play(int rid) {
        stop();

        if (index == 0) {
            tone1.setAlpha(.9f);
            tone2.setAlpha(.7f);
            tone3.setAlpha(.7f);
            tone4.setAlpha(.7f);
        } else if (index == 1) {
            tone1.setAlpha(.7f);
            tone2.setAlpha(.9f);
            tone3.setAlpha(.7f);
            tone4.setAlpha(.7f);
        } else if (index == 2) {
            tone1.setAlpha(.7f);
            tone2.setAlpha(.7f);
            tone3.setAlpha(.9f);
            tone4.setAlpha(.7f);
        } else {
            tone1.setAlpha(.7f);
            tone2.setAlpha(.7f);
            tone3.setAlpha(.7f);
            tone4.setAlpha(.9f);
        }

        mMediaPlayer = MediaPlayer.create(MusikActivity.this, rid);
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            index++;

            if (index > 3) {
                stop();
                index = 0;
                tone1.setAlpha(.7f);
                tone2.setAlpha(.7f);
                tone3.setAlpha(.7f);
                tone4.setAlpha(.7f);
                btn_replay.setVisibility(View.VISIBLE);
            } else {
                playTones(index);
            }
        });
        mMediaPlayer.start();
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void showStar() {
        new CountDownTimer(1000, 950) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                star.setVisibility(View.VISIBLE);
                star.playAnimation();
                play(R.raw.kids_cheering);
            }

            @Override
            public void onFinish() {
                star.setVisibility(View.GONE);
                star.pauseAnimation();
                setUpGames();
                stop();
            }
        }.start();
    }

    private void setScore(int score) {
        tv_score.setText(score + "");
    }

    CountDownTimer countDownTimer;
    int time = 0;

    private void startTimer() {
        countDownTimer = new CountDownTimer(interval * 1000, 1000) {

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                time = (int) seconds;
                //pb_count.setProgress((int) seconds);
                tv_time.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {
                stop();
                tv_time.setText("STOP");
                setScoreShared();
            }
        }.start();
    }

    private void setScoreShared() {
        ll_dialog_text.setText("Score kamu: " + score);
        btn_pause.performClick();
        btn_resume.setTag("finish");
        btn_resume.setImageResource(R.drawable.ic_home_black_24dp);

        Map<String, Object> games = new HashMap<>();
        games.put("name", "musik");
        games.put("email", mAuth.getCurrentUser().getEmail());
        games.put("score", score);
        games.put("created_at", System.currentTimeMillis());

        BaseApp.db
                .collection("scores")
                .add(games)
                .addOnSuccessListener(documentReference -> {

                });

        prefs = getSharedPreferences(appName, MODE_PRIVATE);
        getSharedPreferences(appName, MODE_PRIVATE)
                .edit().putInt("musik", score)
                .apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pause:
                mask_layout.setVisibility(View.VISIBLE);
                cv_pause.setVisibility(View.VISIBLE);
                countDownTimer.cancel();
                break;
            case R.id.btn_resume:
                if (btn_resume.getTag().equals("finish")) {
                    onBackPressed();
                } else {
                    mask_layout.setVisibility(View.GONE);
                    cv_pause.setVisibility(View.GONE);
                    interval = time;
                    startTimer();
                }
                break;
            case R.id.btn_menu:
                onBackPressed();
                break;
            case R.id.btn_replay:
                playTones(0);
                break;
            case R.id.btn_hint:
                //btn_pause.performClick();
                dialogHint("Musik",
                        "Terdapat empat notasi nada, dengarkan baik-baik dan pilihlah satu notasi dengan nada yang berbeda");
                break;
            case R.id.btn_replay_games:
                score = 0;
                interval = 60;
                time = 0;
                setUI();
                break;
        }
    }

    private void dialogHint(String name, String hint) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_hint);
        FontAppCompatTextView tv_games_name = dialog.findViewById(R.id.tv_games_name);
        FontAppCompatTextView tv_desc_hint = dialog.findViewById(R.id.tv_desc_hint);
        FontAppCompatTextView btn_close = dialog.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(v -> {
            dialog.dismiss();
            btn_resume.performClick();
        });
        tv_games_name.setText(name);
        tv_desc_hint.setText(hint);
        dialog.show();
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

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i("countDown", "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, BroadcastService.class));
        Log.i("countDown", "Stopped service");
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            if (millisUntilFinished > 0) {

            } else {
                BaseApp.timerValue = 0;
                stopService(new Intent(this, BroadcastService.class));
                if (tv_time.getText().equals("00:00")) {
                    tv_time.setText("STOP");
                    setScoreShared();
                } else {
                    tv_time.setText("01:00");
                    //pb_count.setProgress(60);
                }
            }
        }
    }

    int currentApiVersion;
    public void hideNavbar() {
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
