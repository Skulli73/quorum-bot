package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.MainQuorum;
import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.objects.Bill;
import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.bills;
import static io.github.Skulli73.Main.MainQuorum.councils;

public class WriteBillFinish extends WriteBillCommand{
    public WriteBillFinish(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        String billMessageId = pInteraction.getOptions().get(0).getOptionStringValueByName("bill_message_id").get();
        Bill lBill = bills.get(billMessageId);
        Council lCouncil = councils.get(lBill.councilId);
        if(pInteraction.getOptions().get(0).getOptionDecimalValueByName("majority").isPresent())
            lBill.majority = pInteraction.getOptions().get(0).getOptionDecimalValueByName("majority").get();
        else
            lBill.majority = lCouncil.standardMajority;
        if(pInteraction.getOptions().get(0).getOptionLongValueByName("type_of_majority").isPresent())
            lBill.typeOfMajority = Math.toIntExact(pInteraction.getOptions().get(0).getOptionLongValueByName("type_of_majority").get());
        else
            lBill.typeOfMajority = lCouncil.standardMajorityType;
        try {
            SlashCommandListener.createMotionEnd(pInteraction.getUser(), lCouncil, "Motion #" + (lCouncil.motionArrayList.size()+1) + ": Introduction of \"" + lBill.title + "\"", lCouncil.firstReadingMajority, lCouncil.firstReadingTypeOfMajority, lBill.toString(), pApi.getServerById(lCouncil.getServerId()).get(), lBill.messageId, null);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        lBill.draftFinished = true;

        bills.put(billMessageId, lBill);
        MainQuorum.saveBills();
    }
}
