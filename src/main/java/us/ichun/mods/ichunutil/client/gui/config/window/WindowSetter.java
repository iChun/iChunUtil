package us.ichun.mods.ichunutil.client.gui.config.window;

import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Property;
import us.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import us.ichun.mods.ichunutil.client.gui.config.window.element.ElementPropSetter;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.common.core.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class WindowSetter extends Window
{
    public ElementPropSetter props;

    public GuiConfigs parent;
    public String selectedCat;

    public WindowSetter(GuiConfigs parent, int x, int y, int w, int h, int minW, int minH)
    {
        super(parent, x, y, w, h, minW, minH, "", true);
        this.parent = parent;

        props = new ElementPropSetter(this, BORDER_SIZE + 1, BORDER_SIZE + 1 + 10, width - (BORDER_SIZE * 2 + 2), height - (BORDER_SIZE * 2 + 2) - 11, 0);
        elements.add(props);
    }

    @Override
    public void resized()
    {
        posX = 219;
        posY = 1;
        width = workspace.width - 1 - posX;
        height = workspace.height - 2;

        super.resized();
    }

    public void draw(int mouseX, int mouseY) //4 pixel border?
    {
        if(parent.windowCats.configs.selectedIdentifier.isEmpty())
        {
            return;
        }
        else
        {
            for(us.ichun.mods.ichunutil.client.gui.window.element.ElementListTree.Tree tree : parent.windowCats.configs.trees)
            {
                if(tree.selected)
                {
                    String cat = (String)tree.attachedObject;
                    if(!cat.equals(selectedCat) && parent.windowCats.selectedConfig != null)
                    {
                        props.saveTimeout = 0;
                        props.save();

                        selectedCat = cat;
                        titleLocale = cat;

                        TreeMap<String, String> localizedToPropName = new TreeMap<String, String>();
                        props.trees.clear();
                        props.sliderProg = 0.0D;
                        ArrayList<PropInfo> propsA = new ArrayList<PropInfo>();
                        for(Property prop : parent.windowCats.selectedConfig.categories.get(parent.windowCats.configs.selectedIdentifier))
                        {
                            String localized = StatCollector.translateToLocal(parent.windowCats.selectedConfig.propName.get(prop));
                            propsA.add(new PropInfo(localized, prop, parent.windowCats.selectedConfig.propType.get(prop)));
                        }
                        Collections.sort(propsA);
                        for(PropInfo prop : propsA)
                        {
                            props.createTree(parent.windowCats.selectedConfig, prop, 17);
                        }
                    }
                }
            }
        }
        super.draw(mouseX, mouseY);
    }

    @Override
    public void elementTriggered(Element element)
    {
    }

    @Override
    public boolean canBeDragged()
    {
        return false;
    }

    @Override
    public boolean isStatic()
    {
        return true;
    }

    @Override
    public boolean canMinimize() { return false; }

    public class PropInfo implements Comparable
    {
        public final String localized;
        public final Property prop;
        public final Config.EnumPropType type;

        public PropInfo(String localized, Property prop, Config.EnumPropType type)
        {
            this.localized = localized;
            this.prop = prop;
            this.type = type;
        }

        @Override
        public int compareTo(Object o)
        {
            if(o instanceof PropInfo)
            {
                PropInfo cfg = (PropInfo)o;
                return localized.compareTo(cfg.localized);
            }
            return 0;

        }
    }
}
