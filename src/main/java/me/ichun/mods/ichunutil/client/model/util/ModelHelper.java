package me.ichun.mods.ichunutil.client.model.util;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.project.Identifiable;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

public class ModelHelper
{
    public static final HashMap<Class<?>, ModelRenderers> RENDERERS_FOR_CLASS = new HashMap<>();

    public static Project convertModelToProject(Object o)
    {
        Project project = new Project();
        project.parts.clear();
        project.name = o.getClass().getSimpleName();
        project.author = "Either Mojang or a mod author (Taken From Memory)";
        if(o instanceof Model)
        {
            project.texWidth = ((Model)o).textureWidth;
            project.texHeight = ((Model)o).textureHeight;
        }

        ModelRenderers renderers = digForModelRenderers(o);

        HashMap<ModelRenderer, Identifiable<?>> done = new HashMap<>();

        renderers.fields.forEach((s, modelRenderer) -> {
            createPartFor(s, modelRenderer, done, project);
        });

        renderers.arrays.forEach((s, modelRenderers) -> {
            for(int i = 0; i < modelRenderers.length; i++)
            {
                createPartFor(s + "[" + i + "]", modelRenderers[i], done, project);
            }
        });

        renderers.lists.forEach((s, modelRenderers) -> {
            for(int i = 0; i < modelRenderers.size(); i++)
            {
                createPartFor(s + "[" + i + "]", modelRenderers.get(i), done, project);
            }
        });

        renderers.fields.forEach(((s, modelRenderer) -> {
            if(done.containsKey(modelRenderer))
            {
                done.get(modelRenderer).name = s;
            }
        }));

        project.partCountProjectLife = done.size();

        return project;
    }

    private static ModelRenderers digForModelRenderers(Object o)
    {
        if(RENDERERS_FOR_CLASS.containsKey(o.getClass()))
        {
            return RENDERERS_FOR_CLASS.get(o.getClass());
        }

        ModelRenderers renderers = new ModelRenderers();

        Class<?> clz = o.getClass();
        while(clz != Model.class && clz != Object.class) //it could also be TileEntityRenderer??
        {
            try
            {
                Field[] clzFields = clz.getDeclaredFields();
                for(Field f : clzFields)
                {
                    f.setAccessible(true);
                    if(ModelRenderer.class.isAssignableFrom(f.getType()))
                    {
                        ModelRenderer rend = (ModelRenderer)f.get(o);
                        if(rend != null)
                        {
                            renderers.fields.put(f.getName(), rend); // Add normal parent fields
                        }
                    }
                    else if(ModelRenderer[].class.isAssignableFrom(f.getType()))
                    {
                        ModelRenderer[] rend = (ModelRenderer[])f.get(o);
                        if(rend != null && rend.length > 0)
                        {
                            renderers.arrays.put(f.getName(), rend);
                        }
                    }
                    else if(f.get(o) instanceof List)
                    {
                        List<?> list = (List<?>)f.get(o);
                        ArrayList<ModelRenderer> catches = new ArrayList<>();
                        for(Object o1 : list)
                        {
                            if(o1 instanceof ModelRenderer)
                            {
                                catches.add((ModelRenderer)o1);
                            }
                            else if(o1 instanceof ModelRenderer[])
                            {
                                Collections.addAll(catches, (ModelRenderer[])o1);
                            }
                        }
                        if(!catches.isEmpty())
                        {
                            renderers.lists.put(f.getName(), catches);
                        }
                    }
                }
            }
            catch(Exception e)
            {
                iChunUtil.LOGGER.error("Something went wrong parsing {}", o.getClass().getSimpleName());
            }

            clz = clz.getSuperclass();
        }

        RENDERERS_FOR_CLASS.put(o.getClass(), renderers);

        return renderers;
    }

    public static ArrayList<ModelRenderer> createModelPartsFromProject(@Nonnull Project project)
    {
        ArrayList<ModelRenderer> models = new ArrayList<>();

        project.parts.forEach(part -> populateModel(models, part));

        return models;
    }

