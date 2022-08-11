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
        this.token = settings.get("token");
        this.organization = settings.get("organization");
        this.repository = settings.get("repository");
        this.branch = settings.get("branch");
        this.serverName = settings.get("server_name");
        this.webhookUrl = settings.get("webhook_url");
        this.jarName = settings.get("jar_name");
    }

    public Optional<String> buildPythonCommand() {
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