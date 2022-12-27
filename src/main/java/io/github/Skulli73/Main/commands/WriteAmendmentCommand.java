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
        if(lSplitArgument.length != 2) {
            pInteraction.createImmediateResponder().append("This amendment does not exist.").respond();
            return;
        }

        lBillMessageId = lSplitArgument[0];
        amendmentDraftId = Integer.parseInt(lSplitArgument[1]);
        if (!bills.containsKey(lBillMessageId)) {
            pInteraction.createImmediateResponder().append("This amendment does not exist.").respond();
            return;
        }

        bill = bills.get(lBillMessageId);
        if(bill.amendmentDrafts.size()> amendmentDraftId) {
            pInteraction.createImmediateResponder().append("This amendment does not exist.").respond();
            return;
        }

        amendment = bill.amendmentDrafts.get(amendmentDraftId);
        if (bill.amendmentDrafts.get(amendmentDraftId) != null) {
            pInteraction.createImmediateResponder().append("This draft is already finished").respond();
            return;
        }

        if (bill.amendmentDrafts.get(amendmentDraftId).introducerId == pInteraction.getUser().getId()) {
            pInteraction.createImmediateResponder().append("This is not your amendment.");
            return;
        }

        this.executeCommand(pInteraction, pApi);
    }
    public abstract void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi);

    public void successfullyExecutedCommandMessage(SlashCommandInteraction pInteraction) {
        pInteraction.createImmediateResponder().append("The amendment was successfully edited.").respond();
    }
}
