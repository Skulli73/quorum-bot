package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ConfigShowCommand extends CouncilCommand{
    public ConfigShowCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        EmbedBuilder lEmbedBuilder      = new EmbedBuilder();
        lEmbedBuilder.addField("Floor Channel", "<#" + council.floorChannel + ">")
                .addField("Agenda Channel", "<#" + council.agendaChannel + ">")
                .addField("Minute Channel", "<#" + council.minuteChannel + ">")
                .addField("Legislation Channel", "<#" + council.legislationChannel + ">")
                .addField("Quorum", council.quorum*100 + "%")
                .addField("Standard Majority", council.standardMajority*100 + "%")
                .addField("Standard Type of Majority", SlashCommandListener.lTypeOfMajorityArray[council.standardMajorityType])
                .addField("Standard Introduction Majority", council.firstReadingMajority*100 + "%")
                .addField("Standard Type of Majority for Introduction", SlashCommandListener.lTypeOfMajorityArray[council.firstReadingTypeOfMajority])
                .addField("Amendment Majority", council.amendmentMajority*100 + "%")
                .addField("Type of Majority for Amendments", SlashCommandListener.lTypeOfMajorityArray[council.amendmentTypeOfMajority])
        ;
        pInteraction.createImmediateResponder().addEmbed(lEmbedBuilder).respond();
    }
}
