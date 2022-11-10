package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Part {
    @Expose
    public String title;
    @Expose
    public ArrayList<Division> divisionArrayList;
    public Part(String pTitle) {
        title = pTitle;
        divisionArrayList = new ArrayList<Division>();
        divisionArrayList.add(new Division(""));
    }
}
