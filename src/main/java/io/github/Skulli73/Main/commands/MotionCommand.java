package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.Main.councils;
import static io.github.Skulli73.Main.Main.councilsPath;

public class MotionCommand {
    public MotionCommand(SlashCommandInteraction interaction, DiscordApi pApi) {
        if(councils.size()==0) {
            interaction.getChannel().get().sendMessage("This is no floor channel of a council, <@" + interaction.getUser().getId() + ">");
        }
        else if(councils.get(0).isChannelFloor(interaction.getChannel().get(), pApi, councils, councilsPath)) {
            if(pApi.getRoleById(councils.get(0).councilByFloorChannel(interaction.getChannel().get(), pApi, councils, councilsPath).getProposeRoleId()).get().hasUser(interaction.getUser())) {
                interaction
                        .createImmediateResponder()
                        .setContent(interaction.getUser().getMentionTag() + " has introduced a motion.")
                        .respond();
                try {
                    SlashCommandListener.createMotion(interaction);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else
                interaction
                        .createImmediateResponder()
                        .setContent(interaction.getUser().getMentionTag() + ", you do not have the role to propose Bills.")
                        .respond();
        }
        else
            interaction.getChannel().get().sendMessage("This is no floor channel of a council, <@" + interaction.getUser().getId() + ">");
    }
}
