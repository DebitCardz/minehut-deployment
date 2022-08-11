package me.tech.common;

import java.util.Map;
import java.util.Optional;

public class DeploymentInformation {
    private final String token;

    private final String organization;

    private final String repository;

    private final String branch;

    private final String serverName;

    private final String webhookUrl;

    private final String jarName;

    public DeploymentInformation(Map<String, String> settings) {
        this.token = settings.getOrDefault("token", null);
        this.organization = settings.getOrDefault("organization", null);
        this.repository = settings.getOrDefault("repository", null);
        this.branch = settings.getOrDefault("branch", null);
        this.serverName = settings.getOrDefault("server_name", null);
        this.webhookUrl = settings.getOrDefault("webhook_url", null);
        this.jarName = settings.getOrDefault("jar_name", "server.jar");
    }

    public Optional<String> buildPythonCommand() {
        // Github deployment not setup, just don't use it.
        if(
                token == null
                || organization == null
                || repository == null
                || branch == null
        ) {
            return Optional.empty();
        }

        return Optional.of(String.format(
                "python3 %s/deployment.py -token %s -organization %s -repository %s -branch %s -servername %s -webhook %s",
                Deployment.SERVER_DIRECTORY,
                token, organization, repository,
                branch, serverName, webhookUrl
        ));
    }

    public String buildJavaCommand() {
        String cmd = "-Ddeployed=1 ";

        cmd += "-jar " + jarName;

        return cmd;
    }
}