package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ConfigAmendmentMajority extends  ConfigCommand{
    public ConfigAmendmentMajority(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "config bills amendment_majority";
        council.amendmentMajority = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("majority").get();
        council.amendmentTypeOfMajority = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("type_of_majority").get().intValue();
    }
}
