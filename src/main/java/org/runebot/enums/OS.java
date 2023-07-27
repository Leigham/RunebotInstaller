package org.runebot.enums;

import java.io.File;

/**
 * An enumeration representing different Operating Systems (OS) and their specific paths.
 */
public enum OS {
    /**
     * Windows OS.
     * - Common paths of the runelite executable: {@code System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\RuneLite.exe"}
     * - Common paths of the .runelite folder: {@code System.getProperty("user.home") + "\\.runelite"}
     */
    WINDOWS("windows", new String[]{
            System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\RuneLite.exe"
    }, new String[]{
            System.getProperty("user.home") + "\\.runelite"
    }),

    /**
     * Linux OS.
     * - Common paths of the runelite executable: {@code "/path/to/common2"}
     * - Common paths of the .runelite folder: {@code "/path/to/install2"}
     */
    LINUX("linux", new String[]{"/path/to/common2"}, new String[]{"/path/to/install2"}),

    /**
     * MacOS (OSX).
     * - Common paths of the runelite executable: {@code "/path"}
     * - Common paths of the .runelite folder: {@code "/path/to/install3"}
     */
    OSX("osx", new String[]{"/path"}, new String[]{"/path/to/install3"});

    private final String value;
    private final String[] commonPaths;
    private final String[] installPaths;

    /**
     * Constructor for the OS enum.
     *
     * @param value        The name of the OS.
     * @param commonPaths  Array of common paths for the runelite executable.
     * @param installPaths Array of common paths for the .runelite folder.
     */
    private OS(String value, String[] commonPaths, String[] installPaths) {
        this.value = value;
        this.commonPaths = commonPaths;
        this.installPaths = installPaths;
    }

    /**
     * Get the name of the OS.
     *
     * @return The name of the OS.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get an array of common paths for the runelite executable.
     * Lazy initialization for the native method call.
     *
     * @return An array of common paths for the runelite executable.
     */
    public String[] getCommonPaths() {
        return commonPaths;
    }

    /**
     * Get an array of common paths for the .runelite folder.
     *
     * @return An array of common paths for the .runelite folder.
     */
    public String[] getInstallPaths() {
        System.out.println("getInstallPaths called");
        return installPaths;
    }

    /**
     * Convert a string representation of an OS to the corresponding OS enum value.
     *
     * @param key The string representation of the OS.
     * @return The corresponding OS enum value.
     * @throws IllegalArgumentException If the input string does not match any of the OS enum values.
     */
    public static OS fromString(String key) {
        System.out.println("fromString called");
        System.out.println("key: " + key);
        for (OS os : OS.values()) {
            System.out.println("os.getValue(): " + os.getValue());
            if (os.getValue().equals(key) ||
                    key.toLowerCase().contains(os.getValue().toLowerCase()) ||
                    os.name().equalsIgnoreCase(key) ||
                    os.name().toLowerCase().contains(key.toLowerCase()) ||
                    os.name().toLowerCase().startsWith(key.toLowerCase()) ||
                    os.name().toLowerCase().endsWith(key.toLowerCase())
            ) {
                return os;
            }
        }
        throw new IllegalArgumentException("Invalid OS value: " + key);
    }
}
