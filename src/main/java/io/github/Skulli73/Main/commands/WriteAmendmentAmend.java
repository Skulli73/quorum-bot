package io.github.Skulli73.Main.commands;

import io.github.Skulli73.Main.MainQuorum;
import io.github.Skulli73.Main.objects.SubSection;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.Arrays;

import static io.github.Skulli73.Main.MainQuorum.bills;

public class WriteAmendmentAmend extends WriteAmendmentCommand{
    public WriteAmendmentAmend(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        super(pInteraction, pApi);
    }

    @Override
    public void executeCommand(SlashCommandInteraction pInteraction, DiscordApi pApi) {
        SlashCommandInteractionOption lSlashCommandInteractionOption = pInteraction.getOptions().get(0);
        String lAmendmentText = lSlashCommandInteractionOption.getOptionStringValueByName("text").get();
        int lPartId = Math.toIntExact(lSlashCommandInteractionOption.getOptionLongValueByName("part_id").get());
        if(lPartId < bill.partArrayList.size()) {
            System.out.println("Part Id: " + lPartId);
            if(lSlashCommandInteractionOption.getOptionLongValueByName("division_id").isPresent()) {
                int lDivisionId = Math.toIntExact(lSlashCommandInteractionOption.getOptionLongValueByName("division_id").get());
                System.out.println("Division Id: " + lDivisionId);
                    if(lDivisionId < bill.partArrayList.get(lPartId).divisionArrayList.size()) {
                        if(lSlashCommandInteractionOption.getOptionLongValueByName("section_id").isPresent()) {
                            int lSectionId = Math.toIntExact(lSlashCommandInteractionOption.getOptionLongValueByName("section_id").get());
                            System.out.println("Section Id: " + lSectionId);
                            if(lDivisionId < bill.partArrayList.get(lPartId).divisionArrayList.get(lDivisionId).sectionArrayList.size()) {
                                if(lSlashCommandInteractionOption.getOptionStringValueByName("sub_section_id").isPresent()) {
                                    String lSubSectionIdString = lSlashCommandInteractionOption.getOptionStringValueByName("sub_section_id").get();
                                    System.out.println("Sub-Section Id as String: " + lSubSectionIdString);
                                    String[] lSubSectionIdStringArray = lSubSectionIdString.split("\\.");
                                    System.out.println("Sub-Section Id as Array: " + Arrays.toString(lSubSectionIdStringArray));
                                    int[] lSubSectionIdIntArray = new int[lSubSectionIdStringArray.length];
                                    boolean lValid = true;
                                    for(int i = 0; i<lSubSectionIdStringArray.length; i++) {
                                        try {
                                            lSubSectionIdIntArray[i] = Integer.parseInt(lSubSectionIdStringArray[i])-1;
                                        }
                                        catch (NumberFormatException | NullPointerException e) {
                                            lValid = false;
                                        }
                                    }
                                    if(lValid) {
                                        if(lSubSectionIdIntArray[0] < bill.partArrayList.get(lPartId).divisionArrayList.get(lDivisionId).sectionArrayList.get(lSectionId).subSectionArrayList.size()) {
                                            SubSection lSubSection  = bill.partArrayList.get(lPartId).divisionArrayList.get(lDivisionId).sectionArrayList.get(lSectionId).subSectionArrayList.get(lSubSectionIdIntArray[0]);
                                            boolean lValid2 = true;
                                            for (int i = 1; i < lSubSectionIdIntArray.length &&lValid2; i++) {
                                                if (lSubSection.subSectionArrayList.size() <= lSubSectionIdIntArray[i]) {
                                                    pInteraction.createImmediateResponder().append("Not a valid Sub-Section.\nError Code: 02").respond();
                                                    lValid2 = false;
                                                } else
                                                    lSubSection = lSubSection.subSectionArrayList.get(lSubSectionIdIntArray[i]);

                                            }
                                            if(lValid2) {
                                                 amendment.amendments.add(new String[]{lPartId + "." + lDivisionId + "." + lSectionId + "." + lSubSectionIdString, lAmendmentText});
                                                successfullyExecutedCommandMessage(pInteraction);
                                            }
                                        } else pInteraction.createImmediateResponder().append("Not a valid Sub-Section.\nError Code: 01").respond();
                                    }
                                } else {
                                    amendment.amendments.add(new String[]{lPartId + "." + lDivisionId + "." + lSectionId, lAmendmentText});
                                    successfullyExecutedCommandMessage(pInteraction);
                                }
                            }
                        }
                    }
            }
            bill.amendmentDrafts.set(amendmentDraftId, amendment);
            bills.put(String.valueOf(bill.messageId), bill);
            MainQuorum.saveBills();
            amendment.updateMessage("n", amendmentDraftId, bill);
        } else
            pInteraction.createImmediateResponder().append("This Part does not exist").respond();

    }
}
