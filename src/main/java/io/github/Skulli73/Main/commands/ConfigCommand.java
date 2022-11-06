package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public abstract class ConfigCommand extends CouncilCommand {
    String configName;
    public ConfigCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "config_name";
        this.changeConfig(pInteraction, pApi);
        SlashCommandListener.saveCouncil(council);
        pInteraction
                .createImmediateResponder()
                .setContent("The config \"" + configName + "\" was successfully changed.")
                .respond();
    }

    public abstract void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi);
}
