package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class SecondCommand extends CouncilCommand{
    public SecondCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(council.hasProposeRole(pInteraction.getUser())) {
            int lMotionId = pInteraction.getOptionDecimalValueByName("motion_id").get().intValue()-1;
            if(lMotionId < council.motionArrayList.size()) {
                Motion lMotion = council.motionArrayList.get(lMotionId);
                if(lMotion.isMoved) {
                    pInteraction.createImmediateResponder().append("Motions that are moved cannot be seconded.");
                    return;
                }
                if(lMotion.completed) {
                    pInteraction.createImmediateResponder().append("This motion is already completed and cannot be seconded.");
                    return;
                }
                if(lMotion.seconderIdList.contains(pInteraction.getUser().getId())) {
                    pInteraction.createImmediateResponder().append("You already seconded this motion");
                    return;
                }
                if(lMotion.introducerId == pInteraction.getUser().getId()) {
                    pInteraction.createImmediateResponder().append("Are you seriously trying to second your own motion?");
                    return;
                }
                lMotion.seconderIdList.add(pInteraction.getUser().getId());
                council.getMinuteChannel().sendMessage(lMotion.getTitle() + " was seconded by " + pInteraction.getUser().getMentionTag()+".");
                SlashCommandListener.saveMotion(council, lMotion);
            } else
                pInteraction.createImmediateResponder().append("This motion does not exist.");
        } else
            pInteraction.createImmediateResponder().append("You are required to have a Propose Role to second a motion");
    }
}
