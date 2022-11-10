package io.github.Skulli73.Main.commands;

import com.google.gson.Gson;
import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import static io.github.Skulli73.Main.MainQuorum.councils;
import static io.github.Skulli73.Main.MainQuorum.councilsPath;

public class CreateCouncilCommand {

    public CreateCouncilCommand(SlashCommandInteraction interaction, DiscordApi pApi) {
        System.out.println("createcouncil executed");

        boolean     lFail = false;
        String      lCouncilName = null;
        TextChannel lFloorChannel= null;
        TextChannel lAgendaChannel = null;
        TextChannel lResultChannel = null;
        Role lCouncillorRole = null;

        try {
            lCouncilName    =               interaction.getArguments().get(0).getStringValue().get();
            lFloorChannel   = (TextChannel) interaction.getArguments().get(1).getChannelValue().get();
            lAgendaChannel  = (TextChannel) interaction.getArguments().get(2).getChannelValue().get();//api.getServerTextChannelById(event.getSlashCommandInteraction().getArguments().get(2).getStringValue().toString().replace("Optional[","").replace("]","")).get();
            lResultChannel  = (TextChannel) interaction.getArguments().get(3).getChannelValue().get();//api.getServerTextChannelById(event.getSlashCommandInteraction().getArguments().get(3).getStringValue().toString().replace("Optional[","").replace("]","")).get();
            lCouncillorRole =               interaction.getArguments().get(4).getRoleValue().get();
            System.out.println(lCouncilName+" | "+lFloorChannel+" | "+lAgendaChannel+" | "+lResultChannel);

        } catch (Exception e) {
            lFail = true;
            interaction.createImmediateResponder().setContent("The Floor, Agenda and Result Channel must be **text channels**.").respond();
            e.printStackTrace();
        }

        if(lFloorChannel == lAgendaChannel || lFloorChannel == lResultChannel|| lAgendaChannel == lResultChannel) {
            lFail = true;
            interaction.createImmediateResponder().setContent("The Floor, Agenda and Result Channel must be **different** channels.").respond();
        }

        for(int i = 0; i < councils.size()&&!lFail; i++) {
            try {
                StringBuilder lStringBuilder = new StringBuilder();
                File myObj = new File(councilsPath + i + "council.json");
                if(myObj.exists()){
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        lStringBuilder.append(data);
                    }
                    myReader.close();
                    String lJson = lStringBuilder.toString();
                    Gson lGson = new Gson();
                    if(lGson.fromJson(lJson, Council.class).getFloorChannel(pApi) == lFloorChannel) {
                        lFail = true;
                        interaction.createImmediateResponder().setContent("The floor channel, you wish to use for your council, is already used by a council").respond();
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        if(!lFail) {
            System.out.println(pApi);
            Council lCouncil = new Council(lCouncilName,lFloorChannel, lAgendaChannel, lResultChannel,  lCouncillorRole.getId(), interaction.getServer().get().getId(), councils.size());
            councils.add(lCouncil);
            SlashCommandListener.saveCouncil(lCouncil);
            interaction.createImmediateResponder().setContent("Your council was successfully created.").respond();
            System.out.println("The following Councils exist as of right now:");
            System.out.println(Arrays.toString(councils.toArray()));
        }
    }
}
