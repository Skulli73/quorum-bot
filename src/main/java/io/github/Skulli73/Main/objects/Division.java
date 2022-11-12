package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Division {
    @Expose
    public String title;
    @Expose
    public ArrayList<Section> sectionArrayList;
    public Division(String pTitle) {
        title = "   " + pTitle;
        sectionArrayList = new ArrayList<Section>();
        sectionArrayList.add(new Section("", ""));
    }
}
