package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;
import io.github.Skulli73.Main.MainQuorum;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.lApi;

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

    public Bill(String pTitle, int pCouncilId, long pInitiatorId) {
        title           =pTitle;
        councilId       =pCouncilId;
        partArrayList = new ArrayList<Part>();
        partArrayList.add(new Part(""));
        initiatorId = pInitiatorId;
    }

    public EmbedBuilder toEmbed(String pDesc) {
        EmbedBuilder lEmbedBuilder = new EmbedBuilder();
        lEmbedBuilder.setTitle(title);
        lEmbedBuilder.setDescription(toString());
        lEmbedBuilder.setFooter("Message id: " + messageId);
        return lEmbedBuilder;
    }
    public EmbedBuilder toEmbed(boolean pEditor) {
        if(pEditor)
            return toEmbed(toString());
        else
            return toEmbed(toString().replace("0." , ""));
    }
    public String toString() {
        StringBuilder lStringBuilder = new StringBuilder();

        for(int i = 0; i < partArrayList.size(); i++) {
            Part lPart = partArrayList.get(i);
            if(i!=0) {
                lStringBuilder.append("\n__** Part " + i + " ").append(lPart.title).append("**__");
            }
            for(int j = 0; j<lPart.divisionArrayList.size(); j++) {
                Division lDivision = lPart.divisionArrayList.get(j);
                if(j!=0) {
                    lStringBuilder.append("\n** Division " + i + "." + j + " ").append(lDivision.title).append("**");
                }
                for(int k = 0; k<lDivision.sectionArrayList.size(); k++) {
                    Section lSection = lDivision.sectionArrayList.get(k);
                    if(k!=0) {
                        lStringBuilder.append("\n**").append(i + "." + j + "." + k).append(" ").append(lSection.title).append("**").append("\n").append(lSection.desc);
                    }
                    for(int l = 0; l<lSection.subSectionArrayList.size(); l++) {
                        SubSection lSubSection = lSection.subSectionArrayList.get(l);
                        lStringBuilder.append("\n(").append(i + "." + j + "." + k + "." + (l+1)).append(")").append(" ").append(lSubSection.desc);
                        lStringBuilder = lSubSection.getSubSubSectionsStringBuilder(lStringBuilder, 1, i + "." + j + "." + k + "." + (l+1));
                    }
                }
            }
        }
        System.out.println("Current bill:" + lStringBuilder.toString());
        return lStringBuilder.toString();
    }
    public void update() {
        try {
            lApi.getMessageById(messageId, lApi.getUserById(initiatorId).get().openPrivateChannel().get()).get().edit(toEmbed(true));
            MainQuorum.saveBills();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getLastObject(ArrayList pArrayList) {
        return pArrayList.get(pArrayList.size()-1);
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
