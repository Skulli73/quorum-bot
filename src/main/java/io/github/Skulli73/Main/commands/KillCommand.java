package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class KillCommand extends WithdrawCommand{
    public KillCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);

    }

    @Override
    public boolean isIntroductor(DiscordApi pApi, SlashCommandInteraction pInteraction, Motion pMotion) {
        return true;
    }
}
