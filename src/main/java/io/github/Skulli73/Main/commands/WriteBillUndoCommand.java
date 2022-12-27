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
        boolean b = lBill.partArrayList.size() > 1;
        Part lPart = lBill.partArrayList.get(lBill.partArrayList.size()-1);

        if(lPart.divisionArrayList.size() > 1 || lPart.divisionArrayList.get(lPart.divisionArrayList.size()-1).sectionArrayList.size() > 1) {
            Division lDivision = lPart.divisionArrayList.get(lPart.divisionArrayList.size()-1);
            if(!(lDivision.sectionArrayList.size() > 1)) {
                lPart.divisionArrayList.remove(lPart.divisionArrayList.size()-1);
                return;
            }

            Section lSection = lDivision.sectionArrayList.get(lDivision.sectionArrayList.size()-1);
            if(!(lSection.subSectionArrayList.size() > 0)) {
                lDivision.sectionArrayList.remove(lDivision.sectionArrayList.size() - 1);
                return;
            }

            SubSection lSubSection = lSection.subSectionArrayList.get(lSection.subSectionArrayList.size()-1);
            if(!lSubSection.hasSubSections()) {
                lSection.subSectionArrayList.remove(lSection.subSectionArrayList.size()-1);
                return;
            }

            lSubSection.deleteLowestSubSection();
            lSection.subSectionArrayList.set(lSection.subSectionArrayList.size()-1, lSubSection);

            lPart.divisionArrayList.set(lPart.divisionArrayList.size()-1,lDivision);


            lBill.partArrayList.set(lBill.partArrayList.size()-1, lPart);
        } else if(b) lBill.partArrayList.remove(lBill.partArrayList.size()-1);

        bills.put(Long.toString(lBill.messageId), lBill);
        lBill.update();
        saveBills();
    }
}
