package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;

public abstract class WriteBillCommand {
    public WriteBillCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(bills.containsKey(pInteraction.getOptions().get(0).getOptionStringValueByName("bill_message_id").get()))
            if (!bills.get(pInteraction.getOptions().get(0).getOptionStringValueByName("bill_message_id").get()).draftFinished)
                this.executeCommand(pInteraction, pApi);
            else
                pInteraction.createImmediateResponder().append("This draft is already finished").respond();
        else
            pInteraction.createImmediateResponder().append("This message does not exist.").respond();
    }
    public abstract void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi);
}
