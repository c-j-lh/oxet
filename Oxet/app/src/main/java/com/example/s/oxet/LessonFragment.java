package com.example.s.oxet;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

public class LessonFragment extends Fragment {
    private Lesson lesson;
    private Iterator<Sentence> iterator;
    private Sentence sentence;
    private TextView question,correctAnswer;
    private EditText answer;
    private ImageView correctOrWrong;
    private Button done;
    private ImageButton next;
    private ProgressBar progressBar;
    private MainActivity mainActivity;
    private int score;
    private int progress;

    private SoundPool soundPool;
    private int correct, wrong;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("onCreateView called");

        View view = inflater.inflate(R.layout.lesson_fragment, container, false);
        question = view.findViewById(R.id.lesson_tv_question);
        answer = view.findViewById(R.id.lesson_et_answer);
        done = view.findViewById(R.id.lesson_button_done);
        next = view.findViewById(R.id.lesson_button_next);
        correctAnswer = view.findViewById(R.id.lesson_tv_answer);
        correctOrWrong = view.findViewById(R.id.lesson_iv_correctOrWrong);
        progressBar = view.findViewById(R.id.lesson_bar_progress);

        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(answer.getText().toString().isEmpty())done.setText(getString(R.string.i_dont_know));
                else done.setText(getString(R.string.done));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        done.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answer.getText().toString().equalsIgnoreCase(sentence.answer)){
                    sentence.correct();
                    score += 1;
                    Log.d("sound","Playing correct now");
                    mainActivity.sound.play(mainActivity.sound.CORRECT);
                    if(mainActivity.preferences.getBoolean("move_on",true)){
                        correctAnswer.setText(sentence.answer);
                        correctOrWrong.setImageResource(R.drawable.correct);
                    }else{
                        Toast.makeText(getContext(),getString(R.string.correct),Toast.LENGTH_SHORT).show();
                        nextQuestion(); return;
                    }
                }else{
                    sentence.wrong();
                    Log.d("sound","Playing wrong now");
                    correctAnswer.setText(sentence.answer);
                    correctOrWrong.setImageResource(R.drawable.wrong);
                    mainActivity.sound.play(mainActivity.sound.WRONG);
                }
                done.setActivated(false);
                next.setVisibility(View.VISIBLE);
                correctOrWrong.setVisibility(View.VISIBLE);
                progressBar.setProgress((++progress)*100/lesson.sentences.size());
            }
        });
        done.setText("I don't know");
        next.setVisibility(View.INVISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });
        progressBar.setProgress(0);

        nextQuestion();
        return view;
    }

    public void setLessonMain(Lesson lesson, MainActivity mainActivity){
        System.out.println("setLesson called");
//        System.out.println("lesson.sentences.size() = " + lesson.sentences.size());
        this.lesson = lesson;
        this.mainActivity = mainActivity;
        iterator = lesson.sentences.iterator();
        score = progress = 0;
    }

    private void nextQuestion(){
        System.out.println("nextQuestion called");
        if(!iterator.hasNext()){
            question.setText(getString(R.string.completed_lesson)); // todo
            mainActivity.updateProgress(lesson.language,lesson,score);
            done.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            answer.setVisibility(View.INVISIBLE);
            correctOrWrong.setVisibility(View.INVISIBLE);
            correctAnswer.setVisibility(View.INVISIBLE);
            score = 0;
//            Intent intent = new Intent(getContext(),MainActivity.class);
////            intent.setComponent(new ComponentName("com.example.s.oxet","com.example.s.oxet.MainActivity"));
//            intent.putExtra("language",lesson.language.name);
//            intent.putExtra("lesson",lesson.name);
//            intent.putExtra("points",score);
//            startActivityForResult(intent,MainActivity.UPDATE_SCORE_REQUEST_CODE);
            return;
        }
        sentence = iterator.next();
        question.setText(sentence.question);
        answer.setText("");
        next.setVisibility(View.INVISIBLE);
        done.setActivated(true);
        done.setText(getString(R.string.i_dont_know));
        correctAnswer.setText("");
        correctOrWrong.setVisibility(View.INVISIBLE);
    }
}
