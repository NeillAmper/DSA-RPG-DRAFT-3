package com.mycompany.game;

import java.util.*;

public class Hero extends Entity {
    public HeroType type;
    public int level;
    public int exp;
    public int mana;
    public int maxMana;
    public LinkedList<Skill> skills = new LinkedList<>();
    public boolean ascended = false;

    // Cooldown tracking: skill name -> turns left
    private Map<String, Integer> skillCooldowns = new HashMap<>();

    public Hero(String name, HeroType type) {
        this.name = name;
        this.type = type;
        this.level = 1;
        this.exp = 0;
        this.maxHp = 100 + type.baseStats[3] * 10;
        this.hp = maxHp;
        this.maxMana = 50 + type.baseStats[2] * 5; // Mana based on INT
        this.mana = maxMana;
        this.stats = Arrays.copyOf(type.baseStats, 5);
        this.skills = new LinkedList<>(type.getSkills());
        this.ascended = false;
        for (Skill s : skills) skillCooldowns.put(s.name, 0);
        skillCooldowns.put(type.ultimate.name, 0);
    }

    public void printStatus() {
        System.out.println();
        System.out.println("--- STATUS ---");
        System.out.printf("Name    : %-20s\n", name);
        System.out.printf("Class   : %-20s\n", type.name + (ascended ? " [Ascended]" : ""));
        System.out.printf("Level   : %-3d\n", level);
        System.out.printf("HP      : %-3d/%-3d\n", hp, maxHp);
        System.out.printf("Mana    : %-3d/%-3d\n", mana, maxMana);
        System.out.printf("Exp     : %-3d/%-3d\n", exp, (level*10 + 50));
        System.out.println("Stats   : STR " + stats[0] + "  AGI " + stats[1] + "  INT " + stats[2] + "  VIT " + stats[3] + "  LUK " + stats[4]);
        System.out.println("Skills  :");
        for (Skill s : skills)
            System.out.printf("  - %-15s : %s (Mana: %d, CD: %d, Ready: %s)\n",
                s.name, s.desc, s.manaCost, s.cooldown, (skillCooldowns.get(s.name) == 0 ? "Yes" : "No ("+skillCooldowns.get(s.name)+" left)"));
        System.out.printf("Ultimate: %-15s (Mana: %d, CD: %d, Ready: %s)\n", type.ultimate.name, type.ultimate.manaCost, type.ultimate.cooldown, (skillCooldowns.get(type.ultimate.name) == 0 ? "Yes" : "No ("+skillCooldowns.get(type.ultimate.name)+" left)"));
        System.out.println("Passive : " + type.passive);
        System.out.println();
    }

    public String getHeroTypeName() {
        return type.name;
    }

    public void gainExp(int amount) {
        exp += amount;
        while (exp >= level*10 + 50) {
            exp -= (level*10 + 50);
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        for (int i = 0; i < stats.length; i++) stats[i] += 1 + (int)(Math.random()*2);
        maxHp += 10 + stats[3];
        hp = maxHp;
        maxMana += 5 + stats[2];
        mana = maxMana;
        System.out.println("[System] Leveled Up! Now level " + level + ".");
        DeathDialogue.onLevelUp(level);
        if (!ascended && level >= 50 && type.ascendedForm != null) {
            ascend();
        }
    }

    private void ascend() {
        System.out.print("[System] You are eligible to ascend to " + type.ascendedForm.name + "! Do you want to ascend? (y/n): ");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if (input.equalsIgnoreCase("y")) {
            this.type = type.ascendedForm;
            this.skills = new LinkedList<>(type.getSkills());
            this.ascended = true;
            // update cooldowns
            for (Skill s : skills) skillCooldowns.put(s.name, 0);
            skillCooldowns.put(type.ultimate.name, 0);
            System.out.println("[System] Ascended to " + type.name + "!");
            DeathDialogue.onAscend(type.name);
        }
    }

    public boolean attack(Entity opponent) {
        decrementCooldowns();
        passiveEffects();
        int dmg = stats[0] + (int)(Math.random()*5) + 1;
        opponent.hp -= dmg;
        System.out.println(this.name + " hit " + opponent.name + " for " + dmg + " damage! (" + opponent.hp + "/" + opponent.maxHp + " HP)");
        return opponent.hp <= 0;
    }

    public boolean useSkillMenu(Entity opponent, Scanner scanner) {
        decrementCooldowns();
        passiveEffects();
        System.out.println("Choose a skill:");
        int i = 1;
        for (Skill s : skills) {
            System.out.printf("  %d. %-15s (Mana: %d, CD: %d, Ready: %s)\n", i, s.name, s.manaCost, s.cooldown, (skillCooldowns.get(s.name) == 0 ? "Yes" : "No ("+skillCooldowns.get(s.name)+" left)"));
            i++;
        }
        System.out.printf("  %d. %-15s (Mana: %d, CD: %d, Ready: %s)\n", i, type.ultimate.name, type.ultimate.manaCost, type.ultimate.cooldown, (skillCooldowns.get(type.ultimate.name) == 0 ? "Yes" : "No ("+skillCooldowns.get(type.ultimate.name)+" left)"));
        System.out.print("> ");
        int choice = 0;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            choice = 1;
        }
        Skill chosenSkill;
        boolean isUltimate = false;
        if (choice >= 1 && choice <= skills.size()) {
            chosenSkill = skills.get(choice-1);
        } else if (choice == skills.size()+1) {
            chosenSkill = type.ultimate;
            isUltimate = true;
        } else {
            System.out.println("Invalid choice.");
            return false;
        }
        // Cooldown and mana check
        if (skillCooldowns.get(chosenSkill.name) > 0) {
            System.out.println("That skill is still on cooldown (" + skillCooldowns.get(chosenSkill.name) + " turns left).");
            return false;
        }
        if (mana < chosenSkill.manaCost) {
            System.out.println("Not enough mana!");
            return false;
        }
        mana -= chosenSkill.manaCost;
        skillCooldowns.put(chosenSkill.name, chosenSkill.cooldown+1); // +1 so it's not usable next turn
        int dmg = chosenSkill.power + stats[chosenSkill.statIndex];
        if (dmg > 0) {
            opponent.hp -= dmg;
            System.out.println(name + " used " + chosenSkill.name + "! (" + dmg + " damage)");
        } else {
            System.out.println(name + " used " + chosenSkill.name + "!");
        }
        return opponent.hp <= 0;
    }

    // Called at start of each player turn
    private void decrementCooldowns() {
        for (String k : skillCooldowns.keySet())
            if (skillCooldowns.get(k) > 0)
                skillCooldowns.put(k, skillCooldowns.get(k) - 1);
    }

    // Called at start of each player turn
    private void passiveEffects() {
        // Mage: Mana Overflow (regen 5 MP/turn)
        if (type.name.equals("Mage")) {
            mana = Math.min(maxMana, mana + 5);
        }
        // Healer: Holy Light (healing is 10% stronger) -- handled in healing logic (not shown here)
        // Warrior: Berserker's Rage (ATK+10% below 50% HP)
        // (Passive effects can be implemented as you wish.)
    }

    public boolean fightDeath() {
        int myPower = level * Arrays.stream(stats).sum();
        int deathPower = 15000;
        if (myPower > deathPower) {
            System.out.println("You defeated Death!");
            return true;
        }
        System.out.println("You lost to Death.");
        return false;
    }
}