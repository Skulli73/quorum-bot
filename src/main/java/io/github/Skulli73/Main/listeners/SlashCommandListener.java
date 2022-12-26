package io.github.Skulli73.Main.listeners;

import com.google.gson.*;
import io.github.Skulli73.Main.MainQuorum;
import io.github.Skulli73.Main.commands.*;
import io.github.Skulli73.Main.objects.CanUseBot;
import io.github.Skulli73.Main.objects.Council;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.*;

public class SlashCommandListener implements SlashCommandCreateListener {
    public static final String[] lTypeOfMajorityArray = new String[]
            {"Majority Vote", "Majority of the entire Membership", "Majority in the Negative"};

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        CanUseBot lCanUseBot = new CanUseBot();
        String lPath = path + "botConfig\\whitelist_config.json";
        File lFile = new File(lPath);
        if(lFile.exists()) {
            StringBuilder lJsonBuilder = new StringBuilder();
            Scanner lReader = null;
            try {
                lReader = new Scanner(lFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while (lReader.hasNextLine()) {
                String data = lReader.nextLine();
                lJsonBuilder.append(data);
            }
            lReader.close();
            System.out.println("Whitelist Config:\n" + lJsonBuilder);
            JsonObject lJsonObject = JsonParser.parseString(lJsonBuilder.toString()).getAsJsonObject();
            for(JsonElement lJsonElement: lJsonObject.get("whitelistedServers").getAsJsonArray())
                lCanUseBot.whitelistedServers.add(lJsonElement.getAsLong());
            for(JsonElement lJsonElement: lJsonObject.get("blackListedServers").getAsJsonArray())
                lCanUseBot.blackListedServers.add(lJsonElement.getAsLong());
            lCanUseBot.whiteListNecessary = lJsonObject.get("whiteListNecessary").getAsBoolean();
        } else {
            try {
                lFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Gson lGson = new GsonBuilder().setPrettyPrinting().create();
            try {
                FileWriter lWriter = new FileWriter(lPath);
                lWriter.write(lGson.toJson(lCanUseBot));
                lWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(interaction.getChannel().get().asTextChannel().get().asPrivateChannel().isPresent() ||lCanUseBot.serverCanUseBot(interaction.getServer().get())) {
            if (interaction.getCommandName().equals("createcouncil")) new CreateCouncilCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("motion")) new MotionCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("move")) new MoveCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("move_ahead")) new MoveAheadCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("postpone")) new PostponeCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("second")) new SecondCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("end_vote")) new EndVoteCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("withdraw")) new WithdrawCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("kill_motion")) new KillCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("bill")) new BillCommand(interaction, discordApi);
            if (interaction.getCommandName().equals("amend")) new AmendCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config default_majority"))
                new ConfigDefaultMajorityCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config first_reading_majority"))
                new ConfigFirstReadingMajority(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config amendment_majority"))
                new ConfigAmendmentMajority(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config motion_timeout"))
                new ConfigMotionTimeout(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config channels"))
                new ConfigChannelCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config vote_weights"))
                new ConfigVoteWeightsCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config quorum")) new ConfigQuorum(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config councillor_role"))
                new ConfigCouncillorRoleCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config show"))
                new ConfigShowCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config vote_weights"))
                new ConfigVoteWeightsCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config forward_channel"))
                new ConfigForwardChannel(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config add_propose_role"))
                new ConfigAddProposeRole(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config remove_propose_role"))
                new ConfigRemoveProposeRole(interaction, discordApi);
            if (interaction.getFullCommandName().equals("config seconding_required"))
                new ConfigSecondingRequiredCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_bill add_part"))
                new WriteBillAddPartCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_bill add_division"))
                new WriteBillAddDivsionCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_bill add_section"))
                new WriteBillAddSectionCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_bill add_subsection"))
                new WriteBillAddSubSectionCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_bill finish"))
                new WriteBillFinish(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_bill undo"))
                new WriteBillUndoCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_bill add_prefix"))
                new WriteBillPrefixCommand(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_amendment omit"))
                new WriteAmendmentOmit(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_amendment amend"))
                new WriteAmendmentAmend(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_amendment add"))
                new WriteAmendmentAdd(interaction, discordApi);
            if (interaction.getFullCommandName().equals("write_amendment finish"))
                new WriteAmendmentFinish(interaction, discordApi);
        } else
            interaction.createImmediateResponder().append("This server cannot use the Quorum Bot").respond();
    }

    public static void saveMotion(Council lCouncil, Motion lMotion) {
        lCouncil.motionArrayList.set(lMotion.id, lMotion);
        saveCouncil(lCouncil);
    }

    public static void createMotion(SlashCommandInteraction pInteraction) throws ExecutionException, InterruptedException {
        Council lCouncil = Council.councilByFloorChannel(pInteraction.getChannel().get(), councils);

        String lMotionName;
        double lMajority = lCouncil.standardMajority;
        int lTypeOfMajority = lCouncil.standardMajorityType;
        if (pInteraction.getArguments().size() > 1 && pInteraction.getArguments().get(1).getName().equals("title")) {
            lMotionName = "Motion #" + (lCouncil.motionArrayList.size() + 1) + ": " + pInteraction.getArguments().get(1).getStringValue().get();
        } else if (pInteraction.getArguments().size() > 2 && pInteraction.getArguments().get(2).getName().equals("title")) {
            lMotionName = "Motion #" + (lCouncil.motionArrayList.size() + 1) + ": " + pInteraction.getArguments().get(2).getStringValue().get();
        } else if (pInteraction.getArguments().size() > 3 && pInteraction.getArguments().get(3).getName().equals("title")) {
            lMotionName = "Motion #" + (lCouncil.motionArrayList.size() + 1) + ": " + pInteraction.getArguments().get(3).getStringValue().get();
        } else
            lMotionName = "Motion #" + (lCouncil.motionArrayList.size() + 1);

        if (pInteraction.getArguments().size() > 1)
            if (pInteraction.getArguments().get(1).getName().equals("majority")) {
                lMajority = pInteraction.getArguments().get(1).getDecimalValue().get();
            } else if (pInteraction.getArguments().size() > 2)
                if (pInteraction.getArguments().get(2).getName().equals("majority")) {
                    lMajority = pInteraction.getArguments().get(2).getDecimalValue().get();
                } else if (pInteraction.getArguments().size() > 3)
                    if (pInteraction.getArguments().get(3).getName().equals("majority")) {
                        lMajority = pInteraction.getArguments().get(3).getDecimalValue().get();
                    }

        if (pInteraction.getArguments().size() > 1)
            if (pInteraction.getArguments().get(1).getName().equals("type_of_majority")) {
                lTypeOfMajority = pInteraction.getArguments().get(1).getDecimalValue().get().intValue();
            } else if (pInteraction.getArguments().size() > 2)
                if (pInteraction.getArguments().get(2).getName().equals("type_of_majority")) {
                    lTypeOfMajority = pInteraction.getArguments().get(2).getDecimalValue().get().intValue();
                } else if (pInteraction.getArguments().size() > 3)
                    if (pInteraction.getArguments().get(3).getName().equals("type_of_majority")) {
                        lTypeOfMajority = pInteraction.getArguments().get(3).getDecimalValue().get().intValue();
                    }
        String lMotionDesc = pInteraction.getArguments().get(0).getStringValue().get();
        createMotionEnd(pInteraction.getUser(), lCouncil, lMotionName, lMajority, lTypeOfMajority, lMotionDesc, pInteraction.getServer().get(), null, null);
    }

    public static Motion createMotionEnd(User pUser, Council pCouncil, String pMotionName, double pMajority, int pTypeOfMajority, String pMotionDesc, Server pServer, @Nullable Long billId, @Nullable Long pAmendmentId) throws InterruptedException, ExecutionException {
        List<EmbedBuilder> lEmbed;
        if(pMotionDesc.length() > 2048) {
            lEmbed = MainQuorum.splitEmbeds(MainQuorum.cutOffText(pMotionDesc, false), Color.red, pMotionName, lTypeOfMajorityArray[pTypeOfMajority] + ", " + pMajority * 100 + "%");
            int i = 0;
            for(EmbedBuilder ignored: lEmbed) {
                lEmbed.get(i).setAuthor(pUser);
                i++;
            }
        } else {
            lEmbed = new LinkedList<EmbedBuilder>();
            lEmbed.add(new EmbedBuilder()
                    .setTitle(pMotionName)
                    .setDescription(pMotionDesc)
                    .setColor(Color.RED)
                    .setAuthor(pUser.getDisplayName(pServer), pUser.getAvatar().getUrl().toString(), pUser.getAvatar())
                    .setFooter(lTypeOfMajorityArray[pTypeOfMajority] + ", " + pMajority * 100 + "%"
                    ));
        }
        Motion lMotion = new Motion(pMotionName, pMotionDesc, pUser.getId(),
                new MessageBuilder().addEmbeds(
                        lEmbed
                ).send(pCouncil.getAgendaChannel()).get().getId(), pMajority, pTypeOfMajority, pCouncil.motionArrayList.size(), billId, pAmendmentId);
        pCouncil.motionArrayList.add(lMotion);
        saveCouncil(pCouncil);
        return lMotion;
    }


    public static void saveCouncil(Council pCouncil) {
        councils.set((int) pCouncil.getId(), pCouncil);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        String fileName = councilsPath + "council.json";
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(fileName);
            myWriter.write(gson.toJson(councils));
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}