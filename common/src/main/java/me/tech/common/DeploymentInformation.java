package me.tech.common;

import java.util.Map;

public class DeploymentInformation {
    private String token;

    private String organization;

    private String repository;

    private String branch;

    private String serverName;

    private String webhookUrl;

    public DeploymentInformation(Map<String, String> settings) {
        this.token = settings.get("token");
        this.organization = settings.get("organization");
        this.repository = settings.get("repository");
        this.branch = settings.get("branch");
        this.serverName = settings.get("server_name");
        this.webhookUrl = settings.get("webhook_url");
    }

    public String getToken() {
        return token;
    }

    public String getOrganization() {
        return organization;
    }

    public String getRepository() {
        return repository;
    }

    public String getBranch() {
        return branch;
    }

    public String getServerName() {
        return serverName;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }
}