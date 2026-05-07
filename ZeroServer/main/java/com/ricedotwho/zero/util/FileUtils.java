package com.ricedotwho.zero.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ricedotwho.zero.Zero;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtils {
    public static Gson gson = new Gson();
    public static Gson pgson = new GsonBuilder().setPrettyPrinting().create();

    public static void writeJson(Object obj, File file) {
        writeJson(obj, file, true);
    }

    public static void writeJson(Object obj, File file, boolean pretty) {
        try {
            Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8);

            if (pretty) {
                pgson.toJson(obj, writer);
            } else {
                gson.toJson(obj, writer);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkDir(File file, Object template) {
        if(file.exists()) return true;
        File parentDir = file.getParentFile();
        if(parentDir != null && !parentDir.exists()) {
            if(!parentDir.mkdirs()) {
                Zero.getLogger().error("Failed to create file: {}", parentDir.getName());
                return false;
            }
            Zero.getLogger().info("Created file:: {}", parentDir.getName());
        }
        try {
            if (file.createNewFile()) {
                Zero.getLogger().info("Created file:: {}", file.getName());
                FileUtils.writeJson(template, file);
                return false; // return false so it doesn't read the empty file
            } else {
                Zero.getLogger().info("File already exists! {}", file.getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}