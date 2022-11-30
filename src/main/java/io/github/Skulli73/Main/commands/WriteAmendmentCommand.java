package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Amendment;
import io.github.Skulli73.Main.objects.Bill;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.Nullable;

import static io.github.Skulli73.Main.MainQuorum.bills;

public abstract class WriteAmendmentCommand {
    @Nullable public Bill bill;
    @Nullable public Amendment amendment;
    @Nullable public int amendmentDraftId;
    public WriteAmendmentCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        String lBillMessageId;
        String[] lSplitArgument = pInteraction.getOptions().get(0).getOptionStringValueByName("amendment_id").get().split("\\.") ;
        if(lSplitArgument.length == 2) {
            lBillMessageId = lSplitArgument[0];
            amendmentDraftId = Integer.parseInt(lSplitArgument[1]);
            if (bills.containsKey(lBillMessageId)) {
                bill = bills.get(lBillMessageId);
                if(bill.amendmentDrafts.size()> amendmentDraftId) {
                    amendment = bill.amendmentDrafts.get(amendmentDraftId);
                    if (bill.amendmentDrafts.get(amendmentDraftId) != null) {
                        if (bill.amendmentDrafts.get(amendmentDraftId).introducerId == pInteraction.getUser().getId())
                            this.executeCommand(pInteraction, pApi);
                        else
                            pInteraction.createImmediateResponder().append("This is not your amendment.");
                    }
                    else
                        pInteraction.createImmediateResponder().append("This draft is already finished").respond();
                } else
                    pInteraction.createImmediateResponder().append("This amendment does not exist.").respond();
            }
            else
                pInteraction.createImmediateResponder().append("This amendment does not exist.").respond();
        } else
            pInteraction.createImmediateResponder().append("This amendment does not exist.").respond();
    }
    public abstract void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi);
}
