package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;
import static io.github.Skulli73.Main.MainQuorum.saveBills;

public class WriteBillUndoCommand extends WriteBillCommand{
    public WriteBillUndoCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        Bill lBill = bills.get(pInteraction.getOptions().get(0).getOptionStringValueByName("bill_message_id").get());
        boolean b = false;
        if(lBill.partArrayList.size() > 1)
            b = true;
        Part lPart = lBill.partArrayList.get(lBill.partArrayList.size()-1);
        if(lPart.divisionArrayList.size() > 1 || lPart.divisionArrayList.get(lPart.divisionArrayList.size()-1).sectionArrayList.size() > 1) {
            Division lDivision = lPart.divisionArrayList.get(lPart.divisionArrayList.size()-1);
            if(lDivision.sectionArrayList.size() > 1) {
                Section lSection = lDivision.sectionArrayList.get(lDivision.sectionArrayList.size()-1);
                if(lSection.subSectionArrayList.size() > 0) {
                    SubSection lSubSection = lSection.subSectionArrayList.get(lSection.subSectionArrayList.size()-1);
                    if(lSubSection.hasSubSections()) {
                        lSubSection.deleteLowestSubSection();
                    } else
                        lSection.subSectionArrayList.remove(lSection.subSectionArrayList.size()-1);
                    lSection.subSectionArrayList.set(lSection.subSectionArrayList.size()-1, lSubSection);
                } else
                    lDivision.sectionArrayList.remove(lDivision.sectionArrayList.size()-1);
                lPart.divisionArrayList.set(lPart.divisionArrayList.size()-1,lDivision);
            } else
                lPart.divisionArrayList.remove(lPart.divisionArrayList.size()-1);
            lBill.partArrayList.set(lBill.partArrayList.size()-1, lPart);
        } else if(b)
            lBill.partArrayList.remove(lBill.partArrayList.size()-1);
        bills.put(Long.toString(lBill.messageId), lBill);
        lBill.update();
        saveBills();
    }
}
