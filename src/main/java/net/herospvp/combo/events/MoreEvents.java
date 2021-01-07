package net.herospvp.combo.events;

import net.herospvp.base_ffa.Main;
import net.herospvp.base_ffa.configuration.CombatTagConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class MoreEvents implements Listener {

    @EventHandler
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (CombatTagConfiguration.getLastHitter() == null
                || CombatTagConfiguration.isOutOfCombat(player)) {
            return;
        }
        Player killer = CombatTagConfiguration.getLastHitter().get(player);

        killer.getInventory().addItem(Main.getKit().getKillRewards());

        if (killer.getItemInHand() == null) return;

        if (killer.getItemInHand().getDurability() != 0) {
            killer.getItemInHand().setDurability((short) 0);
        }

    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        event.getPlayer().getInventory().addItem(event.getItem());
    }

}