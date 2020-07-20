package opd.project.y2w.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import opd.project.y2w.MainActivity;
import com.project.y2w.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 666;
    int is_signed;

    TextView currentUser;
    TextView userName;
    ImageView userPict;

    SignInButton signInButton;
    Button signOutButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Account");
        }

        currentUser = findViewById(R.id.currentUser);
        userName = findViewById(R.id.currentUserName);
        userPict = findViewById(R.id.user_pict);

        is_signed = 1;

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        is_signed = Objects.requireNonNull(extras.getInt("tag_signed_in"));


        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        userPict.setImageResource(R.drawable.logo);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(v -> signIn());


        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(v -> {
            signOut();
            currentUser.setText("");
            userName.setText("");
//                Toast.makeText(getApplicationContext(), "Sign out", Toast.LENGTH_SHORT).show();
        });

        if (is_signed == 0) {
            signIn();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("STATISTICS", MODE_PRIVATE);
        int completed  = sharedPreferences.getInt("tasks_completed", 0);
        int added  = sharedPreferences.getInt("tasks_added", 0);

        TextView addedText = findViewById(R.id.addedTasksField);
        addedText.setText("" + added);
        TextView completedText = findViewById(R.id.completedTasksField);
        completedText.setText("" + completed);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            MainActivity.email = acct.getEmail();
            currentUser.setText(acct.getEmail());
            userName.setText(acct.getDisplayName());

            Uri uri = acct.getPhotoUrl();
            Glide.with(this).load(uri).into(userPict);
            signInButton.setVisibility(View.INVISIBLE);
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            signOutButton.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        signOutButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                });
        signInButton.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.GONE);
        MainActivity.email = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        if (MainActivity.email == null) {

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                MainActivity.email = acct.getEmail();
                currentUser.setText(acct.getEmail());
                userName.setText(acct.getDisplayName());

                Uri uri = acct.getPhotoUrl();
                Glide.with(this).load(uri).into(userPict);
            }
            finish();
            onBackPressed();
            //Intent homeIntent = new Intent(this, SignInActivity.class);
            //startActivity(homeIntent);
            //Toast.makeText(this, "Successful login", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You are already signed in", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
