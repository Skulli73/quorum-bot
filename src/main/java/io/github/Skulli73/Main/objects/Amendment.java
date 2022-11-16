package io.github.Skulli73.Main.objects;

import com.google.gson.annotations.Expose;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.lApi;

public class Amendment {
    @Expose
    public ArrayList<String>            omittings;
    @Expose
    public ArrayList<String[]>          amendments;
    @Expose
    public ArrayList<String[]>          additions;
    @Expose
    public Long                         messageId;
    @Expose
    public Long                         introducerId;
    public Amendment() {
        omittings = new ArrayList<String>();
        amendments = new ArrayList<String[]>();
        additions = new ArrayList<String[]>();

    }

    public String toString() {
        StringBuilder lString = new StringBuilder();
        for (String lOmitting : omittings) {
            String typeOfDivision = "";
            lString.append("Omit ").append(typeOfDivision).append(lOmitting).append("\n");
        }
        for (String[] lAmendments : amendments)
            lString.append("Amend ").append(lAmendments[0]).append(" to\n\"").append(lAmendments[1]).append("\"").append("\n");
        for (String[] lAddition : additions)
            lString.append("Add ").append(lAddition[1]).append(" at ").append(lAddition[0]).append("\n");


        return lString.toString();
    }


    public EmbedBuilder toEmbed(String pFinalNumber, Integer pCurrentNumber, Bill pBill) {
        String lNumber = pFinalNumber;
        EmbedBuilder lEmbed = new EmbedBuilder();
        lEmbed.setTitle("Amendment #" + lNumber + " to " + pBill.title);
        lEmbed.setDescription(toString());
        lEmbed.setFooter("Amendment id: " + pBill.messageId + "." + pCurrentNumber);
        return lEmbed;
    }

    public void updateMessage(String pFinalNumber, Integer pCurrentNumber, Bill pBill) {
        try {
            assert lApi != null;
            lApi.getMessageById(messageId, lApi.getUserById(introducerId).get().openPrivateChannel().get()).get().edit(
                    toEmbed(pFinalNumber, pCurrentNumber, pBill)
            );
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
