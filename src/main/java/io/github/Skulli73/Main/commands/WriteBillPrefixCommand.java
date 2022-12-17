package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Bill;
import io.github.Skulli73.Main.objects.SubSection;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;
import static io.github.Skulli73.Main.MainQuorum.saveBills;

public class WriteBillPrefixCommand extends WriteBillCommand{
    public WriteBillPrefixCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        String billMessageId = pInteraction.getOptions().get(0).getOptionStringValueByName("bill_message_id").get();
        Bill lBill = bills.get(billMessageId);
        lBill.partArrayList.get(0).divisionArrayList.get(0).sectionArrayList.get(0).subSectionArrayList.add(new SubSection(pInteraction.getOptions().get(0).getOptionStringValueByName("text").get()));
        bills.put(String.valueOf(lBill.messageId), lBill);
        saveBills();
        lBill.update();
    }
}
