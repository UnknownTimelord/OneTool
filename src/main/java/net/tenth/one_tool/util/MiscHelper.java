package net.tenth.one_tool.util;

public class MiscHelper {
    public static int interpolateColor(int startColor, int endColor, float percent) {
        percent = Math.max(0, Math.min(1, percent));

        int aA = (startColor >> 24) & 0xFF;
        int rA = (startColor >> 16) & 0xFF;
        int gA = (startColor >> 8) & 0xFF;
        int bA = startColor & 0xFF;

        int aB = (endColor >> 24) & 0xFF;
        int rB = (endColor >> 16) & 0xFF;
        int gB = (endColor >> 8) & 0xFF;
        int bB = endColor & 0xFF;

        int a = Math.round(aA * (1 - percent) + aB * percent);
        int r = Math.round(rA * (1 - percent) + rB * percent);
        int g = Math.round(gA * (1 - percent) + gB * percent);
        int b = Math.round(bA * (1 - percent) + bB * percent);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
