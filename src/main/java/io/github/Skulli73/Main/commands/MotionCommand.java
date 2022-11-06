package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.Main.councils;
import static io.github.Skulli73.Main.Main.councilsPath;

public class MotionCommand extends CouncilCommand{
    public MotionCommand(SlashCommandInteraction interaction, DiscordApi pApi) {
        super(interaction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(pApi.getRoleById(councils.get(0).councilByFloorChannel(pInteraction.getChannel().get(), pApi, councils, councilsPath).getProposeRoleId()).get().hasUser(pInteraction.getUser())) {
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
                    .setContent(pInteraction.getUser().getMentionTag() + ", you do not have the role to propose Bills.")
                    .respond();
    }
}
