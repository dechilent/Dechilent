package id.arv.dechilent.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mukesh.OtpView;
import com.pixplicity.fontview.FontAppCompatTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;

import static id.arv.dechilent.BaseApp.appName;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.sw_mode)
    Switch sw_mode;
    @BindView(R.id.tv_mode)
    FontAppCompatTextView tv_mode;
    @BindView(R.id.ll_time)
    LinearLayout ll_time;
    @BindView(R.id.ll_report)
    LinearLayout ll_report;
    @BindView(R.id.ll_logout)
    LinearLayout ll_logout;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    private SharedPreferences prefs = null;
    private String user_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        getExtrasIntent();
        setUI();
    }

    private void getExtrasIntent() {
        user_id = getIntent().getStringExtra("user_id");
    }

    private void setUI() {
        ll_logout.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        sw_mode.setOnCheckedChangeListener(this);

        prefs = getSharedPreferences(appName, MODE_PRIVATE);
        if (prefs.getBoolean("isParent", false)) {
            sw_mode.setChecked(false);
            tv_mode.setText("Mode: Children");
            ll_report.setVisibility(View.GONE);
            ll_time.setVisibility(View.GONE);
        } else {
            sw_mode.setChecked(true);
            tv_mode.setText("Mode: Parent");
            ll_time.setOnClickListener(this);
            ll_report.setOnClickListener(this);
            ll_time.setVisibility(View.VISIBLE);
            ll_report.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rl_back:
                onBackPressed();
                break;
            case R.id.ll_report:
                intent = new Intent(SettingActivity.this, ReportActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_time:
                intent = new Intent(SettingActivity.this, TimeControlActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_logout:
                BaseApp.mAuth.signOut();
                intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            dialogConfirmPIN();
        } else {
            getSharedPreferences(appName, MODE_PRIVATE)
                    .edit().putBoolean("isParent", true)
                    .commit();
            tv_mode.setText("Mode: Children");
            ll_time.setVisibility(View.GONE);
            ll_report.setVisibility(View.GONE);
        }
    }

    private void dialogConfirmPIN() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirm_pin);
        OtpView mPin = dialog.findViewById(R.id.otp_view);
        LinearLayout bSubmit = dialog.findViewById(R.id.email_sign_in_button);
        FontAppCompatTextView tSubmit = dialog.findViewById(R.id.btn_login);
        ProgressBar pbLoad = dialog.findViewById(R.id.pbUpload);

        bSubmit.setOnClickListener(v -> {
            if (mPin.getText().toString().isEmpty()) {
                mPin.setError("Masukkan PIN anda");
                return;
            }

            tSubmit.setText("Harap Tunggu..");
            pbLoad.setVisibility(View.VISIBLE);
            bSubmit.setEnabled(false);

            BaseApp.db
                    .collection("user")
                    .document(user_id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String pin = documentSnapshot.getData().get("pin").toString();
                        if(!pin.equals(mPin.getText().toString())) {
                            mPin.setError("PIN salah");
                            tSubmit.setText("SUBMIT");
                            pbLoad.setVisibility(View.GONE);
                            bSubmit.setEnabled(true);
                            sw_mode.setChecked(false);
                            return;
                        } else {
                            getSharedPreferences(appName, MODE_PRIVATE)
                                    .edit().putBoolean("isParent", false)
                                    .commit();
                            tv_mode.setText("Mode: Parent");
                            ll_time.setVisibility(View.VISIBLE);
                            ll_report.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                    });
        });

        dialog.setOnDismissListener(dialog1 -> {
            if (prefs.getBoolean("isParent", false)) {
                sw_mode.setChecked(false);
            } else {
                sw_mode.setChecked(true);
            }
        });

        dialog.show();
    }
}
