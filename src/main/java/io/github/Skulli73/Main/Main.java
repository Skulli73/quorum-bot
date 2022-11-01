package io.github.Skulli73.Main;

import com.google.gson.Gson;
import io.github.Skulli73.Main.listeners.MessageComponentListener;
import io.github.Skulli73.Main.listeners.SlashCommandListener;
import io.github.Skulli73.Main.managers.SlashCommandManager;
import io.github.Skulli73.Main.objects.Council;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static ArrayList<Council>  councils             = new ArrayList<>();

    public static String              path                 = System.getProperty("user.dir") + "\\src\\main\\java\\io\\github\\Skulli73\\Main\\";

    public static String              councilsPath         = path + "councils\\";

    public static Gson                gson                 = new Gson();

    public static DiscordApi    lApi;

    public static void main(String[] args) {
        new Main();
    }


    public Main() {
        loadCouncils();
        loadBot();
    }


    private void loadBot() {
        lApi = new DiscordApiBuilder()
                .setToken(getToken())
                .setAllIntents()
                .login()
                .join();

        new SlashCommandManager(lApi);

        SlashCommandListener            slashCommandListener           = new SlashCommandListener();
        MessageComponentListener        messageComponentCreateListener = new MessageComponentListener();

        lApi.addSlashCommandCreateListener(slashCommandListener);
        lApi.addMessageComponentCreateListener(messageComponentCreateListener);

        System.out.println("The following Councils exist as of right now:");
        System.out.println(Arrays.toString(councils.toArray()));
        System.out.println("Invite Link: " + lApi.createBotInvite());
    }

    private void loadCouncils() {
        boolean end = false;
        int i = 0;
        while(!end) {
            StringBuilder lJsonBuilder = new StringBuilder();
            try {
                File myObj = new File(councilsPath + i + "council.json");
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
}