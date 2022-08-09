package me.tech.bungeecord;

import me.tech.common.Deployment;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;

public class BungeecordDeployment extends Plugin {
    public BungeecordDeployment() {
        super();

        try {
            Deployment.runDeployScript(Deployment.getInformation());
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}