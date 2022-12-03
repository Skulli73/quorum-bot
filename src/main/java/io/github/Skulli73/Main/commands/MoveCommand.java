package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.MainQuorum;
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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.*;
import static io.github.Skulli73.Main.listeners.SlashCommandListener.lTypeOfMajorityArray;

public class MoveCommand {

    private final DiscordApi lApi;

    public MoveCommand(SlashCommandInteraction interaction, DiscordApi pApi) {
        lApi = pApi;

        if(councils.size()!=0) {
            if(Council.isChannelFloor(interaction.getChannel().get(), councils)) {
                Council lCouncil = Council.councilByFloorChannel(interaction.getChannel().get(), councils);
                Motion lMotion = lCouncil.motionArrayList.get(lCouncil.currentMotion);
                if(!lMotion.isMoved) {
                    if(lApi.getRoleById(lCouncil.getProposeRoleId()).get().hasUser(interaction.getUser())) {
                        lMotion.isMoved = true;
                        int requiredCouncillors = (int )Math.ceil(lCouncil.getCouncillorRole().getUsers().size()*lMotion.neededMajority);
                        String lFooter;
                        if(lMotion.typeOfMajority<3) {
                            lFooter = " councillors need to vote in favour of this bill for it to be passed. \n";
                        }else
                            lFooter = " councillors need to vote against this bill for it not to be passed. \n";
                        lFooter = lFooter + lTypeOfMajorityArray[lMotion.typeOfMajority] + ", " +  lMotion.neededMajority*100 + "%";
                        List<EmbedBuilder> lEmbed;
                        if(lMotion.getText().length() > 2000) {
                            lEmbed = MainQuorum.splitEmbeds(lMotion.getText(), Color.yellow, lMotion.getTitle(), lFooter);
                        } else {
                            lEmbed = new LinkedList<EmbedBuilder>();
                            lEmbed.add(new EmbedBuilder().setTitle(lMotion.getTitle()).setDescription(lMotion.getText()).setColor(Color.yellow));
                        }
                        int i = 0;
                        for(EmbedBuilder ignored:lEmbed) {
                            try {
                                lEmbed.get(i).setAuthor(discordApi.getUserById(lMotion.introducerId).get());
                            } catch (InterruptedException | ExecutionException e ) {
                                throw new RuntimeException(e);
                            }
                        }
                        try {
                            lMotion.getMessage(lApi, lCouncil.getAgendaChannel()).edit(
                                    lEmbed
                            );
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        MessageBuilder lMessageBuilder = new MessageBuilder()
                                .append(interaction.getUser().getMentionTag() + " moves " + lMotion.getTitle() + " from the agenda")
                                .addEmbeds(lEmbed);
                        lMessageBuilder.send(interaction.getChannel().get());
                        lMessageBuilder.send(lCouncil.getMinuteChannel());
                        String lQuestion = "";
                        if(lMotion.isBill()) {
                            if(!bills.get(Long.toString(lMotion.billId)).amendmentsFinished)
                                lQuestion = "That the bill be introduced";
                            else
                                lQuestion = "That the bill be adopted into law";
                        } else if (lMotion.isAmendment()) {
                            lQuestion = "That the amendment be agreed to";
                        } else
                            lQuestion = "That the motion be agreed to";
                        lCouncil.getMinuteChannel().sendMessage("Question-" + lQuestion + "-put");
                        Object[] lCouncillors = lCouncil.getCouncillorRole().getUsers().toArray();

                        for(int j = 0; i<lCouncillors.length;i++) {
                            PrivateChannel lChannel;
                            try {
                                lChannel = ((User)lCouncillors[j]).openPrivateChannel().get();
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                            lMotion.notVoted.add(((User)lCouncillors[i]).getIdAsString());
                            try {
                                lMotion.dmMessages.add( new MessageBuilder().append("Vote")
                                        .addEmbeds(
                                                lEmbed
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
                        lMotion.startMotionVote(pApi, lCouncil, interaction, lCouncillors);
                        SlashCommandListener.saveMotion(lCouncil, lMotion);
                        lCouncil.getFloorChannel().asServerTextChannel().get().updateTopic("Current Motion: " + lMotion.getTitle() + " | " +lCouncillors.length+" Councillors left to vote.");
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
