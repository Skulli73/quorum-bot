package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Amendment;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;

import static io.github.Skulli73.Main.MainQuorum.bills;
import static io.github.Skulli73.Main.MainQuorum.saveBills;

public class WriteAmendmentFinish extends WriteAmendmentCommand{
    public WriteAmendmentFinish(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        bill.amendments.add(amendment);
        int i = 0;
        for(Amendment lAmendment: bill.amendmentDrafts) {
            if(lAmendment == amendment)
                bill.amendmentDrafts.set(i, null);
            i++;
        }
        bills.put(String.valueOf(bill.messageId), bill);
        saveBills();
        pInteraction.createImmediateResponder().append("The amendment was successfully finished.").respond();
    }
}