    private static void populateModel(Collection<? super ModelRenderer> parts, Project.Part part)
    {
        int[] dims = part.getProjectTextureDims();
        if(!part.matchProject)
        {
            dims[0] = part.texWidth;
            dims[1] = part.texHeight;
        }
        ModelRenderer modelPart = new ModelRenderer(dims[0], dims[1], part.texOffX, part.texOffY);
        modelPart.rotationPointX = part.rotPX;
        modelPart.rotationPointY = part.rotPY;
        modelPart.rotationPointZ = part.rotPZ;

        modelPart.rotateAngleX = (float)Math.toRadians(part.rotAX);
        modelPart.rotateAngleY = (float)Math.toRadians(part.rotAY);
        modelPart.rotateAngleZ = (float)Math.toRadians(part.rotAZ);

        modelPart.mirror = part.mirror;
        modelPart.showModel = part.showModel;

        part.boxes.forEach(box -> {
            int texOffX = modelPart.textureOffsetX;
            int texOffY = modelPart.textureOffsetY;
            modelPart.setTextureOffset(modelPart.textureOffsetX + box.texOffX, modelPart.textureOffsetY + box.texOffY);
            modelPart.addBox(box.posX, box.posY, box.posZ, box.dimX, box.dimY, box.dimZ, box.expandX, box.expandY, box.expandZ);
            modelPart.setTextureOffset(texOffX, texOffY);
        });
        part.children.forEach(part1 -> populateModel(modelPart.childModels, part1));

        parts.add(modelPart);
    }

    public static ArrayList<ModelRenderer> explode(ArrayList<ModelRenderer> parts) //separates the children from their parents (YOU MONSTER >:( )
    {
        ArrayList<ModelRenderer> models = new ArrayList<>();

        parts.forEach(part -> explodeRecursive(models, part));

        return models;
    }

    private static void explodeRecursive(ArrayList<ModelRenderer> parts, ModelRenderer part)
    {
        parts.add(part);

        ObjectListIterator<ModelRenderer> iterator = part.childModels.iterator();
        while(iterator.hasNext())
        {
            ModelRenderer next = iterator.next();

            iterator.remove();

            next.rotationPointX += part.rotationPointX;
            next.rotationPointY += part.rotationPointY;
            next.rotationPointZ += part.rotationPointZ;
            next.rotateAngleX += part.rotateAngleX;
            next.rotateAngleY += part.rotateAngleY;
            next.rotateAngleZ += part.rotateAngleZ;

            explodeRecursive(parts, next);
        }
    }

    public static void createPartFor(String name, ModelRenderer renderer, HashMap<ModelRenderer, Identifiable<?>> done, Identifiable<?> parent)
    {
        createPartFor(name, renderer, done, parent, true);
    }

