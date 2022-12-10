package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.MainQuorum;
import io.github.Skulli73.Main.objects.Bill;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.bills;

public class BillCommand extends CouncilCommand{
    public  BillCommand (SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        try {
            if(council.hasProposeRole(pInteraction.getUser())) {
                Bill lBill = new Bill(pInteraction.getArguments().get(0).getStringValue().get(), (int)council.getId(), pInteraction.getUser().getId());
                String lMessageId = pInteraction.getUser().openPrivateChannel().get().sendMessage("Your bill:").get().getIdAsString();
                lBill.messageId = Long.parseLong(lMessageId);
                bills.put(lMessageId, lBill);
                MainQuorum.saveBills();
                lBill.update();
                pInteraction.createImmediateResponder().append("Bill creation process initiated, look in dms.").respond();
            } else pInteraction.createImmediateResponder().append("You may not write Bills");


        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
