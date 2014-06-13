package ichun.common.core.techne.model.components;

public class ComponentCircularArray extends ComponentGroup
{
    public float radius;
    public int count;

    //TODO this
    @Override
    protected void renderGroup(float f5)
    {
        groupModels.render(f5);
    }
}
