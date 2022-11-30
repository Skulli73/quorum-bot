package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ConfigCouncillorRoleCommand extends ConfigCommand{
    public ConfigCouncillorRoleCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "councillor_role";
        council.councillorRoleId = pInteraction.getOptions().get(0).getOptionRoleValueByName("role").get().getId();
    }
}
