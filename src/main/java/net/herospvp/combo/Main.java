package net.herospvp.combo;

import lombok.Getter;
import net.herospvp.base.Base;
import net.herospvp.combo.events.MoreEvents;
import net.herospvp.heroscore.HerosCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@SuppressWarnings("FieldCanBeLocal")
public class Main extends JavaPlugin {

    private Base base;
    private HerosCore herosCore;
    private ItemStack[] armor, hotBar;

    @Override
    public void onEnable() {

        base = getPlugin(Base.class);
        herosCore = getPlugin(HerosCore.class);

        new MoreEvents(this);

        armor = new ItemStack[4];
        armor[3] = new ItemStack(Material.DIAMOND_HELMET);
        armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        armor[0] = new ItemStack(Material.DIAMOND_BOOTS);

        hotBar = new ItemStack[3];
        hotBar[0] = new ItemStack(Material.DIAMOND_SWORD);
        hotBar[1] = new ItemStack(Material.ENDER_PEARL, 4);
        hotBar[2] = new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1);

        for (ItemStack itemStack : armor) {
            itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        }

        hotBar[0].addEnchantment(Enchantment.DURABILITY, 3);
        hotBar[0].addEnchantment(Enchantment.DAMAGE_ALL, 4);

        base.getWorldConfiguration().setPvpDisabledOver(94);

        Bukkit.getScheduler().runTaskTimer(this, () -> {

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
