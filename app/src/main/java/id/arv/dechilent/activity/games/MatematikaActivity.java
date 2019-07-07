package id.arv.dechilent.activity.games;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.pixplicity.fontview.FontAppCompatTextView;

import java.util.ArrayList;
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

public class MatematikaActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.pb_count)
    ProgressBar pb_count;
    @BindView(R.id.tv_score)
    FontAppCompatTextView tv_score;
    @BindView(R.id.tv_time)
    FontAppCompatTextView tv_time;
    @BindView(R.id.tv_question)
    FontAppCompatTextView tv_question;
    @BindView(R.id.btn_pause)
    ImageView btn_pase;
    @BindView(R.id.anim_btn_1)
    LottieAnimationView anim_btn_1;
    @BindView(R.id.anim_btn_2)
    LottieAnimationView anim_btn_2;
    @BindView(R.id.star)
    LottieAnimationView star;
    @BindView(R.id.cv_pause)
    CardView cv_pause;
    @BindView(R.id.btn_menu)
    FontAppCompatTextView btn_menu;
    @BindView(R.id.mask_layout)
    RelativeLayout mask_layout;
    @BindView(R.id.btn_resume)
    ImageView btn_resume;
    @BindView(R.id.ll_dialog_text)
    FontAppCompatTextView ll_dialog_text;
    @BindView(R.id.btn_replay_games)
    ImageButton btn_replay_games;
    @BindView(R.id.btn_hint)
    ImageButton btn_hint;
    private int interval = 60;
    private int operator;
    private int min = 0;
    private int max = 8;
    private int positionOfAns;
    private int score = 0;
    private SharedPreferences prefs = null;
    private MediaPlayer mMediaPlayer;

    private ArrayList<FontAppCompatTextView> ansBtns;
    private static final int[] BUTTON_IDS = {
            R.id.btn_ans_1,
            R.id.btn_ans_2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matematika);
        ButterKnife.bind(this);
        hideNavbar();
        setUI();
    }

    private void setUI() {
        btn_resume.setTag("play");
        ansBtns = new ArrayList<>();
        star.setRepeatCount(INFINITE);
        btn_pase.setOnClickListener(this);
        btn_hint.setOnClickListener(this);
        btn_resume.setOnClickListener(this);
        btn_menu.setOnClickListener(this);
        btn_replay_games.setOnClickListener(this);

        for (int id : BUTTON_IDS) {
            FontAppCompatTextView ansBtn = findViewById(id);
            ansBtns.add(ansBtn);
        }

        generateQuestion();

        Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
        an.setFillAfter(true);
        pb_count.startAnimation(an);
        startTimer();

        for (final FontAppCompatTextView btn : ansBtns) {
            btn.setOnClickListener(v -> {
                if (ansBtns.get(positionOfAns) == btn) {
                    score += 5;
                    setScore(score);
                    showStar();
                } else {
                    generateQuestion();
                }
            });
        }
    }

    public void play(int rid) {
        stop();
        mMediaPlayer = MediaPlayer.create(MatematikaActivity.this, rid);
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            stop();
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
                generateQuestion();
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
                pb_count.setProgress((int) seconds);
                tv_time.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {
                tv_time.setText("STOP");
                setScoreShared();
            }
        }.start();
    }

    int answer;

    private void generateQuestion() {
        int intEq1;
        int intEq2;

        anim_btn_1.setRepeatCount(INFINITE);
        anim_btn_1.playAnimation();
        anim_btn_2.setRepeatCount(INFINITE);
        anim_btn_2.playAnimation();

        Random r = new Random();
        operator = r.nextInt(4);

        if (operator < 2) {
            answer = r.nextInt(max - min + 1) + min;
            setAnswers(answer);
        }

        switch (operator) {
            case 0:
                intEq1 = r.nextInt(answer - min + 1) + min;
                intEq2 = answer - intEq1;
                tv_question.setText(intEq1 + " + " + intEq2);
                break;
            case 1:
                intEq1 = r.nextInt(max - answer + 1) + answer;
                intEq2 = intEq1 - answer;
                tv_question.setText(intEq1 + " - " + intEq2);
                break;
            case 2:
                intEq1 = r.nextInt(max);
                intEq2 = r.nextInt(max);
                tv_question.setText(intEq1 + " X " + intEq2);
                setAnswers(intEq1 * intEq2);
                break;
            case 3:
                intEq1 = r.nextInt(max);
                intEq2 = r.nextInt(max - 1);
                try {
                    if (intEq1 % intEq2 == 0) {
                        tv_question.setText(intEq1 + " / " + intEq2);
                        setAnswers(intEq1 / intEq2);
                    } else {
                        generateQuestion();
                    }
                } catch (Exception ex) {
                    generateQuestion();
                }
                break;
        }
    }

    private void setAnswers(int answers) {
        Random r = new Random();
        positionOfAns = r.nextInt(1 + 1);
        int[] excluded = new int[4];
        excluded[0] = answers;

        int fakeAns = getRandomWithExclusion(r, min, max, excluded);
        excluded[1] = fakeAns;

        ansBtns.get(positionOfAns).setText("" + answers);

        if (positionOfAns == 0)
            ansBtns.get(1).setText("" + fakeAns);
        else
            ansBtns.get(0).setText("" + fakeAns);

    }

    private int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pause:
                mask_layout.setVisibility(View.VISIBLE);
                cv_pause.setVisibility(View.VISIBLE);
                countDownTimer.cancel();
                anim_btn_1.pauseAnimation();
                anim_btn_2.pauseAnimation();
                break;
            case R.id.btn_resume:
                if(btn_resume.getTag().equals("finish")) {
                    onBackPressed();
                } else {
                    mask_layout.setVisibility(View.GONE);
                    cv_pause.setVisibility(View.GONE);
                    anim_btn_1.playAnimation();
                    anim_btn_2.playAnimation();
                    interval = time;
                    startTimer();
                }
                break;
            case R.id.btn_menu:
                onBackPressed();
                break;
            case R.id.btn_hint:
                //btn_pase.performClick();
                dialogHint("Matematika",
                        "Hitung hasil pada setiap soal dan pilihlah jawaban yang paling benar");
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

    private void setScoreShared() {
        ll_dialog_text.setText("Score kamu: "+score);
        btn_pase.performClick();
        btn_resume.setTag("finish");
        btn_resume.setImageResource(R.drawable.ic_home_black_24dp);

        Map<String, Object> games = new HashMap<>();
        games.put("name", "matematika");
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
                .edit().putInt("matematika", score)
                .apply();
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
