package com.example.s.oxet;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import static android.content.Context.MODE_PRIVATE;

public class GamIO {
    Context context;
    int week, total, streak, today, latestDay;
    int[] ar = new int[4];
    private JSONArray historyGam;

    public GamIO(Context context){
        this.context = context;
        loadGam();
    }

    public void updateGam(Language language, Lesson lesson, int points){
        week += points;
        total += points;
        if(latestDay==today-1 || streak==0)streak++;
        ar[0] += points;
        saveGam(language,lesson,points);
    }

    public void saveGam(Language language, Lesson lesson, int points){
        JSONObject object = new JSONObject();
        try {
            object.put("date",Calendar.getInstance().getTimeInMillis());
            object.put("language",language.name);
            object.put("lesson",lesson.getName());
            object.put("points",points);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        historyGam.put(object);

        try {
            FileOutputStream out = context.openFileOutput("gam.txt", MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(historyGam.toString()+"\n");
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

    private void loadGam() {
        File file = new File(context.getFilesDir().getAbsolutePath()+"/gam.txt");
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
            Log.d("FileIO","gam.txt not found");
            e.printStackTrace();
        }

        try {
            historyGam = new JSONArray(text.toString());
        } catch (JSONException e) {
            Log.d("FileIO","gam.txt was invalid");
            e.printStackTrace();
        }
        updateDerived();
    }

    public void updateDerived(){
        week = total = 0;
        Calendar current = Calendar.getInstance();
        today = (int)(current.getTimeInMillis()/86400000);
        int prevDay = today;
        streak = 0;
        current.add(Calendar.WEEK_OF_YEAR,-1);

        try {
            latestDay = (int)(historyGam.getJSONObject(0).getLong("date")/86400000);
        } catch (JSONException ignored) {}

        for(int i=historyGam.length()-1; i>=0; i--){
            try {
                JSONObject jsonObject = historyGam.getJSONObject(i);
                Calendar tmp = Calendar.getInstance();
                tmp.setTimeInMillis(jsonObject.getLong("date"));
                int points = jsonObject.getInt("points");
//                System.out.println("points = " + points);
                total += points;
                if(tmp.after(current))week += points;
                int day = (int)(tmp.getTimeInMillis()/86400000);
//                System.out.println("tmp.getTimeInMillis() = " + tmp.getTimeInMillis());
//                System.out.println("prevDay = " + prevDay);
//                System.out.println("day = " + day);
                if((prevDay == day+1) || (prevDay == day)){
                    streak++;
                    prevDay = day;
                }else prevDay += 10000;
                if(today-day <= 3){
                    ar[today-day] += points;
                }
            } catch (JSONException e) {
                System.out.println("JSON array prob "+i);
                e.printStackTrace();
            }
        }
    }
}
