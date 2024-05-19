package me.ichun.mods.ichunutil.client.gui.bns;

/**
 * @param width      of the texture file itself
 * @param x1         the region we want to define
 * @param cornerSize sizes
 * @param fillX1     coords for where we use to fill
 */
public record TextureDefinition(
    //of the texture file itself
    double width, double height,

    //the region of the area we want to define
    int x1, int x2, int y1, int y2,

    //sizes
    int cornerSize, int borderSize,

    //coords for where we use to fill
    int fillX1, int fillX2, int fillY1, int fillY2
)
{

    public enum DrawType
    {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP,
        LEFT,
        RIGHT,
        BOTTOM,
        FILL,
        IMAGE
    }

    public double[] getCoords(DrawType type) //x1, x2, y1, y2
    {
        return switch(type)
        {
            case TOP_LEFT -> new double[] {
                (x1             ) / width,
                (x1 + cornerSize) / width,
                (y1             ) / height,
                (y1 + cornerSize) / height
            };
            case TOP_RIGHT -> new double[] {
                (x2 - cornerSize) / width,
                (x2             ) / width,
                (y1             ) / height,
                (y1 + cornerSize) / height
            };
            case BOTTOM_LEFT -> new double[] {
                (x1             ) / width,
                (x1 + cornerSize) / width,
                (y2 - cornerSize) / height,
                (y2             ) / height
            };
            case BOTTOM_RIGHT -> new double[] {
                (x2 - cornerSize) / width,
                (x2             ) / width,
                (y2 - cornerSize) / height,
                (y2             ) / height
            };
            case TOP -> new double[] {
                (x1 + cornerSize) / width,
                (x2 - cornerSize) / width,
                (y1             ) / height,
                (y1 + borderSize) / height
            };
            case LEFT -> new double[] {
                (x1             ) / width,
                (x1 + borderSize) / width,
                (y1 + cornerSize) / height,
                (y2 - cornerSize) / height
            };
            case RIGHT -> new double[] {
                (x2 - borderSize) / width,
                (x2             ) / width,
                (y1 + cornerSize) / height,
                (y2 - cornerSize) / height
            };
            case BOTTOM -> new double[] {
                (x1 + cornerSize) / width,
                (x2 - cornerSize) / width,
                (y2 - borderSize) / height,
                (y2             ) / height
            };
            case FILL -> new double[] {
                fillX1 / width,
                fillX2 / width,
                fillY1 / height,
                fillY2 / height
            };
            case IMAGE -> new double[] {
                x1 / width,
                x2 / width,
                y1 / height,
                y2 / height
            };
        };
    }
}
