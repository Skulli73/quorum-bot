package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.timers;

public class WithdrawCommand extends CouncilCommand{
    public WithdrawCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);

    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        int motionId = pInteraction.getArguments().get(0).getDecimalValue().get().intValue()-1;
        if(motionId < council.motionArrayList.size()) {
            Motion lMotion = council.motionArrayList.get(motionId);
            if(!lMotion.completed) {
                if(isIntroductor(pApi, pInteraction, lMotion)) {
                    if (lMotion.isMoved) {
                        timers.get((int)council.getId()).cancel();
                    }
                    if (motionId == council.currentMotion) {
                        council.toNextMotion();
                    }
                    lMotion.completed = true;
                    TextChannel lAgendaChannel = council.getAgendaChannel();
                    try {
                        Message lMessage =lMotion.getMessage(pApi, lAgendaChannel);
                        lMessage.edit(lMessage.getEmbeds().get(0).toBuilder().setColor(Color.DARK_GRAY));
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    lMotion.deleteMessages(pApi);
                    SlashCommandListener.saveMotion(council, lMotion);
                    pInteraction.createImmediateResponder()
                            .append(pInteraction.getUser().getMentionTag() + " removed " + lMotion.getTitle() + "")
                            .respond();
                } else {
                    pInteraction.createImmediateResponder()
                            .append("You are not the author of this motion.")
                            .respond();
                }
            } else {
                pInteraction.createImmediateResponder()
                        .append("This motion is already completed")
                        .respond();
            }
        } else {
            pInteraction.createImmediateResponder()
                    .append("This motion does not exist")
                    .respond();
        }

    }

    public boolean isIntroductor(DiscordApi pApi, SlashCommandInteraction pInteraction, Motion pMotion) {
        return pMotion.introducerId == pInteraction.getUser().getId();
    }
}
