package com.yourname.hatelock;

public class AggroRule {

    public String player;
    public String mobId;
    public long expireAt;

    public AggroRule() {
    }

    public AggroRule(String player, String mobId, long expireAt) {
        this.player = player;
        this.mobId = mobId;
        this.expireAt = expireAt;
    }

    public boolean isExpired(long now) {
        return expireAt != -1 && now > expireAt;
    }

    public boolean isPermanent() {
        return expireAt == -1;
    }
}