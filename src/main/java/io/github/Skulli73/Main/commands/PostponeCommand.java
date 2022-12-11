package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.councils;

public class PostponeCommand extends CouncilCommand{
    public PostponeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(council.currentMotion < council.motionArrayList.size()) {
            Motion lMotion = council.motionArrayList.get(council.currentMotion);
            if(lMotion.isMoved) {
                lMotion.isMoved = false;
                lMotion.ayeVotes.clear();
                lMotion.nayVotes.clear();
                lMotion.abstainVotes.clear();
                lMotion.notVoted.clear();
                int i = 0;
                for(String lMessageId :lMotion.dmMessages) {
                    try {
                        Message lMessage = pApi.getMessageById(lMessageId, pApi.getUserById(lMotion.dmMessagesCouncillors.get(i)).get().openPrivateChannel().get()).get();
                        lMessage.delete();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                lMotion.dmMessages.clear();
                council.getFloorChannel().asServerTextChannel().get().updateTopic("No active Motion");
                pInteraction.createImmediateResponder().append("The motion was postponed and is no longer moved.").respond();
                council.getMinuteChannel().sendMessage(lMotion.getTitle() + " was postponed.");
                council.motionArrayList.set(lMotion.id, lMotion);
                councils.set((int) council.getId(), council);
                SlashCommandListener.saveCouncil(council);
            } else
                pInteraction.createImmediateResponder().append("The motion is not moved.");
        } else
            pInteraction.createImmediateResponder().append("There is no active motion.").respond();
    }
}
