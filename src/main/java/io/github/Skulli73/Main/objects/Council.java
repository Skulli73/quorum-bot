package io.github.Skulli73.Main.objects;

import io.github.Skulli73.Main.MainQuorum;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.discordApi;

public class Council {
    private                    String name;
    public long               floorChannel, agendaChannel, minuteChannel, legislationChannel, councillorRoleId, proposeRoleId;
    private final int          id;
    private final long         serverId;
    public ArrayList<Motion>   motionArrayList;
    public int                 currentMotion;
    public int                 nextMotion;
    public double              standardMajority;

    public int                 standardMajorityType;

    public double               timeOutTime;

    public double               quorum;

    public boolean              absentionsCountToQuorum;

    public double               firstReadingMajority;
    public int                  firstReadingTypeOfMajority;

    public double               amendmentMajority;
    public int                  amendmentTypeOfMajority;

    public ArrayList<VoteWeight> voteWeightArrayList;



    public Council(String pName, @NotNull TextChannel pFloorChannel, @NotNull TextChannel pAgendaChannel, @NotNull TextChannel pResultChannel, long pCouncillorRoleId, long pServer, int pId) {
        this(pName, pFloorChannel.getId(), pAgendaChannel.getId(), pResultChannel.getId(), pCouncillorRoleId, pServer, pId);
    }

    public Council(String pName, long pFloorChannel, long pAgendaChannel, long pMinuteChannel, long pCouncillorRoleId, long pServer, int pId) {
        name                = pName;
        floorChannel        = pFloorChannel;
        agendaChannel       = pAgendaChannel;
        minuteChannel       = pMinuteChannel;
        id                  = pId;
        serverId            = pServer;
        councillorRoleId    = pCouncillorRoleId;
        proposeRoleId       = councillorRoleId;
        legislationChannel = minuteChannel;
        motionArrayList     = new ArrayList<>();
        standardMajority    = 0.501;
        standardMajorityType= 0;
        timeOutTime         = 24;
        nextMotion          = 1;
        currentMotion       = 0;
        quorum              = 0.5;
        absentionsCountToQuorum = true;
        MainQuorum.timers.add(id, null);
        firstReadingMajority = 0.501;
        firstReadingTypeOfMajority = 2;
        amendmentMajority = 0.501;
        amendmentTypeOfMajority = 0;
        voteWeightArrayList = new ArrayList<>();
        voteWeightArrayList.add(new VoteWeight(councillorRoleId, 1));
    }


    // Getters & Setters

    public TextChannel  getAgendaChannel() { return discordApi.getServerById(serverId).get().getTextChannelById(agendaChannel).get(); }
    public TextChannel  getFloorChannel()  { return discordApi.getServerById(serverId).get().getTextChannelById(floorChannel).get();  }
    public TextChannel getMinuteChannel() { return discordApi.getServerById(serverId).get().getTextChannelById(minuteChannel).get(); }
    public Role         getCouncillorRole()       { return discordApi.getRoleById(councillorRoleId).get(); }
    public String       getName() { return name; }
    public long         getId() { return id; }
    public long         getServerId() { return serverId; }
    public long         getProposeRoleId(){ return proposeRoleId; }

    public void setAgendaChannel(TextChannel pNewTextChannel) { agendaChannel = pNewTextChannel.getId(); }
    public void setFloorChannel(TextChannel pNewTextChannel)  { floorChannel = pNewTextChannel.getId();  }
    public void setMinuteChannel(TextChannel pNewTextChannel) { minuteChannel = pNewTextChannel.getId(); }
    public void setName(String pName) { name = pName; }
    public void setProposeRole(Role pProposeRole){proposeRoleId = pProposeRole.getId();}
    public void setRoleChannel(long pCouncillorRoleId) {councillorRoleId = pCouncillorRoleId;}


    public static boolean isChannelFloor(TextChannel pChannel, ArrayList<Council> pCouncils) {
        System.out.println(pCouncils.size());
        return pCouncils.stream().anyMatch(lCouncil -> {
            try {
                System.out.println(lCouncil.getServerId() + " - " + pChannel.getMessages(1).get().getNewestMessage().get().getServer().get().getId());
                System.out.println(lCouncil.floorChannel + " - " + pChannel.getId());
                return lCouncil.getServerId() == pChannel.getMessages(1).get().getNewestMessage().get().getServer().get().getId() && lCouncil.floorChannel == pChannel.getId();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Failed to pull server id");
                return false;
            }
        });
    }


    public static Council councilByFloorChannel(TextChannel pChannel, ArrayList<Council> pCouncils) {
        return pCouncils.stream().filter(lCouncil -> {
            try {
                return lCouncil.getServerId() == pChannel.getMessages(1).get().getNewestMessage().get().getServer().get().getId() && lCouncil.floorChannel == pChannel.getId();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Failed to pull server id");
                return false;
            }
        }).findFirst().orElse(null);
    }

    public void toNextMotion () {
        boolean end = false;
        while(!end) {
            currentMotion++;
            if(currentMotion < motionArrayList.size()) {
                Motion lMotion = motionArrayList.get(currentMotion);
                if(!lMotion.isMoved && !lMotion.completed) {
                    end = true;
                }
            } else {
                end = true;
            }
        }
        nextMotion = currentMotion + 1;
    }

    public TextChannel getLegislationChannel() {
        return discordApi.getChannelById(legislationChannel).get().asTextChannel().get();
    }
}
