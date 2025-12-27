package ru.illit.money;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class U {
    private static final DecimalFormat DF;
    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.US);
        sym.setGroupingSeparator(' ');
        DF = new DecimalFormat("#,##0.##", sym);
        DF.setGroupingUsed(true);
    }
    private U() {}
    public static String c(String s) { return ChatColor.translateAlternateColorCodes('&', s); }
    public static String fmt(double v) { return DF.format(Math.max(0, v)); }
}
