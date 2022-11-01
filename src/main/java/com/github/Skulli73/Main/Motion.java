package com.github.Skulli73.Main;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Motion {

    private String title;
    private String text;

    public boolean completed = false;

    public ArrayList<String> ayeVotes;

    public ArrayList<String> nayVotes;

    public ArrayList<String> abstainVotes;

    public ArrayList<String> notVoted;

    private boolean isBill;

    public long introductorId;

    private long agendaMessageId;

    public double neededMajority;

    public int typeOfMajority;

    public int id;

    public ArrayList<String> dmMessages;

    public Motion(String pTitle, String pText, long pIntroductorId, long pAgendaMessageId, double pNeededMajority, int pTypeOfMajority, int pId) {
        title = pTitle;
        text = pText;
        isBill = false;
        ayeVotes = new ArrayList<String>();
        nayVotes = new ArrayList<String>();
        abstainVotes = new ArrayList<String>();
        notVoted = new ArrayList<String>();
        introductorId = pIntroductorId;
        agendaMessageId = pAgendaMessageId;
        neededMajority = pNeededMajority;
        typeOfMajority = pTypeOfMajority;
        dmMessages = new ArrayList<String>();
        id = pId;
    }

    //sets
    public void setTitle(String pTitle) {
        title = pTitle;
    }
    public void setText(String pText) {
        text = pText;
    }

    //gets
    public String getTitle(){return title;}
    public String getText(){return text;}

    public boolean isBill(){return isBill;}

    public Message getMessage(DiscordApi pApi, TextChannel pChannel) {
        try {
            return pApi.getMessageById(agendaMessageId, pChannel).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
