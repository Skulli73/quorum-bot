package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.Main.councils;
import static io.github.Skulli73.Main.Main.councilsPath;

public abstract class CouncilCommand {
    Council council;
    public CouncilCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(councils.size() != 0) {
            if(councils.get(0).isChannelFloor(pInteraction.getChannel().get(), pApi, councils, councilsPath)) {
                council = councils.get(0).councilByFloorChannel(pInteraction.getChannel().get(), pApi, councils, councilsPath);
                this.executeCommand(pInteraction, pApi);
            }
            else
                pInteraction
                        .createImmediateResponder()
                        .setContent("This is not a floor channel.")
                        .respond();
        }
        else
            pInteraction
                    .createImmediateResponder()
                    .setContent("This is not a floor channel.")
                    .respond();
    }

    public abstract void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi);
}
