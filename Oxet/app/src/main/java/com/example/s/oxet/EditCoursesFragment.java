package com.example.s.oxet;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class EditCoursesFragment extends Fragment{
    private static final int OPEN_FILE = 300;
    private static final int SAVE_AS = 301;
    RecyclerView master;//,details;
//    DetailsAdapter detailsAdapter;
    public Language language;
    public Lesson lesson;
    public Button saveButton,loadButton,publishButton;//saveAsButton;
    public TextView languageName;
    private MasterAdapter masterAdapter;
    private InfoFragment infoFragment;
    private EditLessonFragment editLessonFragment;
    public MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_courses_fragment, container, false);
        master = view.findViewById(R.id.editCourses_recycler_master);
//        details = view.findViewById(R.id.editCourses_recycler_details);
        saveButton = view.findViewById(R.id.editCourses_button_save);
        loadButton = view.findViewById(R.id.editCourses_button_load);
//        saveAsButton = view.findViewById(R.id.editCourses_button_saveAs);
        publishButton = view.findViewById(R.id.editCourses_button_publish);
        languageName = view.findViewById(R.id.editCourses_et_langName);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        master.setLayoutManager(layoutManager);
        if(language==null)language = new Language(Language.BARE);
        masterAdapter = new MasterAdapter(language,this);
        master.setAdapter(masterAdapter);

        infoFragment = new InfoFragment();
        infoFragment.setLanguage(language);
        infoFragment.setEditCoursesFragment(this);
        editLessonFragment = new EditLessonFragment();
        editLessonFragment.setEditCoursesFragment(this);

        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        editLessonFragment.setLesson(language.lessons.get(0));
        this.lesson = language.lessons.get(0);
        transaction.replace(R.id.editCourses_frame_details, editLessonFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        System.out.println("Set to editLesson");

        languageName.setText(language.name);
        languageName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mainActivity!=null && mainActivity.getCurrentFocus() == languageName) {
                    language.name = languageName.getText().toString();
                    updateInfoFragmentLanguageName();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        saveButton.setOnClickListener(new Save());
        loadButton.setOnClickListener(new Load());
//        saveAsButton.setOnClickListener(new SaveAs());
        publishButton.setOnClickListener(new Publish());
        return view;
    }

    public void setLanguage(Language language) {
        this.language = language;
        languageName.setText(language.name);
        if(masterAdapter!=null)masterAdapter.setLang(language);
        if(infoFragment!=null)infoFragment.setLanguage(language);
    }

    public void setLesson(Lesson lesson) {
        editLessonFragment.setLesson(lesson);
        if(this.lesson==null){
            final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(infoFragment);
            transaction.replace(R.id.editCourses_frame_details, editLessonFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        this.lesson = lesson;
    }
    public void setMain(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void callInfo() {
        if(lesson!=null){
            lesson = null;
            final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(editLessonFragment);
            transaction.replace(R.id.editCourses_frame_details, infoFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void lessonIconChanged() {
        System.out.println("lesson Icon called");
//        for(Lesson lesson:language.lessons) System.out.println("lesson.name = " + lesson.name);
//        for() System.out.println("lesson.name = " + lesson.name);
        int index = language.jsonIndexMap.get(lesson.getName());
        System.out.println("index = " + index);
        masterAdapter.notifyItemChanged(index+1);
    }

    public void updateInfoFragmentLanguageName() {
        infoFragment.setLanguage(language);
    }

    public void updateTitleLanguageName() {
        languageName.setText(language.name);
    }

    public void lessonNameChanged() {
        int index = language.jsonIndexMap.get(lesson.getName());
        System.out.println("index to change = " + index);
        masterAdapter.notifyItemChanged(index);
    }

    public class Load implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getContext(), ImageFilePickerActivity.class);
            i.putExtra(ImageFilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(ImageFilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
//            i.putExtra(ImageFilePickerActivity.EXTRA_MODE, ImageFilePickerActivity.MODE_FILE);
            i.putExtra(ImageFilePickerActivity.EXTRA_START_PATH, mainActivity.unfinishedRoot.getAbsolutePath());
            startActivityForResult(i, OPEN_FILE);
        }
    }

    public class Save implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
//                FileOutputStream out = getContext().openFileOutput(language.name.replace(' ','_')+".txt",MODE_PRIVATE);
                FileOutputStream out = new FileOutputStream(new File(mainActivity.unfinishedRoot.getAbsolutePath()+"/"+language.name.replace(' ','_')+".txt"));
                OutputStreamWriter writer = new OutputStreamWriter(out);
                writer.write(language.getSave());
                writer.flush();
                writer.close();
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Publish implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            if(!language.isReady()){
                Toast.makeText(getContext(),"Language has one or more empty fields", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getContext(),mainActivity.addLanguage(language)
                    ?"The language is now in the navigation drawer!"
                    :String.format(Locale.getDefault(),"A language named %s already exists on your device",language.name),
                    Toast.LENGTH_LONG).show();
        }
    }

//    public class SaveAs implements Button.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            Intent i = new Intent(getContext(), ImageFilePickerActivity.class);
//            i.putExtra(ImageFilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
//            i.putExtra(ImageFilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
//            i.putExtra(ImageFilePickerActivity.EXTRA_MODE, ImageFilePickerActivity.MODE_DIR);
//            i.putExtra(ImageFilePickerActivity.EXTRA_START_PATH, getContext().getFilesDir().getAbsolutePath()+"/languages");
//            startActivityForResult(i, SAVE_AS);
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch(requestCode){
                case OPEN_FILE:
                    Uri uri = data.getData();
                    String path = uri.getPath().replace("root/data/data","data/user/0");
                    System.out.println(path);

                    File file = new File(path);
//                    System.out.println("file.exists() = " + file.exists());
//                    System.out.println("getContext().getFilesDir().getAbsolutePath() = " + getContext().getFilesDir().getAbsolutePath());
//                    System.out.println("getContext().getFilesDir().getAbsolutePath() = " + getContext().getExternalFilesDir(DIRECTORY_DOWNLOADS).getAbsolutePath());
                    StringBuilder text = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                        }
                        br.close();
                    }catch (IOException e) {
                        System.out.println("failed editCourse load");
                    }
                    language = new Language(text.toString());
                    System.out.println(language.lessons.get(0).getName());
                    setLanguage(language);
                    setLesson(language.lessons.get(0));
                    break;
//                case SAVE_AS:
//                    Uri uri2 = data.getData();
//                    System.out.println(uri2.getPath());
//                    File file2 = new File(uri2.getPath());
////                    if(file2.isFile() && !ImageFilePickerFragment.getExtension(file2).equals("txt"))
////                        file2 = file2.getParentFile();
//                    File destination;
////                    if(file2.isDirectory()){
//                        destination = new File(file2.getAbsolutePath()+"/"+
//                                language.name.replace(' ','_')+".txt");
////                        System.out.println("destination.getAbsolutePath() = " + destination.getAbsolutePath());
////                        System.out.println("destination.exists() = " + destination.exists());
////                        if(!destination.exists()) {
////                            try {
////                                destination.createNewFile();
////                            } catch (IOException e) {
////                                Toast.makeText(getContext(),"File already", Toast.LENGTH_SHORT).show();
////                                System.out.println("File could not be created");
////                            }
////                        }
////                    }else destination = file2;
//
//                    try {
//                        FileOutputStream out = getContext().openFileOutput(destination.getAbsolutePath(),MODE_PRIVATE);
//                        OutputStreamWriter writer = new OutputStreamWriter(out);
//                        writer.write(language.getSave(lesson));
//                        writer.flush();
//                        writer.close();
//                        out.flush();
//                        out.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

            }
        }
    }

    //    public static EditCoursesFragment getInstance(Language language){
//        EditCoursesFragment fragment = new EditCoursesFragment();
//        fragment.language = language;
//        return fragment;
//    }
}



