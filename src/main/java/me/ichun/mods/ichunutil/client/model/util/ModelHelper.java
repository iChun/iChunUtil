package me.ichun.mods.ichunutil.client.model.util;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.project.Identifiable;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

public class ModelHelper
{
    //TODO fix this
//    public static final HashMap<Class<?>, ModelRenderers> RENDERERS_FOR_CLASS = new HashMap<>();
//
//    public static Project convertModelToProject(Object o)
//    {
//        Project project = new Project();
//        project.parts.clear();
//        project.name = o.getClass().getSimpleName();
//        project.author = "Either Mojang or a mod author (Taken From Memory)";
//        if(o instanceof Model)
//        {
//            project.texWidth = ((Model)o).texWidth;
//            project.texHeight = ((Model)o).texHeight;
//        }
//
//        ModelRenderers renderers = digForModelRenderers(o);
//
//        HashMap<ModelPart, Identifiable<?>> done = new HashMap<>();
//
//        renderers.fields.forEach((s, modelRenderer) -> {
//            createPartFor(s, modelRenderer, done, project);
//        });
//
//        renderers.arrays.forEach((s, modelRenderers) -> {
//            for(int i = 0; i < modelRenderers.length; i++)
//            {
//                createPartFor(s + "[" + i + "]", modelRenderers[i], done, project);
//            }
//        });
//
//        renderers.lists.forEach((s, modelRenderers) -> {
//            for(int i = 0; i < modelRenderers.size(); i++)
//            {
//                createPartFor(s + "[" + i + "]", modelRenderers.get(i), done, project);
//            }
//        });
//
//        renderers.fields.forEach(((s, modelRenderer) -> {
//            if(done.containsKey(modelRenderer))
//            {
//                done.get(modelRenderer).name = s;
//            }
//        }));
//
//        project.partCountProjectLife = done.size();
//
//        return project;
//    }
//
//    private static ModelRenderers digForModelRenderers(Object o)
//    {
//        if(RENDERERS_FOR_CLASS.containsKey(o.getClass()))
//        {
//            return RENDERERS_FOR_CLASS.get(o.getClass());
//        }
//
//        ModelRenderers renderers = new ModelRenderers();
//
//        Class<?> clz = o.getClass();
//        while(clz != Model.class && clz != Object.class) //it could also be TileEntityRenderer??
//        {
//            try
//            {
//                Field[] clzFields = clz.getDeclaredFields();
//                for(Field f : clzFields)
//                {
//                    f.setAccessible(true);
//                    if(ModelPart.class.isAssignableFrom(f.getType()))
//                    {
//                        ModelPart rend = (ModelPart)f.get(o);
//                        if(rend != null)
//                        {
//                            renderers.fields.put(f.getName(), rend); // Add normal parent fields
//                        }
//                    }
//                    else if(ModelPart[].class.isAssignableFrom(f.getType()))
//                    {
//                        ModelPart[] rend = (ModelPart[])f.get(o);
//                        if(rend != null && rend.length > 0)
//                        {
//                            renderers.arrays.put(f.getName(), rend);
//                        }
//                    }
//                    else if(f.get(o) instanceof List)
//                    {
//                        List<?> list = (List<?>)f.get(o);
//                        ArrayList<ModelPart> catches = new ArrayList<>();
//                        for(Object o1 : list)
//                        {
//                            if(o1 instanceof ModelPart)
//                            {
//                                catches.add((ModelPart)o1);
//                            }
//                            else if(o1 instanceof ModelPart[])
//                            {
//                                Collections.addAll(catches, (ModelPart[])o1);
//                            }
//                        }
//                        if(!catches.isEmpty())
//                        {
//                            renderers.lists.put(f.getName(), catches);
//                        }
//                    }
//                }
//            }
//            catch(Exception e)
//            {
//                iChunUtil.LOGGER.error("Something went wrong parsing {}", o.getClass().getSimpleName());
//            }
//
//            clz = clz.getSuperclass();
//        }
//
//        RENDERERS_FOR_CLASS.put(o.getClass(), renderers);
//
//        return renderers;
//    }
//
//    public static ArrayList<ModelPart> createModelPartsFromProject(@Nonnull Project project)
//    {
//        ArrayList<ModelPart> models = new ArrayList<>();
//
//        project.parts.forEach(part -> populateModel(models, part));
//
//        return models;
//    }
//
//    private static void populateModel(Collection<? super ModelPart> parts, Project.Part part)
//    {
//        int[] dims = part.getProjectTextureDims();
//        if(!part.matchProject)
//        {
//            dims[0] = part.texWidth;
//            dims[1] = part.texHeight;
//        }
//        ModelPart modelPart = new ModelPart(dims[0], dims[1], part.texOffX, part.texOffY);
//        modelPart.x = part.rotPX;
//        modelPart.y = part.rotPY;
//        modelPart.z = part.rotPZ;
//
//        modelPart.xRot = (float)Math.toRadians(part.rotAX);
//        modelPart.yRot = (float)Math.toRadians(part.rotAY);
//        modelPart.zRot = (float)Math.toRadians(part.rotAZ);
//
//        modelPart.mirror = part.mirror;
//        modelPart.visible = part.showModel;
//
//        part.boxes.forEach(box -> {
//            int texOffX = modelPart.xTexOffs;
//            int texOffY = modelPart.yTexOffs;
//            modelPart.texOffs(modelPart.xTexOffs + box.texOffX, modelPart.yTexOffs + box.texOffY);
//            modelPart.addBox(box.posX, box.posY, box.posZ, box.dimX, box.dimY, box.dimZ, box.expandX, box.expandY, box.expandZ);
//            modelPart.texOffs(texOffX, texOffY);
//        });
//        part.children.forEach(part1 -> populateModel(modelPart.children, part1));
//
//        parts.add(modelPart);
//    }
//
//    public static ArrayList<ModelPart> explode(ArrayList<ModelPart> parts) //separates the children from their parents (YOU MONSTER >:( )
//    {
//        ArrayList<ModelPart> models = new ArrayList<>();
//
//        parts.forEach(part -> explodeRecursive(models, part));
//
//        return models;
//    }
//
//    private static void explodeRecursive(ArrayList<ModelPart> parts, ModelPart part)
//    {
//        parts.add(part);
//
//        ObjectListIterator<ModelPart> iterator = part.children.iterator();
//        while(iterator.hasNext())
//        {
//            ModelPart next = iterator.next();
//
//            iterator.remove();
//
//            next.x += part.x;
//            next.y += part.y;
//            next.z += part.z;
//            next.xRot += part.xRot;
//            next.yRot += part.yRot;
//            next.zRot += part.zRot;
//
//            explodeRecursive(parts, next);
//        }
//    }
//
//    public static void createPartFor(String name, ModelPart renderer, HashMap<ModelPart, Identifiable<?>> done, Identifiable<?> parent)
//    {
//        createPartFor(name, renderer, done, parent, true);
//    }
//
//    public static void createPartFor(String name, ModelPart renderer, HashMap<ModelPart, Identifiable<?>> done, Identifiable<?> parent, boolean processChildren)
//    {
//        if(done.containsKey(renderer))
//        {
//            if(!(parent instanceof Project))
//            {
//                Identifiable<?> identifiable = done.get(renderer);
//                identifiable.parent.disown(identifiable);
//                parent.adopt(identifiable);
//            }
//            return;
//        }
//        Project.Part part = new Project.Part(parent, done.size());
//
//        part.boxes.clear();
//        part.name = name;
//        part.texWidth = (int)renderer.xTexSize;
//        part.texHeight = (int)renderer.yTexSize;
//        if(done.isEmpty() && parent instanceof Project) //first one
//        {
//            ((Project)parent).texWidth = part.texWidth;
//            ((Project)parent).texHeight = part.texHeight;
//        }
//        part.matchProject = false;
//        part.texOffX = renderer.xTexOffs;
//        part.texOffY = renderer.yTexOffs;
//
//        part.rotPX = renderer.x;
//        part.rotPY = renderer.y;
//        part.rotPZ = renderer.z;
//
//        part.rotAX = (float)Math.toDegrees(renderer.xRot);
//        part.rotAY = (float)Math.toDegrees(renderer.yRot);
//        part.rotAZ = (float)Math.toDegrees(renderer.zRot);
//
//        part.mirror = renderer.mirror;
//        part.showModel = renderer.visible;
//
//        if(!renderer.cubes.isEmpty())
//        {
//            int lowTexX = part.texWidth;
//            int lowTexY = part.texHeight;
//
//            for(ModelPart.Cube box : renderer.cubes)
//            {
//                Project.Part.Box projBox = new Project.Part.Box(part);
//                projBox.posX = box.minX;
//                projBox.posY = box.minY;
//                projBox.posZ = box.minZ;
//
//                projBox.dimX = Math.abs(box.maxX - box.minX);
//                projBox.dimY = Math.abs(box.maxY - box.minY);
//                projBox.dimZ = Math.abs(box.maxZ - box.minZ);
//
//                if(box.polygons != null)
//                {
//                    boolean mirrored = box.polygons[1].vertices[3].pos.y() < box.polygons[1].vertices[1].pos.y() || box.polygons[2].vertices[0].pos.z() < box.polygons[2].vertices[3].pos.z();
//
//                    projBox.expandX = ((box.polygons[2].vertices[mirrored ? 2 : 3].pos.x() - box.polygons[2].vertices[mirrored ? 3 : 2].pos.x()) - projBox.dimX) / 2F; //x is doubly flipped in mirrored. it's a bit off.
//                    projBox.expandY = ((box.polygons[4].vertices[mirrored ? 0 : 3].pos.y() - box.polygons[2].vertices[mirrored ? 1 : 2].pos.y()) - projBox.dimY) / 2F;
//                    projBox.expandZ = ((box.polygons[1].vertices[mirrored ? 1 : 2].pos.z() - box.polygons[1].vertices[mirrored ? 0 : 3].pos.z()) - projBox.dimZ) / 2F;
//
//                    int texOffX = (int)(box.polygons[1].vertices[mirrored ? 2 : 1].u * renderer.xTexSize);
//                    int texOffY = (int)(Math.min(box.polygons[2].vertices[1].v, box.polygons[2].vertices[2].v) * renderer.yTexSize);
//                    if(texOffX < lowTexX)
//                    {
//                        lowTexX = texOffX;
//                    }
//                    if(texOffY < lowTexY)
//                    {
//                        lowTexY = texOffY;
//                    }
//                    projBox.texOffX = texOffX;
//                    projBox.texOffY = texOffY;
//                }
//
//                part.boxes.add(projBox);
//            }
//
//            part.texOffX = lowTexX;
//            part.texOffY = lowTexY;
//            for(int i = renderer.cubes.size() - 1; i >= 0; i--)
//            {
//                ModelPart.Cube box = renderer.cubes.get(i);
//
//                Project.Part.Box projBox = part.boxes.get(i);
//                projBox.texOffX -= lowTexX;
//                projBox.texOffY -= lowTexY;
//
//                if(i == 0)
//                {
//                    part.mirror = box.polygons[1].vertices[3].pos.y() < box.polygons[1].vertices[1].pos.y();
//                }
//            }
//        }
//
//        if(processChildren)
//        {
//            ObjectList<ModelPart> childModels = renderer.children;
//            for(int i = 0; i < childModels.size(); i++)
//            {
//                ModelPart childModel = childModels.get(i);
//                createPartFor(name + "[" + i + "]", childModel, done, part);
//            }
//        }
//
//        if(parent instanceof Project)
//        {
//            ((Project)parent).parts.add(part);
//        }
//        else if(parent instanceof Project.Part)
//        {
//            ((Project.Part)parent).children.add(part);
//        }
//        done.put(renderer, part);
//    }
//
//    public static Project.Part createPartFor(ModelPart renderer, boolean processChildren)
//    {
//        if(renderer == null)
//        {
//            Project.Part part = new Project.Part(null, 0);
//            part.boxes.clear();
//            return part;
//        }
//        HashMap<ModelPart, Identifiable<?>> store = new HashMap<>();
//        ModelHelper.createPartFor("", renderer, store, null, processChildren);
//        return (Project.Part)store.get(renderer);
//    }
//
//    public static void matchBoxesCount(Project.Part partToChange, Project.Part referencePart)
//    {
//        while(partToChange.boxes.size() < referencePart.boxes.size())
//        {
//            Project.Part.Box box = new Project.Part.Box(partToChange);
//            Project.Part.Box otherBox = referencePart.boxes.get(partToChange.boxes.size());
//            box.dimX = box.dimY = box.dimZ = 0F;
//            box.texOffX = otherBox.texOffX;
//            box.texOffY = otherBox.texOffY;
//            partToChange.boxes.add(box);
//        }
//    }
//
//    public static void matchBoxAndChildrenCount(Project.Part partToChange, Project.Part referencePart)
//    {
//        matchBoxesCount(partToChange, referencePart);
//
//        while(partToChange.children.size() < referencePart.children.size())
//        {
//            partToChange.children.add(createPartFor(null, false));
//        }
//
//        for(int i = 0; i < referencePart.children.size(); i++) //referencePart children size will always be less than partToChange.
//        {
//            matchBoxAndChildrenCount(partToChange.children.get(i), referencePart.children.get(i));
//        }
//    }
//
//    public static Project.Part createInterimPart(Project.Part prevPart, Project.Part nextPart, float prog)
//    {
//        Project.Part part = new Project.Part(null, 0);
//        part.boxes.clear();
//
//        part.texWidth = Math.round(prevPart.texWidth + (nextPart.texWidth - prevPart.texWidth) * prog);
//        part.texHeight = Math.round(prevPart.texHeight + (nextPart.texHeight - prevPart.texHeight) * prog);
//
//        part.texOffX = Math.round(prevPart.texOffX + (nextPart.texOffX - prevPart.texOffX) * prog);
//        part.texOffY = Math.round(prevPart.texOffY + (nextPart.texOffY - prevPart.texOffY) * prog);
//
//        part.mirror = nextPart.mirror;
//
//        part.rotPX = prevPart.rotPX + (nextPart.rotPX - prevPart.rotPX) * prog;
//        part.rotPY = prevPart.rotPY + (nextPart.rotPY - prevPart.rotPY) * prog;
//        part.rotPZ = prevPart.rotPZ + (nextPart.rotPZ - prevPart.rotPZ) * prog;
//
//        part.rotAX = prevPart.rotAX + (nextPart.rotAX - prevPart.rotAX) * prog;
//        part.rotAY = prevPart.rotAY + (nextPart.rotAY - prevPart.rotAY) * prog;
//        part.rotAZ = prevPart.rotAZ + (nextPart.rotAZ - prevPart.rotAZ) * prog;
//
//        for(int i = 0; i < prevPart.boxes.size(); i++)
//        {
//            Project.Part.Box box = new Project.Part.Box(part);
//
//            Project.Part.Box prevBox = prevPart.boxes.get(i);
//            Project.Part.Box nextBox = nextPart.boxes.get(i);
//
//            box.posX = prevBox.posX + (nextBox.posX - prevBox.posX) * prog;
//            box.posY = prevBox.posY + (nextBox.posY - prevBox.posY) * prog;
//            box.posZ = prevBox.posZ + (nextBox.posZ - prevBox.posZ) * prog;
//
//            box.dimX = prevBox.dimX + (nextBox.dimX - prevBox.dimX) * prog;
//            box.dimY = prevBox.dimY + (nextBox.dimY - prevBox.dimY) * prog;
//            box.dimZ = prevBox.dimZ + (nextBox.dimZ - prevBox.dimZ) * prog;
//
//            box.expandX = prevBox.expandX + (nextBox.expandX - prevBox.expandX) * prog;
//            box.expandY = prevBox.expandY + (nextBox.expandY - prevBox.expandY) * prog;
//            box.expandZ = prevBox.expandZ + (nextBox.expandZ - prevBox.expandZ) * prog;
//
//            box.texOffX = Math.round(prevBox.texOffX + (nextBox.texOffX - prevBox.texOffX) * prog);
//            box.texOffY = Math.round(prevBox.texOffY + (nextBox.texOffY - prevBox.texOffY) * prog);
//
//            part.boxes.add(box);
//        }
//
//        for(int i = 0; i < Math.min(prevPart.children.size(), nextPart.children.size()); i++)
//        {
//            part.children.add(createInterimPart(prevPart.children.get(i), nextPart.children.get(i), prog));
//        }
//
//        return part;
//    }
//
//    public static ModelPart createModelRenderer(Project.Part part, boolean processChildren)
//    {
//        ModelPart modelPart = new ModelPart(part.texWidth, part.texHeight, part.texOffX, part.texOffY);
//
//        modelPart.x = part.rotPX;
//        modelPart.y = part.rotPY;
//        modelPart.z = part.rotPZ;
//
//        modelPart.xRot = (float)Math.toRadians(part.rotAX);
//        modelPart.yRot = (float)Math.toRadians(part.rotAY);
//        modelPart.zRot = (float)Math.toRadians(part.rotAZ);
//
//        modelPart.mirror = part.mirror;
//        modelPart.visible = part.showModel;
//
//        part.boxes.forEach(box -> {
//            int texOffX = modelPart.xTexOffs;
//            int texOffY = modelPart.yTexOffs;
//            modelPart.texOffs(modelPart.xTexOffs + box.texOffX, modelPart.yTexOffs + box.texOffY);
//            modelPart.addBox(box.posX, box.posY, box.posZ, box.dimX, box.dimY, box.dimZ, box.expandX, box.expandY, box.expandZ);
//            modelPart.texOffs(texOffX, texOffY);
//        });
//
//        if(processChildren)
//        {
//            part.children.forEach(child -> modelPart.addChild(createModelRenderer(child, true)));
//        }
//        return modelPart;
//    }
//
//    public static ModelPart createModelRenderer(Project.Part part)
//    {
//        return createModelRenderer(part, false);
//    }
//
//    private static class ModelRenderers
//    {
//        private final HashMap<String, ModelPart> fields = new HashMap<>();
//        private final HashMap<String, ModelPart[]> arrays = new HashMap<>();
//        private final HashMap<String, ArrayList<ModelPart>> lists = new HashMap<>();
//    }
}
