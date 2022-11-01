package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.Main.councils;
import static io.github.Skulli73.Main.Main.councilsPath;

public class MotionCommand {
    private final DiscordApi lApi;
    public MotionCommand(SlashCommandInteraction interaction, DiscordApi pApi) {
        lApi = pApi;
        if(councils.size()==0) {
            interaction.getChannel().get().sendMessage("This is no floor channel of a council, <@" + interaction.getUser().getId() + ">");
        }
        else if(councils.get(0).isChannelFloor((TextChannel) interaction.getChannel().get(), lApi, councils, councilsPath)) {
            try {
                SlashCommandListener.createMotion(interaction);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else
            interaction.getChannel().get().sendMessage("This is no floor channel of a council, <@" + interaction.getUser().getId() + ">");
    }
}
