package com.example.farvater.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.farvater.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.farvater.geoquiz.answer_shown";
    private static final String EXTRA_RECIVED_TIPS = "com.example.farvater.geoquiz.recived_tips";
    private static final String EXTRA_SENDED_TIPS = "com.example.farvater.geoquiz.sended_tips";
    private static final String KEY_CHEATED = "saw correct answer for cheating";
    private static final String KEY_NUM_OF_CHEAT = "number of remaining tips";

    private boolean mAnswerIsTrue;
    private boolean mSawAnswer;

    private int mNumOfCheats;
    private static int mNumOfCheatsMax = 3;

    private TextView mAnswerTextView;
    private TextView mTargetApiTextView;
    private Button mShowAnswerButton;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_CHEATED, mSawAnswer);
        savedInstanceState.putInt(KEY_NUM_OF_CHEAT,mNumOfCheats);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        mNumOfCheats = getIntent().getIntExtra(EXTRA_RECIVED_TIPS, mNumOfCheatsMax);
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        if(savedInstanceState != null){
            mSawAnswer = savedInstanceState.getBoolean(KEY_CHEATED,false);
            mNumOfCheats = savedInstanceState.getInt(KEY_NUM_OF_CHEAT, mNumOfCheatsMax);
        }

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mTargetApiTextView = (TextView) findViewById(R.id.target_api_text_view);

        String outSdk = getResources().getString(R.string.API_version) + String.valueOf(Build.VERSION.SDK_INT);
        mTargetApiTextView.setText(outSdk);

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNumOfCheats > 0){
                    if(mAnswerIsTrue)
                        mAnswerTextView.setText(R.string.true_button);
                    else
                        mAnswerTextView.setText(R.string.false_button);
                    mSawAnswer = true;
                    mNumOfCheats--;
                    setAnswerShownResult(mSawAnswer, mNumOfCheats);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        int cx = mShowAnswerButton.getWidth() / 2;
                        int cy = mShowAnswerButton.getHeight() / 2;
                        float radius = mShowAnswerButton.getWidth();
                        Animator anim = ViewAnimationUtils
                                .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationCancel(animation);
                                mShowAnswerButton.setVisibility(View.INVISIBLE);
                            }
                        });
                        anim.start();
                    } else
                        mShowAnswerButton.setVisibility(View.INVISIBLE);

                }
                else {
                    mNumOfCheats = 0;
                    Toast.makeText(CheatActivity.this, R.string.warning_tips,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }
    public static int wasAnswerShownTips(Intent result){
        return result.getIntExtra(EXTRA_SENDED_TIPS, 0);
    }
    private void setAnswerShownResult(boolean isAnswerShown, int numOfCheats){
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_SENDED_TIPS,numOfCheats);
        setResult(RESULT_OK, data);
    }
}
