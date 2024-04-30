package at.hannibal2.skyhanni.config.features.inventory;

import at.hannibal2.skyhanni.utils.LorenzColor;
import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.Accordion;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigEditorDropdown;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class StarDisplayConfig {
    @Expose
    @ConfigOption(name = "Custom Color",
        desc = "Customize the custom color of the stars.")
    @ConfigEditorDropdown
    public LorenzColor customStarColor = LorenzColor.RED;

    @Expose
    @ConfigOption(name = "Item Stars",
        desc = "Modifies how stars are displayed on items.")
    @Accordion
    public ToolTipStarDisplay toolTipStarDisplay = new ToolTipStarDisplay();

    public static class ToolTipStarDisplay {
        @Expose
        @ConfigOption(name = "Item Stars",
            desc = "Show the number of stars an item has in the item name.")
        @ConfigEditorDropdown
        public StarType starType = StarType.OFF;

        public enum StarType {
            OFF("Off"),
            ALLSTAR("All stars"),
            CURRENTSTAR("Current stars"),
            MASTERSTAR("Old Master Stars"),

            ;
            private final String str;

            StarType(String s) {
                this.str = s;
            }

            @Override
            public String toString() {
                return str;
            }
        }

        @Expose
        @ConfigOption(name = "Item Color Type",
            desc = "Change the color of the stars in the item tooltip.")
        @ConfigEditorDropdown
        public StarDisplayColorType starColorType = StarDisplayColorType.STAR;
    }

    @Expose
    @ConfigOption(name = "Stack Tip Stars",
        desc = "Modifies how stars are displayed in the stack tooltip.")
    @Accordion
    public StackTipStarDisplay stackTipStarDisplay = new StackTipStarDisplay();

    public static class StackTipStarDisplay {
        @Expose
        @ConfigOption(name = "Stack Tip Number",
            desc = "Show the number of stars an item has in the stack tooltip.")
        @ConfigEditorDropdown
        public StackTipNumber stackTipNumber = StackTipNumber.OFF;

        public enum StackTipNumber {
            OFF("Off"),
            ALLSTAR("All stars"),
            CURRENTSTAR("Current stars"),

            ;
            private final String str;

            StackTipNumber(String s) {
                this.str = s;
            }

            @Override
            public String toString() {
                return str;
            }
        }

        @Expose
        @ConfigOption(name = "Stack Tip Color Type",
            desc = "Change the color of the stars in the stack tooltip.")
        @ConfigEditorDropdown
        public StarDisplayColorType starColorType = StarDisplayColorType.STAR;

        @Expose
        @ConfigOption(name = "Only on Kuudra Armor",
            desc = "Only show the number of stars in the stack tooltip for Kuudra Armor.")
        @ConfigEditorBoolean
        public boolean stackTipNumberKuudraOnly = true;
    }

    public enum StarDisplayColorType {
        OFF("Off"),
        TIER("Tier Color"),
        STAR("Star Color"),
        CUSTOM("Custom"),

        ;
        private final String str;

        StarDisplayColorType(String s) {
            this.str = s;
        }

        @Override
        public String toString() {
            return str;
        }
    }
}
