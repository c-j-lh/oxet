package com.example.s.oxet;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.LessonsViewHolder> {
    private Language lang;
    public LanguageFragment languageFragment;
    public class LessonsViewHolder extends RecyclerView.ViewHolder {
        TextView lesson;
        ImageView icon;
        ProgressBar progressBar;
        int index;
        public LessonsViewHolder(@NonNull View itemView) {
            super(itemView);
            lesson = itemView.findViewById(R.id.language_tv_lesson);
            icon = itemView.findViewById(R.id.language_iv_icon);
            progressBar = itemView.findViewById(R.id.language_bar_progress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLesson(index);
                }
            });
        }
    }

    public LessonsAdapter(Language lang, LanguageFragment languageFragment) {
        setLang(lang);
        this.languageFragment = languageFragment;
    }

    public void setLang(Language lang) {
        if(lang!=null){
            // todo
        }
        this.lang = lang;
        notifyDataSetChanged();
    }

    public void setLesson(int index){
        languageFragment.setLesson(lang.lessons.get(index));
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LessonsAdapter.LessonsViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        ConstraintLayout item = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lesson_item, parent, false);
        return new LessonsViewHolder(item);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LessonsViewHolder holder, int position) {
        holder.lesson.setText(lang.lessons.get(position).getName());
        holder.icon.setImageResource(lang.lessons.get(position).iconID); //todo
    }

    @Override
    public int getItemCount() {
        return lang.lessons.size();
    }
}