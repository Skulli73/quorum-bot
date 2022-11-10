package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.councils;

public class MotionCommand extends CouncilCommand{
    public MotionCommand(SlashCommandInteraction interaction, DiscordApi pApi) {
        super(interaction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(pApi.getRoleById(Council.councilByFloorChannel(pInteraction.getChannel().get(), councils).getProposeRoleId()).get().hasUser(pInteraction.getUser())) {
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
