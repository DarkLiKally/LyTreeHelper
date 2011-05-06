package net.darklikally.minecraft.utils;

import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.Server;
import net.minecraft.server.Entity;
import net.minecraft.server.WorldServer;
import java.util.HashMap;
import java.lang.reflect.Constructor;

public enum MobType {
    CHICKEN("Chicken", Enemies.FRIENDLY),
    COW("Cow", Enemies.FRIENDLY),
    CREEPER("Creeper", Enemies.ENEMY),
    GHAST("Ghast", Enemies.ENEMY),
    GIANT("Giant", "GiantZombie", Enemies.ENEMY),
    PIG("Pig", Enemies.FRIENDLY),
    PIGZOMB("Pigzombie", Enemies.NEUTRAL),
    SHEEP("Sheep", Enemies.FRIENDLY, ""),
    SKELETON("Skeleton", Enemies.ENEMY),
    SLIME("Slime", Enemies.ENEMY),
    SPIDER("Spider", Enemies.ENEMY),
    SQUID("Squid", Enemies.FRIENDLY),
    ZOMBIE("Zombie", Enemies.ENEMY),
    WOLF("Wolf", Enemies.NEUTRAL);

    private MobType(String n, Enemies en, String s)
    {
        this.s = s;
        this.name = n;
        this.craftClass = n;
        this.entityClass = n;
        this.type = en;
    }

    private MobType(String n, Enemies en)
    {
        this.name = n;
        this.craftClass = n;
        this.entityClass = n;
        this.type = en;
    }

    private MobType(String n, String ec, Enemies en)
    {
        this.name = n;
        this.craftClass = n;
        this.entityClass = ec;
        this.type = en;
    }

    private MobType(String n, String ec, String cc, Enemies en)
    {
        this.name = n;
        this.entityClass = ec;
        this.craftClass = cc;
        this.type = en;
    }
    public String s = "s";
    public String name;
    public Enemies type;
    private String entityClass;
    private String craftClass;
    private static final HashMap<String, MobType> hashMap = new HashMap<String, MobType>();

    static
    {
        for (MobType mob : MobType.values())
        {
            hashMap.put(mob.name, mob);
        }
    }

    @SuppressWarnings("unchecked")
    public CraftEntity spawn(Player player, Server server) throws MobException
    {
        try {
            WorldServer world = ((org.bukkit.craftbukkit.CraftWorld)player.getWorld()).getHandle();
            Constructor<CraftEntity> craft = (Constructor<CraftEntity>)ClassLoader.getSystemClassLoader().loadClass("org.bukkit.craftbukkit.entity.Craft" + craftClass).getConstructors()[0];
            Constructor<Entity> entity = (Constructor<Entity>)ClassLoader.getSystemClassLoader().loadClass("net.minecraft.server.Entity" + entityClass).getConstructors()[0];
            return craft.newInstance((CraftServer)server, entity.newInstance(world));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new MobException();
        }
    }


    public enum Enemies
    {
        FRIENDLY("friendly"),
        NEUTRAL("neutral"),
        ENEMY("enemy");

        private Enemies(String t)
        {
            this.type = t;
        }
        protected String type;
    }


    public class MobException extends Exception
    {
        private static final long serialVersionUID = 1L;
    }

    public static MobType fromName(String n)
    {
        return hashMap.get(n.toLowerCase());
    }
}
