package ichun.common.core.techne.model.components;

public class ComponentLinearArray extends ComponentGroup
{
    public int countX;
    public int countY;
    public int countZ;

    public float spaceX;
    public float spaceY;
    public float spaceZ;

    //TODO this
    @Override
    protected void renderGroup(float f5)
    {
        groupModels.render(f5);
    }
}
