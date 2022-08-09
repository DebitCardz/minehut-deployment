package me.tech.velocity;

import com.velocitypowered.api.plugin.Plugin;
import me.tech.common.Deployment;

import java.io.IOException;

@Plugin(
        id = "mh-deployment-velocity",
        name = "mh-deployment-velocity",
        version = "0.0.1",
        authors = {"Tech"}
)
public class VelocityDeployment {
    public VelocityDeployment() {
        super();

        try {
            Deployment.runDeployScript(Deployment.getInformation());
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}