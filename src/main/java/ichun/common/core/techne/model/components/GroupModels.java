package ichun.common.core.techne.model.components;

import net.minecraft.client.model.ModelRenderer;

import java.util.ArrayList;

public class GroupModels
{
    //Components are null unless it actually has something.
    public ComponentCircularArray componentCircularArray;
    public ComponentLinearArray componentLinearArray;
    public ComponentGroup componentGroup;

    public ArrayList<ModelRenderer> models = new ArrayList<ModelRenderer>();

    public void render(float f5)
    {
        if(componentCircularArray != null)
        {
            componentCircularArray.render(f5);
        }
        if(componentLinearArray != null)
        {
            componentLinearArray.render(f5);
        }
        if(componentGroup != null)
        {
            componentGroup.render(f5);
        }

        for(ModelRenderer model : models)
        {
            model.renderWithRotation(f5);
        }
    }
}
