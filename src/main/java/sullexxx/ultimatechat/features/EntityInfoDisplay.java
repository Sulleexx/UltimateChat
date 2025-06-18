package sullexxx.ultimatechat.features;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import sullexxx.ultimatechat.configuration.LanguageConfig;

public class EntityInfoDisplay implements Listener {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public EntityInfoDisplay() {
    }

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();

        if (!player.hasPermission("ultimatechat.feature.entityinfo")) {
            return;
        }
        String entityName = getEntityName(target);
        if (getEntityName(target).equalsIgnoreCase("undefined")) {
            entityName = target.getName();
        }
        String healthInfo = "";

        if (target instanceof LivingEntity livingEntity) {
            double health = livingEntity.getHealth();
            double maxHealth = livingEntity.getMaxHealth();
            healthInfo = LanguageConfig.getString(
                            "EntityInfo.HealthFormat"
                    )
                    .replace("{health}", String.format("%.1f", health))
                    .replace("{max_health}", String.format("%.1f", maxHealth));
        }

        Component message = LanguageConfig.doubleFormat(
                LanguageConfig.getString(
                        "EntityInfo.Format"
                ).replace("{healthInfo}", healthInfo)
                        .replace("{entityName}", entityName)
        );

        player.sendActionBar(message);
    }

    private String getEntityName(Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).getName();
        }
        return entity.getCustomName() != null ?
                entity.getCustomName() :
                LanguageConfig.getString(
                        "EntityInfo." + entity.getType().name()
                );
    }
}