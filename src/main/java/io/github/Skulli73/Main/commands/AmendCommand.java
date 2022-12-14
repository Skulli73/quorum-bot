package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.objects.Amendment;
import io.github.Skulli73.Main.objects.Bill;
import io.github.Skulli73.Main.objects.Motion;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.Skulli73.Main.MainQuorum.bills;
import static io.github.Skulli73.Main.MainQuorum.saveBills;

public class AmendCommand extends CouncilCommand {
    public AmendCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        // Check permissions
        if(!council.hasProposeRole(pInteraction.getUser())) {
            pInteraction.createImmediateResponder().append("You may not propose motions.");
            return;
        }

        if(!(council.currentMotion < council.motionArrayList.size())) {
            pInteraction.createImmediateResponder().append("There is no Introduction of a Motion right now.").respond();
            return;
        }

        Motion lMotion = council.motionArrayList.get(council.currentMotion);
        if(lMotion.isMoved && !lMotion.completed  && lMotion.isBill()) {
            pInteraction.createImmediateResponder().append("There is no Introduction of a Motion right now.").respond();
            return;
        }

        Bill lBill = bills.get(String.valueOf(lMotion.billId));
        if(!lBill.firstReadingFinished) {
            pInteraction.createImmediateResponder().append("This is not the first reading.").respond();
            return;
        }

        Amendment lAmendment = new Amendment();
        lAmendment.introducerId = pInteraction.getUser().getId();
        try {
            lAmendment.messageId = new MessageBuilder().addEmbed(lAmendment.toEmbed("n", lBill.amendmentDrafts.size(), lBill)).send(pInteraction.getUser()).get().getId();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        lBill.amendmentDrafts.add(lAmendment);

        bills.put(String.valueOf(lMotion.billId), lBill);
        saveBills();
        pInteraction.createImmediateResponder().append("Amendment creation process initiated, look in dms.").respond();
    }
}