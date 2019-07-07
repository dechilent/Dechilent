package id.arv.dechilent.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.pixplicity.fontview.FontAppCompatTextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;

import static id.arv.dechilent.BaseApp.appName;

public class SecurityCodeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.pbUpload)
    ProgressBar pbUpload;
    @BindView(R.id.btn_login)
    FontAppCompatTextView bLogin;
    @BindView(R.id.otp_view)
    OtpView mPIN;
    @BindView(R.id.email_sign_in_button)
    LinearLayout mSubmit;
    private String email = "";
    private String id = "";
    private String user_id = "";
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_code);
        ButterKnife.bind(this);
        currentUser = BaseApp.mAuth.getCurrentUser();
        getExtrasIntent();
        setUI();
    }

    private void getExtrasIntent() {
        try {
            email = getIntent().getStringExtra("email");
        } catch (Exception ex) {
        }
        id = getIntent().getStringExtra("id");
        user_id = getIntent().getStringExtra("user_id");
    }

    private void setUI() {
        mSubmit.setOnClickListener(this);
        mPIN.setOtpCompletionListener(otp -> {
            // do_tone Stuff
            Log.d("onOtpCompleted=>", otp);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                if (mPIN.getText().toString().isEmpty()) {
                    mPIN.setError("Masukkan PIN anda");
                    return;
                }

                savePIN();
                break;
        }
    }

    private void savePIN() {
        onSave();

        BaseApp.db
                .collection("user")
                .document(user_id)
                .update("pin", mPIN.getText().toString())
                .addOnSuccessListener(aVoid -> {

                    getSharedPreferences(appName, MODE_PRIVATE)
                            .edit().putBoolean("isPIN", true)
                            .apply();

                    Intent in = new Intent(SecurityCodeActivity.this, MainActivity.class);
                    in.putExtra("email", email);
                    in.putExtra("id", id);
                    startActivity(in);
                    finish();
                });
    }

    private void onSave() {
        bLogin.setText("Harap Tunggu..");
        pbUpload.setVisibility(View.VISIBLE);
        mSubmit.setEnabled(false);
    }

    private void offSave() {
        bLogin.setText("SUBMIT");
        pbUpload.setVisibility(View.GONE);
        mSubmit.setEnabled(true);
    }
}
