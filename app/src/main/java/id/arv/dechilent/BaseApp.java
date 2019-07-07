package id.arv.dechilent;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by arrivaldwis on 30/04/19.
 */

public class BaseApp extends Application {
    public static FirebaseFirestore db;
    public static FirebaseStorage storage;
    public static FirebaseAuth mAuth;
    public static StorageReference storageRef;
    public static GoogleSignInOptions gso;
    public static String appName = "id.arv.dechilent";
    public static long timerValue = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
