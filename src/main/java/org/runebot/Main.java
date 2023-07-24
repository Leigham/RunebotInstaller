package org.runebot;

import org.jetbrains.annotations.NotNull;
import org.runebot.enums.OS;


import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class Main {
    private static String RUNEBOT_URL = "https://github.com/KALE1111/rblaunch/releases";
    public static void main(String[] args) {
        System.out.println("Starting RuneBot Launcher");
        LoadingWindow loadingWindow = new LoadingWindow();
        loadingWindow.showLoadingWindow();

        // Simulate some background process
        int totalProgress = 100;


        loadingWindow.setStatusText("Finding Runelite Executable");
        String runelite = findRuneLiteExecutable();
        if (runelite.equals("")) {
            System.out.println("Could not find RuneLite executable");
            System.exit(1);
        }

        loadingWindow.setProgress(5);
        loadingWindow.setStatusText("Finding Runelite Plugin Directory");
        String pluginDirectory = getRunelitePluginDirectory();
        if (pluginDirectory.equals("")) {
            System.out.println("Could not find RuneLite executable");
            System.exit(1);
        }
        loadingWindow.setProgress(10);
        loadingWindow.setStatusText("Checking Runebot Version");

        String runebotVersion = getLatestVersionString(RUNEBOT_URL);
        System.out.println("Latest RuneBot Version: " + runebotVersion);

        // sideloaded plugins.
        String sideLoadedPlugins = pluginDirectory + "\\sideloaded-plugins\\";
        File sideLoadedPluginsDir = new File(sideLoadedPlugins);
        if (!sideLoadedPluginsDir.exists()) {
            sideLoadedPluginsDir.mkdir();
        }
        // check for any runebot plugin in the directory
        File[] files = sideLoadedPluginsDir.listFiles();
        boolean found = false;
        for (File file : files) {
            if (file.getName().contains("RuneBot")) {
                found = true;
                System.out.println("Found RuneBot Plugin: " + file.getName());
                String[] split = file.getName().split("-");
                String version = split[split.length - 1].split(".jar")[0];
                System.out.println("Found RuneBot Plugin Version: " + version);
                if (!version.equals(runebotVersion)) {
                    System.out.println("Found RuneBot Plugin Version: " + version + " does not match latest version: " + runebotVersion);
                    System.out.println("Deleting old version");
                    loadingWindow.setStatusText("Runebot out of date, deleting old version");
                    file.delete();
                    found = false;
                }
            }
        }
        if (!found) {
            System.out.println("Downloading latest RuneBot Plugin");
            loadingWindow.setStatusText("Downloading latest RuneBot Plugin");
            String latestVersionURL = getLatestVersionURL(runebotVersion);
            System.out.println("Downloading from: " + latestVersionURL);
            DownloadUtils.downloadFile(latestVersionURL, sideLoadedPlugins + "RuneBot-" + runebotVersion + ".jar", loadingWindow);
        }

        loadingWindow.dispose();

        //start the found executable with the same args as the launcher
        try {
            System.out.println("Starting RuneLite with args: " + Arrays.toString(args));
            Runtime.getRuntime().exec(new String[]{runelite, Arrays.toString(args)});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getLatestVersionString(String latestUrl) {
        int responseCode = 0;
        try {
            URL url = new URL(latestUrl+"/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Use HEAD request to minimize data transfer
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get the redirected URL without downloading the entire response
                String redirectedUrl = connection.getURL().toString();
                System.out.println("Redirected URL: " + redirectedUrl);
                String version = redirectedUrl.split("v")[1];
                return version;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return "";
    };
    private static String getLatestVersionURL(String latestversion) {
        return String.format("https://github.com/KALE1111/rblaunch/releases/download/v%s/RuneBot-%s.jar", latestversion,latestversion);
    }

    public static String findRuneLiteExecutable() {
        String os = getOS();
        OS osutil = OS.fromString(os);
        for (String commonPath : osutil.getCommonPaths()) {
            if (new File(commonPath).exists()) {
                if (new File(commonPath).canExecute()) {
                    System.out.println("Found RuneLite executable at: " + commonPath);
                    return commonPath;
                }
            }
        }
        return "";
    }
    public static String getRunelitePluginDirectory() {
        String os = getOS();
        OS osutil = OS.fromString(os);
        for (String installPath : osutil.getInstallPaths()) {
            if (new File(installPath).exists()) {
                if (new File(installPath).canExecute()) {
                    System.out.println("Found RuneLite Plugins at: " + installPath);
                    return installPath;
                }
            }
        }
        return "";
    }
    public static @NotNull String getOS() {
        System.out.printf("OS: %s%n", System.getProperty("os.name"));
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("windows")) return "windows";
        return "unsupported";
    }
}
