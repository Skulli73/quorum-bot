package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class MotionTimeout extends ConfigCommand {
    public MotionTimeout(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "motion_timeout";
        council.timeOutTime = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("timeout_length").get();
    }
}
