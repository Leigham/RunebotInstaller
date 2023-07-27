package org.runebot.utilities;

import java.io.File;

public class FileUtilities {
    // operating system
    private static final OS os = OS.fromString(System.getProperty("os.name").toLowerCase());
    public static String getPluginPath() {
        for (String path : os.getInstallPaths()) {
            File runelitePluginDirectory = new File(path);
            if (runelitePluginDirectory.exists()) {
                return path;
            }
        }
        return "";
    };
    public static boolean checkRuneLitePluginDirectory() {
        return getPluginPath() != "";
    };


    public static String getExecutablePath() {
        for (String path : os.getCommonPaths()) {
            File runeliteExecutable = new File(path);
            if (runeliteExecutable.exists() && runeliteExecutable.canExecute()) {
                return path;
            }
        }
        return "";
    };
    public static boolean checkRuneLiteExecutable() {
        return getExecutablePath() != "";
    };
    public static boolean ensureSideLoadedPlugins() {
        File runelitePluginDirectory = new File(getPluginPath());
        File SideLoadedPath = getSideLoadedPluginPath();
        if (!SideLoadedPath.exists()) {
            SideLoadedPath.mkdir();
        }
        return SideLoadedPath.exists();
    };

    public static File getSideLoadedPluginPath() {
        File runelitePluginDirectory = new File(getPluginPath());
        File SideLoadedPath = new File(runelitePluginDirectory.getAbsolutePath() + "\\sideloaded-plugins");
        return SideLoadedPath;
    };

    public static void setExecutablePath(String rlpath) {
        File runeliteExecutable = new File(rlpath);
        if (runeliteExecutable.exists() && runeliteExecutable.canExecute()) {
            System.out.println("Setting executable path to " + rlpath);
            System.setProperty("rlpath", rlpath);
        } else {
            System.out.println("Could not set executable path to " + rlpath);
        }
    }
}
