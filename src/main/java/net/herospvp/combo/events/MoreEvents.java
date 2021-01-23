package net.herospvp.combo.events;

import net.herospvp.combo.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
public class MoreEvents implements Listener {

    private final Main instance;
    private final Map<String, Long> ePearlCoolDowns;

    public MoreEvents(Main instance) {
        this.instance = instance;
        ePearlCoolDowns = new HashMap<>();
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

}