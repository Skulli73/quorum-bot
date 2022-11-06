package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class EndVoteCommand extends CouncilCommand {
    public EndVoteCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        Motion lMotion = council.motionArrayList.get(council.currentMotion);
        if(lMotion.isMoved) {
            pInteraction.createImmediateResponder()
                    .append(pInteraction.getUser().getMentionTag() + "ended the vote on " + lMotion.getTitle() + ".")
                    .respond();
            lMotion.endMotionVote(pApi, council, pInteraction, council.getCouncillorRole(pApi).getUsers().toArray());
        } else {
            pInteraction.createImmediateResponder()
                    .append("There is no active Motion")
                    .respond();
        }
    }


}
