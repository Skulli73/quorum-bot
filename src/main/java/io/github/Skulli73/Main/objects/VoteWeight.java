package io.github.Skulli73.Main.objects;

import org.javacord.api.entity.permission.Role;

import static io.github.Skulli73.Main.MainQuorum.discordApi;

public class VoteWeight {
    public long roleId;
    public double votes;
    public VoteWeight(long pRoleId, double pVotes) {
        roleId = pRoleId;
        votes = pVotes;
    }
    public Role getRole() {
        if(discordApi.getRoleById(roleId).isPresent())
            return discordApi.getRoleById(roleId).get();
        else
            return null;
    }
}
