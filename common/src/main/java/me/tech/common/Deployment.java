package me.tech.common;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Deployment {
    private static final String START_CMD_FILE_PATH = "/home/minecraft/signal/start_cmd";

    public static final String SERVER_DIRECTORY = "/home/minecraft/server";

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

        // I mean, we can just reuse this and add our own stuff to it.
        String modifiedJavaCmd = startCmdInitialContents.replace(
                "-jar server.jar",
                information.buildJavaCommand()
        );
        Optional<String> pythonCmd = information.buildPythonCommand();

        var writer = new BufferedWriter(new FileWriter(START_CMD_FILE_PATH, true));
        writer.newLine();

        if(pythonCmd.isPresent()) {
            writer.write(pythonCmd.get());
            writer.newLine();
        }

        writer.write(modifiedJavaCmd);

        writer.close();

        // killed bruh fr bros dead :pray: skull emoji x7
        Runtime.getRuntime().halt(0);
    }

    public static DeploymentInformation getInformation() throws RuntimeException, IOException {
        Set<String> settings = Set.of("token", "organization", "repository", "branch", "server_name", "webhook_url", "jar_name");

        File deploymentInfoFile = new File(SERVER_DIRECTORY, ".deployment_information");
        // Create tmp deployment file if it doesn't exist.
        if(!deploymentInfoFile.exists()) {
            deploymentInfoFile.createNewFile();

            StringBuilder builder = new StringBuilder();
            for(String setting : settings) {
                // Temporary until another solution is made.
                if(setting.equalsIgnoreCase("jar_name")) {
                    builder.append(setting).append("=").append("server.jar");
                } else {
                    builder.append(setting).append("=");
                }

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
                    Objects.requireNonNull(Deployment.class
                            .getClassLoader()
                            .getResourceAsStream("deployment.py")),
                    out
            );
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
