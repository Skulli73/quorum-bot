package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.*;

public class Amendment {
    @Expose
    public ArrayList<String>            omittings;
    @Expose
    public ArrayList<String[]>          amendments;
    @Expose
    public ArrayList<String[]>          additions;
    @Expose
    public Long                         messageId;
    @Expose
    public Long                         introducerId;
    public Amendment() {
        omittings = new ArrayList<String>();
        amendments = new ArrayList<String[]>();
        additions = new ArrayList<String[]>();

    }

    public String toString() {
        StringBuilder lString = new StringBuilder();
        for (String lOmitting : omittings) {
            lString.append("Omit (").append(lOmitting).append(")\n");
        }
        for (String[] lAmendments : amendments)
            lString.append("Amend (").append(lAmendments[0]).append(") to become\n\"").append(lAmendments[1]).append("\"").append("\n");
        for (String[] lAddition : additions)
            lString.append("Add ").append(lAddition[1]).append(" to (").append(lAddition[0]).append(")\n");


        return lString.toString();
    }


    public EmbedBuilder toEmbed(String pFinalNumber, Integer pCurrentNumber, Bill pBill) {
        String lNumber = pFinalNumber;
        EmbedBuilder lEmbed = new EmbedBuilder();
        lEmbed.setTitle("Amendment #" + lNumber + " to " + pBill.title);
        lEmbed.setDescription(toString());
        lEmbed.setFooter("Amendment id: " + pBill.messageId + "." + pCurrentNumber);
        return lEmbed;
    }

    public void updateMessage(String pFinalNumber, Integer pCurrentNumber, Bill pBill) {
        try {
            assert discordApi != null;
            discordApi.getMessageById(messageId, discordApi.getUserById(introducerId).get().openPrivateChannel().get()).get().edit(
                    toEmbed(pFinalNumber, pCurrentNumber, pBill)
            );
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void onAccepted(Bill pBill) {
        for (String lOmitting : omittings) {
            String[] lOmittingArray = lOmitting.split("\\.");
            int lLength = lOmittingArray.length;
            if( lLength > 0) {
                int lPartId = Integer.parseInt(lOmittingArray[0]);
                Part lPart = pBill.partArrayList.get(lPartId);
                if(lLength == 1) {
                    lPart = new Part("");
                    lPart.divisionArrayList.get(0).sectionArrayList.add(new Section("", "*Part repealed*"));
                } else {
                    int lDivisionId = Integer.parseInt(lOmittingArray[1]);
                    Division lDivision = lPart.divisionArrayList.get(lDivisionId);
                    if(lLength == 2) {
                        lDivision = new Division("");
                        lDivision.sectionArrayList.add(new Section("", "*Division repealed*"));
                    } else {
                        int lSectionId = Integer.parseInt(lOmittingArray[2]);
                        Section lSection = lDivision.sectionArrayList.get(lSectionId);
                        if(lLength == 3) {
                            lSection.desc = "*Section repealed*";
                            lSection.subSectionArrayList = new ArrayList<>();
                        } else {
                            Stack<SubSection> lStack = new Stack();
                            int lSubSectionId = Integer.parseInt(lOmittingArray[3]);
                            SubSection lSubSection = lSection.subSectionArrayList.get(lSubSectionId-1);
                            lStack.push(lSubSection);
                            if(lLength == 4) {
                                lSubSection.desc = "*repealed*";
                            } else {
                                int i = 4;
                                while(i < lLength) {
                                    lSubSection = lSubSection.subSectionArrayList.get(Integer.parseInt(lOmittingArray[i])-1);
                                    if(i+1 == lLength)
                                        lSubSection.desc = "*repealed*";
                                    lStack.push(lSubSection);
                                    i++;
                                }

                                SubSection lCurrentSubSection = null;
                                SubSection lPreviousSubSection = lStack.pop();
                                for(int j = lLength-2; !lStack.isEmpty();j--) {
                                    if(lCurrentSubSection != null)
                                        lPreviousSubSection = lCurrentSubSection;
                                    lCurrentSubSection = lStack.pop();
                                    lCurrentSubSection.subSectionArrayList.set(Integer.parseInt(lOmittingArray[j])-1, lPreviousSubSection);
                                }
                                lSubSection = lCurrentSubSection;
                            }
                            lSection.subSectionArrayList.set(lSubSectionId-1, lSubSection);
                        }
                        lDivision.sectionArrayList.set(lSectionId, lSection);
                    }
                }
                pBill.partArrayList.set(lPartId, lPart);
            }
        }
        bills.put(String.valueOf(pBill.messageId), pBill);
        saveBills();
    }
}
