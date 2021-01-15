package net.herospvp.combo;

import net.herospvp.base.Base;
import net.herospvp.base.commands.Spawn;
import net.herospvp.base.events.CombatEvents;
import net.herospvp.base.events.PlayerEvents;
import net.herospvp.base.utils.lambdas.SpawnLambda;
import net.herospvp.combo.events.MoreEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin {

    private Base base;
    private Spawn spawn;
    private PlayerEvents playerEvents;
    private CombatEvents combatEvents;

    @Override
    public void onEnable() {

        base = Base.getInstance();

        new MoreEvents(this);

        ItemStack[] armor = new ItemStack[4];
        armor[3] = new ItemStack(Material.DIAMOND_HELMET);
        armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        armor[0] = new ItemStack(Material.DIAMOND_BOOTS);

        ItemStack[] hotBar = new ItemStack[3];
        hotBar[0] = new ItemStack(Material.DIAMOND_SWORD);
        hotBar[1] = new ItemStack(Material.ENDER_PEARL, 4);
        hotBar[2] = new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1);

        for (ItemStack itemStack : armor) {
            itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        }

        hotBar[0].addEnchantment(Enchantment.DURABILITY, 3);
        hotBar[0].addEnchantment(Enchantment.DAMAGE_ALL, 4);

        base.getWorldConfiguration().setPvpDisabledOver(94);

        spawn = base.getSpawn();
        playerEvents = base.getPlayerEvents();
        combatEvents = base.getCombatEvents();

        SpawnLambda spawnLambda = (player) -> {

            PlayerInventory playerInventory = player.getInventory();
            playerInventory.clear();
            playerInventory.setArmorContents(armor);

            for (int i = 0; i < playerInventory.getContents().length; i++) {

                if (playerInventory.getContents()[i] == null) {
                    continue;
                }

                for (ItemStack itemStack : armor) {
                    if (itemStack.getType().equals(playerInventory.getContents()[i].getType())) {
                        playerInventory.setItem(i, null);
                    }
                }
            }

            for (int i = 0; i < 3; i++) {
                player.getInventory().setItem(i, hotBar[i]);
            }

            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (player.getActivePotionEffects().size() == 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 172800000, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 172800000, 1));
                } else {
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        if (effect.getType().equals(PotionEffectType.NIGHT_VISION) ||
                                effect.getType().equals(PotionEffectType.SPEED)) {
                            continue;
                        }
                        player.removePotionEffect(effect.getType());
                    }
                }
            }, 5L);
        };

        spawn.setSpawnLambda(spawnLambda);
        playerEvents.setSpawnLambda(spawnLambda);
        combatEvents.setCombatEventsLambda((player, killer) -> {
            killer.getInventory().setArmorContents(armor);
            killer.getInventory().addItem(hotBar[1]);
        });


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {

                ItemStack[] itemStacks = player.getInventory().getContents();

                for (int i = 0; i < itemStacks.length; i++) {

                    ItemStack itemStack = itemStacks[i];
                    if (itemStack == null) {
                        continue;
                    }

                    if (itemStack.getType().equals(Material.GOLDEN_APPLE)) {

                        if (itemStack.getAmount() == 64 || player.getItemInHand().equals(itemStack)) {
                            continue;
                        }

                        itemStack.setAmount(64);
                        player.getInventory().setItem(i, itemStacks[i]);
                    } else if (itemStack.getType().equals(Material.DIAMOND_SWORD)) {

                        if (itemStack.getDurability() == 0 || player.getItemInHand().equals(itemStack)) {
                            continue;
                        }

                        itemStack.setDurability((short) 0);
                        player.getInventory().setItem(i, itemStack);
                    }
                }
            }
        }, 600L, 600L);
    }

    @Override
    public void onDisable() {

    }

}
