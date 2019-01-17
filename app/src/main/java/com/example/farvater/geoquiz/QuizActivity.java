package com.example.farvater.geoquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivityT";
    private static final String KEY_INDEX = "index";
    private static final String KEY_USER_ANSWERS_RESULT = "user results";
    private int totalResult = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int[] mUserAnswersResult = new int[mQuestionBank.length];
    private int mCurrentIndex = 0;

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart called");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putIntArray(KEY_USER_ANSWERS_RESULT , mUserAnswersResult);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        Arrays.fill(mUserAnswersResult, 0);

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mUserAnswersResult = savedInstanceState.getIntArray(KEY_USER_ANSWERS_RESULT);
        }


        setContentView(R.layout.activity_quiz);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);


         //not picked = 0; correct picked = 1; incorrect picked =2

        updateQuestion();


        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);

            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(false);
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                totalResult();
            }
        });
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                //if(mCurrentIndex < 0)
                //    mCurrentIndex = 0;
                updateQuestion();
                totalResult();
            }
        });
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex+1) % mQuestionBank.length;
                updateQuestion();
            }
        });
    }
    private void updateQuestion(){
        try{
            int question = mQuestionBank[mCurrentIndex].getTextResId();
            mQuestionTextView.setText(question);
            if(mUserAnswersResult[mCurrentIndex] != 0){
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                mQuestionTextView.setEnabled(false);
            }
            else {
                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);
                mQuestionTextView.setEnabled(true);
            }
        }
        catch (ArrayIndexOutOfBoundsException ex){
            Log.e(TAG, "Error " + ex);
        }
    }
    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        int answerResult;


        if(userPressedTrue == answerIsTrue){
            messageResId = R.string.correct_toast;
            answerResult = 1;
        }
        else{
            messageResId = R.string.incorrect_toast;
            answerResult = 2;
        }
        mUserAnswersResult[mCurrentIndex] = answerResult;
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
    private void totalResult(){
        boolean allSelected = false;
        totalResult = 0;
        for(int answer : mUserAnswersResult){
            if(answer == 0)
                allSelected = false;
            else
                allSelected = true;
        }
        if(allSelected) {
            for (int answer:
                    mUserAnswersResult) {
                if(answer == 1)
                    totalResult++;
            }
            Toast.makeText(this, "Your result is: " + mQuestionBank.length + "/" + totalResult,Toast.LENGTH_SHORT).show();
        }
    }
}
