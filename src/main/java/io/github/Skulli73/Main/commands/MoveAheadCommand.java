package io.github.Skulli73.Main.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

public class MoveAheadCommand extends CouncilCommand {
    public MoveAheadCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        int lMotionId = pInteraction.getOptionLongValueByName("motion_id").get().intValue()-1;
        if(council.motionArrayList.size() > lMotionId) {
            if(!council.motionArrayList.get(lMotionId).completed) {
                MoveCommand.executeMove(pInteraction, pApi, council, lMotionId);
            } else
                pInteraction.createImmediateResponder().append("This Motion is already completed").respond();
        } else
            pInteraction.createImmediateResponder().append("This Motion does not exist").respond();
    }
}
