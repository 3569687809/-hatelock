package com.yourname.hatelock;

public class AggroRule {

    public String player;
    public String mobId;
    public long expireAt;

    public AggroRule(String player, String mobId, long expireAt) {
        this.player = player;
        this.mobId = mobId;
        this.expireAt = expireAt;
    }

    public boolean isPermanent() {
        return expireAt < 0;
    }

    public boolean isExpired(long now) {
        return !isPermanent() && now > expireAt;
    }
}