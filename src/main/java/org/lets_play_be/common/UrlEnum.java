package org.lets_play_be.common;

public enum UrlEnum {

    SUCCESS_URL("http://localhost:8080/index.html"),
    GITHUB_EMAILS_URL("https://api.github.com/user/emails"),
    DISCORD_AVATAR_URL("https://cdn.discordapp.com/avatars/%s/%s.png"),
    DISCORD_USERINFO_URL("https://discordapp.com/api/users/@me");

    private final String url;

    UrlEnum(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
