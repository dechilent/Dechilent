package id.arv.dechilent.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.pixplicity.fontview.FontAppCompatTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.arv.dechilent.BaseApp;
import id.arv.dechilent.R;

import static id.arv.dechilent.BaseApp.appName;
import static id.arv.dechilent.BaseApp.gso;
import static id.arv.dechilent.BaseApp.mAuth;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener, View.OnClickListener {

    @BindView(R.id.pbUpload)
    ProgressBar pbUpload;
    @BindView(R.id.btn_login)
    FontAppCompatTextView bLogin;
    @BindView(R.id.email_sign_in_button)
    LinearLayout mEmailSignInButton;
    @Email
    @NotEmpty
    @BindView(R.id.email)
    MaterialEditText mEmailView;
    @Password
    @NotEmpty
    @BindView(R.id.password)
    MaterialEditText mPasswordView;
    @BindView(R.id.sign_in_button)
    SignInButton mGoogleSignIn;
    private Validator validator;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        setUI();
    }

    private void setUI() {
        prefs = getSharedPreferences(appName, MODE_PRIVATE);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mEmailSignInButton.setOnClickListener(this);
        mGoogleSignIn.setOnClickListener(this);
    }

    private void loginProcess() {
        onSave();
        String email = mEmailView.getText().toString();
        String pass = mPasswordView.getText().toString();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        setModeParent();
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent in = new Intent(LoginActivity.this, MainActivity.class);
                        in.putExtra("email", email);
                        in.putExtra("id", user.getUid());
                        startActivity(in);
                        finish();
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(LoginActivity.this, task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d("", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        loginProcess();
                                    } else {
                                        Log.w("", "createUserWithEmail:failure", task1.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        offSave();
                                    }
                                });
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        onSave();
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            //Log.w("", "createUserWithEmail:failure", e.getMessage());
            Toast.makeText(LoginActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            offSave();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent in = new Intent(LoginActivity.this, MainActivity.class);
                        in.putExtra("email", user.getEmail());
                        in.putExtra("id", user.getUid());
                        startActivity(in);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    @Override
    public void onValidationSucceeded() {
        loginProcess();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof MaterialEditText) {
                ((MaterialEditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                validator.validate();
                break;
            case R.id.sign_in_button:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    private void onSave() {
        bLogin.setText("Harap Tunggu..");
        pbUpload.setVisibility(View.VISIBLE);
        mEmailSignInButton.setEnabled(false);
    }

    private void offSave() {
        bLogin.setText("DAFTAR / MASUK");
        pbUpload.setVisibility(View.GONE);
        mEmailSignInButton.setEnabled(true);
    }

    private void setModeParent() {
        if (prefs.getBoolean("isParent", false)) {
            getSharedPreferences(appName, MODE_PRIVATE)
                    .edit().putBoolean("isParent", true)
                    .apply();
        }
    }
}
