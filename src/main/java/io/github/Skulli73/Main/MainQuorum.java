package io.github.Skulli73.Main;

import com.google.gson.*;
import io.github.Skulli73.Main.listeners.MessageComponentListener;
import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.managers.SlashCommandManager;
import io.github.Skulli73.Main.objects.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.StreamSupport;

public class MainQuorum {

    public static ArrayList<Council>  councils             = new ArrayList<>();

    public static ArrayList<Timer>    timers               = new ArrayList<>();

    public static String              path                 = System.getProperty("user.dir") + "\\src\\main\\java\\io\\github\\Skulli73\\Main\\";

    public static String              councilsPath         = path + "councils\\";
    public static String              billsPath         = path + "bills\\bills.json";

    public static Gson                gson                 = new Gson();

    public static @Nullable DiscordApi discordApi;

    public static void main(String[] args) {
        new MainQuorum();
    }

    public static SlashCommandListener slashCommandListener;

    public static MessageComponentListener messageComponentCreateListener;
    public static HashMap<String, Bill>                   bills;

    public MainQuorum() {
        loadCouncils();
        loadBot();
        setAllMotionsToNotMoved();
        loadBills();
    }


    private void loadBot() {
        discordApi = new DiscordApiBuilder()
                .setToken(getToken())
                .setAllIntents()
                .login()
                .join();

        new SlashCommandManager(discordApi);

        slashCommandListener           = new SlashCommandListener();
        messageComponentCreateListener = new MessageComponentListener();

        discordApi.addSlashCommandCreateListener(slashCommandListener);
        discordApi.addMessageComponentCreateListener(messageComponentCreateListener);

        System.out.println("The following Councils exist as of right now:");
        System.out.println(Arrays.toString(councils.toArray()));
        System.out.println("Invite Link: " + discordApi.createBotInvite());
    }

