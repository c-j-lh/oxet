package com.example.s.oxet;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {
    private static final int QUESTION = 0;
    private static final int ADD_QUESTION = 1;
    private Lesson lesson;
    EditLessonFragment editLessonFragment;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public abstract class DetailsViewHolder extends RecyclerView.ViewHolder {
        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract int getListItemType();
    }

    public class QuestionDetails extends DetailsViewHolder{
        public EditText question,answer;
        public TextView questionNumber;
        public int sentenceNumber;
        public QuestionDetails(ConstraintLayout layout) {
            super(layout);
            question = layout.findViewById(R.id.details_et_question);
            answer = layout.findViewById(R.id.details_et_answer);
            questionNumber = layout.findViewById(R.id.details_tv_questionNumber);
            question.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d("sent","questionET = "+sentenceNumber);
                    if(0<=sentenceNumber && sentenceNumber<lesson.sentences.size())
                        lesson.sentences.get(sentenceNumber).question = question.getText().toString();
                }

                @Override public void afterTextChanged(Editable s) {}
            });

            answer.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d("sent","answerET = "+sentenceNumber);
                    if(0<=sentenceNumber && sentenceNumber<lesson.sentences.size())
                     lesson.sentences.get(sentenceNumber).answer = answer.getText().toString();
                }

                @Override public void afterTextChanged(Editable s) {}
            });
        }

        @Override
        public int getListItemType() {
            return QUESTION;
        }
    }

    public class AddQuestionDetails extends  DetailsViewHolder{
        Button add;
        public AddQuestionDetails(ImageButton add) {
            super(add);
            add.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    System.out.println("Add question");
                    lesson.sentences.add(new Sentence(Sentence.EMPTY));
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getListItemType() {
            return ADD_QUESTION;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DetailsAdapter(Lesson lesson, EditLessonFragment editLessonFragment) {
        setLesson(lesson);
        this.editLessonFragment = editLessonFragment;
    }

    void setLesson(Lesson lesson) {
        this.lesson = lesson;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DetailsAdapter.DetailsViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        switch (viewType){
            case QUESTION:
                ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.details_question, parent, false);
                return new QuestionDetails(v);
            case ADD_QUESTION:
                ImageButton button = (ImageButton) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.details_addquestion, parent, false);
                return new AddQuestionDetails(button);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return (position==getItemCount()-1)?ADD_QUESTION:QUESTION;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DetailsViewHolder rawholder, int position) {
        switch(rawholder.getListItemType()){
            case ADD_QUESTION:
                break;
            case QUESTION:
                QuestionDetails holder = (QuestionDetails)rawholder;
                holder.sentenceNumber = position;
                holder.questionNumber.setText(String.format(Locale.getDefault(),"%d",position+1));
                System.out.println("holder.sentenceNumber = " + holder.sentenceNumber);
                holder.question.setText(lesson.sentences.get(position).question);
                holder.answer.setText(lesson.sentences.get(position).answer);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(lesson==null)return 2;
        return lesson.sentences.size()+1;
    }
}
