package net.herospvp.combo;

import net.herospvp.base_ffa.configuration.CombatTagConfiguration;
import net.herospvp.base_ffa.configuration.DatabaseConfiguration;
import net.herospvp.base_ffa.configuration.WorldConfiguration;
import net.herospvp.base_ffa.configuration.kit.Enchants;
import net.herospvp.base_ffa.configuration.kit.Kit;
import net.herospvp.base_ffa.tasks.ActionBarAnnouncer;
import net.herospvp.base_ffa.tasks.AlwaysDay;
import net.herospvp.base_ffa.tasks.SaveData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        new DatabaseConfiguration(getStringFromConfig("ip"), getStringFromConfig("port"),
                getStringFromConfig("database"), getStringFromConfig("table"), getStringFromConfig("user"),
                getStringFromConfig("password"));

        new SaveData(18000);

        CombatTagConfiguration.setDuration(10000);

        String[] messages = new String[6];
        messages[0] = "&bIP del nostro TeamSpeak: &fts.herospvp.net";
        messages[1] = "&4&lRICORDA! &cIl mouse-abuse non e' consentito!";
        messages[2] = "&bLink del nostro Discord: &fdiscord.gg/6kNTCUjKBg";
        messages[3] = "&6Gestisci le tue notifiche con /notifiche";
        messages[4] = "qualcosa";
        messages[5] = "qualcosa";

        new ActionBarAnnouncer(messages, 200, 1200);
        new AlwaysDay("combo_ffa", 9000, 1200);

        new WorldConfiguration("combo_ffa", null);

        Enchants protection = new Enchants(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        Enchants sharpness = new Enchants(Enchantment.DAMAGE_ALL, 4);
        Enchants durability = new Enchants(Enchantment.DURABILITY, 3);

        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.DIAMOND_HELMET);
        armor[1] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        armor[2] = new ItemStack(Material.DIAMOND_LEGGINGS);
        armor[3] = new ItemStack(Material.DIAMOND_BOOTS);

        ItemStack[] hotBar = new ItemStack[3];
        hotBar[0] = new ItemStack(Material.DIAMOND_SWORD);
        hotBar[1] = new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1);
        hotBar[2] = new ItemStack(Material.ENDER_PEARL, 4);

        ItemStack[] killReward = new ItemStack[1];
        killReward[0] = new ItemStack(Material.ENDER_PEARL, 4);

        PotionEffect[] potionEffects = new PotionEffect[2];
        potionEffects[0] = new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 0);
        potionEffects[0] = new PotionEffect(PotionEffectType.SPEED, 10000000, 1);

        Kit kit = new Kit(armor, hotBar, killReward, potionEffects);

        kit.enchantAllArmor(protection);
        kit.enchantHotBar(0, sharpness);
        kit.enchantHotBar(0, durability);
    }

    @Override
    public void onDisable() {

    }

    private String getStringFromConfig(String string) {
        return getConfig().getString("mysql."  + string);
    }

}
