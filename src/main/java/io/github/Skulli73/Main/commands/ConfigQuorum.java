package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ConfigQuorum extends ConfigCommand {

    public ConfigQuorum(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "quorum";
        council.quorum = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("quorum_percentage").get();
        council.absentionsCountToQuorum = pInteraction.getOptionByName(configName).get().getOptionBooleanValueByName("quorum_absentions_count_quorum").get();
    }
}
