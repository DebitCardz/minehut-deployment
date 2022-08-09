package me.tech.common;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Deployment {
    private static final String START_CMD_FILE_PATH = "/home/minecraft/signal/start_cmd";

    private static final String SERVER_DIRECTORY = "/home/minecraft/server";

    public static void runDeployScript(DeploymentInformation information) throws IOException, RuntimeException {
        if(System.getProperty("deployed") != null) {
            System.out.println("Deployed from Github.");
            return;
        }

        createDeployScript();

        File startCmdFile = new File(START_CMD_FILE_PATH);
        String startCmdInitialContents = Files.readString(Paths.get(startCmdFile.toURI()));

        // "python is awful." - fixed
        if(startCmdInitialContents.toLowerCase().contains("python3")) {
            return;
        }

        String modifiedJavaCmd = startCmdInitialContents.replace(
                "-jar server.jar",
                "-Ddeployed=1 -jar server.jar"
        );

        var writer = new BufferedWriter(new FileWriter(START_CMD_FILE_PATH, true));
        writer.newLine();
        // todo: make this dynamic.
        writer.write(String.format(
                "python3 %s/deployment.py -token %s -organization %s -repository %s -branch %s -servername %s -webhook %s",
                SERVER_DIRECTORY,
                information.getToken(), information.getOrganization(), information.getRepository(),
                information.getBranch(), information.getServerName(), information.getWebhookUrl()
        ));
        writer.newLine();
        writer.write(modifiedJavaCmd);
        writer.close();

        // killed bruh fr bros dead :pray: skull emoji x7
        Runtime.getRuntime().halt(0);
    }

    public static DeploymentInformation getInformation() throws RuntimeException, IOException {
        Set<String> settings = Set.of("token", "organization", "repository", "branch", "server_name", "webhook_url");

        File deploymentInfoFile = new File(SERVER_DIRECTORY, ".deployment_information");
        // Create tmp deployment file if it doesn't exist.
        if(!deploymentInfoFile.exists()) {
            deploymentInfoFile.createNewFile();

            StringBuilder builder = new StringBuilder();
            for(String setting : settings) {
                builder.append(setting + "=");
                builder.append("\n");
            }
            builder.deleteCharAt(builder.length());

            Files.writeString(
                    Paths.get(deploymentInfoFile.toURI()),
                    builder.toString(),
                    StandardCharsets.UTF_8
            );

            throw new RuntimeException("deployment_information doesn't exist.");
        }

        Map<String, String> mappedSettings = new HashMap<>();
        for(String setting : Files.readString(Paths.get(deploymentInfoFile.toURI())).split("\n")) {
            try {
                // K-V
                String[] strs = setting.split("=");
                mappedSettings.put(strs[0], strs[1]);
            } catch(ArrayIndexOutOfBoundsException ex) {
                System.err.println("Property " + setting + " has no value.");
            }
        }

        return new DeploymentInformation(mappedSettings);
    }

    private static void createDeployScript() {
        File oldDeployment = new File(SERVER_DIRECTORY, "deployment.py");
        if(oldDeployment.exists()) {
            oldDeployment.delete();
        }

        try (
                OutputStream out = new FileOutputStream(
                        new File(SERVER_DIRECTORY, "deployment.py")
                )
        ) {
            IOUtils.copy(
                    Deployment.class
                            .getClassLoader()
                            .getResourceAsStream("deployment.py"),
                    out
            );
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
