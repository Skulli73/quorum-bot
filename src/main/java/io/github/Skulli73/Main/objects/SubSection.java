package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class SubSection {
    @Expose
    public String desc;
    @Expose
    public ArrayList<SubSection> subSectionArrayList;
    public SubSection(String pDesc) {
        desc = pDesc;
        subSectionArrayList = new ArrayList<SubSection>();
    }
    public boolean hasSubSections (){
        return subSectionArrayList.size()==0;
    }
    public StringBuilder getSubSubSectionsStringBuilder(StringBuilder pStringBuilder, int pLevel) {
        boolean lEnd = false;
        for(int i = 0; i<subSectionArrayList.size(); i++) {
            StringBuilder lSpace = new StringBuilder();
            for(int j = 0; j<pLevel;j++) {
                lSpace.append("   ");
            }
            pStringBuilder.append("\n         ").append(lSpace).append("(").append(i+1).append(")").append(" ").append(subSectionArrayList.get(i).desc);
            if(subSectionArrayList.get(i).hasSubSections()) {
                pStringBuilder = subSectionArrayList.get(i).getSubSubSectionsStringBuilder(pStringBuilder, pLevel+1);
            }
        }
        return pStringBuilder;
    }
}
