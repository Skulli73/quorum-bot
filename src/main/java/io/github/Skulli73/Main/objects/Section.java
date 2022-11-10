package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Section {
    @Expose
    public String title;
    @Expose
    public String desc;
    @Expose
    public ArrayList<SubSection> subSectionArrayList;
    public Section(String pTitle, String pDesc) {
        title = pTitle;
        desc = pDesc;
        subSectionArrayList = new ArrayList<SubSection>();
    }
}
