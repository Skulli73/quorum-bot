package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;
import io.github.Skulli73.Main.MainQuorum;
import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.councils;
import static io.github.Skulli73.Main.MainQuorum.discordApi;

public class Bill {
    @Expose()
    public String           title;
    @Expose()
    public int              councilId;
    @Expose()
    public long             messageId;
    @Expose()
    public long             initiatorId;

    @Expose(serialize = false, deserialize = false)
    public MessageBuilder   messageBuilder;
    @Expose(serialize = false, deserialize = false)
    public EmbedBuilder     embed;
    @Expose()
    public ArrayList<Part>  partArrayList;

    @Expose
    public boolean          draftFinished;

    @Expose
    public boolean          firstReadingFinished;

    @Expose
    public boolean          amendmentsFinished;

    @Expose
    public boolean          thirdReadingFinished;

    @Expose
    public double          majority;

    @Expose
    public int          typeOfMajority;

    @Expose
    public ArrayList<Amendment>     amendments;
    @Expose
    public ArrayList<Amendment>     amendmentDrafts;

    public Bill(String pTitle, int pCouncilId, long pInitiatorId) {
        title           =pTitle;
        councilId       =pCouncilId;
        partArrayList = new ArrayList<Part>();
        partArrayList.add(new Part(""));
        initiatorId = pInitiatorId;
        draftFinished = false;
        firstReadingFinished = false;
        amendmentsFinished = false;
        thirdReadingFinished = false;
        amendments = new ArrayList<>();
        amendmentDrafts = new ArrayList<>();
    }

