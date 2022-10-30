package io.github.bruce0203.chatswearfilter

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

@Suppress("unused")
class Plugin : JavaPlugin(), Listener {

    val fileName = "swears.json"
    private val swearFilter by lazy { Swear(File(dataFolder, fileName)) }

    private fun loadDefaultConfig() {
        config.options().copyDefaults()
        saveDefaultConfig()
    }

    override fun onEnable() {
        loadDefaultConfig()
        super.onEnable()
        saveResource(fileName, false)
        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @Suppress("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncPlayerChatEvent) {
        val input = event.message
        if (swearFilter.findSwear(input)) {
            event.isCancelled = true
            Bukkit.getScheduler().runTask(this) { _ ->
                val format = config.getString("format")
                if (format === null)
                    throw AssertionError("config.yml 에 format 없습니다")
                event.player.kickPlayer(format.replace("{chat}", input))
            }
        }
    }

}