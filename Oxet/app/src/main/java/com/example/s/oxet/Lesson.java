package com.example.s.oxet;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class Lesson {
    private String name;
    String lecture;
    ArrayList<Sentence> sentences;
    Language language;
    JSONObject object;
    JSONArray jsonSentences;
    int iconID;
    private static Context context;
    public static final int[] iconIDs = {
            R.drawable.lessons_default,
            R.drawable.lessons_animals,
            R.drawable.lessons_body_parts,
            R.drawable.lessons_clothes,
            R.drawable.lessons_colors,
            R.drawable.lessons_conversation,
            R.drawable.lessons_occupations,
            R.drawable.lessons_politics,
            R.drawable.lessons_spirituality,
            R.drawable.lessons_time,
            R.drawable.lessons_transport,
    };
    public static final HashMap<Integer,Integer> iconIDMap = new HashMap<>(iconIDs.length);
    static{
        for(int i=0; i<iconIDs.length; i++)iconIDMap.put(iconIDs[i],i);
    }

    static void setContext(Context mContext){
        context = mContext;
    }

    public Lesson(String input, Language language){
        this.language = language;
        try {
            object = new JSONObject(input);
            name = object.getString("name");
            lecture = object.getString("lecture");
            iconID = object.has("icon ID")?object.getInt("icon ID"):iconIDs[0];
//            System.out.printf("%s iconID = %s\n",name, iconID);
            jsonSentences = object.getJSONArray("sentences");
//            System.out.println("jsonSentences.length() = " + jsonSentences.length());
            sentences = new ArrayList<>(jsonSentences.length());
            for(int i=0; i<jsonSentences.length(); i++){
                sentences.add(new Sentence(jsonSentences.getString(i)));
            }
            assert sentences.size() != 0;
        } catch (JSONException e) {
            System.out.println("Failed lesson setup: "+input);
            e.printStackTrace();
        }
    }

    public static String getLessonNumberName(int lessonNumber){
        Log.d("i18n","getContext() = " + MainActivity.getContext());
        Log.d("i18n","format = '" + MainActivity.getContext().getString(R.string.lesson_number)+"'");
        return String.format(Locale.getDefault(),MainActivity.getContext()==null?"Lesson %d":MainActivity.getContext().getString(R.string.lesson_number),lessonNumber);
    }

    public Lesson(Language language, int lessonNumber){
        this.language = language;
        name = getLessonNumberName(lessonNumber);
        lecture = String.format(Locale.getDefault(),MainActivity.getContext()==null?"Lecture %d":MainActivity.getContext().getString(R.string.lecture_number),lessonNumber);
        sentences = new ArrayList<>(1);
        sentences.add(new Sentence(Sentence.EMPTY));
        iconID = iconIDs[0];
//        System.out.printf("%s iconID = %s\n",name, iconID);
        setupJson();
    }

    private void setupJson() {
        object = new JSONObject();
        try {
            object.put("name",name);
            object.put("lecture",lecture);

            jsonSentences = new JSONArray();
            int i=0;
            for(Sentence sentence:sentences)jsonSentences.put(i++,sentence.toString());
            object.put("sentences",jsonSentences);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        int i=0;
        try {
            for(Sentence sentence:sentences){
                    jsonSentences.put(i++,sentence.toString());
            }
            object.put("sentences",jsonSentences);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isReady() {
        if(name.isEmpty())return false;
        for(Sentence sentence:sentences){
            if(!sentence.isReady())return false;
        }
        return true;
    }

    public boolean setName(String name) {
        if(language.jsonIndexMap.containsKey(name))return false;
        int index = language.jsonIndexMap.get(this.name);
        language.jsonIndexMap.remove(this.name);
        this.name = name;
        language.jsonIndexMap.put(this.name,index);
        System.out.println("new name = " + this.name);
        System.out.println("index = " + language.jsonIndexMap.get(this.name));
        for(Map.Entry<String,Integer> entry:language.jsonIndexMap.entrySet()){
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        return true;
    }

    public String getName() {
        return name;
    }
}