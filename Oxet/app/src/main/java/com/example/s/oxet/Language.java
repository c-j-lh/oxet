package com.example.s.oxet;

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Language {
    public static final char EMPTY = 0;
    public static final char BARE = 1;
    @Deprecated public static final char SAMPLE = 2;
    public HashMap<String, String> dictionary;
    public ArrayList<Lesson> lessons;
    public String name, description;
    private static final String SEPARATOR = "\n---\n";
    private static int count = 0;
    private JSONObject object;
    private JSONArray jsonLessons;
    public HashMap<String,Integer> jsonIndexMap;
    public boolean ready;

    public Language(String input){
//        String[] lessonsRaw = file.split(SEPARATOR);
        try {
            object = new JSONObject(input);
            System.out.println("This should not be null...\nobject = " + object);
            dictionary = new HashMap<>();
            jsonLessons = (JSONArray)object.getJSONArray("lessons");
            lessons = new ArrayList<Lesson>(jsonLessons.length());
            jsonIndexMap = new HashMap<>(jsonLessons.length());
            for(int i=0; i<jsonLessons.length(); i++){
                Lesson lesson = new Lesson(jsonLessons.getString(i),this);
                lessons.add(lesson);
                jsonIndexMap.put(lesson.getName(),i);
            }
            assert lessons.size() != 0;
            name = object.getString("name");
            description = object.getString("description");
            count++;
        } catch (JSONException e) {
            System.out.println("Failed language setup: "+input);
            e.printStackTrace();
        }
    }

    // testing
//    public Language(){
//    }

    public Language(char mode){
        jsonIndexMap = new HashMap<>();
        switch(mode){
            case EMPTY:
                name = "";
                description = "";
                lessons = new ArrayList<>();
                break;
            case BARE:
                name = "";
                description = "";
                lessons = new ArrayList<>(1);
                lessons.add(new Lesson(this,1));
                jsonIndexMap.put(lessons.get(0).getName(),0);
                break;
            case SAMPLE:
                this.name = "Sample language";
                this.description = "Sample description";
                int random = new Random().nextInt(2)+2;
                lessons = new ArrayList<>(random);
                for(int i=0; i<random; i++){
                    Lesson lesson = new Lesson(this,i+1);
                    lessons.add(lesson);
                    jsonIndexMap.put(lesson.getName(),i);
                }
        }
        setupJson();
    }

    public Language(HashMap<String, String> dictionary, ArrayList<Lesson> lessons, String name, String description){
        count++;
        this.dictionary = dictionary;
        this.lessons = lessons;
        this.name = name;
        this.description = description;
        for(Lesson lesson:lessons){
            // todo
        }
        setupJson();
    }

    public void addLesson(){
        int next = lessons.size()+1;
        while(jsonIndexMap.containsKey(Lesson.getLessonNumberName(next))){
            next++;
        }
        Lesson lesson = new Lesson(this,next);
        jsonIndexMap.put(lesson.getName(),lessons.size()+1);
        jsonLessons.put(lessons.size()+1);
        lessons.add(lesson);
    }

    private void setupJson(){
        object = new JSONObject();
        System.out.println(name+" has been inited");
        try {
            object.put("name",name);
            object.put("description",description);

            jsonLessons = new JSONArray();
            jsonIndexMap = new HashMap<>();
            int i=0;
            for(Lesson lesson:lessons){
                jsonIndexMap.put(lesson.getName(),i++);
                jsonLessons.put(jsonIndexMap.get(lesson.getName()),lesson.toString());
            }
            object.put("lessons",jsonLessons);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSave(Lesson lesson) {
        try {
            jsonLessons.put(jsonIndexMap.get(lesson.getName()),lesson.toString());
            object.put("lessons",jsonLessons);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSave() {
        try {
//            if(object==null)setupJson();
//            for(Lesson lesson:lessons){
//                jsonLessons.put(jsonIndexMap.get(lesson.name),lesson.toString());
//            }
            object.put("lessons",jsonLessons);
            object.put("name",name);
            object.put("description",description);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isReady() {
        if(name.isEmpty())return false;
        for(Lesson lesson:lessons){
            if(!lesson.isReady())return false;
        }
        return true;
    }
}
