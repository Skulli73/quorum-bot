package io.github.Skulli73.Main.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.Skulli73.Main.Main;
import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.Main.councils;
import static io.github.Skulli73.Main.Main.councilsPath;

public class Motion {

   private String                      text, title;
    private final boolean               isBill;
    private final long                  agendaMessageId;
    public boolean                      completed = false;

    public ArrayList<String>            ayeVotes, nayVotes, abstainVotes, notVoted, dmMessages, dmMessagesCouncillors;

    public long                         introducerId;
    public int                          id;

    public double                       neededMajority;
    public int                          typeOfMajority;

    public boolean                      isMoved = false;

    public TimerTask                    timerTask;



    public Motion(String pTitle, String pText, long pIntroducerId, long pAgendaMessageId, double pNeededMajority, int pTypeOfMajority, int pId) {
        title           = pTitle;
        text            = pText;
        isBill          = false;
        ayeVotes        = new ArrayList<>();
        nayVotes        = new ArrayList<>();
        abstainVotes    = new ArrayList<>();
        notVoted        = new ArrayList<>();
        introducerId    = pIntroducerId;
        agendaMessageId = pAgendaMessageId;
        neededMajority  = pNeededMajority;
        typeOfMajority  = pTypeOfMajority;
        dmMessages      = new ArrayList<>();
        dmMessagesCouncillors = new ArrayList<>();
        id              = pId;
    }

    // Getters & Setters
    public String   getTitle()  { return title; }
    public String   getText()   { return text; }
    public boolean  isBill()    { return isBill; }
    public Message  getMessage(DiscordApi pApi, TextChannel pChannel) throws ExecutionException, InterruptedException { return pApi.getMessageById(agendaMessageId, pChannel).get(); }

    public void     setTitle(String pTitle) { title = pTitle; }
    public void     setText(String pText)   { text = pText; }

    public void endMotionVote(DiscordApi pApi, Council pCouncil, SlashCommandInteraction pInteraction, Object[] pCouncillors) {
        if(!this.completed) {
            Main.timers.get((int)pCouncil.getId()).cancel();
            EmbedBuilder embed = null;
            try {
                embed = new EmbedBuilder()
                        .setTitle(getTitle())
                        .setDescription(getText())
                        .setColor(Color.GREEN)
                        .setAuthor(pApi.getUserById(introducerId).get().getName(), "", pApi.getUserById(introducerId).get().getAvatar());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            final int ayeVotesAmount = ayeVotes.size();
            final int nayVotesAmount = nayVotes.size();
            final int abstainVotesAmount = abstainVotes.size();
            final int notVotedAmount = notVoted.size();

            StringBuilder ayeVotesMembers = new StringBuilder();
            for(int i = 0; i < ayeVotesAmount; i++) {
                try {
                    String lComma = "";
                    if(i != 0)
                        lComma = ", ";
                    ayeVotesMembers.append(lComma).append(pApi.getUserById(ayeVotes.get(i)).get().getDisplayName(pInteraction.getServer().get()));
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            StringBuilder notVotedMembers = new StringBuilder();
            for(int i = 0; i < notVotedAmount; i++) {
                try {
                    String lComma = "";
                    if(i != 0)
                        lComma = ", ";
                    notVotedMembers.append(lComma).append(pApi.getUserById(notVoted.get(i)).get().getDisplayName(pInteraction.getServer().get()));
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            StringBuilder nayVotesMembers = new StringBuilder();
            for(int i = 0; i < nayVotesAmount; i++) {
                try {
                    String lComma = "";
                    if(i != 0)
                        lComma = ", ";
                    nayVotesMembers.append(lComma).append(pApi.getUserById(nayVotes.get(i)).get().getDisplayName(pInteraction.getServer().get()));
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            StringBuilder abstainVotesMembers = new StringBuilder();
            for(int i = 0; i < abstainVotesAmount; i++) {
                try {
                    String lComma = "";
                    if(i != 0)
                        lComma = ", ";
                    abstainVotesMembers.append(lComma).append(pApi.getUserById(abstainVotes.get(i)).get().getDisplayName(pInteraction.getServer().get()));
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            boolean lPassed = false;
            int lTotalCouncillors;
            if(typeOfMajority == 0)
                lTotalCouncillors = ayeVotesAmount+nayVotesAmount;
            else
                lTotalCouncillors = pCouncillors.length;

            if(typeOfMajority != 3)
                lPassed = ayeVotesAmount>=lTotalCouncillors*neededMajority;
            else
                lPassed = !(nayVotesAmount>=lTotalCouncillors*neededMajority);
            String lQuorumFailed = "";
            if(pCouncil.absentionsCountToQuorum) {
                if(ayeVotesAmount + nayVotesAmount + abstainVotesAmount <= pCouncil.quorum * lTotalCouncillors) {
                    lPassed = false;
                    lQuorumFailed = " due to the Quorum not being reached";
                }
            }
            else {
                if(ayeVotesAmount + nayVotesAmount <= pCouncil.quorum * lTotalCouncillors) {
                    lPassed = false;
                    lQuorumFailed = " due to the Quorum not being reached";
                }
            }
            Color lColour;
            if(lPassed)
                lColour = Color.GREEN;
            else
                lColour = Color.RED;

            String lResultString = "";
            if(lPassed)
                lResultString = "The following motion passed";
            else
                lResultString = "The following motion was denied" + lQuorumFailed;
            new MessageBuilder()
                    .append(lResultString)
                    .setEmbed(

                            embed.addField("Aye", ayeVotesAmount + " (" + ayeVotesMembers.toString() + ")")
                                    .addField("Nay", nayVotesAmount + " (" + nayVotesMembers.toString() + ")")
                                    .addField("Abstain", abstainVotesAmount + " (" + abstainVotesMembers.toString() + ")")
                                    .addField("Did not Vote", notVotedAmount + " (" + notVotedMembers.toString() + ")")
                                    .setColor(lColour)
                    ).send(pCouncil.getResultChannel(pApi));

            Message lAgendaMessage = null;
            try {
                lAgendaMessage = getMessage(pApi, pCouncil.getAgendaChannel(pApi));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            assert lAgendaMessage != null;
            lAgendaMessage.edit(lAgendaMessage.getEmbeds().get(0).toBuilder().setColor(Color.GREEN));

            completed = true;


            pCouncil.currentMotion = pCouncil.nextMotion;
            pCouncil.nextMotion = pCouncil.currentMotion +1;

            SlashCommandListener.saveMotion(pCouncil, this);

            for(int i = 0; i<dmMessages.size(); i++) {
                try {
                    pApi.getMessageById(dmMessages.get(i), pApi.getUserById(dmMessagesCouncillors.get(i)).get().openPrivateChannel().get()).get().delete();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void startMotionVote(DiscordApi pApi, Council pCouncil, SlashCommandInteraction pInteraction, Object[] pCouncillors) {
        timerTask = new java.util.TimerTask() {

            @Override
            public void run() {
                Council lResultCouncil = councils.get((int)pCouncil.getId());
                Motion lResultMotion = lResultCouncil.motionArrayList.get(lResultCouncil.currentMotion);
                lResultMotion.endMotionVote(pApi, pCouncil, pInteraction, pCouncillors);
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
        };
        Main.timers.set((int)pCouncil.getId(), new Timer());
        Main.timers.get((int)pCouncil.getId()).schedule(
                timerTask,
                (int)(pCouncil.timeOutTime* 3600000)
        );
    }
}
