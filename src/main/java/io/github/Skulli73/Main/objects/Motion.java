package io.github.Skulli73.Main.objects;

import io.github.Skulli73.Main.MainQuorum;
import io.github.Skulli73.Main.listeners.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.*;
import static io.github.Skulli73.Main.listeners.SlashCommandListener.lTypeOfMajorityArray;

public class Motion {

   private String                      text, title;
    public final boolean               isBill;
    private final long                  agendaMessageId;
    public boolean                      completed = false;

    public ArrayList<String>            ayeVotes, nayVotes, abstainVotes, notVoted, dmMessages, dmMessagesCouncillors;

    public long                         introducerId;
    public int                          id;

    public double                       neededMajority;
    public int                          typeOfMajority;

    public boolean                      isMoved = false;


   // public TimerTask                    timerTask;
    @Nullable
    public Long                         billId, amendmentId;

    public boolean                      approved;

    public List<Long>                   seconderIdList;

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
        approved = false;
        seconderIdList = new LinkedList<>();
    }
    public Motion(String pTitle, String pText, long pIntroducerId, long pAgendaMessageId, double pNeededMajority, int pTypeOfMajority, int pId, @Nullable Long pBillId, @Nullable Long pAmendmentId) {
        this(pTitle, pText, pIntroducerId, pAgendaMessageId, pNeededMajority, pTypeOfMajority, pId);
        billId = pBillId;
        amendmentId = pAmendmentId;
    }

    // Getters & Setters
    public String   getTitle()  { return title; }
    public String   getText()   { return text; }
    public boolean  isBill()    { return billId!=null&&amendmentId==null; }
    public boolean  isAmendment()    { return billId!=null&&amendmentId!=null; }
    public Message  getMessage(DiscordApi pApi, TextChannel pChannel) throws ExecutionException, InterruptedException {
        if (!pApi.getMessageById(agendaMessageId, pChannel).isDone()) {
            return pApi.getMessageById("1049727331528364062", discordApi.getChannelById("1035207109052211230").get().asTextChannel().get()).get();
        } else
            return pApi.getMessageById(agendaMessageId, pChannel).get();
    }

    public void     setTitle(String pTitle) { title = pTitle; }
    public void     setText(String pText)   { text = pText; }

    public String getSeconderString() {
        StringBuilder lStringBuilder = new StringBuilder();
        int i = 0;
        for(Long lLong:seconderIdList) {
            if(i!=0)
                lStringBuilder.append(", ");
            try {
                lStringBuilder.append(discordApi.getUserById(lLong).get().getName());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        return lStringBuilder.toString();
    }

    public void endMotionVote(DiscordApi pApi, Council pCouncil, SlashCommandInteraction pInteraction, Object[] pCouncillors) {
        if(!this.completed) {
            MainQuorum.timers.get((int)pCouncil.getId()).cancel();
            EmbedBuilder embed = null;
            try {
                embed = new EmbedBuilder()
                        .setTitle(getTitle())
                        //.setDescription(getText())
                        .setColor(Color.GREEN)
                        .setAuthor(pApi.getUserById(introducerId).get().getName(), "", pApi.getUserById(introducerId).get().getAvatar());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            DecimalFormat lFormat = new DecimalFormat("0.#");
            boolean lEveryoneOneVote = true;
            double lTotalVotes= 0;
            HashMap<Long, Double> lVoteWeightsCouncillors = new HashMap<>();
            for(Object lCouncillorObject: pCouncillors) {
                long lCouncillorId = ((User)lCouncillorObject).getId();
                double lVoteWeight = 0;
                for(VoteWeight lVoteWeight2: pCouncil.voteWeightArrayList) {
                    try {
                        if(lVoteWeight2.getRole().hasUser(discordApi.getUserById(lCouncillorId).get())) {
                            lVoteWeight+=lVoteWeight2.votes;
                            lTotalVotes+=lVoteWeight2.votes;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(lVoteWeight != 1)
                    lEveryoneOneVote = false;
                lVoteWeightsCouncillors.put(lCouncillorId, lVoteWeight);
            }
            double ayeVotesAmount = 0;
            if(lEveryoneOneVote)
                ayeVotesAmount = ayeVotes.size();
            else {
                for(String lUser: ayeVotes) {
                    long lUserLong = Long.parseLong(lUser);
                    ayeVotesAmount+=lVoteWeightsCouncillors.get(lUserLong);
                }
            }
            double nayVotesAmount = 0;
            if(lEveryoneOneVote)
                nayVotesAmount = nayVotes.size();
            else {
                for(String lUser: nayVotes) {
                    long lUserLong = Long.parseLong(lUser);
                    nayVotesAmount+=lVoteWeightsCouncillors.get(lUserLong);
                }
            }
            double abstainVotesAmount = 0;
            if(lEveryoneOneVote)
                abstainVotesAmount = abstainVotes.size();
            else {
                for(String lUser: abstainVotes) {
                    long lUserLong = Long.parseLong(lUser);
                    abstainVotesAmount+=lVoteWeightsCouncillors.get(lUserLong);
                }
            }
            double notVotedAmount = 0;
            if(lEveryoneOneVote)
                notVotedAmount = notVoted.size();
            else {
                for(String lUser: notVoted) {
                    long lUserLong = Long.parseLong(lUser);
                    notVotedAmount+=lVoteWeightsCouncillors.get(lUserLong);
                }
            }

            StringBuilder ayeVotesMembers = new StringBuilder();
            for(int i = 0; i < ayeVotes.size(); i++) {
                try {
                    String lComma = "";
                    if(i != 0)
                        lComma = "\n";
                    ayeVotesMembers.append(lComma).append(pApi.getUserById(ayeVotes.get(i)).get().getMentionTag());
                    if(!lEveryoneOneVote)
                        ayeVotesMembers.append(" (").append(lFormat.format(lVoteWeightsCouncillors.get(Long.parseLong(ayeVotes.get(i))))).append(")");
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            StringBuilder notVotedMembers = new StringBuilder();
            for(int i = 0; i < notVoted.size(); i++) {
                try {
                    String lComma = "";
                    if(i != 0)
                        lComma = "\n";
                    notVotedMembers.append(lComma).append(pApi.getUserById(notVoted.get(i)).get().getMentionTag());
                    if(!lEveryoneOneVote)
                        notVotedMembers.append(" (").append(lFormat.format(lVoteWeightsCouncillors.get(Long.parseLong(notVoted.get(i))))).append(")");
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            StringBuilder nayVotesMembers = new StringBuilder();
            for(int i = 0; i < nayVotes.size(); i++) {
                try {
                    String lComma = "";
                    if(i != 0)
                        lComma = "\n";
                    nayVotesMembers.append(lComma).append(pApi.getUserById(nayVotes.get(i)).get().getMentionTag());
                    if(!lEveryoneOneVote)
                        nayVotesMembers.append(" (").append(lFormat.format(lVoteWeightsCouncillors.get(Long.parseLong(nayVotes.get(i))))).append(")");
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            StringBuilder abstainVotesMembers = new StringBuilder();
            for(int i = 0; i < abstainVotes.size(); i++) {
                try {
                    String lComma = "";
                    if(i != 0)
                        lComma = "\n";
                    abstainVotesMembers.append(lComma).append(pApi.getUserById(abstainVotes.get(i)).get().getMentionTag());
                    if(!lEveryoneOneVote)
                        abstainVotesMembers.append(" (").append(lFormat.format(lVoteWeightsCouncillors.get(Long.parseLong(abstainVotes.get(i))))).append(")");
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            boolean lPassed = false;
            double lTotalCouncillors;
            if(typeOfMajority == 0)
                lTotalCouncillors = ayeVotesAmount+nayVotesAmount;
            else
                lTotalCouncillors = lTotalVotes;

            if(typeOfMajority != 2)
                lPassed = ayeVotesAmount>=lTotalCouncillors*neededMajority;
            else
                lPassed = !(nayVotesAmount>=lTotalCouncillors*neededMajority);
            String lQuorumFailed = "";
            if(pCouncil.absentionsCountToQuorum) {
                if(ayeVotesAmount + nayVotesAmount + abstainVotesAmount < pCouncil.quorum * lTotalCouncillors) {
                    lPassed = false;
                    lQuorumFailed = " but the Quorum was not reached";
                }
            }
            else {
                if(ayeVotesAmount + nayVotesAmount < pCouncil.quorum * lTotalCouncillors) {
                    lPassed = false;
                    lQuorumFailed = " but the Quorum was not reached";
                }
            }
            Color lColour;
            if(lPassed)
                lColour = Color.GREEN;
            else
                lColour = Color.RED;

            String lResultString = "";
            if(lPassed) {
                lResultString = pCouncil.getName() + " divided";

            }
            else
                lResultString = pCouncil.getName() + " divided " + lQuorumFailed;
            if(ayeVotesAmount > 0)
                embed.addField("Aye - " + lFormat.format(ayeVotesAmount),  " " + ayeVotesMembers.toString());
            if(nayVotesAmount>0)
                embed.addField("Nay - " + lFormat.format(nayVotesAmount),  " " + nayVotesMembers.toString());
            if(abstainVotesAmount > 0)
                embed.addField("Abstain - " + lFormat.format(abstainVotesAmount),  " " + abstainVotesMembers.toString());
            if(notVotedAmount > 0)
                embed.addField("Not Voted - " + lFormat.format(notVotedAmount),  " " + notVotedMembers.toString());
            MessageBuilder lMessageBuilder = new MessageBuilder()
                    .append(lResultString)
                    .setEmbed(
                            embed
                                    .setColor(lColour)
                                    .setFooter(lTypeOfMajorityArray[typeOfMajority] + ", " +  neededMajority*100 + "%")
                    );

            Message lAgendaMessage = null;
            try {
                lAgendaMessage = getMessage(pApi, pCouncil.getAgendaChannel());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            assert lAgendaMessage != null;
            lAgendaMessage.delete();

            completed = true;


            pCouncil.toNextMotion();

            pCouncil.getFloorChannel().asServerTextChannel().get().updateTopic("No Active Motion");

            SlashCommandListener.saveMotion(pCouncil, this);

            deleteMessages(pApi);
            approved = lPassed;
            System.out.println(lMessageBuilder.send(pCouncil.getMinuteChannel()).toString());
            if(isBill()) {
                if(bills.get(Long.toString(billId)).firstReadingFinished && bills.get(Long.toString(billId)).amendmentsFinished) {
                    bills.get(Long.toString(billId)).thirdReadingFinished = true;
                    saveBills();
                }
            }
            if( isBill() || isAmendment()) {
                boolean b = pCouncil.motionArrayList.stream().filter(c -> c.isAmendment() && Objects.equals(billId, c.billId) && !c.completed).toList().isEmpty() && !bills.get(Long.toString(billId)).thirdReadingFinished && (!isBill()||lPassed);
                if (b)
                    //pCouncil.motionArrayList.stream().filter(c -> c.isAmendment()&& Objects.equals(billId, c.billId) &&!c.completed).toList().isEmpty() && !bills.get(Long.toString(billId)).thirdReadingFinished;
                    //isAmendment() && amendmentId+1 == bills.get(Long.toString(billId)).amendments.size())
                    //|| (isBill() && bills.get(Long.toString(billId)).amendments.size() == 0&& !bills.get(Long.toString(billId)).thirdReadingFinished) && lPassed
                 {

                    Bill lBill = bills.get(Long.toString(billId));
                    lBill.amendmentsFinished = true;
                    List<Motion> lApprovedAmendmentsMotionList = pCouncil.motionArrayList.stream().filter(c -> c.isAmendment() && Objects.equals(billId, c.billId) && c.approved).toList();
                    StringBuilder lApprovedAmendmentsStringBuilder = new StringBuilder();
                    for (Motion lMotion : lApprovedAmendmentsMotionList)
                        lApprovedAmendmentsStringBuilder.append("Amendment #").append(lMotion.amendmentId);
                    List<Motion> lDeniedAmendmentsMotionList = pCouncil.motionArrayList.stream().filter(c -> c.isAmendment() && Objects.equals(billId, c.billId) && !c.approved).toList();
                    StringBuilder lDeniedAmendmentsStringBuilder = new StringBuilder();
                    for (Motion lMotion : lDeniedAmendmentsMotionList)
                        lDeniedAmendmentsStringBuilder.append("Amendment #").append(lMotion.amendmentId + 1).append("\n");
                    String lDesc = lBill.toString(true) + "\n\n**Approved**\n" + lApprovedAmendmentsStringBuilder + "\n**Denied**\n" + lDeniedAmendmentsStringBuilder;
                    List<EmbedBuilder> lEmbedBuilders;
                    if(lDesc.length()>2048) {
                        lEmbedBuilders = MainQuorum.splitEmbeds(lDesc, Color.GREEN, lBill.title + "as amended");
                    } else {
                        EmbedBuilder lEmbedBuilder = new EmbedBuilder()
                                .setTitle(lBill.title + " as amended")
                                .setDescription(lDesc)
                                //.addField("Approved", lApprovedAmendmentsStringBuilder.toString(), true)
                                //.addField("Denied", lDeniedAmendmentsStringBuilder.toString(), true)
                                .setColor(Color.green);
                        lEmbedBuilders = new LinkedList<>();
                        lEmbedBuilders.add(lEmbedBuilder);
                    }
                    File lFile = toTxtFile(lDesc,  lBill.messageId + "_bill_as_amended");
                    new MessageBuilder()
                            .addEmbeds(lEmbedBuilders)
                            .addAttachment(lFile)
                            .append("\nThere being no amendments left on the agenda, the bill was taken to be agreed to with amendments.")
                            .send(pCouncil.getMinuteChannel());
                    try {
                        SlashCommandListener.createMotionEnd(discordApi.getUserById(lBill.initiatorId).get(), pCouncil, "Motion #" + (pCouncil.motionArrayList.size() + 1) + ": " + lBill.title, lBill.majority, lBill.typeOfMajority, lBill.toString(false), discordApi.getServerById(pCouncil.getServerId()).get(), lBill.messageId, null);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    lFile.delete();
                    bills.put(String.valueOf(lBill.messageId), lBill);
                    saveBills();
                }// else lMessageBuilder.send(pCouncil.getMinuteChannel());
            } //else lMessageBuilder.send(pCouncil.getMinuteChannel());

            if(lPassed)
                onPassed();
        }
    }

    public void deleteMessages(DiscordApi pApi) {
        for(int i = 0; i<dmMessages.size(); i++) {
            try {
                pApi.getMessageById(dmMessages.get(i), pApi.getUserById(dmMessagesCouncillors.get(i)).get().openPrivateChannel().get()).get().delete();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startMotionVote(DiscordApi pApi, Council pCouncil, SlashCommandInteraction pInteraction, Object[] pCouncillors) {
        TimerTask timerTask = new java.util.TimerTask() {

            @Override
            public void run() {
                Council lResultCouncil = councils.get((int)pCouncil.getId());
                Motion lResultMotion = lResultCouncil.motionArrayList.get(lResultCouncil.currentMotion);
                lResultMotion.endMotionVote(pApi, pCouncil, pInteraction, pCouncillors);
            }
        };
        MainQuorum.timers.set((int)pCouncil.getId(), new Timer());
        MainQuorum.timers.get((int)pCouncil.getId()).schedule(
                timerTask,
                (int)(pCouncil.timeOutTime* 3600000)
        );
    }

    private void onPassed() {
        completed = true;
        if(isBill()) {
            Bill lBill = bills.get(Long.toString(billId));
            Council lCouncil = councils.get(lBill.councilId);
            if(!lBill.firstReadingFinished)
                lBill.endIntroduction();
            else {
                lBill.thirdReadingFinished = true;
                File lFile = toTxtFile(text, lBill.messageId + "_bill_passed" + lCouncil.getId());
                if(lCouncil.hasForwardChannel() && !lBill.oneHousePassed) {
                    Council lCouncil2 =  councils.get(lCouncil.forwardCouncil);
                    if (lBill.toString(false).length() > 2048) {
                        lCouncil.getMinuteChannel().sendMessage("Bill passed in " + lCouncil.getName() + "\nIt is now required to be passed by " + lCouncil2.getName(), lBill.toEmbeds(false, Color.green, false), lFile);
                    } else
                        lCouncil.getMinuteChannel().sendMessage("Bill passed in " + lCouncil.getName()+ "\nIt is now required to be passed by " + lCouncil2.getName(), lBill.toEmbed(false).setColor(Color.green), lFile);
                    lBill.thirdReadingFinished = false;
                    lBill.firstReadingFinished = false;
                    lBill.amendmentsFinished = false;
                    lBill.amendments = new ArrayList<>();
                    lBill.oneHousePassed = true;
                    lBill.councilId = (int) lCouncil2.getId();
                    try {
                        SlashCommandListener.createMotionEnd(discordApi.getUserById(lBill.initiatorId).get(), lCouncil2, "Motion #" + (lCouncil2.motionArrayList.size()+1) + ": Introduction of " + lBill.title, lBill.majority, lBill.typeOfMajority, lBill.toString(true), discordApi.getServerById(lCouncil2.getServerId()).get(), (long) lBill.messageId, null);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    councils.set((int) lCouncil2.getId(), lCouncil2);
                    SlashCommandListener.saveCouncil(lCouncil2);
                } else {
                    TextChannel lLegislationChannel;
                    lLegislationChannel = lCouncil.getLegislationChannel();
                    if (lBill.toString(false).length() > 2048) {
                        lLegislationChannel.sendMessage("Bill passed ", lBill.toEmbeds(false, Color.green, false), lFile);
                    } else
                        lLegislationChannel.sendMessage("Bill passed", lBill.toEmbed(false).setColor(Color.green), lFile);
                }
                lFile.delete();

            }
            councils.set((int) lCouncil.getId(), lCouncil);
            bills.put(Long.toString(billId), lBill);
            SlashCommandListener.saveCouncil(lCouncil);
            saveBills();
        } else if (isAmendment()) {
            Bill lBill = bills.get(Long.toString(billId));
            Council lCouncil = councils.get(lBill.councilId);
            Amendment lAmendment = lBill.amendments.get(Math.toIntExact(amendmentId));
            lAmendment.onAccepted(lBill);
            if(lBill.oneHousePassed) {
                lBill.oneHousePassed = false;
                lCouncil.getMinuteChannel().sendMessage("As the Bill was amended, it now is required to pass through the other house again.");
            }
            councils.set((int) lCouncil.getId(), lCouncil);
            SlashCommandListener.saveCouncil(lCouncil);
            bills.put(String.valueOf(lBill.messageId), lBill);
            saveBills();
        }
    }
}
