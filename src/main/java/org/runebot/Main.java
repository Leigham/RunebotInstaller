package org.runebot;

import org.jetbrains.annotations.NotNull;
import org.runebot.enums.OS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Main {
    private static boolean debug = false;
    public static void main(String[] args) throws IOException {
        // check if debug mode is enabled
        final boolean debug = isDebug();
        if (debug) {
            System.out.println("Debug mode enabled");
        }
        LoadingWindow loadingWindow = new LoadingWindow();
        loadingWindow.fadeIn();
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

        String RUNEBOT_URL = "https://github.com/KALE1111/rblaunch/releases";
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

        // finish the loading.
        int progress = loadingWindow.currentprogress;
        // current progress to 100 in 1 sconds very smooth
        for (int i = progress; i <= totalProgress; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadingWindow.setProgress(i);
            loadingWindow.setStatusText("Starting RuneLite");
        }

        loadingWindow.fadeOut();
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
    }
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


    public static boolean isDebug() {
        try {
            // Get the manifest from the current JAR file
            InputStream input = Main.class.getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(input);

            // Access the main attributes
            Attributes mainAttributes = manifest.getMainAttributes();
            String debug = mainAttributes.getValue("debug");
            if (debug == null) {
                return true;
            }
            return Boolean.parseBoolean(debug);
        } catch (Exception e) {
            return true;
        }

    };
}

