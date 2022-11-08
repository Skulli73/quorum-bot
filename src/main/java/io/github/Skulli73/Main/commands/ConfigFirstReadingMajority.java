package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ConfigFirstReadingMajority extends  ConfigCommand{
    public ConfigFirstReadingMajority(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "config bills first_reading_majority";
        council.firstReadingMajority = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("majority").get();
        council.firstReadingTypeOfMajority = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("type_of_majority").get().intValue();
    }
}
