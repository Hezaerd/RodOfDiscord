package com.hezaerd.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

public final class ModLib {
    public static final String MOD_ID = "rodofdiscord";
    public static final String MOD_NAME = "Rod of Discord";

    private static String modVersion = null;

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static String getVersion() {
        if (modVersion == null) {
            try (InputStream in = ModLib.class.getClassLoader().getResourceAsStream("fabric.mod.json")) {
                if (in != null) {
                    JsonObject json = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
                    modVersion = json.get("version").getAsString();
                } else {
                    modVersion = "unknown";
                }
            } catch (Exception e) {
                modVersion = "unknown";
            }
        }

        return modVersion;
    }
}
