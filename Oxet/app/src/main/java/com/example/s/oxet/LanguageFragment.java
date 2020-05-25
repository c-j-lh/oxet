package com.example.s.oxet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LanguageFragment extends Fragment {
    TextView languageName,description;
    RecyclerView recyclerView;
    LessonsAdapter lessonsAdapter;
    private Language language;
    private MainActivity main;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        System.out.println("Being called\n\nBeing called");
        View view = inflater.inflate(R.layout.language_fragment, container, false);
        languageName = view.findViewById(R.id.language_tv_languageName);
        description = view.findViewById(R.id.language_tv_description);
        recyclerView = view.findViewById(R.id.language_recycler_lessons);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        lessonsAdapter = new LessonsAdapter(language,this);
        recyclerView.setAdapter(lessonsAdapter);
        if(language!=null)setLanguage(language);
        return view;
    }

    public void setLanguage(Language language){
        this.language = language;
        if(languageName!=null){
            languageName.setText(language.name);
            description.setText(language.description);
        }
    }

    public void setLesson(Lesson lesson) {
        main.callLesson(lesson);
    }

    public void setMain(MainActivity main){
        this.main = main;
    }
}
