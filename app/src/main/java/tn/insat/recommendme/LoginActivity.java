package tn.insat.recommendme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import tn.insat.recommendme.InstagramApp;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private InstagramApp mApp;
    private Button btnConnect,btn_login,btnRecommend,
            btnViewInfo;
    private View line1,line2;
    private TextView sign_up,forgot_password , or,interest;
    private LinearLayout llAfterLoginView;
    private EditText username,password;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(LoginActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setWidgetReference();
        bindEventHandlers();

        mApp = new InstagramApp(this, AppConfig.CLIENT_ID,
                AppConfig.CLIENT_SECRET, AppConfig.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                // tvSummary.setText("Connected as " + mApp.getUserName());
                btnConnect.setText("Déconnecter");
                username.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                btn_login.setVisibility(View.GONE);
                forgot_password.setVisibility(View.GONE);
                sign_up.setVisibility(View.GONE);

                line1.setVisibility(View.GONE);
                line2.setVisibility(View.GONE);
                or.setVisibility(View.GONE);
                llAfterLoginView.setVisibility(View.VISIBLE);
                // userInfoHashmap = mApp.
                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });


        if (mApp.hasAccessToken()) {
            // tvSummary.setText("Connected as " + mApp.getUserName());
            username.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            btn_login.setVisibility(View.GONE);
            forgot_password.setVisibility(View.GONE);
            sign_up.setVisibility(View.GONE);

            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            or.setVisibility(View.GONE);
            btnConnect.setText("Déconnecter");
            llAfterLoginView.setVisibility(View.VISIBLE);
            mApp.fetchUserName(handler);

        }

    }

    private void bindEventHandlers() {
        btnConnect.setOnClickListener(this);
        btnViewInfo.setOnClickListener(this);
    }

    private void setWidgetReference() {
        llAfterLoginView = (LinearLayout) findViewById(R.id.llAfterLoginView);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnViewInfo = (Button) findViewById(R.id.btnViewInfo);
        btnRecommend = (Button) findViewById(R.id.btnRecommend);
        btn_login = (Button) findViewById(R.id.btn_login);
        line1 = (View) findViewById(R.id.line1);
        line2 = (View) findViewById(R.id.line2);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        sign_up = (TextView) findViewById(R.id.sign_up);
        forgot_password  = (TextView) findViewById(R.id.forgot_password);
        or = (TextView) findViewById(R.id.or);
        interest = (TextView) findViewById(R.id.interest);

    }

    // OAuthAuthenticationListener listener ;

    @Override
    public void onClick(View v) {
        if (v == btnConnect) {
            connectOrDisconnectUser();
        } else if (v == btnViewInfo) {
            displayInfoDialogView();
        }
    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    LoginActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                    // btnConnect.setVisibility(View.VISIBLE);
                                    username.setVisibility(View.VISIBLE);
                                    password.setVisibility(View.VISIBLE);
                                    btn_login.setVisibility(View.VISIBLE);
                                    forgot_password.setVisibility(View.VISIBLE);
                                    sign_up.setVisibility(View.VISIBLE);

                                    line1.setVisibility(View.VISIBLE);
                                    line2.setVisibility(View.VISIBLE);
                                    or.setVisibility(View.VISIBLE);
                                    llAfterLoginView.setVisibility(View.GONE);
                                    btnConnect.setText("Sign in with Instagram");
                                    // tvSummary.setText("Not connected");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }


    private void displayInfoDialogView() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LoginActivity.this);
        alertDialog.setTitle("Profile Info");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.profile_view, null);
        alertDialog.setView(view);
        TextView tvName = (TextView) view.findViewById(R.id.tvUserName);
        TextView tvNoOfFollwers = (TextView) view
                .findViewById(R.id.tvNoOfFollowers);
        TextView tvNoOfFollowing = (TextView) view
                .findViewById(R.id.tvNoOfFollowing);
        tvName.setText(userInfoHashmap.get(InstagramApp.TAG_USERNAME));
        tvNoOfFollowing.setText(userInfoHashmap.get(InstagramApp.TAG_FOLLOWS));
        tvNoOfFollwers.setText(userInfoHashmap
                .get(InstagramApp.TAG_FOLLOWED_BY));
        alertDialog.create().show();
    }


    public  void browser1(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/accounts/emailsignup/"));
        startActivity(browserIntent);

    }

    public  void browser2(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"));
        startActivity(browserIntent);

    }
    public  void switchToRecommend(View view){
        Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
        startActivity(intent);

    }
}
