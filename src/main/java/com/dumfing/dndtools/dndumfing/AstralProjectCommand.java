package com.dumfing.dndtools.dndumfing;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class AstralProjectCommand implements CommandExecutor {
    AstralProject projectionTracker;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            switch (args[0].toLowerCase()) {
                case "on" -> {
                    projectionTracker.enableAstralProject(player);

                }
                case "off" -> {
                    projectionTracker.disableAstralProject(player);
                }
            }
        }
        return true;
    }
}
