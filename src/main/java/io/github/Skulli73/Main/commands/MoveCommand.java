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
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.*;
import static io.github.Skulli73.Main.listeners.SlashCommandListener.lTypeOfMajorityArray;

public class MoveCommand extends CouncilCommand{

    public MoveCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);

    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        executeMove(pInteraction, pApi, council, council.currentMotion);
    }

    public static void executeMove(SlashCommandInteraction pInteraction, DiscordApi pApi, Council pCouncil, int pCurrentMotion) {
        if(pCouncil.currentMotion != pCurrentMotion) {
            pCouncil.nextMotion = pCouncil.currentMotion;
            pCouncil.currentMotion = pCurrentMotion;
        }
        Motion lMotion = pCouncil.motionArrayList.get(pCouncil.currentMotion);
        if(!lMotion.isMoved) {
            if((pCouncil.getCouncillorRole().hasUser(pInteraction.getUser()))) {
                lMotion.isMoved = true;
                int requiredCouncillors = (int )Math.ceil(pCouncil.getCouncillorRole().getUsers().size()*lMotion.neededMajority);
                String lFooter;
                if(lMotion.typeOfMajority<3) {
                    lFooter = requiredCouncillors + " councillors need to vote in favour of this bill for it to be passed. \n";
                }else
                    lFooter = requiredCouncillors + " councillors need to vote against this bill for it not to be passed. \n";
                lFooter = lFooter + lTypeOfMajorityArray[lMotion.typeOfMajority] + ", " +  lMotion.neededMajority*100 + "%";
                List<EmbedBuilder> lEmbed;
                if(lMotion.getText().length() > 2000) {
                    lEmbed = MainQuorum.splitEmbeds(MainQuorum.cutOffText(lMotion.getText(), false), Color.yellow, lMotion.getTitle(), lFooter);
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
                    lMotion.getMessage(pApi, pCouncil.getAgendaChannel()).edit(
                            lEmbed
                    );
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                File lFile = toTxtFile(lMotion.getText(),  pCouncil.getId() + "_" + lMotion.id + "_bill_as_amended");
                MessageBuilder lMessageBuilder = new MessageBuilder()
                        .append(pInteraction.getUser().getMentionTag() + " moves " + lMotion.getTitle() + " from the agenda")
                        .addEmbeds(lEmbed)
                        .addAttachment(lFile);
                lMessageBuilder.send(pInteraction.getChannel().get());
                lMessageBuilder.send(pCouncil.getMinuteChannel());
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

                Object[] lCouncillors = pCouncil.getCouncillorRole().getUsers().toArray();
                lFile.delete();
                for(int j = 0; i<lCouncillors.length;i++) {
                    PrivateChannel lChannel;
                    try {
                        lChannel = ((User)lCouncillors[j]).openPrivateChannel().get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    lMotion.notVoted.add(((User)lCouncillors[i]).getIdAsString());
                    try {
                        File lFile2 = toTxtFile(lMotion.getText(),  pCouncil.getId() + "_" + lMotion.id + "_bill_as_amended");
                        lMotion.dmMessages.add( new MessageBuilder().append("Vote")
                                .addEmbeds(
                                        lEmbed
                                )
                                .addComponents(
                                        ActionRow.of(
                                                Button.success("aye" , "Aye"),
                                                Button.danger("nay" , "Nay"),
                                                Button.secondary("abstain" , "Abstain")
                                        )
                                )
                                .addAttachment(lFile2)
                                .send(lChannel).get().getIdAsString());
                        lMotion.dmMessagesCouncillors.add(((User) lCouncillors[i]).getIdAsString());
                        lFile2.delete();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                pCouncil.getMinuteChannel().sendMessage("Question-" + lQuestion + "-put");
                lMotion.startMotionVote(pApi, pCouncil, pInteraction, lCouncillors);
                SlashCommandListener.saveMotion(pCouncil, lMotion);
                pCouncil.getFloorChannel().asServerTextChannel().get().updateTopic("Current Motion: " + lMotion.getTitle() + " | " +lCouncillors.length+" Councillors left to vote.");
            }
            else
                pInteraction.getChannel().get().sendMessage(pInteraction.getUser().getMentionTag() + ", you do not have the propose role and cannot move the motion.");
        }
        else
            pInteraction.getChannel().get().sendMessage(pInteraction.getUser().getMentionTag() + ", the motion is already moved.");
    }
}
