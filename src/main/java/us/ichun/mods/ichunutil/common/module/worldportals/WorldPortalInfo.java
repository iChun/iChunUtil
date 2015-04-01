package us.ichun.mods.ichunutil.common.module.worldportals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class WorldPortalInfo
{
    //scanzone is not what its name implies, it's actually a 2 dimensional plane where the illusion/projection would have been rendered.
    public WorldPortalCarrier parent; //MIGHT BE NULL

    public int orientation = 0;// 90 degree increments
    public int face = 0; //0 = face up, 1 = face down, 2 = horizontal
    public boolean horizontal = false;

    public double height = 3.0D; //height of scanzone
    public double width = 1.5D; //width of scanzone

    public double offsetHeight = 1.0D; //height offset of scanzone
    public double offsetDepth = 0.1D; //depth offset of scanzone

    public boolean active = true; //if inactive... don't render the projection or teleport at all

    public boolean project = false; //show projection, obviously.

    public int teleport = 1; //0 = off, 1 = oneway/twoway, 2 = frustrum, 3 = windowstyle //TODO think about how to do frustrum style teleporting and window style teleporting.

    public String channelName = "Unnamed";

    public WorldPortalInfo link;

    //TODO windows...? maybe have frustrum boxes outside the frustrum scanzone to check

    //TODO update render distance based on the required distance by the projection, so that no need to grab 10000000000 chunks cause of "far" render.

    public void setParent(WorldPortalCarrier te)
    {
        parent = te;
    }

    public void update() //Tile Entity updating this, World object
    {
    }

    public void transverseTo(WorldPortalInfo otherPortal)
    {
        //TODO active check
        //TODO teleport checks esp for window... maybe in shouldTransverse() or something
    }

    public void write(NBTTagCompound tag)
    {
        tag.setInteger("orientation", orientation);
        tag.setInteger("face", face);
        tag.setBoolean("horizontal", horizontal);

        tag.setDouble("height", height);
        tag.setDouble("width", width);

        tag.setDouble("offsetHeight", offsetHeight);
        tag.setDouble("offsetDepth", offsetDepth);

        tag.setBoolean("active", active);

        tag.setBoolean("project", project);

        tag.setInteger("teleport", teleport);

        tag.setString("channelName", channelName);

        if(link != null)
        {
            NBTTagCompound linkTag = new NBTTagCompound();

            link.write(linkTag);

            tag.setTag("linkInfo", linkTag);
        }
    }

    public void readSelfInfo(NBTTagCompound tag)
    {
        orientation = tag.getInteger("orientation");
        face = tag.getInteger("face");
        horizontal = tag.getBoolean("horizontal");

        height = tag.getDouble("height");
        width = tag.getDouble("width");

        offsetHeight = tag.getDouble("offsetHeight");
        offsetDepth = tag.getDouble("offsetDepth");

        active = tag.getBoolean("active");

        project = tag.getBoolean("project");

        teleport = tag.getInteger("teleport");

        channelName = tag.getString("channelName");

        if(height < 0.1D)
        {
            height = 0.1D;
        }
        if(width < 0.05D)
        {
            width = 0.05D;
        }

        if(tag.hasKey("linkInfo"))
        {
            link = new WorldPortalInfo();
            link.read(tag.getCompoundTag("linkInfo"));
        }
    }

    public void read(NBTTagCompound tag)
    {
        readSelfInfo(tag);
    }

    public AxisAlignedBB getScanZone()
    {
        return null; //TODO this
    }

    @SideOnly(Side.CLIENT)
    public void render(World world, double x, double y, double z, float f) //x, y, z not necessary, already translated to the tile entity, normally
    {
        Minecraft mc = Minecraft.getMinecraft();

        if(!(mc.gameSettings.showDebugInfo || active && project && link == null))
        {
            return;
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.enableCull();

        orientPlane();

        if(mc.gameSettings.showDebugInfo)
        {
            GlStateManager.disableTexture2D();

            GlStateManager.disableNormalize();
            GlStateManager.disableLighting();

            GlStateManager.color(1.0F, 0.0F, 1.0F, 1.0F);

            renderPlane();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableLighting();
            GlStateManager.enableNormalize();

            GlStateManager.enableTexture2D();
        }

        GlStateManager.disableCull();

        GlStateManager.disableBlend();
    }

    @SideOnly(Side.CLIENT)
    public void orientPlane()
    {
        GlStateManager.translate(0D, 0.5D + (height / 2D) + offsetHeight, 0D);

        if(horizontal)
        {
            GlStateManager.rotate(-90F, 1F, 0F, 0F);
        }
        GlStateManager.rotate(orientation * 90F, 0F, 1F, 0F);

        GlStateManager.translate(0D, 0D, -offsetDepth);
    }

    public void renderPlane()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(-width,  (height / 2D), 0D);
        worldRenderer.addVertex(-width, -(height / 2D), 0D);
        worldRenderer.addVertex(width, -(height / 2D), 0D);
        worldRenderer.addVertex(width,  (height / 2D), 0D);
        tessellator.draw();
    }
}
