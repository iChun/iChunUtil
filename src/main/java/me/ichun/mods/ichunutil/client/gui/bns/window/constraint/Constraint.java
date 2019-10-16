package me.ichun.mods.ichunutil.client.gui.bns.window.constraint;

public class Constraint
{
    public static final Constraint NONE = new Constraint(null);

    private final IConstrained parent;
    private Property left;
    private Property right;
    private Property top;
    private Property bottom;

    public Constraint(IConstrained parent) //TODO several elements constrained to each other then back to the same parent?
    {
        this.parent = parent;
        left = right = top = bottom = Property.NONE;
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

    public static Constraint matchParent(IConstrained c1, IConstrainable c, int i)
    {
        return new Constraint(c1)
                .left(c, Property.Type.LEFT, i)
                .right(c, Property.Type.RIGHT, i)
                .top(c, Property.Type.TOP, i)
                .bottom(c, Property.Type.BOTTOM, i);
    }

    public void apply()
    {
        //check our size first to make sure we're set up and non-zero
        if(parent != null)
        {
            //contract if we're above max dimensions
            parent.contractX(parent.getMaxWidth().get());
            parent.contractY(parent.getMaxHeight().get());
            //expand if we're below minimum dimensions
            parent.expandX(parent.getMinWidth().get());
            parent.expandY(parent.getMinHeight().get());
        }
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
        if(parent != null)
        {
            //contract if we're above max dimensions
            parent.contractX(parent.getMaxWidth().get());
            parent.contractY(parent.getMaxHeight().get());
            //expand if we're below minimum dimensions
            parent.expandX(parent.getMinWidth().get());
            parent.expandY(parent.getMinHeight().get());
        }
    }

    public static class Property
    {
        private static final Property NONE = new Property(null, null, null, 0)
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
            BOTTOM; // -

            public int get(IConstrainable reference)
            {
                switch(this)
                {
                    case LEFT: return reference.getLeft();
                    case RIGHT: return reference.getRight();
                    case TOP: return reference.getTop();
                    case BOTTOM: return reference.getBottom();
                }
                return -1;
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
            }
            return false;
        }
    }
}
