package fr.esu.mc.cannons.furnitures;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelVector;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import fr.esu.mc.cannons.math.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.ejml.simple.SimpleMatrix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleCannon extends Furniture {
    protected String pivotName = "#CANNON1#";

    public SimpleCannon(ObjectID id){
        super(id);
    }

    @Override
    public void onClick(Player player){
        if(getObjID() == null)
            return;
        if(getObjID().getSQLAction().equals(Type.SQLAction.REMOVE))
            return;
        if(player == null)
            return;
        if(player.isSneaking()) {
            Vector direction = player.getEyeLocation().getDirection();
            direction.setY(-direction.getY());
            alignEntityWithDirection(getPivot(), direction);
            alignCannonWithPivot(pivotName);
        }
        else if(canBuild(player)){
            fEntity stand = getPivot();
            if(stand == null)
                return;
            ItemStack is = player.getInventory().getItemInMainHand();
            if(is.getType().equals(Material.FIRE_CHARGE))
                if(!hasArrow()){
                    fEntity entity = getPivot();
                    entity.setItemInMainHand(is.clone());
                    update();
                    consumeItem(player);
                    getWorld().playSound(getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1.4f);
                    return;
                }
            if(hasArrow())
                spawnFireball(getArrow().getType(), player);
        }
    }

    private void alignEntityWithDirection(fEntity entity, Vector direction){
        Location entityLocation = entity.getLocation();
        if(entity instanceof fArmorStand)
            ((fArmorStand) entity).setHeadPose(new EulerAngle(Math.atan2(Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ()), direction.getY()) - Math.PI / 2, 0, 0));
        entityLocation.setDirection(direction);
        entity.teleport(entityLocation);
    }

    private void alignCannonWithPivot(String pivotName){
        Location pivotEntityLocation = null;
        Location pivotModelLocation = null;

        // find pivotEntityLocation & pivotModelLocation
        HashMap<ModelVector, fEntity> em = this.getProject().getModelschematic().getEntityMap();
        Iterator it = em.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry me = (Map.Entry) it.next();
            fEntity entity = getEntityByName(((fEntity) me.getValue()).getName());
            if(entity == null)
                continue;
            Location modelLocation = ((ModelVector) me.getKey()).toLocation(entity.getWorld());
            if(entity.getName().equalsIgnoreCase(pivotName)){
                pivotEntityLocation = entity.getLocation();
                pivotModelLocation = modelLocation;
                break;
            }
        }
        if(pivotEntityLocation == null) {
            System.err.println("Can't align cannon with player view, pivot " + pivotName + " is not found.");
            return;
        }

        // apply transformations
        it = em.entrySet().iterator();
        SimpleMatrix rotation = Util.getRotationMatrix(pivotModelLocation.getDirection().clone().normalize().rotateAroundY(Math.PI), pivotEntityLocation.getDirection().clone().normalize());
        while (it.hasNext()){
            Map.Entry me = (Map.Entry) it.next();
            fArmorStand entity = (fArmorStand) getEntityByName(((fEntity) me.getValue()).getName());
            if(entity == null || entity.getName().equalsIgnoreCase(pivotName))
                continue;
            Location modelLocation = ((ModelVector) me.getKey()).toLocation(null);
            Location entityLocation = entity.getLocation();

            // set direction
            Vector direction = Util.applyRotationMatrix(rotation, modelLocation.getDirection());
            entityLocation.setDirection(direction);
            entity.setHeadPose(new EulerAngle(Math.atan2(Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ()), direction.getY()) - Math.PI / 2, 0, 0));

            // set position
            Vector translation = pivotEntityLocation.toVector().add(
                    Util.applyRotationMatrix(rotation, modelLocation.toVector().subtract(pivotModelLocation.toVector()))
            );
            entityLocation.setX(translation.getX());
            entityLocation.setY(translation.getY());
            entityLocation.setZ(translation.getZ());

            entity.teleport(entityLocation);
        }
        update();
    }

    private Vector getCannonDirection(){
        return getPivot().getLocation().getDirection();
    }

    private void spawnFireball(Material mat, Player p){
        /*Location loc = getRelative(getCenter(), getBlockFace(), 0,18);
        loc.setYaw(getYaw());
        Vector v = getCannonDirection();
        if(v == null)
            return;
        getWorld().playSound(getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
        Location start = getRelative(getCenter(), getBlockFace(), 0,0);
        start.setYaw(getYaw());
        start = start.add(0, 0, 0);*/
        getWorld().playSound(getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 0.1f);
        Vector direction = getCannonDirection();
        Location start = this.getLocation().add(direction.clone().multiply(2));
        Fireball a = (Fireball) getWorld().spawnEntity(start, EntityType.FIREBALL);
        a.setVelocity(direction);
        a.setShooter(p);
        a.setDirection(direction.setY(-0.15));
        fEntity entity = getPivot();
        entity.setItemInMainHand(null);
        update();
    }

    private fEntity getEntityByName(String name){
        if(name.length() == 0)
            return null;
        for(fEntity stand : getfAsList()){
            if(stand.getName().equalsIgnoreCase(name))
                return stand;
        }
        return null;
    }

    private fEntity getPivot(){
        return getEntityByName(pivotName);
    }

    private ItemStack getArrow(){
        for(fEntity stand : getfAsList()){
            if(stand.getName().equalsIgnoreCase(pivotName)){
                if(!(stand.getItemInMainHand() == null || stand.getItemInMainHand().getType().equals(Material.AIR)))
                    return stand.getItemInMainHand();
            }
        }
        return null;
    }

    private boolean hasArrow(){
        for(fEntity stand : getfAsList()){
            if(stand.getName().equalsIgnoreCase(pivotName))
                return !(stand.getItemInMainHand() == null || stand.getItemInMainHand().getType().equals(Material.AIR));
        }
        return false;
    }

    @Override
    public void onBreak(Player player) {
        if(getObjID() == null)
            return;
        if(getObjID().getSQLAction().equals(Type.SQLAction.REMOVE))
            return;
        if(player == null)
            return;
        if(canBuild(player))
            this.destroy(player);
    }

    @Override
    public void spawn(Location location) {

    }
}