package io.github.Skulli73.Main.objects;

import com.google.gson.Gson;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Council {
    private                    String name;
    private long               floorChannel, agendaChannel, resultChannel;
    private final int          id;
    private final long         serverId;
    private long               councillorRoleId, proposeRoleId;
    public ArrayList<Motion>   motionArrayList;
    public int                 currentMotion = 0;
    public int                 nextMotion = 1;
    public double              standardMajority = 0.501;


    public Council(String pName, TextChannel pFloorChannel, TextChannel pAgendaChannel, TextChannel pResultChannel,  long pCouncillorRoleId, long pServer, int pId) {
        name                = pName;
        floorChannel        = pFloorChannel.getId();
        agendaChannel       = pAgendaChannel.getId();
        resultChannel       = pResultChannel.getId();
        id                  = pId;
        serverId            = pServer;
        councillorRoleId     = pCouncillorRoleId;
        motionArrayList     = new ArrayList<>();
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


    public boolean isChannelFloor(TextChannel pChannel, DiscordApi pApi, ArrayList<Council> pCouncils, String path) {
        boolean lResult = false;
        for(int i = 0; i < pCouncils.size() && !lResult; i++) {
            try {
                StringBuilder lStringBuilder = new StringBuilder();
                File myObj = new File(path + i + "council.json");

                if(myObj.exists()) {
                    Scanner myReader = new Scanner(myObj);

                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        lStringBuilder.append(data);
                    }

                    myReader.close();

                    String lJson = lStringBuilder.toString();
                    Gson lGson = new Gson();

                    if(lGson.fromJson(lJson, Council.class).getFloorChannel(pApi).equals(pChannel)) lResult = true;
                }

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        return lResult;
    }


    public Council councilByFloorChannel(TextChannel pChannel, DiscordApi pApi, ArrayList<Council> pCouncils, String path) {
        Council lResult = null;
        for(int i = 0; i < pCouncils.size(); i++) {
            try {
                StringBuilder lStringBuilder = new StringBuilder();
                File myObj = new File(path + i + "council.json");

                if(myObj.exists()){
                    Scanner myReader = new Scanner(myObj);

                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        lStringBuilder.append(data);
                    }

                    myReader.close();

                    String lJson = lStringBuilder.toString();
                    Gson lGson = new Gson();

                    if(lGson.fromJson(lJson, Council.class).getFloorChannel(pApi).equals(pChannel)) lResult = lGson.fromJson(lJson, Council.class);
                }

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        return lResult;
    }
}
