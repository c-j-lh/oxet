package com.example.s.oxet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.s.oxet.CustomTypefaceSpan.applyFontToMenuItem;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences preferences;
    public static final int UPDATE_SCORE_REQUEST_CODE = 300;
    private static final int MENU_ITEMS = 6;
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private LessonFragment lessonFragment;
    private ProfileFragment profileFragment;
//    private JSONArray historyGam;
    private Menu menu;
    private MenuItem previousMenuItem;
    private EditCoursesFragment editCoursesFragment;
    private ArrayList<ArrayList<MenuItem> > items;
    private HashMap<MenuItem,Lesson> menuItemLessonHashMap;
    private HashMap<PairString,Lesson> nameLessonHashMap;
//    private int week, total, streak, today, latestDay;
//    private int[] ar = new int[4];
    private ArrayList<Language> languages;
    private ArrayList<MenuItem> langItems;
    private HashMap<MenuItem,Language> menuItemLanguageHashMap;
    private LanguageFragment languageFragment;

    public GamIO gamIO;
    public Sound sound;
    private NavigationView navigationView;
    public File readyRoot,unfinishedRoot;
    public NotificationUtils mNotificationUtils;
    private static Context context;

    public final class PairString{
        final String language, lesson;
        public PairString(String language, String lesson){
            assert language!=null;
            assert lesson!=null;
            this.language = language;
            this.lesson = lesson;
        }

        @Override public boolean equals(Object other) {
            if (other == this) return true;
            if (other instanceof PairString) {
                PairString that = (PairString) other;
                return this.language.equals(that.language)
                        && this.lesson.equals(that.lesson);
            }
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override public int hashCode() {
            return Objects.hash(language, lesson);
        }
    }

    public static Context getContext(){
        return context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        System.out.println(Calendar.getInstance().getTimeInMillis());

        PreferenceManager.setDefaultValues(this,
                R.xml.pref_general, true);
        PreferenceManager.setDefaultValues(this,
                R.xml.pref_notification, true);
        PreferenceManager.setDefaultValues(this,
                R.xml.pref_data_sync, true);

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        mNotificationUtils = new NotificationUtils(this);
        AlarmReceiver.setMainActivity(this);
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis()+5*1000);
        calendar.set(Calendar.HOUR,20);
        calendar.set(Calendar.MINUTE,0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), 24*60*60*1000, alarmIntent);

        Log.d("FileIO","MainActivity onCreate called");
        readyRoot = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/ready languages");
        unfinishedRoot = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/unfinished languages");
        Log.d("FileIO","readyRoot exists? "+readyRoot.exists());
        Log.d("FileIO","unfinishedRoot exists? "+unfinishedRoot.exists());
        File files[] = readyRoot.listFiles();
        languages = new ArrayList<>(files.length);
        Log.d("FileIO",files.length+" files in directory");
        for(File file:files)Log.d("FileIO","File: "+file.getName());
        for(int h=0; h<files.length; h++){
            StringBuilder text = new StringBuilder();
            try {
                if(files[h].isDirectory() || !ImageFilePickerFragment.getExtension(files[h]).equals(".txt"))continue;
                BufferedReader br = new BufferedReader(new FileReader(files[h]));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
//                    System.out.println("line = " + line);
                    text.append('\n');
                }
                br.close();
                languages.add(new Language(text.toString()));
            }catch (IOException e) {
                Log.d("FileIO",files[h].getName()+" couldn't be loaded");
            }
        }
        Log.d("FileIO",languages.size()+" languages loaded");

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int homePage = Integer.parseInt(preferences.getString("home_page","0"));

        // UI

        drawerLayout = findViewById(R.id.drawer_layout);
        frameLayout = findViewById(R.id.content_frame);
        lessonFragment = new LessonFragment();
        profileFragment = new ProfileFragment();
        languageFragment = new LanguageFragment();
        languageFragment.setMain(this);
        editCoursesFragment = new EditCoursesFragment();
        editCoursesFragment.setMain(this);

        navigationView = findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        items = new ArrayList<>(languages.size());
        langItems = new ArrayList<>(languages.size());
        menuItemLessonHashMap = new HashMap<>();
        menuItemLanguageHashMap = new HashMap<>();
        nameLessonHashMap = new HashMap<>();
        menu.getItem(homePage).setChecked(true);

        for(int h=0; h<languages.size(); h++){
            items.add(new ArrayList<MenuItem>(languages.get(h).lessons.size()));
            SubMenu lang0 = menu.addSubMenu("");
            langItems.add(lang0.add(languages.get(h).name));
            applyFontToMenuItem(langItems.get(h),Typeface.BOLD);
            menuItemLanguageHashMap.put(langItems.get(h),languages.get(h));

            int j=0;
            for(Lesson lesson:languages.get(h).lessons){
                items.get(h).add(lang0.add(lesson.getName()));
                items.get(h).get(j).setIcon(lesson.iconID);
                applyFontToMenuItem(items.get(h).get(j),Typeface.NORMAL);
                menuItemLessonHashMap.put(items.get(h).get(j++),lesson);
                nameLessonHashMap.put(new PairString(languages.get(h).name,lesson.getName()),lesson);
            }
        }

        if(homePage == 2){ // if settings
            settings();
            homePage = 0;
        }
        final int[] menuItems = {R.id.menuitem_profile,R.id.menuitem_edit_courses,R.id.menuitem_settings};
        final Fragment[] fragments = {profileFragment,editCoursesFragment};
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragments[homePage]);
        transaction.addToBackStack(null);
        transaction.commit();

        previousMenuItem = navigationView.getCheckedItem();
        previousMenuItem = menu.getItem(0);
        update(navigationView);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.menuitem_settings){
                            settings();
                            menuItem.setChecked(false);
                            if(previousMenuItem!=null){
                                previousMenuItem.setChecked(true);
                            }
                            drawerLayout.closeDrawers();
                            return true;
                        }

                        if(previousMenuItem!=null){
                            previousMenuItem.setChecked(false);
                        }
                        menuItem.setChecked(true);

                        previousMenuItem = menuItem;
                        drawerLayout.closeDrawers();

                        Lesson lesson = menuItemLessonHashMap.get(menuItem);
                        if(lesson != null){
                            callLesson(lesson);
                            System.out.println("Fragment finished setting");
                            return true;
                        }
                        
                        Language language = menuItemLanguageHashMap.get(menuItem);
                        if(language != null){
                            languageFragment.setLanguage(language);
                            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.content_frame, languageFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                        
                        switch (menuItem.getItemId()){
                            case R.id.menuitem_profile:
                                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, profileFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                break;
                            case R.id.menuitem_edit_courses:
//                                editCoursesFragment.setLanguage(languages.get(0));
                                System.out.println("editCoursesFragment.language = " + editCoursesFragment.language);
                                final FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                                transaction2.replace(R.id.content_frame, editCoursesFragment);
                                transaction2.addToBackStack(null);
                                System.out.println("Calling commit soon");
                                transaction2.commit();
                                break;

                        }
                        return true;
                    }
                });

        gamIO = new GamIO(getApplicationContext());
        sound = new Sound(this);
        profileFragment.update(gamIO.week,gamIO.total,gamIO.streak,gamIO.ar);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FileIO","app resuming");
    }

