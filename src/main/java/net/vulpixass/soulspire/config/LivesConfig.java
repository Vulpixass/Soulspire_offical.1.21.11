package net.vulpixass.soulspire.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.vulpixass.soulspire.network.PlayerSoulData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class LivesConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/soulspire/lives.json");

    private static final Type TYPE = new TypeToken<HashMap<String, PlayerSoulData>>(){}.getType();

    public static HashMap<UUID, PlayerSoulData> load() {
        try {
            if (!FILE.exists()) {
                FILE.getParentFile().mkdirs();
                FILE.createNewFile();
                save(new HashMap<>());
                return new HashMap<>();
            }

            FileReader reader = new FileReader(FILE);
            HashMap<String, PlayerSoulData> raw = GSON.fromJson(reader, TYPE);
            reader.close();

            HashMap<UUID, PlayerSoulData> result = new HashMap<>();
            if (raw != null) {
                for (var entry : raw.entrySet()) {
                    result.put(UUID.fromString(entry.getKey()), entry.getValue());
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void save(HashMap<UUID, PlayerSoulData> map) {
        try {
            HashMap<String, PlayerSoulData> raw = new HashMap<>();
            for (var entry : map.entrySet()) {
                raw.put(entry.getKey().toString(), entry.getValue());
            }

            FileWriter writer = new FileWriter(FILE);
            GSON.toJson(raw, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
