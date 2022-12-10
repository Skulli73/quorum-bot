package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ConfigRemoveProposeRole extends ConfigCommand{
    public ConfigRemoveProposeRole(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "propose_role";
        council.proposeRoleList.remove(pInteraction.getOptions().get(0).getOptionRoleValueByName("role").get().getId());
    }
}
