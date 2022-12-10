package io.github.Skulli73.Main.objects;

import org.javacord.api.entity.server.Server;

import java.util.LinkedList;
import java.util.List;

public class CanUseBot {
    public List<Long>   whitelistedServers;
    public List<Long>   blackListedServers;
    public boolean      whiteListNecessary;
    public CanUseBot() {
        whitelistedServers = new LinkedList<>();
        blackListedServers = new LinkedList<>();
        whiteListNecessary = false;
    }
    public boolean serverCanUseBot(Server pServer) {
        long lServerId = pServer.getId();
        if(whiteListNecessary) {
            return whitelistedServers.contains(lServerId);
        } else
            return !blackListedServers.contains(lServerId);
    }
}
