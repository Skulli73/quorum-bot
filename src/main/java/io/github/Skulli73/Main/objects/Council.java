package io.github.Skulli73.Main.objects;

import io.github.Skulli73.Main.MainQuorum;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Council {
    private                    String name;
    private long               floorChannel, agendaChannel, resultChannel, councillorRoleId, proposeRoleId;
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



    public Council(String pName, @NotNull TextChannel pFloorChannel, @NotNull TextChannel pAgendaChannel, @NotNull TextChannel pResultChannel, long pCouncillorRoleId, long pServer, int pId) {
        this(pName, pFloorChannel.getId(), pAgendaChannel.getId(), pResultChannel.getId(), pCouncillorRoleId, pServer, pId);
    }

    public Council(String pName, long pFloorChannel, long pAgendaChannel, long pResultChannel, long pCouncillorRoleId, long pServer, int pId) {
        name                = pName;
        floorChannel        = pFloorChannel;
        agendaChannel       = pAgendaChannel;
        resultChannel       = pResultChannel;
        id                  = pId;
        serverId            = pServer;
        councillorRoleId    = pCouncillorRoleId;
        proposeRoleId       = councillorRoleId;
        motionArrayList     = new ArrayList<>();
        standardMajority = 0.501;
        standardMajorityType = 0;
        timeOutTime = 24;
        nextMotion = 1;
        currentMotion = 0;
        quorum = 0.5;
        absentionsCountToQuorum = true;
        MainQuorum.timers.add(id, null);
        firstReadingMajority = 0.501;
        firstReadingTypeOfMajority = 2;
        amendmentMajority = 0.501;
        amendmentTypeOfMajority = 0;
    }


    // Getters & Setters

    public TextChannel  getAgendaChannel(DiscordApi pApi) { return pApi.getServerById(serverId).flatMap(serverById -> serverById.getTextChannelById(agendaChannel)).get(); }
    public TextChannel  getFloorChannel(DiscordApi pApi)  { return pApi.getServerById(serverId).flatMap(serverById -> serverById.getTextChannelById(floorChannel)).get();  }
    public TextChannel  getResultChannel(DiscordApi pApi) { return pApi.getServerById(serverId).flatMap(serverById -> serverById.getTextChannelById(resultChannel)).get(); }
    public Role         getCouncillorRole(DiscordApi pApi)       { return pApi.getRoleById(councillorRoleId).get(); }
    public String       getName() { return name; }
    public long         getId() { return id; }
    public long         getServerId() { return serverId; }
    public long         getProposeRoleId(){ return proposeRoleId; }

    public void setAgendaChannel(TextChannel pNewTextChannel) { agendaChannel = pNewTextChannel.getId(); }
    public void setFloorChannel(TextChannel pNewTextChannel)  { floorChannel = pNewTextChannel.getId();  }
    public void setResultChannel(TextChannel pNewTextChannel) { resultChannel = pNewTextChannel.getId(); }
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
}