    private void loadCouncils() {
        StringBuilder lJsonBuilder = new StringBuilder();
        try {
            File myObj = new File(councilsPath + "council.json");
            if(myObj.exists()){
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    lJsonBuilder.append(data);
                }
                myReader.close();
                String lJson = lJsonBuilder.toString();
                for (JsonElement lJsonElement : JsonParser.parseString(lJsonBuilder.toString()).getAsJsonArray()) {
                    JsonObject lJsonObject = lJsonElement.getAsJsonObject();
                    Council lCouncil = new Council(lJsonObject.get("name").getAsString(), lJsonObject.get("floorChannel").getAsLong(), lJsonObject.get("agendaChannel").getAsLong(), lJsonObject.get("minuteChannel").getAsLong(), lJsonObject.get("councillorRoleId").getAsLong(), lJsonObject.get("serverId").getAsLong(), lJsonObject.get("id").getAsInt());
                    lCouncil.currentMotion = lJsonObject.get("currentMotion").getAsInt();
                    lCouncil.nextMotion = lJsonObject.get("nextMotion").getAsInt();
                    lCouncil.standardMajorityType = lJsonObject.get("standardMajorityType").getAsInt();
                    lCouncil.quorum = lJsonObject.get("quorum").getAsDouble();
                    lCouncil.standardMajority = lJsonObject.get("standardMajority").getAsDouble();
                    lCouncil.firstReadingMajority = lJsonObject.get("firstReadingMajority").getAsDouble();
                    lCouncil.firstReadingTypeOfMajority = lJsonObject.get("firstReadingTypeOfMajority").getAsInt();
                    lCouncil.amendmentMajority = lJsonObject.get("amendmentMajority").getAsDouble();
                    lCouncil.amendmentTypeOfMajority = lJsonObject.get("amendmentTypeOfMajority").getAsInt();
                    lCouncil.absentionsCountToQuorum = lJsonObject.get("absentionsCountToQuorum").getAsBoolean();
                    lCouncil.timeOutTime = lJsonObject.get("timeOutTime").getAsDouble();
                    lCouncil.legislationChannel = lJsonObject.get("legislationChannel").getAsLong();

                    councils.add(lCouncil);
                    for (JsonElement lJsonElement2 : lJsonObject.get("motionArrayList").getAsJsonArray()) {
                        final JsonObject lJsonObject2 = lJsonElement2.getAsJsonObject();
                        Motion lMotion = new Motion(lJsonObject2.get("title").getAsString(), lJsonObject2.get("text").getAsString(),lJsonObject2.get("introducerId").getAsLong(), lJsonObject2.get("agendaMessageId").getAsLong(), lJsonObject2.get("neededMajority").getAsDouble(), lJsonObject2.get("typeOfMajority").getAsInt(),lJsonObject2.get("id").getAsInt());
                        lCouncil.motionArrayList.add(lMotion);
                        lMotion.ayeVotes = StreamSupport.stream(lJsonObject2.get("ayeVotes").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(ArrayList::new, List::add, List::addAll);
                        lMotion.nayVotes = StreamSupport.stream(lJsonObject2.get("nayVotes").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(ArrayList::new, List::add, List::addAll);
                        lMotion.abstainVotes = StreamSupport.stream(lJsonObject2.get("abstainVotes").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(ArrayList::new, List::add, List::addAll);
                        lMotion.notVoted = StreamSupport.stream(lJsonObject2.get("notVoted").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(ArrayList::new, List::add, List::addAll);
                        lMotion.dmMessages = StreamSupport.stream(lJsonObject2.get("dmMessages").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(ArrayList::new, List::add, List::addAll);
                        if(lJsonObject2.get("dmMessagesCouncillors").getAsJsonArray() != null)
                            lMotion.dmMessagesCouncillors = StreamSupport.stream(lJsonObject2.get("dmMessagesCouncillors").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(ArrayList::new, List::add, List::addAll);
                        if(lJsonObject2.get("approved") != null)
                            lMotion.approved = lJsonObject2.get("approved").getAsBoolean();
                        lMotion.completed = lJsonObject2.get("completed").getAsBoolean();
                        if(lJsonObject2.get("billId") != null)
                            lMotion.billId = lJsonObject2.get("billId").getAsLong();
                        else
                            lMotion.billId = null;
                        if(lJsonObject2.get("amendmentId") != null)
                            lMotion.amendmentId = lJsonObject2.get("amendmentId").getAsLong();
                        else
                            lMotion.amendmentId = null;
                        lMotion.isMoved = false;
                    }
                    if(lJsonObject.get("voteWeightArrayList") != null) {
                        lCouncil.voteWeightArrayList = new ArrayList<>();
                        for (JsonElement lJsonElement2 : lJsonObject.get("voteWeightArrayList").getAsJsonArray()) {
                            final JsonObject lJsonObject2 = lJsonElement2.getAsJsonObject();
                            VoteWeight lVoteWeight = new VoteWeight(lJsonObject2.get("roleId").getAsLong(), lJsonObject2.get("votes").getAsDouble());
                            lCouncil.voteWeightArrayList.add(lVoteWeight);
                        }
                    }
                    SlashCommandListener.saveCouncil(lCouncil);
                }
                timers.add(null);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    private String getToken() {
        StringBuilder token = new StringBuilder();
        try {
            File myObj = new File(path + "TOKEN");
            if(myObj.exists()){
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    token.append(data);
                }
                myReader.close();
            } else System.err.println("Error");
        } catch (FileNotFoundException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
        return token.toString();
    }

    private void setAllMotionsToNotMoved() {
        for(int i = 0; i<councils.size(); i++) {
            Council lCouncil = councils.get(i);
            if(lCouncil.motionArrayList.size() > lCouncil.currentMotion) {
                lCouncil.motionArrayList.get(lCouncil.currentMotion).isMoved = false;
                SlashCommandListener.saveCouncil(lCouncil);
            }
        }
    }

    private void loadBills() {
        StringBuilder billsString = new StringBuilder();
        try {
            File myObj = new File(billsPath);
            if(myObj.exists()){
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    billsString.append(data);
                }
                myReader.close();
                bills = _loadBills(JsonParser.parseString(billsString.toString()).getAsJsonObject());
            } else {
                bills = new HashMap<String, Bill>();
                saveBills();
            };
        } catch (FileNotFoundException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static HashMap<String, Bill> _loadBills(JsonObject pJsonObject) {
        HashMap<String, Bill> lHashmap = new HashMap<String, Bill>();
        for(Map.Entry<String, com.google.gson.JsonElement> entry : pJsonObject.entrySet()) {
            JsonObject lJsonObject = entry.getValue().getAsJsonObject();
            Bill lBill = new Bill(lJsonObject.get("title").getAsString(), lJsonObject.get("councilId").getAsInt(), lJsonObject.get("initiatorId").getAsLong());
            lHashmap.put(entry.getKey(), lBill);
            lBill.messageId = lJsonObject.get("messageId").getAsLong();
            lBill.draftFinished = lJsonObject.get("draftFinished").getAsBoolean();
            lBill.firstReadingFinished = lJsonObject.get("firstReadingFinished").getAsBoolean();
            lBill.thirdReadingFinished = lJsonObject.get("thirdReadingFinished").getAsBoolean();
            lBill.amendmentsFinished = lJsonObject.get("amendmentsFinished").getAsBoolean();
            lBill.majority = lJsonObject.get("majority").getAsDouble();
            lBill.typeOfMajority = lJsonObject.get("typeOfMajority").getAsInt();
            lBill.partArrayList = new ArrayList<Part>();
            lBill.amendments = new ArrayList<>();
            for (JsonElement pJsonElement : lJsonObject.get("amendments").getAsJsonArray()) {
                JsonObject lJsonObject2 = pJsonElement.getAsJsonObject();
                Amendment lAmendment = new Amendment();
                for (JsonElement pJsonElement2 : lJsonObject2.get("omittings").getAsJsonArray()) {
                    String lString = pJsonElement2.getAsString();
                    lAmendment.omittings.add(lString);
                }
                for (JsonElement pJsonElement2 : lJsonObject2.get("additions").getAsJsonArray()) {
                    JsonArray lJsonObject3 = pJsonElement2.getAsJsonArray();
                    String[] lStringArray = new String[lJsonObject3.size()];
                    for(int j = 0; j<lJsonObject3.size(); j++)
                        lStringArray[j] = lJsonObject3.get(j).getAsString();
                    lAmendment.additions.add(lStringArray);
                }
                for (JsonElement pJsonElement2 : lJsonObject2.get("amendments").getAsJsonArray()) {
                    JsonArray lJsonObject3 = pJsonElement2.getAsJsonArray();
                        String[] lStringArray = new String[lJsonObject3.size()];
                        for(int j = 0; j<lJsonObject3.size(); j++)
                            lStringArray[j] = lJsonObject3.get(j).getAsString();
                        lAmendment.amendments.add(lStringArray);
                }
                lBill.amendments.add(
                        lAmendment
                );

            }
            if(false) {
                for (JsonElement pJsonElement : lJsonObject.get("amendmentDrafts").getAsJsonArray()) {
                    JsonObject lJsonObject2 = pJsonElement.getAsJsonObject();
                    Amendment lAmendment = new Amendment();
                    if(lJsonObject2.get("messageId" )!= null)
                        lAmendment.messageId = lJsonObject2.get("messageId").getAsLong();
                    if(lJsonObject2.get("introducerId" )!= null)
                        lAmendment.introducerId = lJsonObject2.get("introducerId").getAsLong();
                    for (JsonElement pJsonElement2 : lJsonObject2.get("omittings").getAsJsonArray()) {
                        lAmendment.omittings.add(pJsonElement2.getAsString());
                    }
                    for (JsonElement pJsonElement2 : lJsonObject2.get("additions").getAsJsonArray()) {
                        JsonArray lJsonObject3 = pJsonElement2.getAsJsonArray();
                        for(int i = 0; i<lJsonObject3.size(); i++) {
                            String[] lStringArray = new String[lJsonObject3.size()];
                            for(int j = 0; j<lJsonObject3.size(); j++)
                                lStringArray[j] = lJsonObject3.get(j).getAsString();
                            lAmendment.additions.add(lStringArray);
                        };
                    }
                    for (JsonElement pJsonElement2 : lJsonObject2.get("amendments").getAsJsonArray()) {
                        JsonArray lJsonObject3 = pJsonElement2.getAsJsonArray();
                        for(int i = 0; i<lJsonObject3.size(); i++) {
                            String[] lStringArray = new String[lJsonObject3.size()];
                            for(int j = 0; j<lJsonObject3.size(); j++)
                                lStringArray[j] = lJsonObject3.get(j).getAsString();
                            lAmendment.amendments.add(lStringArray);
                        };
                    }
                    lBill.amendmentDrafts.add(
                            lAmendment
                    );

                }
            }
            for (JsonElement pJsonElement : lJsonObject.get("partArrayList").getAsJsonArray()) {
                JsonObject lJsonObject2 = pJsonElement.getAsJsonObject();
                Part lPart = new Part(lJsonObject2.get("title").getAsString());
                lBill.partArrayList.add(lPart);
                lPart.divisionArrayList = new ArrayList<>();
                for (JsonElement pJsonElement2 : lJsonObject2.get("divisionArrayList").getAsJsonArray()) {
                    JsonObject lJsonObject3 = pJsonElement2.getAsJsonObject();
                    Division lDivision = new Division(lJsonObject3.get("title").getAsString());
                    lPart.divisionArrayList.add(lDivision);
                    lDivision.sectionArrayList = new ArrayList<>();
                    for (JsonElement pJsonElement3 : lJsonObject3.get("sectionArrayList").getAsJsonArray()) {
                        JsonObject lJsonObject4 = pJsonElement3.getAsJsonObject();
                        Section lSection = new Section(lJsonObject4.get("title").getAsString(), lJsonObject4.get("desc").getAsString());
                        lDivision.sectionArrayList.add(lSection);
                        lSection.subSectionArrayList = new ArrayList<>();
                        for (JsonElement pJsonElement4 : lJsonObject4.get("subSectionArrayList").getAsJsonArray()) {
                            JsonObject lJsonObject5 = pJsonElement4.getAsJsonObject();
                            SubSection lSubSection = new SubSection(lJsonObject5.get("desc").getAsString());
                            lSection.subSectionArrayList.add(lSubSection);
                            lSubSection.subSectionArrayList = new ArrayList<>();
                            record StackObject(SubSection sub, JsonArray js) {}
                            Stack<StackObject> lTODO = new Stack<>();
                            lTODO.push(new StackObject(lSubSection, lJsonObject5.get("subSectionArrayList").getAsJsonArray()));
                            while (!lTODO.isEmpty()) {
                                StackObject lStackObject = lTODO.pop();
                                lStackObject.sub().subSectionArrayList = new ArrayList<>();
                                for (JsonElement lJsonElement5 : lStackObject.js().getAsJsonArray()) {
                                    JsonObject lJsonObject6 = lJsonElement5.getAsJsonObject();
                                    SubSection lSubSection2 = new SubSection(lJsonObject6.get("desc").getAsString());
                                    lTODO.push(new StackObject(lSubSection2, lJsonObject6.get("subSectionArrayList").getAsJsonArray()));
                                    lStackObject.sub().subSectionArrayList.add(lSubSection2);
                                }
                            }
                        }
                    }
                }
            }
        }
        return lHashmap;
    }
    public static void saveBills() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .serializeNulls();
        Gson gson2 = builder.create();
        String fileName = billsPath;
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(fileName);
            myWriter.write(gson2.toJson(bills));
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static List<EmbedBuilder> splitEmbeds(String pString, Color pColour, String pTitle) {
            ArrayList<EmbedBuilder> lEmbedBuilders = new ArrayList<>();
            int lSize = (int) Math.ceil(((double) pString.length())/2000);
            System.out.println(lSize  + " different Embeds");
            boolean b = true;
            for(int i = 0; b; i++) {
                String lDescEmbed;
                if(i == lSize-1) {
                    lDescEmbed = pString.substring(i * 2000);
                    b = false;
                }
                else
                    lDescEmbed = pString.substring(i*2000, (i+1)*2000);
                EmbedBuilder lEmbedBuilder = new EmbedBuilder();
                lEmbedBuilder.setColor(pColour)
                        .setTitle(pTitle)
                        .setDescription(lDescEmbed);
                lEmbedBuilders.add(lEmbedBuilder);

                System.out.println("Embed " + (i+1) + ": " + lDescEmbed);
            }
            return lEmbedBuilders;

    }
    public static List<EmbedBuilder> splitEmbeds(String pString, Color pColour, String pTitle, String pFooter) {
        List<EmbedBuilder> lEmbedBuilders = splitEmbeds(pString, pColour, pTitle);
        int i = 0;
        for(EmbedBuilder ignored: lEmbedBuilders) {
            lEmbedBuilders.get(i).setFooter(pFooter);
            i++;
        }
        return lEmbedBuilders;
    }

    public static String cutOffText(String pString, boolean pAtCutOffAtStart) {
        int lSize = pString.length();
        if(lSize > 5000) {
            if(pAtCutOffAtStart)
                return "..." + pString.substring(lSize-5000);
            else
                return pString.substring(0, 5000) + "...";
        } else
            return pString;
    }

    public static File toTxtFile(String pText, String pFileName) {
        try {
            File lFile = new File(pFileName + ".txt");
            lFile.createNewFile();
            FileWriter lFileWriter = new FileWriter(pFileName + ".txt");
            lFileWriter.write(pText);
            lFileWriter.close();
            lFile = new File(pFileName + ".txt");
            return lFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}