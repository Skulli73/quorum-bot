package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Division;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;

public class WriteBillAddDivsionCommand extends WriteBillCommand{
    public WriteBillAddDivsionCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        String lMessageId = pInteraction.getOptionByName("add_division").get().getOptionStringValueByName("bill_message_id").get();
        bills.get(lMessageId).partArrayList.get(bills.get(lMessageId).partArrayList.size()-1).divisionArrayList.add(new Division(pInteraction.getOptionByName("add_division").get().getOptionStringValueByName("title").get()));
        bills.get(lMessageId).update();
    }
}
