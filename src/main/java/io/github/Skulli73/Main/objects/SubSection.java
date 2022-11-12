package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SubSection {
    @Expose
    public String desc;
    @Expose @Nullable
    public ArrayList<SubSection> subSectionArrayList;
    public SubSection(String pDesc) {
        desc = pDesc;
        subSectionArrayList = new ArrayList<SubSection>();
    }
    public boolean hasSubSections (){
        if (subSectionArrayList != null) {
            return subSectionArrayList.size()>0;
        } else return false;
    }
    public StringBuilder getSubSubSectionsStringBuilder(StringBuilder pStringBuilder, int pLevel) {
        for(int i = 0; i<subSectionArrayList.size(); i++) {
            pStringBuilder.append("\n").append("   ".repeat(Math.max(0, pLevel))).append("(").append(i+1).append(")").append(" ").append(subSectionArrayList.get(i).desc);
            if(subSectionArrayList.get(i).hasSubSections()) {
                pStringBuilder = subSectionArrayList.get(i).getSubSubSectionsStringBuilder(pStringBuilder, pLevel+1);
            }
        }
        return pStringBuilder;
    }

    public void newSubSection(String pText, int pLevel) {
        if(pLevel<=1) {
            subSectionArrayList.add(new SubSection(pText));
        } else {
            if(subSectionArrayList.size()==0)
               subSectionArrayList.add(new SubSection(""));
            subSectionArrayList.get(subSectionArrayList.size()-1).newSubSection(pText, pLevel-1);
        }
    }
}