    public EmbedBuilder toEmbed(String pDesc) {
        EmbedBuilder lEmbedBuilder = new EmbedBuilder();
        lEmbedBuilder.setTitle(title);
        lEmbedBuilder.setDescription(pDesc);
        lEmbedBuilder.setFooter("Message id: " + messageId);
        return lEmbedBuilder;
    }
    public List<EmbedBuilder> toEmbeds(boolean pEditorMode, Color pColour, boolean pSplitAtStart) {
        return MainQuorum.splitEmbeds(MainQuorum.cutOffText(toString(pEditorMode), pSplitAtStart), pColour, title);
    }
    public List<EmbedBuilder> toEmbeds(boolean pEditorMode, Color pColour, String pFooter, boolean pSplitAtStart) {
        return MainQuorum.splitEmbeds(MainQuorum.cutOffText(toString(pEditorMode), pSplitAtStart), pColour, title, pFooter);
    }
    public EmbedBuilder toEmbed(boolean pEditor) {
            return toEmbed(toString(pEditor));
    }
    public String toString(boolean pEditorMode) {
        int lSectionCounter = 1;
        StringBuilder lStringBuilder = new StringBuilder();
        for(int i = 0; i < partArrayList.size(); i++) {
            Part lPart = partArrayList.get(i);
            if(i!=0) {
                lStringBuilder.append("\n__** Part ").append(i).append(" ").append(lPart.title).append("**__");
            }
            for(int j = 0; j<lPart.divisionArrayList.size(); j++) {
                Division lDivision = lPart.divisionArrayList.get(j);
                if(j!=0) {
                    lStringBuilder.append("\n** Division ").append(j).append(" ").append(lDivision.title).append("**");
                    if(pEditorMode)
                        lStringBuilder.append(" (").append(i).append(".").append(j).append(")");
                }
                for(int k = 0; k<lDivision.sectionArrayList.size(); k++) {
                    Section lSection = lDivision.sectionArrayList.get(k);
                    if(k!=0) {
                        lStringBuilder.append("\n**").append(lSectionCounter).append(" ").append(lSection.title).append("**");
                        if(pEditorMode) {
                            lStringBuilder.append(" (").append(i).append(".").append(j).append(".").append(k).append(")");
                        }
                        lStringBuilder.append("\n").append(lSection.desc);
                        lSectionCounter++;
                    }
                    for(int l = 0; l<lSection.subSectionArrayList.size(); l++) {
                        SubSection lSubSection = lSection.subSectionArrayList.get(l);
                        lStringBuilder.append("\n(").append((l+1)).append(")").append(" ").append(lSubSection.desc);
                        lStringBuilder = lSubSection.getSubSubSectionsStringBuilder(lStringBuilder, 1, String.valueOf((l+1)));
                    }
                }
            }
        }

        System.out.println("Current bill:" + lStringBuilder.toString());
        return lStringBuilder.toString();
    }
    public String toString() {
        return toString(true);
    }
    public void update() {
        try {
            if(toString(true).length() > 2048) {
                List<EmbedBuilder> lEmbedBuilders = toEmbeds(true, Color.yellow,"Message id: " + messageId, false);
                discordApi.getMessageById(messageId, discordApi.getUserById(initiatorId).get().openPrivateChannel().get()).get().edit(lEmbedBuilders);
            }
            else
                discordApi.getMessageById(messageId, discordApi.getUserById(initiatorId).get().openPrivateChannel().get()).get().edit(toEmbed(true));
            MainQuorum.saveBills();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getLastObject(ArrayList pArrayList) {
        return pArrayList.get(pArrayList.size()-1);
    }

    public void endIntroduction() {
        firstReadingFinished = true;
        Council lCouncil = councils.get(councilId);
        int i = 1;
        for (Amendment lAmendment: amendments) {
            try {
                SlashCommandListener.createMotionEnd(discordApi.getUserById(lAmendment.introducerId).get(), lCouncil, "Motion #" + (lCouncil.motionArrayList.size()+1) + ": Amendment " + i + " to " + title, lCouncil.amendmentMajority, lCouncil.amendmentTypeOfMajority, lAmendment.toString(), discordApi.getServerById(lCouncil.getServerId()).get(), messageId, (long) (i-1));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }

    //public MessageBuilder updateMessage(boolean isSubSection) {
    //    embed = new EmbedBuilder()
    //            .setTitle(title)
    //            .setDescription("Be it enacted by " + councils.get(councilId).getName() + ":");
    //    messageBuilder = new MessageBuilder();
    //    messageBuilder.setEmbed(embed)
    //            .addComponents(
    //                    ActionRow.of(
    //                            org.javacord.api.entity.message.component.Button.secondary("cancel" , "Cancel")
    //                    )
    //            )
    //            .addComponents(
    //                    ActionRow.of(
    //                            org.javacord.api.entity.message.component.Button.success("add_part" , "Add Part")
    //                    )
    //            );
    //    if(partArrayList.size()>0) {
    //        try {
    //            lApi.getMessageById(messageId, lApi.getUserById(initiatorId).get().openPrivateChannel().get()).get().addComponents(
    //                            ActionRow.of(
    //                                    org.javacord.api.entity.message.component.Button.success("add_division" , "Add Division")
    //                            )
    //                    )
    //                    .addComponents(
    //                            ActionRow.of(
    //                                    org.javacord.api.entity.message.component.Button.success("add_section" , "Add Section")
    //                            )
    //                    )
    //                    .addComponents(
    //                            ActionRow.of(
    //                                    org.javacord.api.entity.message.component.Button.success("add_sub_section" , "Add Sub-Section")
    //                            )
    //                    );
    //        } catch (InterruptedException | ExecutionException e) {
    //            throw new RuntimeException(e);
    //        }
    //        if(isSubSection)
    //            messageBuilder.addComponents(
    //                    ActionRow.of(
    //                            org.javacord.api.entity.message.component.Button.success("add_sub_sub_section" , "Add Sub-Section below this Sub Section")
    //                    )
    //            );
    //        messageBuilder
    //                .addComponents(
    //                        ActionRow.of(
    //                                org.javacord.api.entity.message.component.Button.danger("finish" , "Finish and propose Bill")
    //                        )
    //                );
    //
    //    }
    //    return messageBuilder;
    //}
}
