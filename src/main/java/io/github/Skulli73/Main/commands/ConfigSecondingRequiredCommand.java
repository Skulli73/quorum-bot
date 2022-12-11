package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ConfigSecondingRequiredCommand extends ConfigCommand{
    public ConfigSecondingRequiredCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        council.secondRequired = pInteraction.getOptions().get(0).getOptionBooleanValueByName("seconding_required").get();
    }
}
