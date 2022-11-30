package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;

public class WriteBillAddSubSectionCommand extends WriteBillCommand{
    public WriteBillAddSubSectionCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        String lMessageId = pInteraction.getOptionByName("add_subsection").get().getOptionStringValueByName("bill_message_id").get();
        long subSectionLevel = pInteraction.getOptionByName("add_subsection").get().getOptionLongValueByName("level_of_subsection").get();
        String lText = pInteraction.getOptionByName("add_subsection").get().getOptionStringValueByName("text").get();
        Section lSection = (Section) Bill.getLastObject(
                ((Division)Bill.getLastObject(
                        ((Part)Bill.getLastObject(bills.get(lMessageId).partArrayList))
                                .divisionArrayList))
                        .sectionArrayList);
        if(lSection.subSectionArrayList.size() !=0 && subSectionLevel != 0) {
            lSection.subSectionArrayList.get(lSection.subSectionArrayList.size()-1).newSubSection(lText, Math.toIntExact(subSectionLevel));
            respondMessageSuccessful(pInteraction);
        } else if (subSectionLevel == 0) {
            lSection.subSectionArrayList.add(new SubSection(lText));
            respondMessageSuccessful(pInteraction);
        } else {
            lSection.subSectionArrayList.add(new SubSection(""));
            lSection.subSectionArrayList.get(0).newSubSection(lText, Math.toIntExact(subSectionLevel));
            respondMessageSuccessful(pInteraction);
        }
        bills.get(lMessageId).
                partArrayList.get(bills.get(lMessageId).partArrayList.size()-1)
                .divisionArrayList.get(bills.get(lMessageId).partArrayList.get(bills.get(lMessageId).partArrayList.size()-1).divisionArrayList.size()-1)
                .sectionArrayList.set(
                        bills.get(lMessageId).partArrayList.get(bills.get(lMessageId).partArrayList.size()-1)
                                .divisionArrayList.get(bills.get(lMessageId).partArrayList.get(bills.get(lMessageId).partArrayList.size()-1).divisionArrayList.size()-1).sectionArrayList.size()-1
                        , lSection);
        bills.get(lMessageId).update();
    }
}
