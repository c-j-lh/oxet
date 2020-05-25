package com.example.s.oxet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class ProfileFragment extends Fragment {
    TextView totalTv,weekTv,streakTv,streakLabelTv;
    int week,total,streak;
    private int[] ar;
//    public ProfileFragment(int week, int total){
//        this.week = week;
//        this.total = total;
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        System.out.println("Being called\n\nBeing called");
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        totalTv = view.findViewById(R.id.profile_tv_total);
        weekTv = view.findViewById(R.id.profile_tv_week);
        streakTv = view.findViewById(R.id.profile_tv_streak);
        streakLabelTv = view.findViewById(R.id.profile_tv_streakLabel);
        GraphView graph = (GraphView) view.findViewById(R.id.graph);

        weekTv.setText(String.format(Locale.getDefault(),"%d",week));
        totalTv.setText(String.format(Locale.getDefault(),"%d",total));
        streakTv.setText(String.format(Locale.getDefault(),"%d",streak));
        streakLabelTv.setText(MainActivity.getContext().getResources().getQuantityString(R.plurals.day_streak, streak));

//        long now = Calendar.getInstance().getTimeInMillis();
//        DataPoint[] data = new DataPoint[] {
//                new DataPoint(0, ar[3])};
//        System.out.println("data = " + data);

//        Calendar calendar = Calendar.getInstance();
//        Date dm0 = calendar.getTime();
//        calendar.add(Calendar.DATE, -1);
//        Date dm1 = calendar.getTime();
//        calendar.add(Calendar.DATE, -1);
//        Date dm2 = calendar.getTime();
//        calendar.add(Calendar.DATE, -1);
//        Date dm3 = calendar.getTime();
////        DataPoint[] data = new DataPoint[] {
////                new DataPoint(dm3, ar[3]),
////                new DataPoint(dm2, ar[2]),
////                new DataPoint(dm1, ar[1]),
////                new DataPoint(dm0, ar[0])
////        };
//        DataPoint[] data = new DataPoint[] {
//                new DataPoint(0, 0),
//                new DataPoint(1, 1),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2)
//        };
////        System.out.println("data = " + data);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(data);
//        graph.addSeries(series);
//        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
//
//        graph.getViewport().setMinX(dm3.getTime());
//        graph.getViewport().setMaxX(dm0.getTime());
//        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getGridLabelRenderer().setHumanRounding(false);

        return view;
    }

    public void update(int week, int total, int streak, int[] ar){
//        System.out.println("week = " + week);
//        System.out.println("total = " + total);
//        System.out.println("streak = " + streak);
//        System.out.println("ar[0] = " + ar[0]);
//        System.out.println("ar[1] = " + ar[1]);
//        System.out.println("ar[2] = " + ar[2]);
//        System.out.println("ar[3] = " + ar[3]);
        this.week = week;
        this.total = total;
        this.streak = streak;
        this.ar = ar;
//        weekTv.setText(String.format(Locale.getDefault(),"%d",week));
//        totalTv.setText(String.format(Locale.getDefault(),"%d",total));
//        streakTv.setText(String.format(Locale.getDefault(),"%d",streak));

    }
}