    public static void createPartFor(String name, ModelRenderer renderer, HashMap<ModelRenderer, Identifiable<?>> done, Identifiable<?> parent, boolean processChildren)
    {
        if(done.containsKey(renderer))
        {
            if(!(parent instanceof Project))
            {
                Identifiable<?> identifiable = done.get(renderer);
                identifiable.parent.disown(identifiable);
                parent.adopt(identifiable);
            }
            return;
        }
        Project.Part part = new Project.Part(parent, done.size());

        part.boxes.clear();
        part.name = name;
        part.texWidth = (int)renderer.textureWidth;
        part.texHeight = (int)renderer.textureHeight;
        if(done.isEmpty() && parent instanceof Project) //first one
        {
            ((Project)parent).texWidth = part.texWidth;
            ((Project)parent).texHeight = part.texHeight;
        }
        part.matchProject = false;
        part.texOffX = renderer.textureOffsetX;
        part.texOffY = renderer.textureOffsetY;

        part.rotPX = renderer.rotationPointX;
        part.rotPY = renderer.rotationPointY;
        part.rotPZ = renderer.rotationPointZ;

        part.rotAX = (float)Math.toDegrees(renderer.rotateAngleX);
        part.rotAY = (float)Math.toDegrees(renderer.rotateAngleY);
        part.rotAZ = (float)Math.toDegrees(renderer.rotateAngleZ);

        part.mirror = renderer.mirror;
        part.showModel = renderer.showModel;

        if(!renderer.cubeList.isEmpty())
        {
            int lowTexX = part.texWidth;
            int lowTexY = part.texHeight;

            for(ModelRenderer.ModelBox box : renderer.cubeList)
            {
                Project.Part.Box projBox = new Project.Part.Box(part);
                projBox.posX = box.posX1;
                projBox.posY = box.posY1;
                projBox.posZ = box.posZ1;

                projBox.dimX = Math.abs(box.posX2 - box.posX1);
                projBox.dimY = Math.abs(box.posY2 - box.posY1);
                projBox.dimZ = Math.abs(box.posZ2 - box.posZ1);

                if(box.quads != null)
                {
                    boolean mirrored = box.quads[1].vertexPositions[3].position.getY() < box.quads[1].vertexPositions[1].position.getY() || box.quads[2].vertexPositions[0].position.getZ() < box.quads[2].vertexPositions[3].position.getZ();

                    projBox.expandX = ((box.quads[2].vertexPositions[mirrored ? 2 : 3].position.getX() - box.quads[2].vertexPositions[mirrored ? 3 : 2].position.getX()) - projBox.dimX) / 2F; //x is doubly flipped in mirrored. it's a bit off.
                    projBox.expandY = ((box.quads[4].vertexPositions[mirrored ? 0 : 3].position.getY() - box.quads[2].vertexPositions[mirrored ? 1 : 2].position.getY()) - projBox.dimY) / 2F;
                    projBox.expandZ = ((box.quads[1].vertexPositions[mirrored ? 1 : 2].position.getZ() - box.quads[1].vertexPositions[mirrored ? 0 : 3].position.getZ()) - projBox.dimZ) / 2F;

                    int texOffX = (int)(box.quads[1].vertexPositions[mirrored ? 2 : 1].textureU * renderer.textureWidth);
                    int texOffY = (int)(Math.min(box.quads[2].vertexPositions[1].textureV, box.quads[2].vertexPositions[2].textureV) * renderer.textureHeight);
                    if(texOffX < lowTexX)
                    {
                        lowTexX = texOffX;
                    }
                    if(texOffY < lowTexY)
                    {
                        lowTexY = texOffY;
                    }
                    projBox.texOffX = texOffX;
                    projBox.texOffY = texOffY;
                }

                part.boxes.add(projBox);
            }

            part.texOffX = lowTexX;
            part.texOffY = lowTexY;
            for(int i = renderer.cubeList.size() - 1; i >= 0; i--)
            {
                ModelRenderer.ModelBox box = renderer.cubeList.get(i);

                Project.Part.Box projBox = part.boxes.get(i);
                projBox.texOffX -= lowTexX;
                projBox.texOffY -= lowTexY;

                if(i == 0)
                {
                    part.mirror = box.quads[1].vertexPositions[3].position.getY() < box.quads[1].vertexPositions[1].position.getY();
                }
            }
        }

        if(processChildren)
        {
            ObjectList<ModelRenderer> childModels = renderer.childModels;
            for(int i = 0; i < childModels.size(); i++)
            {
                ModelRenderer childModel = childModels.get(i);
                createPartFor(name + "[" + i + "]", childModel, done, part);
            }
        }

        if(parent instanceof Project)
        {
            ((Project)parent).parts.add(part);
        }
        else if(parent instanceof Project.Part)
        {
            ((Project.Part)parent).children.add(part);
        }
        done.put(renderer, part);
    }

    public static Project.Part createPartFor(ModelRenderer renderer, boolean processChildren)
    {
        if(renderer == null)
        {
            Project.Part part = new Project.Part(null, 0);
            part.boxes.clear();
            return part;
        }
        HashMap<ModelRenderer, Identifiable<?>> store = new HashMap<>();
        ModelHelper.createPartFor("", renderer, store, null, processChildren);
        return (Project.Part)store.get(renderer);
    }

    public static void matchBoxesCount(Project.Part partToChange, Project.Part referencePart)
    {
        while(partToChange.boxes.size() < referencePart.boxes.size())
        {
            Project.Part.Box box = new Project.Part.Box(partToChange);
            Project.Part.Box otherBox = referencePart.boxes.get(partToChange.boxes.size());
            box.dimX = box.dimY = box.dimZ = 0F;
            box.texOffX = otherBox.texOffX;
            box.texOffY = otherBox.texOffY;
            partToChange.boxes.add(box);
        }
    }

    public static void matchBoxAndChildrenCount(Project.Part partToChange, Project.Part referencePart)
    {
        matchBoxesCount(partToChange, referencePart);

        while(partToChange.children.size() < referencePart.children.size())
        {
            partToChange.children.add(createPartFor(null, false));
        }

        for(int i = 0; i < partToChange.children.size(); i++)
        {
            matchBoxAndChildrenCount(partToChange.children.get(i), referencePart.children.get(i));
        }
    }

