package com.github.Skulli73.Main;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import javax.xml.soap.Text;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static ArrayList<Council> councils;

    public static String path = System.getProperty("user.dir") + "\\src\\main\\java\\com\\github\\Skulli73\\Main\\councils\\";

    public static DiscordApi api;

    public static class SlashCommandListener implements SlashCommandCreateListener {

        private DiscordApi lApi;
        public SlashCommandListener(DiscordApi pApi) {
            lApi = pApi;
        }

        @Override
        public void onSlashCommandCreate(SlashCommandCreateEvent event) {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            if(interaction.getCommandName().equals("createcouncil")){
                System.out.println("createcouncil executed");
                boolean lFail = false;
                String lCouncilName = null;
                TextChannel lFloorChannel= null;
                TextChannel lAgendaChannel = null;
                TextChannel lResultChannel = null;
                Role lCouncillorRole = null;
                try {
                    lCouncilName = interaction.getArguments().get(0).getStringValue().get();
                    System.out.println(lCouncilName.toString());
                    lFloorChannel = (TextChannel)interaction.getArguments().get(1).getChannelValue().get();
                    System.out.println(lFloorChannel.toString());
                    lAgendaChannel = (TextChannel)interaction.getArguments().get(2).getChannelValue().get();//api.getServerTextChannelById(event.getSlashCommandInteraction().getArguments().get(2).getStringValue().toString().replace("Optional[","").replace("]","")).get();
                    System.out.println(lAgendaChannel.toString());
                    lResultChannel = (TextChannel)interaction.getArguments().get(3).getChannelValue().get();//api.getServerTextChannelById(event.getSlashCommandInteraction().getArguments().get(3).getStringValue().toString().replace("Optional[","").replace("]","")).get();
                    System.out.println(lResultChannel.toString());
                    lCouncillorRole = interaction.getArguments().get(4).getRoleValue().get();
                }
                catch (Exception e) {
                    lFail = true;
                    interaction.createImmediateResponder().setContent("The Floor, Agenda and Result Channel must be **text channels**.").respond();
                }
                if(lFloorChannel == lAgendaChannel || lFloorChannel == lResultChannel|| lAgendaChannel == lResultChannel) {
                    lFail = true;
                    interaction.createImmediateResponder().setContent("The Floor, Agenda and Result Channel must be **different** channels.").respond();
                }
                for(int i = 0; i < councils.size()&&!lFail; i++) {
                    try {
                        StringBuilder lStringBuilder = new StringBuilder();
                        File myObj = new File(path + i + "council.json");
                        if(myObj.exists()){
                            Scanner myReader = new Scanner(myObj);
                            while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                lStringBuilder.append(data);
                            }
                            myReader.close();
                            String lJson = lStringBuilder.toString();
                            Gson lGson = new Gson();
                            if(lGson.fromJson(lJson, Council.class).getFloorChannel(lApi) == lFloorChannel) {
                                lFail = true;
                                interaction.createImmediateResponder().setContent("The floor channel, you wish to use for your council, is already used by a council").respond();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                }
                if(!lFail) {
                    System.out.println(api);
                    councils.add(new Council(lCouncilName,lFloorChannel, lAgendaChannel, lResultChannel,  lCouncillorRole.getId(), interaction.getServer().get().getId(), councils.size()));

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    String fileName = path +(councils.size()-1) + "council.json";
                    try {
                        File file = new File(fileName);
                        if (file.createNewFile()) {
                            System.out.println("File created: " + file.getName());
                        } else {
                            System.out.println("File already exists.");
                        }
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                    FileWriter myWriter = null;
                    try {
                        myWriter = new FileWriter(fileName);
                        myWriter.write(gson.toJson(councils.get(councils.size()-1)));
                        myWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    interaction.createImmediateResponder().setContent("Your council was successfully created.").respond();
                    interaction.getChannel().get().sendMessage("The council \"" + lCouncilName + "\" was created in this channel.");
                    System.out.println("The following Councils exist as of right now:");
                    System.out.println(Arrays.toString(councils.toArray()));
                }
            }
            if(interaction.getCommandName().equals("motion")) {

            }
        }
    }

    public static void main(String[] args) {
        // Insert your bot's token here
        String token = "MTAzNDE3MTI3MTkwNDI0Nzg0OA.G9Iauf.6xMD2pkOoB4NBWk_ZhJtfPr2HC9mlrJlFEOs6E";

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        Gson gson = new Gson();

        api.addSlashCommandCreateListener(new SlashCommandListener(api));

        councils = new ArrayList<>();

        boolean end = false;
        int i = 0;
        while(!end) {
            StringBuilder lJsonBuilder = new StringBuilder();
            try {
                File myObj = new File(path + i + "council.json");
                if(myObj.exists()){
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        lJsonBuilder.append(data);
                    }
                    myReader.close();
                    String lJson = lJsonBuilder.toString();
                    councils.add(gson.fromJson(lJson, Council.class));
                } else
                    end = true;
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
                end = true;
            }

            i++;
        }
        System.out.println("The following Councils exist as of right now:");
        System.out.println(Arrays.toString(councils.toArray()));
       // for(int j = 0; j<councils.size(); j++)
        //    System.out.println(((councils.get(j))).getName());



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
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "title", "Title of the motion", true),
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Text of the motion", true)
                ))
                        .setEnabledInDms(false)
                                .createGlobal(api)
                                        .join();
      // List<SlashCommand> commands = (List<SlashCommand>) api.getGlobalSlashCommands().join();


        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }

}