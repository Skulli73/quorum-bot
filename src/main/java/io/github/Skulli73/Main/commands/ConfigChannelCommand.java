package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Optional;

public class ConfigChannelCommand extends ConfigCommand{
    public ConfigChannelCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "channel";
        Optional<ServerChannel> lOptionalFloorChannel = pInteraction.getOptions().get(0).getOptionChannelValueByName("floorchannel");
        if(lOptionalFloorChannel.isPresent()) {
            if(lOptionalFloorChannel.get().getType().isTextChannelType()) {
                council.floorChannel = lOptionalFloorChannel.get().getId();
            } else {
                pInteraction.createImmediateResponder().append("Invalid Floor Channel").respond();
                return;
            }
        }

        Optional<ServerChannel> lOptionalAgendaChannel = pInteraction.getOptions().get(0).getOptionChannelValueByName("agendachannel");
        if(lOptionalAgendaChannel.isPresent()) {
            if(lOptionalAgendaChannel.get().getType().isTextChannelType()) {
                council.agendaChannel = lOptionalAgendaChannel.get().getId();
            } else {
                pInteraction.createImmediateResponder().append("Invalid Agenda Channel").respond();
                return;
            }
        }

        Optional<ServerChannel> lOptionalResultChannel = pInteraction.getOptions().get(0).getOptionChannelValueByName("minutechannel");
        if(lOptionalResultChannel.isPresent()) {
            if(lOptionalResultChannel.get().getType().isTextChannelType()) {
                council.minuteChannel = lOptionalResultChannel.get().getId();
            } else {
                pInteraction.createImmediateResponder().append("Invalid Minute Channel").respond();
                return;
            }
        }

        Optional<ServerChannel> lOptionalLegislationChannel = pInteraction.getOptions().get(0).getOptionChannelValueByName("legislationchannel");
        if(lOptionalLegislationChannel.isPresent()) {
            if(lOptionalLegislationChannel.get().getType().isTextChannelType()) {
                council.legislationChannel = lOptionalLegislationChannel.get().getId();
            } else {
                pInteraction.createImmediateResponder().append("Invalid Legislation Channel").respond();
                return;
            }
        }
    }
}