    public static Project.Part createInterimPart(Project.Part prevPart, Project.Part nextPart, float prog)
    {
        Project.Part part = new Project.Part(null, 0);
        part.boxes.clear();

        part.texWidth = Math.round(prevPart.texWidth + (nextPart.texWidth - prevPart.texWidth) * prog);
        part.texHeight = Math.round(prevPart.texHeight + (nextPart.texHeight - prevPart.texHeight) * prog);

        part.texOffX = Math.round(prevPart.texOffX + (nextPart.texOffX - prevPart.texOffX) * prog);
        part.texOffY = Math.round(prevPart.texOffY + (nextPart.texOffY - prevPart.texOffY) * prog);

        part.mirror = nextPart.mirror;

        part.rotPX = prevPart.rotPX + (nextPart.rotPX - prevPart.rotPX) * prog;
        part.rotPY = prevPart.rotPY + (nextPart.rotPY - prevPart.rotPY) * prog;
        part.rotPZ = prevPart.rotPZ + (nextPart.rotPZ - prevPart.rotPZ) * prog;

        part.rotAX = prevPart.rotAX + (nextPart.rotAX - prevPart.rotAX) * prog;
        part.rotAY = prevPart.rotAY + (nextPart.rotAY - prevPart.rotAY) * prog;
        part.rotAZ = prevPart.rotAZ + (nextPart.rotAZ - prevPart.rotAZ) * prog;

        for(int i = 0; i < prevPart.boxes.size(); i++)
        {
            Project.Part.Box box = new Project.Part.Box(part);

            Project.Part.Box prevBox = prevPart.boxes.get(i);
            Project.Part.Box nextBox = nextPart.boxes.get(i);

            box.posX = prevBox.posX + (nextBox.posX - prevBox.posX) * prog;
            box.posY = prevBox.posY + (nextBox.posY - prevBox.posY) * prog;
            box.posZ = prevBox.posZ + (nextBox.posZ - prevBox.posZ) * prog;

            box.dimX = prevBox.dimX + (nextBox.dimX - prevBox.dimX) * prog;
            box.dimY = prevBox.dimY + (nextBox.dimY - prevBox.dimY) * prog;
            box.dimZ = prevBox.dimZ + (nextBox.dimZ - prevBox.dimZ) * prog;

            box.expandX = prevBox.expandX + (nextBox.expandX - prevBox.expandX) * prog;
            box.expandY = prevBox.expandY + (nextBox.expandY - prevBox.expandY) * prog;
            box.expandZ = prevBox.expandZ + (nextBox.expandZ - prevBox.expandZ) * prog;

            box.texOffX = Math.round(prevBox.texOffX + (nextBox.texOffX - prevBox.texOffX) * prog);
            box.texOffY = Math.round(prevBox.texOffY + (nextBox.texOffY - prevBox.texOffY) * prog);

            part.boxes.add(box);
        }

        for(int i = 0; i < Math.min(prevPart.children.size(), nextPart.children.size()); i++)
        {
            part.children.add(createInterimPart(prevPart.children.get(i), nextPart.children.get(i), prog));
        }

        return part;
    }

    public static ModelRenderer createModelRenderer(Project.Part part)
    {
        ModelRenderer modelPart = new ModelRenderer(part.texWidth, part.texHeight, part.texOffX, part.texOffY);

        modelPart.rotationPointX = part.rotPX;
        modelPart.rotationPointY = part.rotPY;
        modelPart.rotationPointZ = part.rotPZ;

        modelPart.rotateAngleX = (float)Math.toRadians(part.rotAX);
        modelPart.rotateAngleY = (float)Math.toRadians(part.rotAY);
        modelPart.rotateAngleZ = (float)Math.toRadians(part.rotAZ);

        modelPart.mirror = part.mirror;
        modelPart.showModel = part.showModel;

        part.boxes.forEach(box -> {
            int texOffX = modelPart.textureOffsetX;
            int texOffY = modelPart.textureOffsetY;
            modelPart.setTextureOffset(modelPart.textureOffsetX + box.texOffX, modelPart.textureOffsetY + box.texOffY);
            modelPart.addBox(box.posX, box.posY, box.posZ, box.dimX, box.dimY, box.dimZ, box.expandX, box.expandY, box.expandZ);
            modelPart.setTextureOffset(texOffX, texOffY);
        });
        return modelPart;
    }

    private static class ModelRenderers
    {
        private final HashMap<String, ModelRenderer> fields = new HashMap<>();
        private final HashMap<String, ModelRenderer[]> arrays = new HashMap<>();
        private final HashMap<String, ArrayList<ModelRenderer>> lists = new HashMap<>();
    }
}
