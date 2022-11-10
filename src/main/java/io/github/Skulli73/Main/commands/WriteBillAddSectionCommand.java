package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Section;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;

public class WriteBillAddSectionCommand {
    public WriteBillAddSectionCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        String lMessageId = pInteraction.getOptionByName("add_section").get().getOptionStringValueByName("bill_message_id").get();
        bills.get(lMessageId).partArrayList.get(bills.get(lMessageId).partArrayList.size()-1).divisionArrayList.get(bills.get(lMessageId).partArrayList.get(bills.get(lMessageId).partArrayList.size()-1).divisionArrayList.size()-1).sectionArrayList
                .add(new Section(pInteraction.getOptionByName("add_section").get().getOptionStringValueByName("section_title").get(), pInteraction.getOptionByName("add_section").get().getOptionStringValueByName("section_text").get()));
        bills.get(lMessageId).update();
    }
}
