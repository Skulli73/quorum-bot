package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.VoteWeight;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.text.DecimalFormat;

public class ConfigShowCommand extends CouncilCommand{
    public ConfigShowCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        EmbedBuilder lEmbedBuilder      = new EmbedBuilder();
        StringBuilder lVoteWeights      = new StringBuilder();
        StringBuilder lProposeRoles     = new StringBuilder();
        DecimalFormat lFormat = new DecimalFormat("0.#");
        for(VoteWeight lVoteWeight : council.voteWeightArrayList) {
            lVoteWeights.append(pApi.getRoleById(lVoteWeight.roleId).get().getMentionTag()).append(": ").append(lFormat.format(lVoteWeight.votes)).append("\n");
        }
        for(Long lProposeRole : council.proposeRoleList) {
            lProposeRoles.append(pApi.getRoleById(lProposeRole).get().getMentionTag() + "\n");
        }
        lEmbedBuilder.setTitle(council.getName() + " Configurations")
                .addField("Floor Channel", "<#" + council.floorChannel + ">")
                .addField("Agenda Channel", "<#" + council.agendaChannel + ">")
                .addField("Minute Channel", "<#" + council.minuteChannel + ">")
                .addField("Legislation Channel", "<#" + council.legislationChannel + ">")
                .addField("Quorum", council.quorum*100 + "%")
                .addField("Abstentions count to Quorum", String.valueOf(council.absentionsCountToQuorum))
                .addField("Standard Majority", council.standardMajority*100 + "%")
                .addField("Standard Type of Majority", SlashCommandListener.lTypeOfMajorityArray[council.standardMajorityType])
                .addField("Standard Introduction Majority", council.firstReadingMajority*100 + "%")
                .addField("Standard Type of Majority for Introduction", SlashCommandListener.lTypeOfMajorityArray[council.firstReadingTypeOfMajority])
                .addField("Amendment Majority", council.amendmentMajority*100 + "%")
                .addField("Type of Majority for Amendments", SlashCommandListener.lTypeOfMajorityArray[council.amendmentTypeOfMajority])
                .addField("Motion Timeout Time", lFormat.format(council.timeOutTime) + "h")
                .addField("Vote Weights", lVoteWeights.toString())
                .addField("Propose Roles", lProposeRoles.toString())
                .addField("Seconding Required", String.valueOf(council.secondRequired))
        ;
        String lForwardChannel = "/";
        if(council.hasForwardChannel()) {
            lForwardChannel = council.getForwardCouncil().getName();
        }
        lEmbedBuilder.addField("Forward Council", lForwardChannel);
        pInteraction.createImmediateResponder().addEmbed(lEmbedBuilder).respond();
    }
}
