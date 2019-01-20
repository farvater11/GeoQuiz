package com.example.farvater.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivityT";
    private static final String KEY_INDEX = "index";
    private static final String KEY_USER_ANSWERS_RESULT = "user results";
    private static final String KEY_CHEAT_ANSWERS = "cheat answers";
    private static final String KEY_CHEATER = "cheater!";
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.farvater.geoquiz.answer_is_true";
    private static final int REQUEST_CODE_CHEAT = 0;
    private float totalResult = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private ImageButton mCheatButton;
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
    private boolean[] mCheatingAnswers = new boolean[mQuestionBank.length];
    private int mCurrentIndex = 0;
    private boolean mIsCheater;

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
        savedInstanceState.putBooleanArray(KEY_CHEAT_ANSWERS, mCheatingAnswers);
        savedInstanceState.putBoolean(KEY_CHEATER, mIsCheater);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(TAG,"onActivityResult");
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null)
                return;
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mCheatingAnswers[mCurrentIndex] = mIsCheater;
        }
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        Arrays.fill(mUserAnswersResult, 0);
        Arrays.fill(mCheatingAnswers, false);


        if(savedInstanceState != null){
            Log.i(TAG, "restore");
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mUserAnswersResult = savedInstanceState.getIntArray(KEY_USER_ANSWERS_RESULT);
            mCheatingAnswers = savedInstanceState.getBooleanArray(KEY_CHEAT_ANSWERS);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER, false);
        }

        setContentView(R.layout.activity_quiz);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mCheatButton = (ImageButton) findViewById(R.id.cheat_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);


         //not picked = 0; correct picked = 1; incorrect picked =2; correct with cheat = 3;

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
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), R.string.judgement_toast, Toast.LENGTH_SHORT).show();
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                if(mCurrentIndex < 0)
                    mCurrentIndex = 0;
                updateQuestion();
                totalResult();
            }
        });
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex+1) % mQuestionBank.length;
                updateQuestion();
                totalResult();
            }
        });
    }


    public static Intent newIntent(Context packageContext, boolean answerIsTrue){
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }
    private void updateQuestion(){
        try{
            if(mCheatingAnswers[mCurrentIndex])
                mIsCheater = true;
            else
                mIsCheater = false;

            Log.d(TAG,"Updating question text", new Exception());
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

            if(mIsCheater){
                messageResId = R.string.judgement_toast;
                answerResult = 3;
            }
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
            if(answer == 0){
                allSelected = false;
                break;
            }
            else
                allSelected = true;
        }
        if(allSelected) {
            for (int answer:
                    mUserAnswersResult) {
                if(answer == 1)
                    totalResult++;
                else if (answer == 3)
                    totalResult += 0.5;
            }
            Toast.makeText(this, "Your result is: " + mQuestionBank.length + "/" + totalResult,Toast.LENGTH_SHORT).show();
        }
    }
}