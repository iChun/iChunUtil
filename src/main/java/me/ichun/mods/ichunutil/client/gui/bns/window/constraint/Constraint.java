package me.ichun.mods.ichunutil.client.gui.bns.window.constraint;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public class Constraint
{
    public static final Constraint NONE = new Constraint(null) {
        @Override
        public void apply() {} //Do nothing
    };

    protected final IConstrained parent;
    private Property left;
    private Property right;
    private Property top;
    private Property bottom;
    private Property width;
    private Property height;

    public Constraint(IConstrained parent) //TODO several elements constrained to each other then back to the same parent?
    {
        this.parent = parent;
        left = right = top = bottom = width = height = Property.NONE;
    }

    public Constraint left(IConstrainable c, Property.Type type, int i)
    {
        left = c == null ? Property.NONE : new Property(c, Property.Type.LEFT, type, i);
        return this;
    }

    public Constraint right(IConstrainable c, Property.Type type, int i)
    {
        right = c == null ? Property.NONE : new Property(c, Property.Type.RIGHT, type, i);
        return this;
    }

    public Constraint top(IConstrainable c, Property.Type type, int i)
    {
        top = c == null ? Property.NONE : new Property(c, Property.Type.TOP, type, i);
        return this;
    }

    public Constraint bottom(IConstrainable c, Property.Type type, int i)
    {
        bottom = c == null ? Property.NONE : new Property(c, Property.Type.BOTTOM, type, i);
        return this;
    }

    public Constraint width(IConstrainable c, Property.Type type, int i)
    {
        width = c == null ? Property.NONE : new Property(c, Property.Type.WIDTH, type, i);
        return this;
    }

    public Constraint height(IConstrainable c, Property.Type type, int i)
    {
        height = c == null ? Property.NONE : new Property(c, Property.Type.HEIGHT, type, i);
        return this;
    }

    public Constraint type(Property.Type link, IConstrainable c, Property.Type type, int i)
    {
        switch(link)
        {
            case LEFT : return left(c, type, i);
            case RIGHT : return right(c, type, i);
            case TOP : return top(c, type, i);
            case BOTTOM : return bottom(c, type, i);
            case WIDTH: return width(c, type, i);
            case HEIGHT: return height(c, type, i);
        }
        return this;
    }

    public Property get(Property.Type type)
    {
        switch (type)
        {
            case LEFT: return left;
            case RIGHT: return right;
            case TOP: return top;
            case BOTTOM: return bottom;
            case WIDTH: return width;
            default: case HEIGHT:return height;
        }
    }

    public boolean hasLeft()
    {
        return left != Property.NONE;
    }

    public boolean hasRight()
    {
        return right != Property.NONE;
    }

    public boolean hasTop()
    {
        return top != Property.NONE;
    }

    public boolean hasBottom()
    {
        return bottom != Property.NONE;
    }

    public boolean hasWidth() { return width != Property.NONE; }

    public boolean hasHeight() { return height != Property.NONE; }

    public static Constraint matchParent(@Nonnull IConstrained c, @Nonnull IConstrainable parent, int i)
    {
        return new Constraint(c)
                .left(parent, Property.Type.LEFT, i)
                .right(parent, Property.Type.RIGHT, i)
                .top(parent, Property.Type.TOP, i)
                .bottom(parent, Property.Type.BOTTOM, i);
    }

    public static Constraint matchParentRatio(@Nonnull IConstrained c, @Nonnull IConstrainable parent, int i) // specifically for width/height ratio
    {
        return new Constraint(c)
                .width(parent, Property.Type.WIDTH, i)
                .height(parent, Property.Type.HEIGHT, i);
    }

    public static Constraint sizeOnly(@Nonnull IConstrained c)
    {
        return new Constraint(c) {
            @Override
            public void apply()
            {
                //contract if we're above max dimensions
                parent.contractX(parent.getMaxWidth());
                parent.contractY(parent.getMaxHeight());
                //expand if we're below minimum dimensions
                parent.expandX(parent.getMinWidth());
                parent.expandY(parent.getMinHeight());
            }
        };
    }

    public void apply()
    {
        //check our size first to make sure we're set up and non-zero
        if(parent != null)
        {
            //contract if we're above max dimensions
            parent.contractX(parent.getMaxWidth());
            parent.contractY(parent.getMaxHeight());
            //expand if we're below minimum dimensions
            parent.expandX(parent.getMinWidth());
            parent.expandY(parent.getMinHeight());

            //apply our width/heights before we follow the boundary constraints
            width.apply(this);
            height.apply(this);

            //do the boundary constraints
            if(!(left.apply(this) | right.apply(this)))
            {
                if(hasTop() || hasBottom())
                {
                    //free floating on x axis, align centrally to attachment on Y axis
                    IConstrainable attachment = hasTop() ? top.reference : bottom.reference;
                    parent.setLeft(attachment.getLeft() + (((attachment.getRight() - attachment.getLeft()) - parent.getWidth()) / 2));
                }
                else
                {
                    //parent is free floating. set to middle on X axis
                    parent.setPosX((parent.getParentWidth() - parent.getWidth()) / 2);
                }
            }
            if(!(top.apply(this) | bottom.apply(this)))
            {
                if(hasLeft() || hasRight())
                {
                    //free floating on y axis, align centrally to attachment on X axis
                    IConstrainable attachment = hasLeft() ? left.reference : right.reference;
                    parent.setTop(attachment.getTop() + (((attachment.getBottom() - attachment.getTop()) - parent.getHeight()) / 2));
                }
                else
                {
                    //parent is free floating. set to middle on Y axis
                    parent.setPosY((parent.getParentHeight() - parent.getHeight()) / 2);
                }
            }

            //contract if we're above max dimensions
            parent.contractX(parent.getMaxWidth());
            parent.contractY(parent.getMaxHeight());
            //expand if we're below minimum dimensions
            parent.expandX(parent.getMinWidth());
            parent.expandY(parent.getMinHeight());
        }
    }

    public static class Property
    {
        public static final Property NONE = new Property(null, null, null, 0)
        {
            @Override
            public boolean apply(Constraint c)
            {
                return false;
            }
        };

        private IConstrainable reference;
        private Type self;
        private Type type;
        private int dist;

        public enum Type
        {
            LEFT, // +
            RIGHT, // -
            TOP, // +
            BOTTOM, // -
            WIDTH, //dist = value/100
            HEIGHT; //dist = value/100

            public int get(IConstrainable reference)
            {
                switch(this)
                {
                    case LEFT: return reference.getLeft();
                    case RIGHT: return reference.getRight();
                    case TOP: return reference.getTop();
                    case BOTTOM: return reference.getBottom();
                    case WIDTH: return reference.getWidth();
                    case HEIGHT: return reference.getHeight();
                }
                return -1;
            }

            public Type getOpposite()
            {
                switch(this)
                {
                    case LEFT: return RIGHT;
                    case RIGHT: return LEFT;
                    case TOP: return BOTTOM;
                    case BOTTOM: return TOP;
                    case WIDTH: return HEIGHT;
                    default: case HEIGHT: return WIDTH;
                }
            }

            public Direction.Axis getAxis()
            {
                switch(this)
                {
                    case LEFT:
                    case RIGHT:
                    case WIDTH:
                        return Direction.Axis.X;
                    case TOP:
                    case BOTTOM:
                    case HEIGHT:
                        return Direction.Axis.Y;
                    default:
                        return Direction.Axis.Z;
                }
            }
        }

        public Property(IConstrainable reference, Type self, Type type, int dist)
        {
            this.reference = reference;
            this.self = self;
            this.type = type;
            this.dist = dist;
        }

        public boolean apply(Constraint c) //return true if we did something.
        {
            switch(self)
            {
                case LEFT:
                {
                    c.parent.setLeft(type.get(reference) + dist); // doesn't adjust the width...
                    if(c.hasRight())
                    {
                        c.right.apply(c); // tell the right to set itself
                    }
                    return true;
                }
                case RIGHT:
                {
                    if(c.hasLeft())
                    {
                        c.parent.setRight(type.get(reference) - dist); //this sets the width
                    }
                    else
                    {
                        c.parent.setLeft(type.get(reference) - dist - c.parent.getWidth()); //move the entire thing to the right
                    }
                    return true;
                }
                case TOP:
                {
                    c.parent.setTop(type.get(reference) + dist); // doesn't adjust the width...
                    if(c.hasBottom())
                    {
                        c.bottom.apply(c); // tell the right to set itself
                    }
                    return true;
                }
                case BOTTOM:
                {
                    if(c.hasTop())
                    {
                        c.parent.setBottom(type.get(reference) - dist); //this sets the width
                    }
                    else
                    {
                        c.parent.setTop(type.get(reference) - dist - c.parent.getHeight()); //move the entire thing down
                    }
                    return true;
                }
                case WIDTH:
                {
                    c.parent.setWidth((int)(type.get(reference) * dist / 100F)); //get the reference object's width, * dist / 100 for ratio
                    return true;
                }
                case HEIGHT:
                {
                    c.parent.setHeight((int)(type.get(reference) * dist / 100F)); //get the reference object's width, * dist / 100 for ratio
                    return true;
                }
            }
            return false;
        }

        public IConstrainable getReference()
        {
            return reference;
        }

        public Type getType()
        {
            return type;
        }

        public int getDist()
        {
            return dist;
        }
    }
}
