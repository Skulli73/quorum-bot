package com.github.Skulli73.Main;

import com.google.gson.Gson;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Council {
    private String name;
    private long floorChannel;
    private long agendaChannel;
    private long resultChannel;

    private int id;

    private long serverId;

    private long councillorRoleId;

    private long proposeRoleId;

    public ArrayList<Motion> motionArrayList;

    public int currentMotion = 0;

    public int nextMotion = 1;

    public double standardMajority = 0.501;



    public Council(String pName, TextChannel pFloorChannel, TextChannel pAgendaChannel, TextChannel pResultChannel,  long pCouncillorRoleId, long pServer, int pId){ //Constructor
        name = pName;
        floorChannel = pFloorChannel.getId();
        agendaChannel = pAgendaChannel.getId();
        resultChannel = pResultChannel.getId();
        id = pId;
        serverId = pServer;
        councillorRoleId = pCouncillorRoleId;
        motionArrayList = new ArrayList<Motion>();
    }
    public void setAgendaChannel(TextChannel pNewTextChannel){ //set a new Agenda Channel
        agendaChannel = pNewTextChannel.getId();
    }
    public void setFloorChannel(TextChannel pNewTextChannel){ //set a new Floor Channel
        floorChannel = pNewTextChannel.getId();
    }
    public void setResultChannel(TextChannel pNewTextChannel){ //Set a new Result Channel
        resultChannel = pNewTextChannel.getId();
    }

    public TextChannel getAgendaChannel(DiscordApi pApi){ //return the Agenda Channel
        return pApi.getServerById(serverId).flatMap(serverById -> serverById
                .getTextChannelById(agendaChannel)).get();
    }
    public TextChannel getFloorChannel(DiscordApi pApi) { //return the Floor Channel
        return pApi.getServerById(serverId).flatMap(serverById -> serverById
                .getTextChannelById(floorChannel)).get();
    }
    public TextChannel getResultChannel(DiscordApi pApi){ //return the Result Channel
        return pApi.getServerById(serverId).flatMap(serverById -> serverById
                .getTextChannelById(resultChannel)).get();
    }

    public Role getCouncillorRole(DiscordApi pApi) {
        return pApi.getRoleById(councillorRoleId).get();
    }

    public void setName(String pName) {
        name = pName;
    }

    public void setRoleChannel(long pCouncillorRoleId) {councillorRoleId = pCouncillorRoleId;}

    public String getName() {
        return name;
    }

    public long getId(){return id;}

    public long getServerId(){return serverId;}

    public void setProposeRole(Role pProposerole){proposeRoleId = pProposerole.getId();}

    public long getProposeRoleId(){return proposeRoleId;}

    public boolean isChannelFLoor(TextChannel pChannel, DiscordApi pApi, ArrayList<Council> pCouncils, String path) {
        boolean lResult = false;
        for(int i = 0; i < pCouncils.size()&&!lResult; i++) {
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
                    if(lGson.fromJson(lJson, Council.class).getFloorChannel(pApi) == pChannel) {
                        lResult = true;
                    }
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
                    if(lGson.fromJson(lJson, Council.class).getFloorChannel(pApi) == pChannel) {
                        lResult = lGson.fromJson(lJson, Council.class);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        return lResult;
    }
}
