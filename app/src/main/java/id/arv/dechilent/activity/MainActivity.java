package id.arv.dechilent.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;
import id.arv.dechilent.activity.games.BahasaActivity;
import id.arv.dechilent.activity.games.InterpersonalActivity;
import id.arv.dechilent.activity.games.MatematikaActivity;
import id.arv.dechilent.activity.games.MusikActivity;
import id.arv.dechilent.activity.games.VisualActivity;
import id.arv.dechilent.service.BroadcastService;

import static id.arv.dechilent.BaseApp.appName;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rl_setting)
    RelativeLayout img_setting;
    @BindView(R.id.rl_exit)
    RelativeLayout btn_exit;
    @BindView(R.id.btn_musical)
    ImageView btn_musical;
    @BindView(R.id.btn_visual)
    ImageView btn_visual;
    @BindView(R.id.btn_interpersonal)
    ImageView btn_interpersonal;
    @BindView(R.id.btn_math)
    ImageView btn_math;
    @BindView(R.id.btn_lang)
    ImageView btn_lang;
    private FirebaseUser currentUser;
    private String email = "";
    private String id = "";
    private String user_id = "";
    private boolean isParent = false;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        hideNavbar();
        currentUser = BaseApp.mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            getExtrasIntent();
            setUI();
        }
    }

    private void getExtrasIntent() {
        email = getIntent().getStringExtra("email") == null ? currentUser.getEmail() : getIntent().getStringExtra("email");
        id = getIntent().getStringExtra("id") == null ? currentUser.getUid() : getIntent().getStringExtra("id");

        checkAndSaveUser();
    }

    private void setUI() {
        img_setting.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_interpersonal.setOnClickListener(v -> intentGames("interpersonal"));
        btn_lang.setOnClickListener(v -> intentGames("lang"));
        btn_math.setOnClickListener(v -> intentGames("matematika"));
        btn_visual.setOnClickListener(v -> intentGames("visual"));
        btn_musical.setOnClickListener(v -> intentGames("musical"));
        prefs = getSharedPreferences(appName, MODE_PRIVATE);

        if (prefs.getBoolean("isParent", true)) {
            isParent = true;
        }
    }

    private void intentGames(String menu) {
        if (menu.equals("matematika")) {
            Intent intent = new Intent(MainActivity.this, MatematikaActivity.class);
            startActivity(intent);
        }
        if (menu.equals("musical")) {
            Intent intent = new Intent(MainActivity.this, MusikActivity.class);
            startActivity(intent);
        }
        if (menu.equals("lang")) {
            Intent intent = new Intent(MainActivity.this, BahasaActivity.class);
            startActivity(intent);
        }
        if (menu.equals("visual")) {
            Intent intent = new Intent(MainActivity.this, VisualActivity.class);
            startActivity(intent);
        }
        if (menu.equals("interpersonal")) {
            Intent intent = new Intent(MainActivity.this, InterpersonalActivity.class);
            startActivity(intent);
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
                Toast.makeText(MainActivity.this, "Habis Waktu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkAndSaveUser() {
        Map<String, Object> users = new HashMap<>();
        users.put("user_id", "");
        users.put("pin", "");
        users.put("email", email);
        users.put("role", "parent");
        users.put("uid", id);

        BaseApp.db
                .collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        if (queryDocumentSnapshots.size() <= 0) {
                            BaseApp.db
                                    .collection("user")
                                    .add(users)
                                    .addOnSuccessListener(documentReference -> {
                                        user_id = documentReference.getId();
                                        BaseApp.db
                                                .collection("user")
                                                .document(documentReference.getId())
                                                .update("user_id", documentReference.getId())
                                                .addOnSuccessListener(aVoid -> {
                                                    Intent intent = new Intent(MainActivity.this, SecurityCodeActivity.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("id", id);
                                                    intent.putExtra("user_id", documentReference.getId());
                                                    startActivity(intent);
                                                    finish();
                                                });
                                    });
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                user_id = documentSnapshot.getId();
                                if (documentSnapshot.getData().get("pin") == null ||
                                        documentSnapshot.getData().get("pin").toString().isEmpty()) {
                                    Intent intent = new Intent(MainActivity.this, SecurityCodeActivity.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("id", id);
                                    intent.putExtra("user_id", documentSnapshot.getId());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
                break;
            case R.id.rl_exit:
                finish();
                System.exit(0);
                break;
        }
    }
}
