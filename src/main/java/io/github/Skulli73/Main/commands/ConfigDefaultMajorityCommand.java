package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ConfigDefaultMajorityCommand extends ConfigCommand {

    public ConfigDefaultMajorityCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "default_majority";
        council.standardMajorityType = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("type_of_majority").get().intValue();
        council.standardMajority = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("majority").get();
    }
}
