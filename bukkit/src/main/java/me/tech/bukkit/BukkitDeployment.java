package me.tech.bukkit;

import me.tech.common.Deployment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class BukkitDeployment extends JavaPlugin {
    public BukkitDeployment() {
        super();

        try {
            Deployment.runDeployScript(Deployment.getInformation());
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
