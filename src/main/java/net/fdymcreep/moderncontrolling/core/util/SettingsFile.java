package net.fdymcreep.moderncontrolling.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;

import java.io.*;

public abstract class SettingsFile<Bean> {
    private final Class<? extends Bean> beanClass;
    protected Bean content;
    protected Minecraft mc;
    protected File file;
    public final String name;

    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public SettingsFile(String name, Class<? extends Bean> beanClass) throws JsonSyntaxException, IOException, JsonIOException {
        this(name, beanClass, new File(
                new File(
                        Minecraft.getMinecraft().mcDataDir,
                        "keybindings"
                ),
                name
        ));
    }

    public SettingsFile(String name, Class<? extends Bean> beanClass, File file) throws JsonSyntaxException, IOException, JsonIOException {
        this.name = name;
        this.beanClass = beanClass;
        this.file = file;
        this.mc = Minecraft.getMinecraft();
        if (!this.file.exists()) {
            if (!this.file.getParentFile().exists()) {
                this.file.getParentFile().mkdirs();
            }
            this.file.createNewFile();
            try (FileWriter writer = new FileWriter(this.file)) {
                writer.write("{}");
            }
        }
        this.content = GSON.fromJson(new BufferedReader(new FileReader(this.file)), this.beanClass);
    }

    public Bean getContent() {
        return this.content;
    }

    public void rereadContent() throws FileNotFoundException, JsonSyntaxException, JsonIOException {
        this.content = GSON.fromJson(new BufferedReader(new FileReader(this.file)), this.beanClass);
    }

    public void saveContent() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))) {
            String json = GSON.toJson(this.content);
            writer.write(json);
        }
    }
}
