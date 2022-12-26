package io.github.Skulli73.Main.listeners;

import io.github.Skulli73.Main.objects.Council;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static io.github.Skulli73.Main.MainQuorum.councils;
import static io.github.Skulli73.Main.MainQuorum.councilsPath;

public class MessageComponentListener implements MessageComponentCreateListener {
    @Override
    public void onComponentCreate(MessageComponentCreateEvent lEvent) {
        System.out.println("Component Interaction happened");
        MessageComponentInteraction messageComponentInteraction = lEvent.getMessageComponentInteraction();
        String                      customId                    = messageComponentInteraction.getCustomId();
        Interaction                 lInteraction                = lEvent.getInteraction();

        boolean isAVote = false;
        for (Council lCouncil : councils) {
            if (lCouncil.motionArrayList.size() > lCouncil.currentMotion)
                if (lCouncil.motionArrayList.get(lCouncil.currentMotion).dmMessages.contains(messageComponentInteraction.getMessage().getIdAsString()))
                    isAVote = true;
        }
        if(isAVote) {

            lInteraction.createImmediateResponder().append("Your Vote has been registered.").respond();

            String lUserId = lInteraction.getUser().getIdAsString();
            Motion lMotion   = null;
            boolean lEnd     = true;
            Council lCouncil = councils.stream()
                                        .filter(c -> c.motionArrayList.size()>c.currentMotion)
                                        .filter(c -> c.motionArrayList.get(c.currentMotion).dmMessages.contains(messageComponentInteraction.getMessage().getIdAsString()))
                                        .findFirst()
                                        .orElse(null);

            if (lCouncil == null) {
                System.err.println("Failed to find motion");
                messageComponentInteraction.acknowledge();
                messageComponentInteraction.getChannel().get().sendMessage("An error has occurred! :frowning: :eggplant: :flushed:");
                return;
            }
            lMotion = lCouncil.motionArrayList.get(lCouncil.currentMotion);
            int j = 0;

            while(!lEnd) {
                StringBuilder lJsonBuilder = new StringBuilder();
                try {
                    File myObj = new File( councilsPath + j + "council.json");
                    if(myObj.exists()){
                        Scanner myReader = new Scanner(myObj);
                        while (myReader.hasNextLine()) {
                            String data = myReader.nextLine();
                            lJsonBuilder.append(data);
                        }
                        myReader.close();
                        String lJson = lJsonBuilder.toString();
                        System.out.println(councils.get(j).motionArrayList.size()-1);
                        System.out.println(councils.get(j).currentMotion);
                        if(councils.get(j).motionArrayList.size()-1 >= councils.get(j).currentMotion ) {
                            System.out.println(lEvent.getMessageComponentInteraction().getMessage().getIdAsString() + "\n" + councils.get(j).motionArrayList.get(councils.get(j).currentMotion).dmMessages);
                            if(councils.get(j).motionArrayList.get(councils.get(j).currentMotion).dmMessages.contains(lEvent.getMessageComponentInteraction().getMessage().getIdAsString())) {
                                lMotion = councils.get(j).motionArrayList.get(councils.get(j).currentMotion);
                                lCouncil = councils.get(j);
                            }
                        }
                    } else
                        lEnd = true;
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                    lEnd = true;
                }

                j++;
            }
            while(lMotion.ayeVotes.contains(lUserId))
                lMotion.ayeVotes.remove(lUserId);
            while(lMotion.nayVotes.contains(lUserId))
                lMotion.nayVotes.remove(lUserId);
            while(lMotion.abstainVotes.contains(lUserId))
                lMotion.abstainVotes.remove(lUserId);
            while(lMotion.notVoted.contains(lUserId))
                lMotion.notVoted.remove(lUserId);
            switch (customId) {
                case "aye" -> lMotion.ayeVotes.add(lUserId);
                case "nay" -> lMotion.nayVotes.add(lUserId);
                case "abstain" -> lMotion.abstainVotes.add(lUserId);
            }
            SlashCommandListener.saveMotion(lCouncil, lMotion);
            lCouncil.getFloorChannel().asServerTextChannel().get().updateTopic("Current Motion: " + lMotion.getTitle() + " | " +(lCouncil.getCouncillorRole().getUsers().size() - lMotion.ayeVotes.size() - lMotion.nayVotes.size() - lMotion.abstainVotes.size())+" Councillor(s) left to vote.");

        }

    }
}
