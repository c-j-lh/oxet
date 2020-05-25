package com.example.s.oxet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class InfoFragment extends Fragment {
    private EditText name,description;
    private Language language;
    private EditCoursesFragment editCoursesFragment;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_info_fragment, container, false);
        name = view.findViewById(R.id.detailsInfo_et_name);
        description = view.findViewById(R.id.detailsInfo_et_description);

        if(language!=null){
            name.setText(language.name);
            description.setText(language.description);
        }
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editCoursesFragment.mainActivity!=null && editCoursesFragment.mainActivity.getCurrentFocus() == name) {
                    language.name = name.getText().toString();
                    editCoursesFragment.updateTitleLanguageName();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                language.description = description.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        return view;
    }

    public void setLanguage(Language language){
        this.language = language;
        if(name!=null){
            name.setText(language.name);
            description.setText(language.description);
        }
    }

    public void setEditCoursesFragment(EditCoursesFragment editCoursesFragment){
        this.editCoursesFragment = editCoursesFragment;
    }
}
