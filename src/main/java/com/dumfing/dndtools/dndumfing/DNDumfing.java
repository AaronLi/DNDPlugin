package com.dumfing.dndtools.dndumfing;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DNDumfing extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("DNDumfing!");
        getCommand("astralproject").setExecutor(new AstralProjectCommand(new AstralProject(new HashMap<>())));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
