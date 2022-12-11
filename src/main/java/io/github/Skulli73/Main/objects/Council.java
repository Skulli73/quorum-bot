package io.github.Skulli73.Main.objects;

import io.github.Skulli73.Main.MainQuorum;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static io.github.Skulli73.Main.MainQuorum.councils;
import static io.github.Skulli73.Main.MainQuorum.discordApi;

public class Council {
    private                    String name;
    public long               floorChannel, agendaChannel, minuteChannel, legislationChannel, councillorRoleId;
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

    public Integer              forwardCouncil;

    public boolean              isForwardCouncil;

    public List<Long>           proposeRoleList;

    public boolean              secondRequired;



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
        legislationChannel  = minuteChannel;
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
        forwardCouncil = null;
        isForwardCouncil = false;
        proposeRoleList = new LinkedList<>();
        proposeRoleList.add(pCouncillorRoleId);
        secondRequired = false;
    }


    public TextChannel  getAgendaChannel() { return discordApi.getServerById(serverId).get().getTextChannelById(agendaChannel).get(); }
    public TextChannel  getFloorChannel()  { return discordApi.getServerById(serverId).get().getTextChannelById(floorChannel).get();  }
    public TextChannel getMinuteChannel() { return discordApi.getServerById(serverId).get().getTextChannelById(minuteChannel).get(); }
    public Role         getCouncillorRole()       { return discordApi.getRoleById(councillorRoleId).get(); }
    public String       getName() { return name; }
    public long         getId() { return id; }
    public long         getServerId() { return serverId; }


    public static boolean isChannelFloor(TextChannel pChannel, ArrayList<Council> pCouncils) {

        for(Council lCouncil: councils) {
            if(lCouncil.getFloorChannel() == pChannel)
                return  true;
        }
        return false;
    }


    public static Council councilByFloorChannel(TextChannel pChannel, ArrayList<Council> pCouncils) {
        for(Council lCouncil: councils) {
            if(lCouncil.getFloorChannel() == pChannel)
                return lCouncil;
        }
        return null;
    }

    public void toNextMotion () {
        boolean end = false;
        currentMotion = nextMotion;
        while(!end) {
            nextMotion++;
            if(nextMotion < motionArrayList.size()) {
                Motion lMotion = motionArrayList.get(nextMotion);
                if(!lMotion.isMoved && !lMotion.completed) {
                    end = true;
                }
            } else {
                end = true;
            }
        }
        if(currentMotion < motionArrayList.size()) {
            Motion lMotion = motionArrayList.get(currentMotion);
            while(lMotion.completed)
                toNextMotion();
        }
    }

    public TextChannel getLegislationChannel() {
        return discordApi.getChannelById(legislationChannel).get().asTextChannel().get();
    }

    public boolean hasForwardChannel() {
        return forwardCouncil != null;
    }

    public @Nullable Council getForwardCouncil() {
        return councils.get(forwardCouncil);
    }

    public boolean hasProposeRole(User pUser) {
        return proposeRoleList.stream().filter(lLong -> discordApi.getRoleById(lLong).get().hasUser(pUser)).toList().size() > 0;
    }
}
