package me.ichun.mods.ichunutil.client.gui.mouse;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;

public class MouseHelper
{
    public static double getMouseDistanceFromCenter(MainWindow window)
    {
        double centerX = window.getScaledWidth() / 2D;
        double centerY = window.getScaledHeight() / 2D;

        Minecraft mc = Minecraft.getInstance();

        double posX = mc.mouseHelper.mouseX * window.getScaledWidth() / window.getWidth() - centerX;
        double posY = mc.mouseHelper.mouseY * window.getScaledHeight() / window.getHeight() - centerY;

        return Math.sqrt(posX * posX + posY * posY);
    }

    public static float getMouseAngleFromCenter(MainWindow window)
    {
        double centerX = window.getScaledWidth() / 2D;
        double centerY = window.getScaledHeight() / 2D;

        Minecraft mc = Minecraft.getInstance();

        double posX = mc.mouseHelper.mouseX * window.getScaledWidth() / window.getWidth() - centerX;
        double posY = mc.mouseHelper.mouseY * window.getScaledHeight() / window.getHeight() - centerY;

        return (float)(Math.toDegrees(Math.atan2(posY, posX)) + 90F + 360F) % 360F;
    }

    public static int getSelectedIndex(int count)
    {
        float angle = getMouseAngleFromCenter(Minecraft.getInstance().getMainWindow());

        float segment = 360F / count;

        float startSeg = segment / 2F;

        for(int i = 0; i < count; i++)
        {
            if(angle < startSeg)
            {
                return i;
            }
            startSeg += segment;
        }

        return 0; //angle is larger than (360 - (segment / 2)), it's back to index of 0
    }
}
