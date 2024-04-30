package at.hannibal2.skyhanni.features.inventory

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.config.features.inventory.StarDisplayConfig
import at.hannibal2.skyhanni.config.features.inventory.StarDisplayConfig.StackTipStarDisplay
import at.hannibal2.skyhanni.config.features.inventory.StarDisplayConfig.ToolTipStarDisplay
import at.hannibal2.skyhanni.data.jsonobjects.repo.ItemsJson
import at.hannibal2.skyhanni.events.RenderItemTipEvent
import at.hannibal2.skyhanni.events.RepositoryReloadEvent
import at.hannibal2.skyhanni.utils.ItemUtils.name
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.SkyBlockItemModifierUtils.getDungeonStarCount
import at.hannibal2.skyhanni.utils.SkyBlockItemModifierUtils.getUpgradeLevel
import net.minecraft.item.ItemStack
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.max

class ItemStars {
    private val config get() = SkyHanniMod.feature.inventory.starDisplay

    private val stackTipConfig get() = config.stackTipStarDisplay
    private val toolTipConfig get() = config.toolTipStarDisplay

    private var armorNames = listOf<String>()
    private var tiers = mapOf<String, Int>()
    private val armorParts = listOf("Helmet", "Chestplate", "Leggings", "Boots")

    @SubscribeEvent(priority = EventPriority.LOW)
    fun onTooltip(event: ItemTooltipEvent) {
        if (!toolTipIsEnabled()) return

        val stack = event.itemStack ?: return
        if (stack.stackSize != 1) return
        val name = stack.name ?: return

        var stars = -1
        var color = "§c"
        val useColor = toolTipConfig.starColorType != StarDisplayConfig.StarDisplayColorType.OFF

        if (usesMasterStars()) {
            stars = stack.getDungeonStarCount() ?: -1
        } else {
            stars = stack.getUpgradeLevel() ?: 0

            val isKuudraArmor = armorNames.any { name.contains(it) } && armorParts.any { name.contains(it) }

            if (isKuudraArmor) {
                if (useColor)
                    color = if (toolTipConfig.starColorType == StarDisplayConfig.StarDisplayColorType.TIER) {
                        tierToColor(tiers.entries.find { name.contains(it.key) }?.key ?: "Basic")
                    } else starCountToColor(stack, stars)

                if (toolTipConfig.starType == ToolTipStarDisplay.StarType.ALLSTAR)
                    stars += tiers.entries.find { name.contains(it.key) }?.value ?: 0
            } else if (useColor) {
                color = starCountToColor(stack, stars)
            }
        }

        if (toolTipConfig.starColorType == StarDisplayConfig.StarDisplayColorType.CUSTOM)
            color = config.customStarColor.getChatColor()

        if (stars > 0) {
            val displayName = name.replace("§.[✪➊➋➌➍➎]".toRegex(), "").trim()

            if (usesMasterStars()) {
                val masterStars = max(0, stars - 5)
                val normalStars = 5 - masterStars
                event.toolTip[0] = "$displayName ${"§c✪".repeat(masterStars) + "§6✪".repeat(normalStars)}"
            } else {
                event.toolTip[0] = "$displayName $color$stars✪"
            }
        }
    }

    @SubscribeEvent
    fun onRepoReload(event: RepositoryReloadEvent) {
        val data = event.getConstant<ItemsJson>("Items")
        armorNames = data.crimson_armors
        tiers = data.crimson_tiers
    }

    @SubscribeEvent()
    fun onRenderItemTip(event: RenderItemTipEvent) {
        if (!stackTipIsEnabled()) return

        val stack = event.stack
        if (stack.stackSize != 1) return
        val name = stack.name ?: return
        val isKuudraArmor = armorNames.any { name.contains(it) } && armorParts.any { name.contains(it) }
        if (stackTipConfig.stackTipNumberKuudraOnly && !isKuudraArmor) return

        var stars = -1
        var color = "§f"
        val useColor = stackTipConfig.starColorType != StarDisplayConfig.StarDisplayColorType.OFF

        if (isKuudraArmor) {
            stars = stack.getUpgradeLevel() ?: 0

            if (useColor)
                color = if (stackTipConfig.starColorType == StarDisplayConfig.StarDisplayColorType.TIER) {
                    tierToColor(tiers.entries.find { name.contains(it.key) }?.key ?: "Basic")
                } else starCountToColor(stack, stars)

            if (stackTipConfig.stackTipNumber == StackTipStarDisplay.StackTipNumber.ALLSTAR)
                stars += tiers.entries.find { name.contains(it.key) }?.value ?: 0
        } else if (!stackTipConfig.stackTipNumberKuudraOnly) {
            stars = stack.getUpgradeLevel() ?: -1

            if (useColor)
                color = starCountToColor(stack, stars)
        }

        if (stackTipConfig.starColorType == StarDisplayConfig.StarDisplayColorType.CUSTOM)
            color = config.customStarColor.getChatColor()

        if (stars >= 0) {
            event.stackTip = "$color$stars"
        }
    }

    // TODO add to repo
    private fun tierToColor(tier: String) = when (tier) {
        "Basic" -> "§a"
        "Hot" -> "§2"
        "Burning" -> "§e"
        "Fiery" -> "§6"
        "Infernal" -> "§c"
        else -> "§f"
    }

    private fun starCountToColor(stack: ItemStack, stars: Int) = when {
        stars <= 5 -> "§6"                              // gold
        stack.getDungeonStarCount() != null -> "§c"     // red
        stars <= 10 -> "§d"                             // pink
        else -> "§b"                                    // aqua
    }

    private fun toolTipIsEnabled() =
        LorenzUtils.inSkyBlock && toolTipConfig.starType != ToolTipStarDisplay.StarType.OFF
    private fun stackTipIsEnabled() =
        LorenzUtils.inSkyBlock && stackTipConfig.stackTipNumber != StackTipStarDisplay.StackTipNumber.OFF

    private fun usesMasterStars() =
        toolTipConfig.starType == ToolTipStarDisplay.StarType.MASTERSTAR
}
