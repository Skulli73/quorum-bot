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
        MessageComponentInteraction messageComponentInteraction = lEvent.getMessageComponentInteraction();
        String                      customId                    = messageComponentInteraction.getCustomId();
        Interaction                 lInteraction                = lEvent.getInteraction();

        boolean isAVote = false;
        for(int i = 0; i<councils.size(); i++) {
            Council lCouncil = councils.get(i);
            if(lCouncil.motionArrayList.size() > lCouncil.currentMotion)
                if(lCouncil.motionArrayList.get(lCouncil.currentMotion).dmMessages.contains(messageComponentInteraction.getMessage().getIdAsString()))
                    isAVote = true;
        }
        if(isAVote) {
            messageComponentInteraction.createImmediateResponder()
                    .setContent("Your Vote has been registered")
                    .respond();

            String lUserId = lInteraction.getUser().getIdAsString();
            Motion lMotion   = null;
            boolean lEnd     = true;
            Council lCouncil = councils.stream()
                                        .filter(c -> c.motionArrayList.size()-1<=c.currentMotion)
                                        .filter(c -> c.motionArrayList.get(c.currentMotion).dmMessages.contains(messageComponentInteraction.getMessage().getIdAsString()))
                                        .findFirst()
                                        .orElse(null);

            if (lCouncil == null) {
                System.err.println("Failed to find motion");
                messageComponentInteraction.createImmediateResponder().append("An error has occurred! :frowning: :eggplant: :flushed:").respond();
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

            switch (customId) {
                case "aye":
                    assert lMotion != null;
                    lMotion.ayeVotes.remove(lUserId);
                    lMotion.nayVotes.remove(lUserId);
                    lMotion.abstainVotes.remove(lUserId);
                    lMotion.notVoted.remove(lUserId);

                    lMotion.ayeVotes.add(lUserId);
                    break;
                case "nay":
                    assert lMotion != null;
                    lMotion.ayeVotes.remove(lUserId);
                    lMotion.nayVotes.remove(lUserId);
                    lMotion.abstainVotes.remove(lUserId);
                    lMotion.notVoted.remove(lUserId);

                    lMotion.nayVotes.add(lUserId);
                    break;
                case "abstain":
                    assert lMotion != null;
                    lMotion.ayeVotes.remove(lUserId);
                    lMotion.nayVotes.remove(lUserId);
                    lMotion.abstainVotes.remove(lUserId);
                    lMotion.notVoted.remove(lUserId);


                    lMotion.abstainVotes.add(lUserId);
                    break;
            }
            assert lCouncil != null;
            SlashCommandListener.saveMotion(lCouncil, lMotion);
            lCouncil.getFloorChannel().asServerTextChannel().get().updateTopic("Current Motion: " + lMotion.getTitle() + " | " +(lCouncil.getCouncillorRole().getUsers().size() - lMotion.ayeVotes.size() - lMotion.nayVotes.size() - lMotion.abstainVotes.size())+" Councillor(s) left to vote.");

        }
    }
}
