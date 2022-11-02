package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.Main.*;

public class ConfigDefaultMajorityCommand {

    public ConfigDefaultMajorityCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        if(councils.size() != 0) {
            if(councils.get(0).isChannelFloor(pInteraction.getChannel().get(), pApi, councils, councilsPath)) {
                Council lCouncil = councils.get(0).councilByFloorChannel(pInteraction.getChannel().get(), lApi, councils, councilsPath);
                lCouncil.standardMajorityType = pInteraction.getOptionByName("default_majority").get().getOptionDecimalValueByName("type_of_majority").get().intValue();
                lCouncil.standardMajority = pInteraction.getOptionByName("default_majority").get().getOptionDecimalValueByName("majority").get();
                SlashCommandListener.saveCouncil(lCouncil);
                pInteraction
                        .createImmediateResponder()
                        .setContent("The config \"default_majority\" was successfully changed.")
                        .respond();
            }
            else
                pInteraction
                        .createImmediateResponder()
                        .setContent("This is not a floor channel.")
                        .respond();
        }
        else
            pInteraction
                    .createImmediateResponder()
                    .setContent("This is not a floor channel.")
                    .respond();
    }
}
