package ichun.common.core.techne.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.techne.TC2Info;
import ichun.common.core.techne.model.components.ModelPart;
import net.minecraft.client.model.ModelBase;

import java.util.ArrayList;

/**
 * All the fields are public. Go ahead and rotate/animate them before calling render.
 */
@SideOnly(Side.CLIENT)
public class ModelTechne2 extends ModelBase
{

    public ArrayList<ModelPart> modelParts = new ArrayList<ModelPart>();

    public ModelTechne2(TC2Info info)
    {

    }
}
