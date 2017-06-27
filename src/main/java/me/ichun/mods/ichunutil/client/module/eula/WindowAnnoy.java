package me.ichun.mods.ichunutil.client.module.eula;

import me.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTextWrapper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class WindowAnnoy extends Window
{
    public int timeout = 6000;
    public ElementButton btnOk;

    public WindowAnnoy()
    {
        super(new IWorkspace()
        {
            @Override
            public boolean canClickOnElement(Window window, Element element)
            {
                return true;
            }

            {
                VARIABLE_LEVEL = 0;
                mc = Minecraft.getMinecraft();
                fontRenderer = mc.fontRenderer;
            }
        }, 0, 0, 300, 90, 300, 90, "window.popup.title", true);

        elements.add(new ElementTextWrapper(this, 10, 15, width - 20, height - 30, 0, true, false, I18n.translateToLocal("ichunutil.eula.message")));

        btnOk = new ElementButton(this, width - 70, height - 25, 60, 16, 3, false, 1, 1, I18n.translateToLocal("element.button.ok") + " (" + (int)Math.floor(timeout / 20F) + ")");
        elements.add(btnOk);
    }

    @Override
    public void draw(int mouseX, int mouseY)
    {
        super.draw(mouseX, mouseY);
    }

    @Override
    public void elementTriggered(Element element)
    {
        iChunUtil.eventHandlerClient.eulaDrawEulaNotice = false;
        iChunUtil.config.eulaAcknowledged = RandomStringUtils.random(20, 32, 127, false, false, null, (new Random(Math.abs(Minecraft.getMinecraft().getSession().getPlayerID().replaceAll("-", "").hashCode() + (Math.abs("iChunUtilEULA".hashCode()))))));
        iChunUtil.config.save();
        iChunUtil.LOGGER.info("Thanks for acknowledging the message! The EULA message should no longer pop up when you launch Minecraft.");
    }

    @Override
    public void update()
    {
        super.update();
        if(timeout < 0)
        {
            iChunUtil.eventHandlerClient.eulaDrawEulaNotice = false;
            iChunUtil.config.eulaAcknowledged = RandomStringUtils.random(20, 32, 127, false, false, null, (new Random(Math.abs(Minecraft.getMinecraft().getSession().getPlayerID().replaceAll("-", "").hashCode() + (Math.abs("iChunUtilEULA".hashCode()))))));
            iChunUtil.config.save();
            iChunUtil.LOGGER.info("Acknoledgement period timed out. Automatically accepting the EULA. The EULA message should no longer pop up when you launch Minecraft.");
        }
        else
        {
            btnOk.text = I18n.translateToLocal("element.button.ok") + " (" + (int)Math.floor(timeout / 20F) + ")";
        }
        timeout--;
    }

    @Override
    public boolean canMinimize()
    {
        return false;
    }
}
