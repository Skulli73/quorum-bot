package io.github.Skulli73.Main.managers;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.Arrays;

public class SlashCommandManager {
    public SlashCommandManager(DiscordApi api) {
        SlashCommand createCouncilCommand =
                SlashCommand.with("createcouncil", "Creates a Council", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "Name of the Council", true),
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "floorchannel", "Channel that is used for debating motions and bills", true),
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "agendachannel", "Channel in which all proposed bills are noted and in which voting takes place", true),
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "minutechannel", "Channel that in which all actions are sent", true),
                                SlashCommandOption.create(SlashCommandOptionType.ROLE, "councillorrole", "Role for Councillors", true)
                        ))
                        .setDefaultEnabledForPermissions(PermissionType.MANAGE_CHANNELS, PermissionType.ADMINISTRATOR)
                        .setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand motionCommand =
                SlashCommand.with("motion", "Start a motion", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Text of the motion - New Lines are marked with \"\\n\"", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "title", "Title of the motion", false),
                                SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "majority", "Required majority (e.g 50% = 0.5)", false),
                                SlashCommandOption.createWithChoices(SlashCommandOptionType.DECIMAL, "type_of_majority", "What type of a majority is necessary", false,
                                        Arrays.asList(
                                                SlashCommandOptionChoice.create("majority_vote", 0),
                                                SlashCommandOptionChoice.create("majority_of_the_entire_membership", 1),
                                                SlashCommandOptionChoice.create("majority_in_the_negative", 2)
                                        ))
                        ))
                        .setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand moveCommand =
                SlashCommand.with("move", "Move the motion on top of the queue to the floor").
                        setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand moveAheadCommand =
                SlashCommand.with("move_ahead", "Move a specific motion.", Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.LONG, "motion_id", "Id of the Motion to move ahead", true)
                        )).
                        setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand configCommand =
                SlashCommand.with("config", "Change the configurations of the Quorum Bot in this Council.", Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "show", "Show all the current configs"),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "default_majority", "The majority needed if not stated in the motion.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "majority", "Required majority (e.g 50% = 0.5)", true),
                                SlashCommandOption.createWithChoices(SlashCommandOptionType.DECIMAL, "type_of_majority", "What type of a majority is necessary", true,
                                        Arrays.asList(
                                            SlashCommandOptionChoice.create("majority_vote", 0),
                                            SlashCommandOptionChoice.create("majority_of_the_entire_membership", 1),
                                            SlashCommandOptionChoice.create("majority_in_the_negative", 2)
                                        )
                                )
                        )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "motion_timeout", "How long it takes until a motion ends", Arrays.asList(
                            SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "timeout_length", "How many hours it takes until a motion ends", true)
                        )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, "bills", "How long it takes until a motion ends", Arrays.asList(
                               SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "first_reading_majority", "The majority needed in the first reading", Arrays.asList(
                                       SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "majority", "Needed majority", true),
                                       SlashCommandOption.createWithChoices(SlashCommandOptionType.DECIMAL, "type_of_majority", "Type of majority", true, Arrays.asList(
                                                SlashCommandOptionChoice.create("majority_vote", 0),
                                                SlashCommandOptionChoice.create("majority_of_the_entire_membership", 1),
                                                SlashCommandOptionChoice.create("majority_in_the_negative", 2)
                                        ))
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "amendment_majority", "The majority needed in the first reading", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "majority", "Needed majority", true),
                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.DECIMAL, "type_of_majority", "Type of majority", true, Arrays.asList(
                                                SlashCommandOptionChoice.create("majority_vote", 0),
                                                SlashCommandOptionChoice.create("majority_of_the_entire_membership", 1),
                                                SlashCommandOptionChoice.create("majority_in_the_negative", 2)
                                        ))
                                ))
                        )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "channels", "The different channels such as the floor channel", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "floorchannel", "Channel used for debating motions and bills.", false),
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "agendachannel", "Channel used for displaying all motions.", false),
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "minutechannel", "Channel into which all actions are put into.", false),
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "legislationchannel", "Channel into which all passed bills are put.", false)
                        )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "vote_weights", "Give a role a specific vote weight.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.ROLE, "role", "The role to be given a vote weight.", true),
                                SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "vote_weight", "The vote weight for the aforementioned role.", true)
                        )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "councillor_role", "The Role for Councillors.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.ROLE, "role", "The new Councillor Role.", true)
                        )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "quorum", "Necessary portion of councillors to have voted for it to be able to be passed.", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "quorum_percentage", "The percentage.", true),
                                        SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "quorum_absentions_count_quorum", "Do absentions count to the quorum?", true)
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "forward_channel", "Council that has to confirm a bill.", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "The Floor Channel of the Council to forward the Bill to.", false)
                                ))
                ))
                        .setDefaultEnabledForPermissions(PermissionType.MANAGE_CHANNELS, PermissionType.ADMINISTRATOR)
                        .setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand endVoteCommand =
                SlashCommand.with("end_vote", "Ends the current vote and gets the results.")
                        .setDefaultEnabledForPermissions(PermissionType.MANAGE_CHANNELS, PermissionType.ADMINISTRATOR)
                        .setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand withdrawCommand =
                SlashCommand.with("withdraw", "Withdraw your own motion.", Arrays.asList(
                            SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "motion_id", "Id of the Motion you wish to withdraw", true)
                        ))
                        .setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand killCommand =
                SlashCommand.with("kill_motion", "Kill a motion.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "motion_id", "Id of the Motion you wish to kill", true)
                        ))
                        .setDefaultEnabledForPermissions(PermissionType.MANAGE_CHANNELS, PermissionType.ADMINISTRATOR)
                        .setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand billCommand =
                SlashCommand.with("bill", "Create a Bill", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "title", "Title of the Bill", true)
                        ))
                        .setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand billCommandDms =
                SlashCommand.with("write_bill", "Write your Bills - Only Usable after /bill was done", Arrays.asList(
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add_part", "Add a Part", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "title", "Title of the Part", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "bill_message_id", "Id of the Message of the Bill", true)
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add_division", "Add a Division", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "title", "Title of the Division", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "bill_message_id", "Id of the Message of the Bill", true)
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add_section", "Add a Section", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "section_title", "Title of the Section", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "section_text", "Text of the Section", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "bill_message_id", "Id of the Message of the Bill", true)
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add_subsection", "Add a Sub-Section", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Text of the Sub-Section", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "bill_message_id", "Id of the Message of the Bill", true),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "level_of_subsection", "How far down this sub section is, e.g 1 means a sub-subsection and 3 a sub-sub-sub-subsection", true)
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "finish", "Finishes the writing of the bill and sends it to the council.", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "bill_message_id", "Id of the Amendment", true),
                                        SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "majority", "Required majority in the Third Reading", false),
                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.LONG, "type_of_majority", "Type of majority", false, Arrays.asList(
                                                SlashCommandOptionChoice.create("majority_vote", 0),
                                                SlashCommandOptionChoice.create("majority_of_the_entire_membership", 1),
                                                SlashCommandOptionChoice.create("majority_in_the_negative", 2)
                                        ))
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "undo", "Undo the last addition to a bill", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "bill_message_id", "Id of the Message of the Bill", true)
                                ))
                            )
                        )
                        .setEnabledInDms(true)
                        .createGlobal(api)
                        .join();
        SlashCommand amendCommand =
                SlashCommand.with("amend", "Amendment the bill currently moved")
                        .setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand writeAmendmentCommand =
                SlashCommand.with("write_amendment", "Write your amendment - DM only", Arrays.asList(
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "omit", "Omit something", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "amendment_id", "Id of the Message of the amendment", true),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "part_id", "Id of the Part you wish to Omit", true),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "division_id", "Id of the Part you wish to Omit", false),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "section_id", "Id of the Part you wish to Omit", false),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "sub_section_id", "Id of the Part you wish to Omit", false)
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "amend", "Amend something", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "amendment_id", "Id of the Message of the amendment", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "New Text of the amended section/subsection", true),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "part_id", "Id of the Part you wish to Omit", true),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "division_id", "Id of the Part you wish to Omit", true),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "section_id", "Id of the Part you wish to Omit", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "sub_section_id", "Id of the Part you wish to Omit", false)
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add", "Add a another (sub-)section.", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "amendment_id", "Id of the Message of the amendment", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "New Text of the amended section/subsection", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "title", "New Title of the amended section", true),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "part_id", "Id of the Part you wish to Omit", true),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "division_id", "Id of the Part you wish to Omit", false),
                                        SlashCommandOption.create(SlashCommandOptionType.LONG, "section_id", "Id of the Part you wish to Omit", false),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "sub_section_id", "Id of the Part you wish to Omit", false)
                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "finish", "Finish the Amendment", Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "amendment_id", "Id of the Message of the amendment", true)
                                ))
                        ))
                        .setEnabledInDms(true)
                        .createGlobal(api)
                        .join();
    }
}
