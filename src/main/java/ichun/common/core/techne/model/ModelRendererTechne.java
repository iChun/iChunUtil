package ichun.common.core.techne.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelRendererTechne extends ModelRenderer
{
    private boolean compiled;
    private int displayList;

    public ModelRendererTechne(ModelBase par1ModelBase, String par2Str)
    {
        super(par1ModelBase, par2Str);
    }

    public ModelRendererTechne(ModelBase par1ModelBase)
    {
        super(par1ModelBase);
    }

    public ModelRendererTechne(ModelBase par1ModelBase, int par2, int par3)
    {
        super(par1ModelBase, par2, par3);
    }

    @SideOnly(Side.CLIENT)
    public void renderWithTechneRotation(float par1)
    {
        if (!this.isHidden)
        {
            if (this.showModel)
            {
                if (!this.compiled)
                {
                    this.compileDisplayList(par1);
                }

                GL11.glPushMatrix();
                GL11.glTranslatef(this.rotationPointX * par1, this.rotationPointY * par1, this.rotationPointZ * par1);

                if (this.rotateAngleY != 0.0F)
                {
                    GL11.glRotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleZ != 0.0F)
                {
                    GL11.glRotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleX != 0.0F)
                {
                    GL11.glRotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }

                GL11.glCallList(this.displayList);
                GL11.glPopMatrix();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float par1)
    {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (int i = 0; i < this.cubeList.size(); ++i)
        {
            ((ModelBox)this.cubeList.get(i)).render(tessellator, par1);
        }

        GL11.glEndList();
        this.compiled = true;
    }

}
