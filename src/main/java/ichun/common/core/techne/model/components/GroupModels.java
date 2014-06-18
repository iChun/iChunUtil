package ichun.common.core.techne.model.components;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GroupModels
{
    //Components are null unless it actually has something.
    public ArrayList<ComponentCircularArray> componentCircularArray = new ArrayList<ComponentCircularArray>();
    public ArrayList<ComponentLinearArray> componentLinearArray = new ArrayList<ComponentLinearArray>();
    public ArrayList<ComponentGroup> componentGroup = new ArrayList<ComponentGroup>();

    public ArrayList<ModelRenderer> models = new ArrayList<ModelRenderer>();

    /**
     * In order of Techne's save file.
     * @param f5
     */
    public void render(float f5)
    {
        for(ComponentCircularArray component : componentCircularArray)
        {
            component.render(f5);
        }
        for(ModelRenderer model : models)
        {
            model.renderWithRotation(f5);
        }
        for(ComponentLinearArray component : componentLinearArray)
        {
            component.render(f5);
        }
        for(ComponentGroup component : componentGroup)
        {
            component.render(f5);
        }
    }
}
