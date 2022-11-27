package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.VoteWeight;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.List;

public class ConfigVoteWeightsCommand extends ConfigCommand{
    public ConfigVoteWeightsCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void changeConfig(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        configName = "vote_weights";
        long lRoleId = pInteraction.getOptionByName(configName).get().getOptionRoleValueByName("role").get().getId();
        double lVoteWeight = pInteraction.getOptionByName(configName).get().getOptionDecimalValueByName("vote_weight").get();
        List<VoteWeight> lExistingVoteWeights = council.voteWeightArrayList.stream().filter(c -> c.roleId == lRoleId).toList();
        if (!lExistingVoteWeights.isEmpty()) {
            council.voteWeightArrayList.remove(lExistingVoteWeights.get(0));
        }
        council.voteWeightArrayList.add(new VoteWeight(lRoleId, lVoteWeight));
    }
}
