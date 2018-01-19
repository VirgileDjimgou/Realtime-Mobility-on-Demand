package com.bee.passenger.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bee.passenger.R;
import com.bee.passenger.data.StaticConfig;


public class RegisterActivity extends AppCompatActivity {
    FloatingActionButton fab;
    CardView cvAdd;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText UserName, mPhoneNumberField, mVerificationField, email_user, editTextPassword, editTextRepeatPassword;
    public static String STR_EXTRA_ACTION_REGISTER = "register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        email_user = (EditText) findViewById(R.id.email_user);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        editTextRepeatPassword = (EditText) findViewById(R.id.et_repeatpassword);
        UserName = (EditText) findViewById(R.id.username);
        mPhoneNumberField = (EditText) findViewById(R.id.phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.ic_signup);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    public void clickRegister(View view) {
        String uname = UserName.getText().toString().trim();
        String Phone = mPhoneNumberField.getText().toString().trim();
        String email_u = email_user.getText().toString();
        String password = editTextPassword.getText().toString();
        String repeatPassword = editTextRepeatPassword.getText().toString();
        if(validate(uname , Phone , email_u, password, repeatPassword)){
            Intent data = new Intent();
            data.putExtra(StaticConfig.STR_EXTRA_USERNAME, uname);
            data.putExtra(StaticConfig.STR_EXTRA_PHONE_NUMBER , Phone);
            data.putExtra(StaticConfig.STR_EXTRA_EMAIL, email_u);
            data.putExtra(StaticConfig.STR_EXTRA_PASSWORD, password);
            data.putExtra(StaticConfig.STR_EXTRA_ACTION, STR_EXTRA_ACTION_REGISTER);
            setResult(RESULT_OK, data);
            finish();
        }else {
            Toast.makeText(this, "Invalid email , name , Phone or not match password", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate email, pass == re_pass
     * @param emailStr
     * @param password
     * @return
     */
    private boolean validate(String uname, String ph , String emailStr, String password, String repeatPassword) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return  ph.length()> 0 && uname.length() > 0  && password.length() > 0 && repeatPassword.equals(password) && matcher.find();
    }


}
