package io.eliotesta98.AnvilPlus.Objects;

import org.bukkit.Location;

import java.util.Random;

public class AnvilInformations {

    private Location anvilLocation;
    private int anvilUses;
    private Random random;
    private double damageProbability = 0.12;

    public AnvilInformations() {
        this.random = new Random();
    }

    public AnvilInformations(Location anvilLocation, int anvilUses) {
        this.anvilLocation = anvilLocation;
        this.anvilUses = anvilUses;
        this.random = new Random();
    }

    public Location getAnvilLocation() {
        return anvilLocation;
    }

    public void setAnvilLocation(Location anvilLocation) {
        this.anvilLocation = anvilLocation;
    }

    public int getAnvilUses() {
        return anvilUses;
    }

    public void setAnvilUses(int anvilUses) {
        this.anvilUses = anvilUses;
    }

    public double getDamageProbability() {
        return damageProbability;
    }

    public void setDamageProbability(double damageProbability) {
        this.damageProbability = damageProbability;
    }

    public boolean damage() {
        boolean result = shouldDamage(this.damageProbability);
        if (result) {
            this.damageProbability = 0.12;
            return true;
        } else {
            this.damageProbability = this.damageProbability + 0.12;
            return false;
        }
    }

    private boolean shouldDamage(double damageProbability) {
        return this.random.nextDouble() <= damageProbability;
    }

    @Override
    public String toString() {
        return "AnvilInformations{" +
                "anvilLocation=" + anvilLocation +
                ", anvilUses=" + anvilUses +
                '}';
    }
}
