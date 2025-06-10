package com.mycompany.game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Game {
    private static Scanner scanner = new Scanner(System.in);
    private static Stack<String> deathStack = new Stack<>();
    private static final int MAX_DEATHS = 10;
    private static int reincarnations = 0;
    private static Hero player;
    private static String playerName;
    private static HashMap<String, Dungeon> dungeons = new HashMap<>();
    private static Arena arena;
    private static LinkedList<String> dungeonHistory = new LinkedList<>();
    private static boolean isGameOver = false;

    public static void main(String[] args) {
        cinematicIntro();

        HeroType chosenType = HeroType.chooseHeroType(scanner);
        player = new Hero(playerName, chosenType);

        printTitle("SYSTEM WINDOW");
        System.out.println("  Welcome to ????? ????");
        printSectionEnd();
        System.out.println("[System] You have been granted another chance at life, as a new adventurer.");
        System.out.println("[System] Your class: " + chosenType.name);
        DeathDialogue.onGameStart();
        pause();

        initializeDungeons();
        arena = new Arena();

        firstDungeon();

        mainMenu();

        if (isGameOver) endGame();
    }

    private static void cinematicIntro() {
        printTitle("DEATH'S GAME");
        printSectionEnd();
        pause();

        System.out.print("Enter your name: ");
        playerName = scanner.nextLine();
        System.out.println();

        System.out.println("You sit in darkness, cold and numb. The memories replay: poverty, ridicule, loneliness. The world always felt unfair.");
        System.out.println();
        System.out.println("A moment of despair. The pain overwhelms you.");
        pause();
        System.out.println();
        System.out.println("You make a choice—to end it all.");
        printDivider();
        System.out.println("But there is no light at the end. Only darkness... and then, a voice.");
        System.out.println();
        System.out.println("??? : \"Is this really the end you wanted?\"");
        pause();
        System.out.println();
        System.out.println("A figure, featureless but powerful, stands before you—Death itself.");
        System.out.println();
        DeathDialogue.onGameStart();
        System.out.println("A SYSTEM WINDOW appears: < THE GAME BEGINS >");
        pause();
    }

    private static void firstDungeon() {
        System.out.println("[System] You awaken in a new world. Your hands don’t feel like your own, but you are alive.");
        System.out.println();
        System.out.println("[A floating system window shimmers in front of you.]");
        Dungeon testDungeon = new Dungeon("Test Dungeon", "F", "Trial of Beginnings", "Slime", 30, 1);
        System.out.println("[System] Welcome to '" + testDungeon.getWelcomeMessage() + "'!");
        DeathDialogue.onTestDungeonStart();
        pause();
        testDungeon.runDungeon(player, scanner, deathStack, dungeonHistory);
        DeathDialogue.onDungeonClear(testDungeon.name, testDungeon.rank);
    }

    private static void mainMenu() {
        while (!isGameOver) {
            printTitle("MAIN MENU");
            printMenuOption(1, "Enter Dungeon");
            printMenuOption(2, "Enter Arena (" + arena.getArenaName() + ")");
            printMenuOption(3, "Check Status");
            printMenuOption(4, "Surrender (Quit)");
            printSectionEnd();
            printInputPrompt();
            String choice = scanner.nextLine();

            System.out.println();

            switch (choice) {
                case "1":
                    chooseDungeon();
                    break;
                case "2":
                    enterArena();
                    break;
                case "3":
                    player.printStatus();
                    break;
                case "4":
                    DeathDialogue.onQuit();
                    System.out.print("[System] Are you sure you want to surrender? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        isGameOver = true;
                    }
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void chooseDungeon() {
        printTitle("AVAILABLE DUNGEONS");
        int idx = 1;
        for (String key : dungeons.keySet()) {
            printMenuOption(idx, dungeons.get(key).name + " (" + dungeons.get(key).rank + ")");
            idx++;
        }
        printSectionEnd();
        printInputPrompt();
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            System.out.println();
            if (choice > 0 && choice <= dungeons.size()) {
                String selected = (String)dungeons.keySet().toArray()[choice-1];
                Dungeon dungeon = dungeons.get(selected);
                DeathDialogue.onDungeonEnter(dungeon.name, dungeon.rank);
                dungeon.runDungeon(player, scanner, deathStack, dungeonHistory);
                DeathDialogue.onDungeonClear(dungeon.name, dungeon.rank);
                checkDeath();
            } else {
                System.out.println("Invalid dungeon!");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Please enter a number.");
        }
    }

    private static void enterArena() {
        String arenaLevel = arena.getArenaName();
        boolean survived = arena.startArena(player, scanner, deathStack);
        if (survived) {
            DeathDialogue.onArenaWin(arenaLevel);
        } else {
            DeathDialogue.onArenaLose();
            checkDeath();
        }
    }

    private static void checkDeath() {
        if (player.isDead()) {
            reincarnations++;
            deathStack.push(player.getHeroTypeName());
            DeathDialogue.onDeath(deathStack.size(), MAX_DEATHS);
            deathCutscene();
            if (deathStack.size() >= MAX_DEATHS) {
                finalDeathCutscene();
                isGameOver = true;
            } else {
                HeroType newType = HeroType.chooseHeroType(scanner);
                player = new Hero(playerName, newType);
                System.out.println("[System] Another new body. Another chance. Will you finally prove yourself?");
                pause();
            }
        }
    }

    private static void deathCutscene() {
        printDivider();
        System.out.println("[You fall. The pain fades. The world turns grey.]");
        System.out.println();
        System.out.println("[Death stands before you, arms crossed, bemused.]");
        printDivider();
        pause();
    }

    private static void finalDeathCutscene() {
        printDivider();
        System.out.println("[Your soul stumbles in the void. The system window shuts down, its light gone.]");
        System.out.println();
        System.out.println("DEATH: \"Every story ends. Yours just ends here. Regret, anger, hope—it’s all meaningless now.\"");
        System.out.println();
        System.out.println("[Your Progress]");
        player.printStatus();
        System.out.println("Dungeon History: " + dungeonHistory);
        System.out.println("Lives used: " + deathStack.size());
        System.out.println();
        System.out.println("[System] GAME OVER. You cease to exist.");
        printDivider();
        pause();
    }

    private static void endGame() {
        printDivider();
        System.out.println("[Ending]");
        if (player.level >= 100) {
            System.out.println();
            System.out.println("You stand before Death, who now appears almost human, almost sad.");
            System.out.println();
            System.out.println("DEATH: \"You suffered. You changed. Maybe you even understand life’s value now.\"\n"
                + "DEATH: \"Do you want to fight for complete freedom, or accept another chance at life?\"");
            System.out.println();
            System.out.println("1. Challenge Death");
            System.out.println("2. Accept Rebirth");
            printInputPrompt();
            String finalChoice = scanner.nextLine();
            System.out.println();
            if (finalChoice.equals("1")) {
                boolean win = player.fightDeath();
                if (win) {
                    DeathDialogue.generic("You really are something special. Your story continues—this time, on your own terms.");
                    System.out.println();
                    System.out.println("[You awaken in a hospital, gasping for breath. Sunlight, voices, and the world welcome you back.]");
                    System.out.println();
                    System.out.println("[Your phone buzzes: 'Game Cleared: Death’s Game'.]");
                } else {
                    DeathDialogue.generic("You lost. The void claims you.");
                    System.out.println();
                    System.out.println("[System] GAME OVER.");
                }
            } else {
                System.out.println("[You nod, humbled, ready to try again as yourself—no powers, no system, only life.]");
                DeathDialogue.generic("Live well, this time. You won’t get another game.");
                System.out.println();
                System.out.println("[You awaken in a hospital, surrounded by those who care. Was it a dream? A warning?]");
                System.out.println();
                System.out.println("[Your phone buzzes: 'Game Cleared: Death’s Game'.]");
            }
            printDivider();
            System.out.println("[System] The true name is revealed: 'Death's Game'");
        } else {
            System.out.println("[System] Thanks for playing. Try reaching Level 100 for the true ending!");
        }
        printDivider();
    }

    private static void initializeDungeons() {
        dungeons.put("F", new Dungeon("Slime Fields", "F", "Beginner's Trial", "Slime", 30, 1));
        dungeons.put("E", new Dungeon("Goblin Den", "E", "Forest of Mischief", "Goblin", 35, 5));
        dungeons.put("D", new Dungeon("Spider Nest", "D", "Webbed Abyss", "Spider", 38, 10));
        dungeons.put("C", new Dungeon("Wolf Cavern", "C", "Moonlit Grotto", "Wolf", 42, 15));
        dungeons.put("B", new Dungeon("Orc Fortress", "B", "Crimson Hold", "Orc", 46, 20));
        dungeons.put("A", new Dungeon("Undead Crypt", "A", "Halls of the Damned", "Undead", 50, 25));
        dungeons.put("S", new Dungeon("Dragon Lair", "S", "Eternal Flame", "Dragon", 60, 30));
        dungeons.put("SS", new Dungeon("Demon Abyss", "SS", "Abyssal Gate", "Demon", 70, 40));
        dungeons.put("SSS", new Dungeon("Celestial Arena", "SSS", "Heaven's Door", "Angel", 80, 50));
    }

    // --- Formatting helpers (simple, visually appealing) ---
    public static void printTitle(String s) {
        System.out.println();
        System.out.println("--- " + s + " ---");
    }

    public static void printSectionEnd() {
        System.out.println();
    }

    public static void printMenuOption(int num, String text) {
        System.out.printf("  %d. %-30s\n", num, text);
    }

    public static void printInputPrompt() {
        System.out.print("> ");
    }

    public static void printDivider() {
        System.out.println("-------------------------------");
    }

    private static void pause() {
        System.out.print("[Press Enter to continue]");
        scanner.nextLine();
        System.out.println();
    }
}