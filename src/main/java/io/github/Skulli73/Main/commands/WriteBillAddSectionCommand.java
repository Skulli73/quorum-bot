package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Bill;
import io.github.Skulli73.Main.objects.Division;
import io.github.Skulli73.Main.objects.Part;
import io.github.Skulli73.Main.objects.Section;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;

public class WriteBillAddSectionCommand extends WriteBillCommand{
    public WriteBillAddSectionCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        String lMessageId = pInteraction.getOptionByName("add_section").get().getOptionStringValueByName("bill_message_id").get();
        ((Division)Bill.getLastObject(((Part)Bill.getLastObject(bills.get(lMessageId).partArrayList)).divisionArrayList))
                .sectionArrayList
                .add(new Section(pInteraction.getOptionByName("add_section").get().getOptionStringValueByName("section_title").get(), pInteraction.getOptionByName("add_section").get().getOptionStringValueByName("section_text").get()));
        bills.get(lMessageId).update();
        respondMessageSuccessful(pInteraction);
    }
}
