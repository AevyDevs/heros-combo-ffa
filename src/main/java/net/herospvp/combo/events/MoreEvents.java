package net.herospvp.combo.events;

import net.herospvp.base.events.custom.CombatKillEvent;
import net.herospvp.base.events.custom.SpawnEvent;
import net.herospvp.combo.Main;
import net.herospvp.heroscore.HerosCore;
import net.herospvp.heroscore.objects.HPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MoreEvents implements Listener {

    private final Main instance;
    private final HerosCore herosCore;
    private final Map<String, Long> ePearlCoolDowns;

    public MoreEvents(Main instance) {
        ePearlCoolDowns = new HashMap<>();
        this.instance = instance;
        this.herosCore = instance.getHerosCore();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        ePearlCoolDowns.put(event.getPlayer().getName(), 0L);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        ePearlCoolDowns.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        Event.Result useHandItem = event.useItemInHand();

        if (useHandItem.equals(Event.Result.DENY) || !event.getItem().getType().equals(Material.ENDER_PEARL)) return;
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) return;

        Player player = event.getPlayer();
        String playerName = player.getName();

        long mapGet = ePearlCoolDowns.get(playerName);

        if (mapGet <= System.currentTimeMillis()) {
            ePearlCoolDowns.replace(playerName, System.currentTimeMillis() + 16000);
        } else {
            event.setCancelled(true);
            DecimalFormat decimalFormat = new DecimalFormat("0.0");

            player.sendMessage(ChatColor.RED + "Puoi utilizzare la prossima perla tra " +
                    decimalFormat.format((float) (mapGet - System.currentTimeMillis()) / 1000) + "s");
        }
    }

    @EventHandler
    public void on(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            Player player = event.getPlayer();

            if (to.getY() >= 92 || player.getLocation().getY() >= 92) {
                ePearlCoolDowns.replace(player.getName(), 0L);
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                player.sendMessage(ChatColor.RED + "Non puoi teletrasportarti qui!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(SpawnEvent event) {
        Player player = event.getPlayer();

        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();
        playerInventory.setArmorContents(instance.getArmor());

        for (int i = 0; i < playerInventory.getContents().length; i++) {

            if (playerInventory.getContents()[i] == null) {
                continue;
            }

            for (ItemStack itemStack : instance.getArmor()) {
                if (itemStack.getType().equals(playerInventory.getContents()[i].getType())) {
                    playerInventory.setItem(i, null);
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            player.getInventory().setItem(i, instance.getHotBar()[i]);
        }

        Bukkit.getScheduler().runTaskLater(instance, () -> {
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
        }, 7L);

    }

    @EventHandler
    public void on(CombatKillEvent event) {
        Player killer = event.getKiller();

        for (ItemStack itemStack : killer.getInventory()) {
            if (itemStack == null) continue;

            for (ItemStack stack : instance.getArmor()) {
                if (stack.getType().equals(itemStack.getType())) {
                    killer.getInventory().remove(itemStack);
                }
            }
        }

        killer.getInventory().setArmorContents(instance.getArmor());
        killer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 4));

        HPlayer hKiller = herosCore.getPlayersHandler().getPlayer(killer.getUniqueId());
        hKiller.setCoins(hKiller.getCoins() + 5);
    }

}