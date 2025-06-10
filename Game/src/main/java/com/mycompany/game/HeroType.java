package com.mycompany.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class HeroType {
    public static final HashMap<Integer, HeroType> types = new HashMap<>();
    public String name, passive;
    public int[] baseStats; // STR, AGI, INT, VIT, LUK
    public LinkedList<Skill> skills;
    public Skill ultimate;
    public HeroType ascendedForm;

    static {
        // BEGINNER CLASSES
        HeroType warrior = new HeroType("Warrior", "Berserker's Rage: Gain +10% ATK when below 50% HP.",
                new int[]{10, 4, 3, 9, 4},
                new Skill[]{
                        new Skill("Slash", "Quick sword attack", 10, 0, 7, 1),
                        new Skill("Guard", "Block incoming damage", 0, 3, 5, 2),
                        new Skill("Battle Cry", "Increase STR for 3 turns", 0, 0, 8, 3)
                }, new Skill("Berserker Strike", "Devastating blow", 40, 0, 20, 5)
        );
        HeroType mage = new HeroType("Mage", "Mana Overflow: Regenerate 5 MP/turn.",
                new int[]{3, 6, 13, 5, 3},
                new Skill[]{
                        new Skill("Fireball", "Cast a fireball", 12, 2, 10, 1),
                        new Skill("Ice Shard", "Cast ice shard", 10, 2, 8, 2),
                        new Skill("Magic Shield", "Block damage for 1 turn", 0, 3, 12, 3)
                }, new Skill("Meteor", "Summon meteor", 45, 2, 30, 6)
        );
        HeroType archer = new HeroType("Archer", "Eagle Eye: +15% ACC.",
                new int[]{7, 13, 4, 6, 5},
                new Skill[]{
                        new Skill("Arrow Shot", "Shoot arrow", 9, 1, 6, 1),
                        new Skill("Double Shot", "Shoot twice", 13, 1, 12, 2),
                        new Skill("Trap", "Set a trap (chance to stun)", 7, 1, 10, 3)
                }, new Skill("Rain of Arrows", "Volley of arrows", 35, 1, 22, 5)
        );
        HeroType healer = new HeroType("Healer", "Holy Light: Healing is 10% stronger.",
                new int[]{2, 6, 10, 6, 6},
                new Skill[]{
                        new Skill("Heal", "Restore HP", 12, 2, 10, 1),
                        new Skill("Barrier", "Reduce damage for 2 turns", 0, 3, 8, 2),
                        new Skill("Smite", "Light attack", 8, 2, 6, 1)
                }, new Skill("Divine Blessing", "Massive heal", 0, 2, 30, 6)
        );
        HeroType rogue = new HeroType("Rogue", "Shadowstep: +10% Evade.",
                new int[]{8, 10, 5, 5, 8},
                new Skill[]{
                        new Skill("Backstab", "High crit attack", 14, 4, 10, 1),
                        new Skill("Poison", "Poison enemy", 6, 4, 8, 2),
                        new Skill("Evade", "Dodge next attack", 0, 1, 6, 3)
                }, new Skill("Assassinate", "Lethal strike", 30, 4, 25, 6)
        );

        // Ascended forms (could be given new/stronger skills similarly)

        warrior.ascendedForm = null;
        mage.ascendedForm = null;
        archer.ascendedForm = null;
        healer.ascendedForm = null;
        rogue.ascendedForm = null;

        types.put(1, warrior);
        types.put(2, mage);
        types.put(3, archer);
        types.put(4, healer);
        types.put(5, rogue);
    }

    public HeroType(String name, String passive, int[] baseStats, Skill[] skills, Skill ultimate) {
        this.name = name;
        this.passive = passive;
        this.baseStats = baseStats;
        this.skills = new LinkedList<>(Arrays.asList(skills));
        this.ultimate = ultimate;
        this.ascendedForm = null;
    }

    public LinkedList<Skill> getSkills() {
        return new LinkedList<>(skills);
    }

    public static HeroType chooseHeroType(java.util.Scanner scanner) {
        System.out.println("[Choose Your Hero Type]");
        int i = 1;
        for (HeroType t : types.values()) {
            System.out.println(i + ". " + t.name + " | Passive: " + t.passive);
            i++;
        }
        System.out.print("Choose class (1-5): ");
        int choice = 1;
        try {
            choice = Integer.parseInt(scanner.nextLine());
            if (!types.containsKey(choice)) choice = 1;
        } catch (Exception e) {
            choice = 1;
        }
        return types.get(choice);
    }
}