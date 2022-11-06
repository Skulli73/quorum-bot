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
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "resultchannel", "Channel that in which all results are sent", true),
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
                SlashCommand.with("move", "Move the motion or bill on top of the queue to the floor").
                        setEnabledInDms(false)
                        .createGlobal(api)
                        .join();
        SlashCommand configCommand =
                SlashCommand.with("config", "Change the configurations of the Quorum Bot in this Council.", Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "default_majority", "The majority needed if not stated in the motion.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "majority", "Required majority (e.g 50% = 0.5)", false),
                                SlashCommandOption.createWithChoices(SlashCommandOptionType.DECIMAL, "type_of_majority", "What type of a majority is necessary", false,
                                        Arrays.asList(
                                            SlashCommandOptionChoice.create("majority_vote", 0),
                                            SlashCommandOptionChoice.create("majority_of_the_entire_membership", 1),
                                            SlashCommandOptionChoice.create("majority_in_the_negative", 2)
                                        )
                                )
                        )),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "motion_timeout", "How long it takes until a motion ends", Arrays.asList(
                            SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "timeout_length", "How many hours it takes until a motion ends", false)
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
    }
}
