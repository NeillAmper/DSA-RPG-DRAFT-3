package com.mycompany.game;

import java.util.Random;

public class DeathDialogue {
    private static final Random rand = new Random();

    public static void onLevelUp(int level) {
        System.out.println();
        storyPause();
        if (level == 2) {
            say("You figured out how to get stronger. Maybe you’re not hopeless after all.");
        } else if (level % 10 == 0) {
            say("Another ten levels? Hah. I wonder if you’ll keep this up.");
        } else if (level == 50) {
            say("Halfway there. Most mortals never make it this far.");
        } else if (level == 100) {
            say("You’ve reached the peak. Let's see if you can escape me now...");
        } else {
            String[] msgs = {
                "You look a bit less pathetic now.",
                "Enjoy this little boost? Don’t get cocky.",
                "A step forward. Don’t trip.",
                "Every level, a new test. Don’t fail."
            };
            say(msgs[rand.nextInt(msgs.length)]);
        }
        System.out.println();
        storyPause();
    }

    public static void onDungeonClear(String dungeon, String rank) {
        System.out.println();
        storyPause();
        String[] msgs = {
            "You survived " + dungeon + " (" + rank + ")? I’m mildly impressed.",
            "Well, well. You lived through " + dungeon + ".",
            "One dungeon down... a lifetime to go.",
            "Maybe you’re not just lucky after all."
        };
        say(msgs[rand.nextInt(msgs.length)]);
        System.out.println();
        storyPause();
    }

    public static void onArenaWin(String arenaLevel) {
        System.out.println();
        storyPause();
        String[] msgs = {
            "You won at " + arenaLevel + "? Against all odds.",
            "Victory. Savor it. The real fight is still ahead.",
            "Not bad for someone who gave up on life.",
            "The crowd loves you. I’m still undecided."
        };
        say(msgs[rand.nextInt(msgs.length)]);
        System.out.println();
        storyPause();
    }

    public static void onArenaLose() {
        System.out.println();
        storyPause();
        String[] msgs = {
            "Defeated? I expected more.",
            "Losing stings, doesn’t it?",
            "Even in a crowd, you still fall alone.",
            "Maybe you need another strategy."
        };
        say(msgs[rand.nextInt(msgs.length)]);
        System.out.println();
        storyPause();
    }

    public static void onDeath(int deathCount, int maxDeaths) {
        System.out.println();
        storyPause();
        if (deathCount < maxDeaths / 2) {
            say("Back already? You barely tried.");
        } else if (deathCount < maxDeaths - 1) {
            say("Running out of lives, little soul.");
        } else {
            say("One more game. Fail and you're mine for good.");
        }
        System.out.println();
        storyPause();
    }

    public static void onAscend(String newClass) {
        System.out.println();
        storyPause();
        say("Ascended to " + newClass + "? Don’t let it go to your head.");
        System.out.println();
        storyPause();
    }

    public static void onTestDungeonStart() {
        System.out.println();
        storyPause();
        say("This is your first trial. Survive, or start over. Prove yourself.");
        System.out.println();
        storyPause();
    }

    public static void onGameStart() {
        System.out.println();
        storyPause();
        say("Let’s see if you can amuse me this time.");
        System.out.println();
        storyPause();
    }

    public static void onFirstAwakening() {
        System.out.println();
        storyPause();
        say("New world, new body. Don't waste it.");
        System.out.println();
        storyPause();
    }

    public static void onDungeonEnter(String dungeon, String rank) {
        System.out.println();
        storyPause();
        say("Stepping into " + dungeon + " (" + rank + ")? You might regret this.");
        System.out.println();
        storyPause();
    }

    public static void onBossDefeat(String boss) {
        System.out.println();
        storyPause();
        say("You toppled " + boss + "? Maybe you have potential.");
        System.out.println();
        storyPause();
    }

    public static void onQuit() {
        System.out.println();
        storyPause();
        say("Giving up? I didn’t think you’d last long.");
        System.out.println();
        storyPause();
    }

    public static void generic(String msg) {
        System.out.println();
        storyPause();
        say(msg);
        System.out.println();
        storyPause();
    }

    private static void say(String msg) {
        System.out.println("  DEATH: \"" + msg + "\"");
    }

    private static void storyPause() {
        try {
            Thread.sleep(550); // half-second pause for story pacing
        } catch (InterruptedException ignored) {}
    }
}