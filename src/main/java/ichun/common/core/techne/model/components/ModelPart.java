package ichun.common.core.techne.model.components;

import ichun.common.iChunUtil;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;

public class ModelPart
{
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    public int textureWidth;
    public int textureHeight;

    public BufferedImage image;
    public int imageId = -1;

    public GroupModels models = new GroupModels();

    public boolean render(boolean bindTexture, float f5)
    {
        if(bindTexture)
        {
            if(image == null)
            {
                iChunUtil.console("A Techne 2 model part is trying to render without a texture! Removing from overall Model Parts.");
                return false;
            }
            if(imageId == -1)
            {
                imageId = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), image);
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, imageId);
        }

        GL11.glPushMatrix();
        GL11.glScalef(scaleX, scaleY, scaleZ);

        models.render(f5);

        GL11.glPopMatrix();

        return true;
    }
}
