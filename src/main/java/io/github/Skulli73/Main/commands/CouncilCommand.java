package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.councils;

public abstract class CouncilCommand {
    Council council;
    public CouncilCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(Council.isChannelFloor(pInteraction.getChannel().get(), councils)) {
            council = Council.councilByFloorChannel(pInteraction.getChannel().get(), councils);
            this.executeCommand(pInteraction, pApi);
        }
        else
            pInteraction
                    .createImmediateResponder()
                    .setContent("This is not a floor channel.")
                    .respond();
    }

    public abstract void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi);
}
