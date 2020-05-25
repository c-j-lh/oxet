package com.example.s.oxet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.MasterViewHolder> {
    private Language lang;
    public EditCoursesFragment editCoursesFragment;
    private static final int INFO = 0;
    private static final int LESSON = 1;
    private static final int ADD_LESSON = 2;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public abstract class MasterViewHolder extends RecyclerView.ViewHolder {
        public MasterViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract int getListItemType();
    }

    public class LessonMaster extends MasterViewHolder{
        public int lesson;
        public TextView name;
        public ImageView icon;
        public LessonMaster(LinearLayout v) {
            super(v);
            v.setOnClickListener(new LinearLayout.OnClickListener(){
                @Override
                public void onClick(View v) {
                    setLesson(lesson);
                }
            });
            name = v.findViewById(R.id.master_tv_lessonName);
            icon = v.findViewById(R.id.master_iv_lessonIcon);
        }

        @Override
        public int getListItemType() {
            return LESSON;
        }
    }

    public class AddLessonMaster extends MasterViewHolder{
        public AddLessonMaster(ImageButton b) {
            super(b);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lang.addLesson();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getListItemType() {
            return ADD_LESSON;
        }
    }

    public class InfoLessonMaster extends MasterViewHolder{
        public InfoLessonMaster(LinearLayout v) {
            super(v);
            v.setOnClickListener(new LinearLayout.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editCoursesFragment.callInfo();
                }
            });
        }

        @Override
        public int getListItemType() {
            return INFO;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MasterAdapter(Language lang, EditCoursesFragment editCoursesFragment) {
        setLang(lang);
        this.editCoursesFragment = editCoursesFragment;
//        System.out.println("ma lang = " + lang.name);
//        System.out.println("ma lang.lessons = " + lang.lessons);
    }

    public void setLesson(int lessonNumber){
        editCoursesFragment.setLesson(lang.lessons.get(lessonNumber));
    }

    public void setLang(Language lang) {
        if(lang!=null){
            // todo
        }
        this.lang = lang;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MasterAdapter.MasterViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        switch (viewType){
            case LESSON:
                LinearLayout item = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.master_item, parent, false);
                return new LessonMaster(item);
            case ADD_LESSON:
                ImageButton button = (ImageButton) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.master_addlesson, parent, false);
                return new AddLessonMaster(button);
            case INFO:
                LinearLayout item2 = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.master_info, parent, false);
                return new InfoLessonMaster(item2);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MasterViewHolder rawholder, int position) {
        switch (rawholder.getListItemType()){
            case LESSON:
                LessonMaster holder = (LessonMaster)rawholder;
                Lesson lesson = lang.lessons.get(--position);
                System.out.println("position = " + position);
                System.out.println("lesson name = " + lesson.getName());
                holder.name.setText(lesson.getName());
                holder.lesson = position;
//                System.out.println("lesson.iconID = " + lesson.iconID);
                holder.icon.setImageResource(lesson.iconID);
                return;
            case ADD_LESSON:
                return;
            case INFO:
                return;
        }
    }

    @Override
    public int getItemViewType(int position){
        return (position==getItemCount()-1)?ADD_LESSON:position==0?INFO:LESSON;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(lang==null)return 2;
        return lang.lessons.size()+2;
    }
}