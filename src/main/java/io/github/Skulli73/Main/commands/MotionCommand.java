package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

public class MotionCommand extends CouncilCommand{
    public MotionCommand(SlashCommandInteraction interaction, DiscordApi pApi) {
        super(interaction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(council.hasProposeRole(pInteraction.getUser())) {
            pInteraction
                    .createImmediateResponder()
                    .setContent(pInteraction.getUser().getMentionTag() + " has introduced a motion.")
                    .respond();
            try {
                SlashCommandListener.createMotion(pInteraction);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else
            pInteraction
                    .createImmediateResponder()
                    .setContent(pInteraction.getUser().getMentionTag() + ", you do not have the role to propose motions.")
                    .respond();
    }
}
