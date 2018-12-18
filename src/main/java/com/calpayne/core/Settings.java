package com.calpayne.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cal Payne
 */
public class Settings {

    private String handle = "handle";
    private String serverIP = "0.0.0.0";
    private int serverPort = 9090;
    public static int GLOBAL_TIMEOUT_TIME = 120;

    public String getHandle() {
        return handle;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void config() {
        File config = new File("config.json");
        if (!config.exists()) {
            createConfig(config);
        } else {
            readConfig(config);
        }
    }

    private void readConfig(File config) {
        Settings settings = null;
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(config));
            String json = reader.readLine();
            settings = gson.fromJson(json, Settings.class);
        } catch (JsonSyntaxException | IOException ex) {
        }

        if (settings != null) {
            this.handle = settings.handle;
            this.serverIP = settings.serverIP;
            this.serverPort = settings.serverPort;
        } else {
            // Recreate config (assume it was broke)
            createConfig(config);
        }
    }

    private void createConfig(File config) {
        Gson gson = new Gson();

        try (PrintWriter writer = new PrintWriter(config)) {
            String json = gson.toJson(this);
            writer.write(json);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
