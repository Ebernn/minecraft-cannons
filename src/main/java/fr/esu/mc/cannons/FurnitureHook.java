package fr.esu.mc.cannons;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurniturePlugin;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import fr.esu.mc.cannons.furnitures.SimpleCannon;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Field;
import java.util.Objects;

public class FurnitureHook extends FurniturePlugin {

    private final boolean editModels = FurnitureLib.getInstance().getConfig().getBoolean("config.editDiceFurnitureModels", false);

    public FurnitureHook(Plugin pluginInstance) {
        super(pluginInstance);
        // Bukkit.getPluginManager().registerEvents(new exampleEvent(), pluginInstance);
    }

    @Override
    public void registerProjects() {
        try {
            String modelFolder = FurnitureHook.isNewVersion() ? "Models113/" : "Models109/";
            String ending = FurnitureHook.isNewVersion() ? ".dModel" : ".yml";
            new Project("SimpleCannon", getPlugin(), getResource(modelFolder + "SimpleCannon" + ending), SimpleCannon.class).setSize(1, 1, 1, Type.CenterType.RIGHT);
            FurnitureLib.getInstance().getFurnitureManager().getProjects().stream().filter(pro -> pro.getPlugin().equals(getPlugin())).forEach(pro -> pro.setEditorProject(editModels));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void applyPluginFunctions() {
        FurnitureLib.getInstance().getFurnitureManager().getProjects().stream()
                .filter(pro -> pro.getPlugin().getName().equals(getPlugin().getDescription().getName()))
                .forEach(Project::applyFunction);
    }

    @Override
    public void onFurnitureLateSpawn(ObjectID objectID) {

    }

    private static Boolean newVersion = null;
    public static boolean isNewVersion() {
        if(Objects.isNull(newVersion)) {
            try {
                Class<?> descriptionClass = PluginDescriptionFile.class;
                Field field = descriptionClass.getDeclaredField("apiVersion");
                boolean bool = Objects.nonNull(field);
                newVersion = bool;
                return bool;
            }catch (Exception e) {
                newVersion = false;
                return false;
            }
        }else {
            return newVersion;
        }
    }
}