package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.Main.*;

public abstract class ConfigCommand {
    String configName;
    Council council;
    public ConfigCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "config_name";
        if(councils.size() != 0) {
            if(councils.get(0).isChannelFloor(pInteraction.getChannel().get(), pApi, councils, councilsPath)) {
                council = councils.get(0).councilByFloorChannel(pInteraction.getChannel().get(), lApi, councils, councilsPath);
                this.changeConfig(pInteraction, pApi);
                SlashCommandListener.saveCouncil(council);
                pInteraction
                        .createImmediateResponder()
                        .setContent("The config \"" + configName + "\" was successfully changed.")
                        .respond();
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

    public abstract void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi);
}
