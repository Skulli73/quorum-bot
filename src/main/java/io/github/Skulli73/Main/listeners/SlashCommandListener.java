package io.github.Skulli73.Main.listeners;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.Skulli73.Main.commands.ConfigDefaultMajorityCommand;
import io.github.Skulli73.Main.commands.CreateCouncilCommand;
import io.github.Skulli73.Main.commands.MotionCommand;
import io.github.Skulli73.Main.commands.MoveCommand;
import io.github.Skulli73.Main.objects.Council;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.Main.*;

public class SlashCommandListener implements SlashCommandCreateListener {
        public static final String[] lTypeOfMajorityArray = new String[]
                {"Majority Vote", "Majority of the entire Membership", "Majority in the Negative"};

        @Override
        public void onSlashCommandCreate(SlashCommandCreateEvent event) {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            if(interaction.getCommandName().equals("createcouncil")) new CreateCouncilCommand(interaction, lApi);
            if(interaction.getCommandName().equals("motion"))        new MotionCommand(interaction, lApi);
            if(interaction.getCommandName().equals("move"))          new MoveCommand(interaction, lApi);
            if(interaction.getFullCommandName().equals("config default_majority"))        new ConfigDefaultMajorityCommand(interaction, lApi);
        }

        public static void saveMotion(Council lCouncil, Motion lMotion) {
            lCouncil.motionArrayList.set(lMotion.id, lMotion);
            saveCouncil(lCouncil);
        }

        public static void createMotion(SlashCommandInteraction pInteraction) throws ExecutionException, InterruptedException {
            Council lCouncil = councils.get(0).councilByFloorChannel(pInteraction.getChannel().get(), lApi, councils, councilsPath);

            String lMotionName;
            double lMajority = lCouncil.standardMajority;
            int lTypeOfMajority = lCouncil.standardMajorityType;
            if(pInteraction.getArguments().size() > 1 && pInteraction.getArguments().get(1).getName().equals("title"))
            {lMotionName = "Motion #" + (lCouncil.motionArrayList.size()+1) + ": " +  pInteraction.getArguments().get(1).getStringValue().get();}
            else if(pInteraction.getArguments().size() > 2 && pInteraction.getArguments().get(2).getName().equals("title"))
            {lMotionName = "Motion #" + (lCouncil.motionArrayList.size()+1) + ": " +  pInteraction.getArguments().get(2).getStringValue().get();}
            else if(pInteraction.getArguments().size() > 3 && pInteraction.getArguments().get(3).getName().equals("title"))
            {lMotionName = "Motion #" + (lCouncil.motionArrayList.size()+1) + ": " +  pInteraction.getArguments().get(3).getStringValue().get();}
            else
                lMotionName = "Motion #" + (lCouncil.motionArrayList.size()+1);

            if(pInteraction.getArguments().size()>1)if(pInteraction.getArguments().get(1).getName().equals("majority"))
            {lMajority = pInteraction.getArguments().get(1).getDecimalValue().get();}
            else if(pInteraction.getArguments().size()>2)if(pInteraction.getArguments().get(2).getName().equals("majority"))
            {lMajority = pInteraction.getArguments().get(2).getDecimalValue().get();}
            else if(pInteraction.getArguments().size()>3)if(pInteraction.getArguments().get(3).getName().equals("majority"))
            {lMajority = pInteraction.getArguments().get(3).getDecimalValue().get();}

            if(pInteraction.getArguments().size()>1)if(pInteraction.getArguments().get(1).getName().equals("type_of_majority"))
            {lTypeOfMajority = pInteraction.getArguments().get(1).getDecimalValue().get().intValue();}
            else if(pInteraction.getArguments().size()>2)if(pInteraction.getArguments().get(2).getName().equals("type_of_majority"))
            {lTypeOfMajority = pInteraction.getArguments().get(2).getDecimalValue().get().intValue();}
            else if(pInteraction.getArguments().size()>3)if(pInteraction.getArguments().get(3).getName().equals("type_of_majority"))
            {lTypeOfMajority = pInteraction.getArguments().get(3).getDecimalValue().get().intValue();}
            String lMotionDesc = pInteraction.getArguments().get(0).getStringValue().get();
            Motion lMotion = new Motion(lMotionName, lMotionDesc, pInteraction.getUser().getId(),
                    new MessageBuilder().setEmbed(
                            new EmbedBuilder()
                                    .setTitle(lMotionName)
                                    .setDescription(lMotionDesc)
                                    .setColor(Color.RED)
                                    .setAuthor(pInteraction.getUser().getDisplayName(pInteraction.getServer().get()), pInteraction.getUser().getAvatar().getUrl().toString(), pInteraction.getUser().getAvatar())
                                    .setFooter(lTypeOfMajorityArray[lTypeOfMajority] + ", " +  lMajority*100 + "%")
                    ).send(lCouncil.getAgendaChannel(lApi)).get().getId(), lMajority, lTypeOfMajority, lCouncil.motionArrayList.size());
            lCouncil.motionArrayList.add(lMotion);
            saveCouncil(lCouncil);
        }


        public static void saveCouncil(Council pCouncil) {
            councils.set((int)pCouncil.getId(), pCouncil);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for(int i = 0; i<councils.size(); i++) {
                String fileName = councilsPath + i + "council.json";
                FileWriter myWriter;
                try {
                    myWriter = new FileWriter(fileName);
                    myWriter.write(gson.toJson(councils.get(i)));
                    myWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }