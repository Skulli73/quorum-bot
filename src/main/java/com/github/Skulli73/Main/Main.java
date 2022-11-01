package com.github.Skulli73.Main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {

    public static ArrayList<Council> councils;

    public static String path = System.getProperty("user.dir") + "\\src\\main\\java\\com\\github\\Skulli73\\Main\\councils\\";

    static String[] lTypeOfMajorityArray = new String[]{"Majority Vote", "Majority of the entire Membership", "Majority in the Negative"};

    public static DiscordApi api;

    public static String[] majorityTypes;
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
                if(councils.size()==0) {
                    interaction.getChannel().get().sendMessage("This is no floor channel of a council, <@" + interaction.getUser().getId() + ">");
                }
                else if(councils.get(0).isChannelFLoor((TextChannel) interaction.getChannel().get(), lApi, councils, path)) {
                    try {
                        this.createMotion(interaction);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else
                    interaction.getChannel().get().sendMessage("This is no floor channel of a council, <@" + interaction.getUser().getId() + ">");
            }
            if(interaction.getCommandName().equals("move")) {
                if(councils.size()!=0) {
                    if(councils.get(0).isChannelFLoor(interaction.getChannel().get(), lApi, councils, path)) {
                        Council lCouncil = councils.get(0).councilByFloorChannel(interaction.getChannel().get(), lApi, councils, path);
                        Motion lMotion = lCouncil.motionArrayList.get(lCouncil.currentMotion);
                        EmbedBuilder lEmbed = null;
                        try {
                            lEmbed = new EmbedBuilder()
                                    .setTitle(lMotion.getTitle())
                                    .setDescription(lMotion.getText())
                                    .setColor(Color.YELLOW)
                                    .setAuthor(lApi.getUserById(lMotion.introductorId).get().getDisplayName(interaction.getServer().get()), lApi.getUserById(lMotion.introductorId).get().getAvatar().getUrl().toString(), lApi.getUserById(lMotion.introductorId).get().getAvatar());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                            lMotion.getMessage(lApi, lCouncil.getAgendaChannel(lApi)).edit(
                                lEmbed
                            );
                        int requiredCouncillors = (int )Math.ceil(lCouncil.getCouncillorRole(lApi).getUsers().size()*lMotion.neededMajority);
                        String lFooter;
                        if(lMotion.typeOfMajority<3) {
                            lFooter = " councillors need to vote in favour of this bill for it to be passed. \n";
                        }else
                            lFooter = " councillors need to vote against this bill for it not to be passed. \n";
                        new MessageBuilder()
                                .append(interaction.getUser().getMentionTag() + " moves " + lMotion.getTitle() + " from the agenda")
                                .setEmbed(lEmbed.setFooter(requiredCouncillors + lFooter + lTypeOfMajorityArray[lMotion.typeOfMajority] + ", " +  lMotion.neededMajority*100 + "%")
                                ).send(interaction.getChannel().get());

                        Object[] lCouncillors = lCouncil.getCouncillorRole(lApi).getUsers().toArray();

                        for(int i = 0; i<lCouncillors.length;i++) {
                            lMotion.notVoted.add(((User)lCouncillors[i]).getIdAsString());
                            try {
                                lMotion.dmMessages.add( new MessageBuilder().append("Vote")
                                        .setEmbed(
                                                lEmbed.setFooter(requiredCouncillors + lFooter + lTypeOfMajorityArray[lMotion.typeOfMajority] + ", " +  lMotion.neededMajority*100 + "%")
                                        )
                                        .addComponents(
                                                ActionRow.of(
                                                        Button.success("aye" , "Aye"),
                                                        Button.danger("nay" , "Nay"),
                                                        Button.secondary("abstain" , "Abstain")
                                                )
                                        )
                                        .send(((User)lCouncillors[i]).openPrivateChannel().get()).get().getIdAsString());
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        this.saveMotion(lCouncil, lMotion);

                        EmbedBuilder finalLEmbed = lEmbed;
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {

                                    @Override
                                    public void run() {
                                        Council lResultCouncil = councils.get((int)lCouncil.getId());
                                        Motion lResultMotion = lResultCouncil.motionArrayList.get(lResultCouncil.currentMotion);
                                        final int ayeVotes = lResultMotion.ayeVotes.size();
                                        final int nayVotes = lResultMotion.nayVotes.size();
                                        final int abstainVotes = lResultMotion.abstainVotes.size();
                                        final int notVoted = lResultMotion.notVoted.size();

                                        StringBuilder ayeVotesMembers = new StringBuilder();
                                        for(int i = 0; i < ayeVotes; i++) {
                                            try {
                                                String lComma = "";
                                                if(i != 0)
                                                    lComma = ", ";
                                                ayeVotesMembers.append(lComma + lApi.getUserById(lResultMotion.ayeVotes.get(i)).get().getDisplayName(interaction.getServer().get()));
                                            }
                                            catch (InterruptedException | ExecutionException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        StringBuilder notVotedMembers = new StringBuilder();
                                        for(int i = 0; i < notVoted; i++) {
                                            try {
                                                String lComma = "";
                                                if(i != 0)
                                                    lComma = ", ";
                                                notVotedMembers.append(lComma + lApi.getUserById(lResultMotion.notVoted.get(i)).get().getDisplayName(interaction.getServer().get()) );
                                            }
                                            catch (InterruptedException | ExecutionException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        StringBuilder nayVotesMembers = new StringBuilder();
                                        for(int i = 0; i < nayVotes; i++) {
                                            try {
                                                String lComma = "";
                                                if(i != 0)
                                                    lComma = ", ";
                                                nayVotesMembers.append( lComma + lApi.getUserById(lResultMotion.nayVotes.get(i)).get().getDisplayName(interaction.getServer().get()));
                                            }
                                            catch (InterruptedException | ExecutionException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        StringBuilder abstainVotesMembers = new StringBuilder();
                                        for(int i = 0; i < abstainVotes; i++) {
                                            try {
                                                String lComma = "";
                                                if(i != 0)
                                                    lComma = ", ";
                                                abstainVotesMembers.append(lComma+ lApi.getUserById(lResultMotion.abstainVotes.get(i)).get().getDisplayName(interaction.getServer().get()));
                                            }
                                            catch (InterruptedException | ExecutionException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        boolean lPassed = false;
                                        int lTotalCouncillors;
                                        if(lResultMotion.typeOfMajority == 0)
                                            lTotalCouncillors = ayeVotes+nayVotes;
                                        else
                                            lTotalCouncillors = lCouncillors.length;

                                        if(lResultMotion.typeOfMajority != 3)
                                            lPassed = ayeVotes>=lTotalCouncillors*lResultMotion.neededMajority;
                                        else
                                            lPassed = !(nayVotes>=lTotalCouncillors*lResultMotion.neededMajority);
                                        Color lColour;
                                        if(lPassed)
                                            lColour = Color.GREEN;
                                        else
                                            lColour = Color.RED;

                                        String lResultString = "";
                                        if(lPassed)
                                            lResultString = "The following motion passed";
                                        else
                                            lResultString = "The following motion was denied";
                                        new MessageBuilder()
                                                .append(lResultString)
                                                .setEmbed(

                                                finalLEmbed.addField("Aye", ayeVotes + " (" + ayeVotesMembers.toString() + ")")
                                                        .addField("Nay", nayVotes + " (" + nayVotesMembers.toString() + ")")
                                                        .addField("Abstain", abstainVotes + " (" + abstainVotesMembers.toString() + ")")
                                                        .addField("Did not Vote", notVoted + " (" + notVotedMembers.toString() + ")")
                                                        .setColor(lColour)
                                        ).send(lResultCouncil.getResultChannel(lApi));

                                        Message lAgendaMessage = lResultMotion.getMessage(lApi, lResultCouncil.getAgendaChannel(lApi));

                                        lAgendaMessage.edit(lAgendaMessage.getEmbeds().get(0).toBuilder().setColor(Color.GREEN));

                                        lResultMotion.completed = true;


                                        int lNextMotionOld = lResultCouncil.nextMotion;
                                        lResultCouncil.currentMotion = lResultCouncil.nextMotion;
                                        lResultCouncil.nextMotion = lNextMotionOld +1;

                                        this.saveMotion(lResultCouncil, lResultMotion);
                                    }

                                    private void saveMotion(Council lCouncil, Motion lMotion) {
                                        lCouncil.motionArrayList.set(lMotion.id, lMotion);
                                        this.saveCouncil(lCouncil);
                                    }

                                    public void saveCouncil(Council pCouncil) {
                                        GsonBuilder builder = new GsonBuilder();
                                        Gson gson = builder.create();
                                        String fileName = path +(pCouncil.getId()) + "council.json";
                                        FileWriter myWriter = null;
                                        try {
                                            myWriter = new FileWriter(fileName);
                                            myWriter.write(gson.toJson(pCouncil));
                                            myWriter.close();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                },
                                10000
                        );
                    }
                    else
                        interaction.getChannel().get().sendMessage(interaction.getUser().getMentionTag() + ", this is not a floor channel");
                }
                else
                    interaction.getChannel().get().sendMessage(interaction.getUser().getMentionTag() + ", this is not a floor channel");
            }
        }

        private void saveMotion(Council lCouncil, Motion lMotion) {
            lCouncil.motionArrayList.set(lMotion.id, lMotion);
            this.saveCouncil(lCouncil);
        }

        public void createMotion(SlashCommandInteraction pInteraction) throws ExecutionException, InterruptedException {
            Council lCouncil = councils.get(0).councilByFloorChannel((TextChannel) pInteraction.getChannel().get(), lApi, councils, path);

            String lMotionName;
            double lMajority = lCouncil.standardMajority;
            int lTypeOfMajority = 0;
            if(pInteraction.getArguments().size() > 1 && pInteraction.getArguments().get(1).getName().equals("title"))
                {lMotionName = "Motion #" + (lCouncil.motionArrayList.size()+1) + ": " +  pInteraction.getArguments().get(1).getStringValue().get();}
            else if(pInteraction.getArguments().size() > 2 && pInteraction.getArguments().get(2).getName().equals("title"))
                {lMotionName = "Motion #" + (lCouncil.motionArrayList.size()+1) + ": " +  pInteraction.getArguments().get(2).getStringValue().get();}
            else if(pInteraction.getArguments().size() > 3 && pInteraction.getArguments().get(3).getName().equals("title"))
                {lMotionName = "Motion #" + (lCouncil.motionArrayList.size()+1) + ": " +  pInteraction.getArguments().get(3).getStringValue().get();}
            else
                lMotionName = "Motion #" + (lCouncil.motionArrayList.size()+1);

            if(pInteraction.getArguments().size()>1)if(pInteraction.getArguments().get(1).getName().equals("majority"))
                {lMajority = pInteraction.getArguments().get(1).getDecimalValue().get();}
            else if(pInteraction.getArguments().size()>2)if(pInteraction.getArguments().get(2).getName().equals("majority"))
                {lMajority = pInteraction.getArguments().get(2).getDecimalValue().get();}
            else if(pInteraction.getArguments().size()>3)if(pInteraction.getArguments().get(3).getName().equals("majority"))
            {lMajority = pInteraction.getArguments().get(3).getDecimalValue().get();}

            if(pInteraction.getArguments().size()>1)if(pInteraction.getArguments().get(1).getName().equals("type_of_majority"))
                {lTypeOfMajority = pInteraction.getArguments().get(1).getDecimalValue().get().intValue();}
            else if(pInteraction.getArguments().size()>2)if(pInteraction.getArguments().get(2).getName().equals("type_of_majority"))
                {lTypeOfMajority = pInteraction.getArguments().get(2).getDecimalValue().get().intValue();}
            else if(pInteraction.getArguments().size()>3)if(pInteraction.getArguments().get(3).getName().equals("type_of_majority"))
                {lTypeOfMajority = pInteraction.getArguments().get(3).getDecimalValue().get().intValue();}
            String lMotionDesc = pInteraction.getArguments().get(0).getStringValue().get();
            Motion lMotion = new Motion(lMotionName, lMotionDesc, pInteraction.getUser().getId(),
                    new MessageBuilder().setEmbed(
                            new EmbedBuilder()
                                    .setTitle(lMotionName)
                                    .setDescription(lMotionDesc)
                                    .setColor(Color.RED)
                                    .setAuthor(pInteraction.getUser().getDisplayName(pInteraction.getServer().get()), pInteraction.getUser().getAvatar().getUrl().toString(), pInteraction.getUser().getAvatar())
                                    .setFooter(lTypeOfMajorityArray[lTypeOfMajority] + ", " +  lMajority*100 + "%")
                    ).send(lCouncil.getAgendaChannel(lApi)).get().getId(), lMajority, lTypeOfMajority, lCouncil.motionArrayList.size());
            lCouncil.motionArrayList.add(lMotion);
            this.saveCouncil(lCouncil);
        }


        public void saveCouncil(Council pCouncil) {
            councils.set((int)pCouncil.getId(), pCouncil);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for(int i = 0; i<councils.size(); i++) {
                String fileName = path + i + "council.json";
                FileWriter myWriter = null;
                try {
                    myWriter = new FileWriter(fileName);
                    myWriter.write(gson.toJson(councils.get(i)));
                    myWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Insert your bot's token here
        String token = "";
        try {
            File myObj = new File("TOKEN");
            if(myObj.exists()){
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    token = token + data;
                }
                myReader.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        DiscordApi api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        Gson gson = new Gson();

        SlashCommandListener slashCommandListener = new SlashCommandListener(api);
        api.addSlashCommandCreateListener(slashCommandListener);

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
      // List<SlashCommand> commands = (List<SlashCommand>) api.getGlobalSlashCommands().join();


        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());


        api.addMessageComponentCreateListener(lEvent -> {
            MessageComponentInteraction messageComponentInteraction = lEvent.getMessageComponentInteraction();
            String customId = messageComponentInteraction.getCustomId();
            Interaction lInteraction = lEvent.getInteraction();

            messageComponentInteraction.createImmediateResponder()
                    .setContent("Your Vote has been registered")
                    .respond();
            String lUserId = lInteraction.getUser().getIdAsString();

            Council lCouncil = null;

            Motion lMotion = null;

            boolean lEnd = false;
            int j = 0;

            while(!lEnd) {
                StringBuilder lJsonBuilder = new StringBuilder();
                try {
                    File myObj = new File(path + j + "council.json");
                    if(myObj.exists()){
                        Scanner myReader = new Scanner(myObj);
                        while (myReader.hasNextLine()) {
                            String data = myReader.nextLine();
                            lJsonBuilder.append(data);
                        }
                        myReader.close();
                        String lJson = lJsonBuilder.toString();
                        councils.set(j, gson.fromJson(lJson, Council.class));
                        System.out.println(councils.get(j).motionArrayList.size()-1);
                        System.out.println(councils.get(j).currentMotion);
                        if(councils.get(j).motionArrayList.size()-1 >= councils.get(j).currentMotion ) {
                            System.out.println(lEvent.getMessageComponentInteraction().getMessage().getIdAsString() + "\n" + councils.get(j).motionArrayList.get(councils.get(j).currentMotion).dmMessages);
                            if(councils.get(j).motionArrayList.get(councils.get(j).currentMotion).dmMessages.contains(lEvent.getMessageComponentInteraction().getMessage().getIdAsString())) {
                                lCouncil = councils.get(j);
                                lMotion = councils.get(j).motionArrayList.get(councils.get(j).currentMotion);
                            }
                        }
                    } else
                        lEnd = true;
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                    lEnd = true;
                }

                j++;
            }

            switch (customId) {
                case "aye":
                    assert lMotion != null;
                    lMotion.nayVotes.remove(lUserId);
                    lMotion.abstainVotes.remove(lUserId);
                    lMotion.notVoted.remove(lUserId);

                    lMotion.ayeVotes.add(lUserId);
                    break;
                case "nay":
                    assert lMotion != null;
                    lMotion.ayeVotes.remove(lUserId);
                    lMotion.abstainVotes.remove(lUserId);
                    lMotion.notVoted.remove(lUserId);

                    lMotion.nayVotes.add(lUserId);
                    break;
                case "abstain":
                    assert lMotion != null;
                    lMotion.ayeVotes.remove(lUserId);
                    lMotion.nayVotes.remove(lUserId);
                    lMotion.notVoted.remove(lUserId);


                    lMotion.abstainVotes.add(lUserId);
                    break;
            }
            assert lCouncil != null;
            slashCommandListener.saveMotion(lCouncil, lMotion);
        });
    }

    public Main getCurrentInstance() {return this;}

}