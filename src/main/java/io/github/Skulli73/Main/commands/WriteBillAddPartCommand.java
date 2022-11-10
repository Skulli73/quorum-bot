package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Part;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;

public class WriteBillAddPartCommand {
    public WriteBillAddPartCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        String lMessageId = pInteraction.getOptionByName("add_part").get().getOptionStringValueByName("bill_message_id").get();
        bills.get(lMessageId).partArrayList.add(new Part(pInteraction.getOptionByName("add_part").get().getOptionStringValueByName("title").get()));
        bills.get(lMessageId).update();
    }
}
