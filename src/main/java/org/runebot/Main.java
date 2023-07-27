package org.runebot;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import org.runebot.utilities.DownloadUtils;
import org.runebot.utilities.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
public class Main {
    private static boolean debug = false;
    private static String rlpath = "";
    private static LoadingWindow loadingWindow = new LoadingWindow(isDebug());

    public static void main(String[] args) throws IOException {
        debug = isDebug();
        if (debug) {
            println("Debug mode enabled");
        }

        loadingWindow.showLoadingWindow();
        loadingWindow.setVisible(true);
        loadingWindow.setOpacity(1.0f);
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();

        // we should have an option --rlpath="path/to/rl.exe"
        parser.accepts("rlpath").withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse(args);

        // is the rlpath passed
        if (options.has("rlpath")){
            rlpath = (String) options.valueOf("rlpath");
           println("Setting executable path to " + rlpath);
        };

        args = Arrays.stream(args).filter(arg -> !arg.contains("--rlpath")).toArray(String[]::new);

        int totalProgress = 100;


        loadingWindow.setStatusText("Finding Runelite Executable");
        Boolean runelite = FileUtilities.checkRuneLiteExecutable();
        if (!runelite) {
            println("Could not find RuneLite executable");
            System.exit(1);
        }

        loadingWindow.setProgress(5);
        loadingWindow.setStatusText("Finding Runelite Plugin Directory");

        boolean pluginDirectory = FileUtilities.checkRuneLitePluginDirectory();
        if (!pluginDirectory) {
            println("Could not find RuneLite plugin directory");
            System.exit(1);
        }

        loadingWindow.setProgress(10);
        loadingWindow.setStatusText("Checking Runebot Version");

        String RUNEBOT_URL = "https://github.com/KALE1111/rblaunch/releases";
        String runebotVersion = getLatestVersionString(RUNEBOT_URL);
        println("Latest RuneBot Version: " + runebotVersion);

        File[] files = FileUtilities.getSideLoadedPluginPath().listFiles();

       println("Checking for RuneBot Plugin");
        println("Found " + files.length + " files");

        boolean found = false;
        String foundVersion = null;

        for (File file : files) {
            if (file.getName().contains("RuneBot")) {
                found = true;
                println("Found RuneBot Plugin: " + file.getName());
                String[] split = file.getName().split("-");
                String version = split[split.length - 1].replace(".jar", "");
                println("Found RuneBot Plugin Version: " + version);

                if (!version.equals(runebotVersion)) {
                    println("Found RuneBot Plugin Version: " + version + " does not match latest version: " + runebotVersion);
                    println("Deleting old version");
                    loadingWindow.setStatusText("Runebot out of date, deleting old version");
                    if (file.delete()) {
                        println("Old version deleted successfully.");
                    } else {
                       println("Failed to delete old version.");
                    }
                    found = false;
                } else {
                    foundVersion = version;
                }
            }
        }

        if (!found) {
            println("Downloading latest RuneBot Plugin");
            loadingWindow.setStatusText("Downloading latest RuneBot Plugin");
            String latestVersionURL = getLatestVersionURL(runebotVersion);
            println("Downloading from: " + latestVersionURL);
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



            //start the found executable with the same args as the launcher
            try {
                println("Starting RuneLite with args: " + Arrays.toString(args));
                String executePath = (rlpath != null && !rlpath.isEmpty()) ? rlpath : FileUtilities.getExecutablePath();
                // check the path exists
                if (executePath == null || executePath.isEmpty() || !new File(executePath).canExecute()) {
                    throw new IOException("Could not find executable path");
                }
                String argsString = String.join(" ", args);
                // if debug pause for 30 seconds
                if (debug) {
                    println("Debug mode enabled, pausing for 30 seconds");
                    Thread.sleep(30000);
                }
                Runtime.getRuntime().exec(executePath + " " + argsString);
                loadingWindow.fadeOut();
                loadingWindow.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
        println("OS: " + System.getProperty("os.name").toLowerCase());
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
    public static void println(String string){
        System.out.println(debug);
        System.out.println(string);
        if (debug) {
            loadingWindow.debugPrintln(string);
        }
    }
}

