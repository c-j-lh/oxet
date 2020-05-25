package com.example.s.oxet;

import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EditLessonFragment extends Fragment {
    private EditText name,lecture;
    private Spinner icon;
    private RecyclerView details;
    DetailsAdapter detailsAdapter;
    private Lesson lesson;
    private LinearLayoutManager layoutManager;
    private EditCoursesFragment editCoursesFragment;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ConstraintLayout view = (ConstraintLayout)inflater.inflate(R.layout.details_lesson_fragment, container, false);
        details = view.findViewById(R.id.detailsLesson_recycler_sentences);
        name = view.findViewById(R.id.detailsLesson_et_name);
        lecture = view.findViewById(R.id.detailsLesson_et_lecture);
        icon = view.findViewById(R.id.detailsLesson_spinner_icon);

        layoutManager = new LinearLayoutManager(getContext());
        details.setLayoutManager(layoutManager);
        if(lesson!=null){
            detailsAdapter = new DetailsAdapter(lesson,this);
            details.setAdapter(detailsAdapter);
            name.setText(lesson.getName());
            lecture.setText(lesson.lecture);
        }

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(lesson!=null && editCoursesFragment.mainActivity.getCurrentFocus()==name){
                    if(lesson.setName(name.getText().toString())){
                        System.out.println("editCoursesFragment = " + editCoursesFragment);
                        if(editCoursesFragment!=null)editCoursesFragment.lessonNameChanged();
                    }else{
                        Toast.makeText(getContext(),"Lessons must have unique names",Toast.LENGTH_SHORT).show();
                        name.clearFocus();
                        name.setText(lesson.getName());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        lecture.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(lesson!=null)lesson.lecture = lecture.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // todo carcinogenic
        String[] iconNames = getResources().getStringArray(R.array.lesson_iconNames);
//        int[] iconIDs = getResources().getIntArray(R.array.lesson_iconIDs);
//        TypedArray iconIDs = getResources().obtainTypedArray(R.array.lesson_iconIDs);
//        imgs.recycle();
//        System.out.println("iconIDs[0] = " + iconIDs.getResourceId(0,-1));
        System.out.println("R.drawable.lessons_animals = " + R.drawable.lessons_animals);
        List<HashMap<String,String>> data = new ArrayList<>(iconNames.length);
        for(int i=0; i<iconNames.length; i++){
            HashMap<String,String> hashMap = new HashMap<>(2);
//            hashMap.put("icon",""+iconIDs.getResourceId(i,-1));
            hashMap.put("icon",""+Lesson.iconIDs[i]);
            hashMap.put("name",iconNames[i]);
            data.add(hashMap);
        }
//        iconIDs.recycle();
//        System.out.println("Check 1"); printWeird(data);
        icon.setAdapter(new SimpleAdapter(getContext(),
                data,
                R.layout.test,
//                android.R.layout.activity_list_item,
                new String[]{"icon", "name"},
                new int[]{android.R.id.icon, R.id.text1}));

        icon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lesson.iconID = Lesson.iconIDs[position];
                System.out.println("position = " + position);
                System.out.println("editCoursesFragment = " + editCoursesFragment);
                editCoursesFragment.lessonIconChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        System.out.println("Check 2"); printWeird(data);
        return view;
    }

    public void setEditCoursesFragment(EditCoursesFragment editCoursesFragment){
        this.editCoursesFragment = editCoursesFragment;
    }

    public void setLesson(Lesson lesson){
        this.lesson = lesson;
        if(layoutManager!=null){
            detailsAdapter = new DetailsAdapter(lesson,this);
            details.setAdapter(detailsAdapter);
            name.clearFocus();
            name.setText(lesson.getName());
            lecture.setText(lesson.lecture);
            icon.setSelection(Lesson.iconIDMap.get(lesson.iconID));
        }
        if(detailsAdapter!=null)detailsAdapter.setLesson(lesson);
    }

    private static void printWeird(List<HashMap<String,String>> data){
        int i=0;
        for(HashMap<String,String> hashMap:data){
            Iterator<Map.Entry<String,String>> iterator = hashMap.entrySet().iterator();
            System.out.println("item = " + i++);
            while(iterator.hasNext()){
                Map.Entry<String,String> entry = iterator.next();
                System.out.printf("%s: %s\n", entry.getKey(), entry.getValue());
            }
        }
    }
}
