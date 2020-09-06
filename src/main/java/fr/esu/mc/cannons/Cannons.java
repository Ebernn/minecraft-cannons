package fr.esu.mc.cannons;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Logger{
    public static String prefix = ChatColor.YELLOW + "[Cannons] ";
    public static String format(String message){
        return prefix + message;
    }
    private static void print(String message){
        System.out.println(format(message));
    }
    public static void debug(String message){
        print("[DEBUG] " + message);
    }
    public static void info(String message){
        print("[INFO] " + message);
    }
}

public final class Cannons extends JavaPlugin {
    public static JavaPlugin instance;
    public static World defaultWorld;

    private config c;
    private FileConfiguration file;
    public static double damage = 0;

    static LocationUtil util;
    public static List<Material> materialWhiteList = new ArrayList<Material>();
    public static HashMap<String, Vector> catapultRange = new HashMap<String, Vector>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Logger.info("Enabling Cannons plugin...");
        instance = this;
        FurnitureHook furniturePlugin = new FurnitureHook(getInstance());
        furniturePlugin.register();

        // getServer().getPluginManager().registerEvents(new CannonsListener(), this);
        defaultWorld = Bukkit.getServer().getWorld("world");
        Logger.info("Cannons plugin enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger.info("Cannons plugin is now shut down.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        return false;
    }

    public static LocationUtil getLocationUtil(){return util;}

    public static Plugin getInstance() {
        return instance;
    }
}