package io.github.Skulli73.Main.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Council;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.Main.councils;
import static io.github.Skulli73.Main.Main.councilsPath;
import static io.github.Skulli73.Main.listeners.SlashCommandListener.lTypeOfMajorityArray;

public class MoveCommand {

    private final DiscordApi lApi;

    public MoveCommand(SlashCommandInteraction interaction, DiscordApi pApi) {
        lApi = pApi;

        if(councils.size()!=0) {
            if(councils.get(0).isChannelFloor(interaction.getChannel().get(), lApi, councils, councilsPath)) {
                Council lCouncil = councils.get(0).councilByFloorChannel(interaction.getChannel().get(), lApi, councils, councilsPath);
                Motion lMotion = lCouncil.motionArrayList.get(lCouncil.currentMotion);
                if(!lMotion.isMoved) {
                    if(lApi.getRoleById(lCouncil.getProposeRoleId()).get().hasUser(interaction.getUser())) {
                        lMotion.isMoved = true;
                        EmbedBuilder lEmbed = null;
                        try {
                            lEmbed = new EmbedBuilder()
                                    .setTitle(lMotion.getTitle())
                                    .setDescription(lMotion.getText())
                                    .setColor(Color.YELLOW)
                                    .setAuthor(lApi.getUserById(lMotion.introducerId).get().getName(), "", lApi.getUserById(lMotion.introducerId).get().getAvatar());
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            lMotion.getMessage(lApi, lCouncil.getAgendaChannel(lApi)).edit(
                                    lEmbed
                            );
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        int requiredCouncillors = (int )Math.ceil(lCouncil.getCouncillorRole(lApi).getUsers().size()*lMotion.neededMajority);
                        String lFooter;
                        if(lMotion.typeOfMajority<3) {
                            lFooter = " councillors need to vote in favour of this bill for it to be passed. \n";
                        }else
                            lFooter = " councillors need to vote against this bill for it not to be passed. \n";
                        new MessageBuilder()
                                .append(interaction.getUser().getMentionTag() + " moves " + lMotion.getTitle() + " from the agenda")
                                .setEmbed(lEmbed.setFooter(requiredCouncillors + lFooter + lTypeOfMajorityArray[lMotion.typeOfMajority] + ", " +  lMotion.neededMajority*100 + "%")
                                ).send(interaction.getChannel().get());

                        Object[] lCouncillors = lCouncil.getCouncillorRole(lApi).getUsers().toArray();

                        for(int i = 0; i<lCouncillors.length;i++) {
                            PrivateChannel lChannel = null;
                            try {
                                lChannel = ((User)lCouncillors[i]).openPrivateChannel().get();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                            lMotion.notVoted.add(((User)lCouncillors[i]).getIdAsString());
                            try {
                                lMotion.dmMessages.add( new MessageBuilder().append("Vote")
                                        .setEmbed(
                                                lEmbed.setFooter(requiredCouncillors + lFooter + lTypeOfMajorityArray[lMotion.typeOfMajority] + ", " +  lMotion.neededMajority*100 + "%")
                                        )
                                        .addComponents(
                                                ActionRow.of(
                                                        org.javacord.api.entity.message.component.Button.success("aye" , "Aye"),
                                                        org.javacord.api.entity.message.component.Button.danger("nay" , "Nay"),
                                                        Button.secondary("abstain" , "Abstain")
                                                )
                                        )
                                        .send(lChannel).get().getIdAsString());
                                lMotion.dmMessagesCouncillors.add(((User) lCouncillors[i]).getIdAsString());
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        SlashCommandListener.saveMotion(lCouncil, lMotion);

                        EmbedBuilder finalLEmbed = lEmbed;
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {

                                    @Override
                                    public void run() {
                                        Council lResultCouncil = councils.get((int)lCouncil.getId());
                                        Motion lResultMotion = lResultCouncil.motionArrayList.get(lResultCouncil.currentMotion);

                                    }

                                    private void saveMotion(Council lCouncil, Motion lMotion) {
                                        lCouncil.motionArrayList.set(lMotion.id, lMotion);
                                        this.saveCouncil(lCouncil);
                                    }

                                    public void saveCouncil(Council pCouncil) {
                                        GsonBuilder builder = new GsonBuilder();
                                        Gson gson = builder.create();
                                        String fileName = councilsPath +(pCouncil.getId()) + "council.json";
                                        FileWriter myWriter = null;
                                        try {
                                            myWriter = new FileWriter(fileName);
                                            myWriter.write(gson.toJson(pCouncil));
                                            myWriter.close();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                },
                                (int)(lCouncil.timeOutTime* 3600000)
                        );
                    }
                    else
                        interaction.getChannel().get().sendMessage(interaction.getUser().getMentionTag() + ", you do not have the propose role and cannot move the motion.");
                }
                else
                    interaction.getChannel().get().sendMessage(interaction.getUser().getMentionTag() + ", the motion is already moved.");
            }
            else
                interaction.getChannel().get().sendMessage(interaction.getUser().getMentionTag() + ", this is not a floor channel");
        }
        else
            interaction.getChannel().get().sendMessage(interaction.getUser().getMentionTag() + ", this is not a floor channel");
    }
}