//    public boolean hasPracticedToday(){
//        loadGam();
//        for(int i=historyGam.length()-1; i>=0; i--){
//            if(historyGam.getJSONObject(i).)
//        }
//    }

    public void callLesson(Lesson lesson) {
        lessonFragment.setLessonMain(lesson,this);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, lessonFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void update(NavigationView navigationView) {
        for (int i = 0, count = navigationView.getChildCount(); i < count; i++) {
            final View child = navigationView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }

    public boolean addLanguage(Language language){
        for(Language language1:languages)if(language1.name.equalsIgnoreCase(language.name))return false;
        SubMenu subMenu = menu.addSubMenu("");
        languages.add(language);
        MenuItem languageItem = subMenu.add(language.name);
        langItems.add(languageItem);
        menuItemLanguageHashMap.put(languageItem,language);
        applyFontToMenuItem(languageItem,Typeface.BOLD);

        items.add(new ArrayList<MenuItem>(language.lessons.size()));
        int lessonNo = 0;
        for(Lesson lesson:language.lessons){
            MenuItem lessonItem = subMenu.add(lesson.getName());
            applyFontToMenuItem(lessonItem,Typeface.NORMAL);
            lessonItem.setIcon(lesson.iconID);
            menuItemLessonHashMap.put(lessonItem,lesson);
            nameLessonHashMap.put(new PairString(language.name,lesson.getName()),lesson);
        }
        update(navigationView);

        try {
            FileOutputStream out = new FileOutputStream (new File(readyRoot.getAbsolutePath()+
                    "/"+language.name.replace(' ','_')+".txt"));
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(language.getSave());
            writer.flush();
            writer.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.d("FileIO","Couldn't save new language");
            e.printStackTrace();
        }
        return true;
    }

    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void updateProgress(Language language, Lesson lesson, int points){
//        week += points;
//        total += points;
//        if(latestDay==today-1 || streak==0)streak++;
//        ar[0] += points;
        gamIO.updateGam(language,lesson,points);
        profileFragment.update(gamIO.week,gamIO.total,gamIO.streak,gamIO.ar);

        try {
            FileOutputStream out = new FileOutputStream (new File(readyRoot.getAbsolutePath()+
                    "/"+language.name.replace(' ','_')+".txt"));  // 2nd line
//            out = openFileOutput(getApplicationContext().getFilesDir().getAbsolutePath()+
//                    "/"+language.name.replace(' ','_')+".txt",MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(language.getSave(lesson));
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        try {
//            super.onActivityResult(requestCode, resultCode, data);
//
//            if (requestCode == UPDATE_SCORE_REQUEST_CODE  && resultCode  == RESULT_OK) {
//                String languageName = data.getStringExtra("language");
//                String lessonName = data.getStringExtra("language");
//                int points = data.getIntExtra("points",0);
//                Lesson lesson = nameLessonHashMap.get(new PairString(languageName,lessonName));
//                updateProgress(lesson.language,lesson,points);
//            }
//        } catch (Exception ex) {
//            Toast.makeText(MainActivity.this, ex.toString(),
//                    Toast.LENGTH_SHORT).show();
//        }
//
//    }
}
