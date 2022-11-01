package io.github.Skulli73.Main.listeners;

import io.github.Skulli73.Main.objects.Council;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static io.github.Skulli73.Main.Main.councils;
import static io.github.Skulli73.Main.Main.councilsPath;
import static io.github.Skulli73.Main.Main.gson;

public class MessageComponentListener implements MessageComponentCreateListener {
    public MessageComponentListener(DiscordApi api) {

    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent lEvent) {
        MessageComponentInteraction messageComponentInteraction = lEvent.getMessageComponentInteraction();
        String                      customId                    = messageComponentInteraction.getCustomId();
        Interaction                 lInteraction                = lEvent.getInteraction();

        messageComponentInteraction.createImmediateResponder()
                .setContent("Your Vote has been registered")
                .respond();

        String lUserId = lInteraction.getUser().getIdAsString();
        Council lCouncil = null;
        Motion lMotion   = null;
        boolean lEnd     = false;

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
                    councils.set(j, gson.fromJson(lJson, Council.class));
                    System.out.println(councils.get(j).motionArrayList.size()-1);
                    System.out.println(councils.get(j).currentMotion);
                    if(councils.get(j).motionArrayList.size()-1 >= councils.get(j).currentMotion ) {
                        System.out.println(lEvent.getMessageComponentInteraction().getMessage().getIdAsString() + "\n" + councils.get(j).motionArrayList.get(councils.get(j).currentMotion).dmMessages);
                        if(councils.get(j).motionArrayList.get(councils.get(j).currentMotion).dmMessages.contains(lEvent.getMessageComponentInteraction().getMessage().getIdAsString())) {
                            lCouncil = councils.get(j);
                            lMotion = councils.get(j).motionArrayList.get(councils.get(j).currentMotion);
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
                lMotion.nayVotes.remove(lUserId);
                lMotion.abstainVotes.remove(lUserId);
                lMotion.notVoted.remove(lUserId);

                lMotion.ayeVotes.add(lUserId);
                break;
            case "nay":
                assert lMotion != null;
                lMotion.ayeVotes.remove(lUserId);
                lMotion.abstainVotes.remove(lUserId);
                lMotion.notVoted.remove(lUserId);

                lMotion.nayVotes.add(lUserId);
                break;
            case "abstain":
                assert lMotion != null;
                lMotion.ayeVotes.remove(lUserId);
                lMotion.nayVotes.remove(lUserId);
                lMotion.notVoted.remove(lUserId);


                lMotion.abstainVotes.add(lUserId);
                break;
        }
    }
}
