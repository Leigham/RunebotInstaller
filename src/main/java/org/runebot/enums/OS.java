package org.runebot.enums;

import java.io.File;
public enum OS {
    WINDOWS("windows", new String[]{System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\RuneLite.exe"}, new String[]{System.getProperty("user.home") + "\\.runelite"}),
    LINUX("linux", new String[]{"/path/to/common2"}, new String[]{"/path/to/install2"}),
    OSX("osx", new String[]{"/path"}, new String[]{"/path/to/install3"});

    private final String value;
    private final String[] commonPaths;
    private final String[] installPaths;

    private OS(String value, String[] commonPaths, String[] installPaths) {
        this.value = value;
        this.commonPaths = commonPaths;
        this.installPaths = installPaths;
    }


    public String getValue() {
        return value;
    }

    public String[] getCommonPaths() {
        // Lazy initialization for the native method call
        return commonPaths;
    }

    public String[] getInstallPaths() {
        System.out.println("getInstallPaths called");
        return installPaths;
    }

    public static OS fromString(String key) {
        for (OS os : OS.values()) {
            if (os.getValue().equals(key)) {
                return os;
            }
        }
        throw new IllegalArgumentException("Invalid OS value: " + key);
    }
}
