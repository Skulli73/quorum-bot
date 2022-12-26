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
        council.amendmentMajority = pInteraction.getOptions().get(0).getOptions().get(0).getOptionDecimalValueByName("majority").get();
        council.amendmentTypeOfMajority = pInteraction.getOptions().get(0).getOptions().get(0).getOptionDecimalValueByName("type_of_majority").get().intValue();
    }
}
