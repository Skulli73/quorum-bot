package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Optional;

import static io.github.Skulli73.Main.MainQuorum.councils;
import static io.github.Skulli73.Main.listeners.SlashCommandListener.saveCouncil;

public class ConfigForwardChannel extends ConfigCommand{
    public ConfigForwardChannel (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "forward_channel";
        Optional<ServerChannel> lForwardCouncilOptional = pInteraction.getOptions().get(0).getOptionChannelValueByName("channel");

        // Check if council to forward to exists
        if(lForwardCouncilOptional.isEmpty()) {
            Council lCouncil = council.getForwardCouncil();
            council.forwardCouncil = null;
            lCouncil.forwardCouncil = null;
            lCouncil.isForwardCouncil = false;
            councils.set((int) council.getId(), council);
            councils.set((int) lCouncil.getId(), lCouncil);
            saveCouncil(lCouncil);
            return;
        }

        // Forwards the motion
        if(lForwardCouncilOptional.get().getType().isTextChannelType()) {
            if(Council.isChannelFloor(lForwardCouncilOptional.get().asTextChannel().get(), councils)) {
                Council lForWardCouncil = Council.councilByFloorChannel(lForwardCouncilOptional.get().asTextChannel().get(), councils);
                council.forwardCouncil = Math.toIntExact(lForWardCouncil.getId());
                lForWardCouncil.legislationChannel = council.legislationChannel;
                lForWardCouncil.forwardCouncil = Math.toIntExact(council.getId());
                lForWardCouncil.isForwardCouncil = true;
                councils.set((int) lForWardCouncil.getId(), lForWardCouncil);
            } else
                pInteraction.getChannel().get().sendMessage("You did not mention a Floor Channel");
        }
    }
}
