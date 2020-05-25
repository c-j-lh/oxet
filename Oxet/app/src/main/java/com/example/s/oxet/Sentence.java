package com.example.s.oxet;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class Sentence implements Comparable<Sentence>{
    public static final char EMPTY = 0;
    @Deprecated public static final char SAMPLE = 1;
    String question, answer;
    int group;
    Calendar lastTested;
    JSONObject sentence;

    public Sentence(String input){
        try {
            sentence = new JSONObject(input);
            question = sentence.getString("question");
            answer = sentence.getString("answer");
            group = sentence.getInt("group");
            lastTested = Calendar.getInstance();
            lastTested.setTimeInMillis(sentence.getLong("last tested"));
        } catch (JSONException e) {
            System.out.println("Failed sentence setup: "+input);
            e.printStackTrace();
        }
    }



    public Sentence(char mode){
        switch (mode){
            case EMPTY:
                question = "";
                answer = "";
                group = 0;
                lastTested = Calendar.getInstance();
                lastTested.add(Calendar.DATE,-1);
                sentence = new JSONObject();
                break;
            case SAMPLE:
                question = "Sample question";
                answer = "Sample answer";
                group = 0;
                lastTested = Calendar.getInstance();
                lastTested.add(Calendar.DATE,-1);
                sentence = new JSONObject();
        }
    }

    public Sentence(String question, String answer){
        this.question = question;
        this.answer = answer;
        this.group = 0;
        this.lastTested = Calendar.getInstance();
        sentence = new JSONObject();
    }

    public long getExpected(){
        Calendar expected = (Calendar) lastTested.clone();
        expected.add(Calendar.DATE,2*group+1);
        return expected.getTime().getTime();
    }

    public void correct(){
        group++;
        lastTested = Calendar.getInstance();
    }

    public void wrong(){
        if(group!=0)group--;
        lastTested = Calendar.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(Sentence b){
        if(this.getExpected() == b.getExpected())return Integer.compare(b.group,this.group);
        return Long.compare(this.getExpected(),b.getExpected());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        try {
            sentence.put("question",question);
            sentence.put("answer",answer);
            sentence.put("group",group);
            sentence.put("last tested",lastTested.getTimeInMillis());
            return String.format("%s:%s:%d:%d",question,answer,group,lastTested.getTimeInMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isReady() {
        return (!question.isEmpty()) && (!answer.isEmpty());
    }
}
