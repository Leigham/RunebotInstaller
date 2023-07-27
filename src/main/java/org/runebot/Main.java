package org.runebot;

import org.jetbrains.annotations.NotNull;
import org.runebot.enums.OS;
import org.runebot.utilities.FileUtilities;

import javax.swing.*;
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
        Boolean runelite = FileUtilities.checkRuneLiteExecutable();
        if (!runelite) {
            // error popup
            JDialog error = new JDialog();
            error.setTitle("Error");
            error.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            error.setSize(300, 100);
            error.setLocationRelativeTo(null);
            error.setResizable(false);
            error.setVisible(true);
            error.add(new JLabel("Could not find RuneLite executable"));
            System.out.println("Could not find RuneLite executable");
            System.exit(1);
        }

        loadingWindow.setProgress(5);
        loadingWindow.setStatusText("Finding Runelite Plugin Directory");

        boolean pluginDirectory = FileUtilities.checkRuneLitePluginDirectory();
        if (!pluginDirectory) {
            // error popup
            JDialog error = new JDialog();
            error.setTitle("Error");
            error.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            error.setSize(300, 100);
            error.setLocationRelativeTo(null);
            error.setResizable(false);
            error.setVisible(true);
            error.add(new JLabel("Could not find RuneLite plugin directory"));
            System.out.println("Could not find RuneLite plugin directory");
            System.exit(1);
        }

        loadingWindow.setProgress(10);
        loadingWindow.setStatusText("Checking Runebot Version");

        String RUNEBOT_URL = "https://github.com/KALE1111/rblaunch/releases";
        String runebotVersion = getLatestVersionString(RUNEBOT_URL);
        System.out.println("Latest RuneBot Version: " + runebotVersion);

        File[] files = FileUtilities.getSideLoadedPluginPath().listFiles();

        System.out.println("Checking for RuneBot Plugin");
        System.out.println("Found " + files.length + " files");

        boolean found = false;
        String foundVersion = null;

        for (File file : files) {
            if (file.getName().contains("RuneBot")) {
                found = true;
                System.out.println("Found RuneBot Plugin: " + file.getName());
                String[] split = file.getName().split("-");
                String version = split[split.length - 1].replace(".jar", "");
                System.out.println("Found RuneBot Plugin Version: " + version);

                if (!version.equals(runebotVersion)) {
                    System.out.println("Found RuneBot Plugin Version: " + version + " does not match latest version: " + runebotVersion);
                    System.out.println("Deleting old version");
                    loadingWindow.setStatusText("Runebot out of date, deleting old version");
                    if (file.delete()) {
                        System.out.println("Old version deleted successfully.");
                    } else {
                        System.out.println("Failed to delete old version.");
                    }
                    found = false;
                } else {
                    foundVersion = version;
                }
            }
        }

        if (!found) {
            System.out.println("Downloading latest RuneBot Plugin");
            loadingWindow.setStatusText("Downloading latest RuneBot Plugin");
            String latestVersionURL = getLatestVersionURL(runebotVersion);
            System.out.println("Downloading from: " + latestVersionURL);
            String downloadPath = FileUtilities.getSideLoadedPluginPath().getPath() + File.separator + "RuneBot-" + runebotVersion + ".jar";
            DownloadUtils.downloadFile(latestVersionURL, downloadPath, loadingWindow);
            foundVersion = runebotVersion; // Update the found version to the latest one.
        }

        if (foundVersion != null) {
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
                Runtime.getRuntime().exec(new String[]{FileUtilities.getExecutablePath(), Arrays.toString(args)});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: RuneBot Plugin not found or downloaded.");
            System.exit(1);
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

