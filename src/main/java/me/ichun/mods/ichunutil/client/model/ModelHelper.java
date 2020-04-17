package me.ichun.mods.ichunutil.client.model;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.project.Identifiable;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.lang.reflect.Field;
import java.util.*;

public class ModelHelper
{
    public static Project convertModelToProject(Model model)
    {
        Project project = new Project();
        project.parts.clear();
        project.name = model.getClass().getSimpleName();
        project.author = "Either Mojang or a mod author (Taken From Memory)";
        project.texWidth = model.textureWidth;
        project.texHeight = model.textureHeight;

        HashMap<String, ModelRenderer> fields = new HashMap<>();
        HashMap<String, ModelRenderer[]> arrays = new HashMap<>();
        HashMap<String, ArrayList<ModelRenderer>> lists = new HashMap<>();

        Class<? extends Model> clz = model.getClass();
        while(clz != Model.class)
        {
            try
            {
                Field[] clzFields = clz.getDeclaredFields();
                for(Field f : clzFields)
                {
                    f.setAccessible(true);
                    if(ModelRenderer.class.isAssignableFrom(f.getType()))
                    {
                        ModelRenderer rend = (ModelRenderer)f.get(model);
                        if(rend != null)
                        {
                            fields.put(f.getName(), rend); // Add normal parent fields
                        }
                    }
                    else if(ModelRenderer[].class.isAssignableFrom(f.getType()))
                    {
                        ModelRenderer[] rend = (ModelRenderer[])f.get(model);
                        if(rend != null && rend.length > 0)
                        {
                            arrays.put(f.getName(), rend);
                        }
                    }
                    else if(f.get(model) instanceof List)
                    {
                        List<?> list = (List<?>)f.get(model);
                        ArrayList<ModelRenderer> catches = new ArrayList<>();
                        for(Object o : list)
                        {
                            if(o instanceof ModelRenderer)
                            {
                                catches.add((ModelRenderer)o);
                            }
                            else if(o instanceof ModelRenderer[])
                            {
                                Collections.addAll(catches, (ModelRenderer[])o);
                            }
                        }
                        if(!catches.isEmpty())
                        {
                            lists.put(f.getName(), catches);
                        }
                    }
                }
            }
            catch(Exception e)
            {
                iChunUtil.LOGGER.error("Something went wrong parsing {}", model.getClass().getSimpleName());
            }

            clz = (Class<? extends Model>)clz.getSuperclass();
        }

        HashMap<ModelRenderer, Identifiable<?>> done = new HashMap<>();

        fields.forEach((s, modelRenderer) -> {
            createPartFor(s, modelRenderer, done, project);
        });

        arrays.forEach((s, modelRenderers) -> {
            for(int i = 0; i < modelRenderers.length; i++)
            {
                createPartFor(s + "_" + i, modelRenderers[i], done, project);
            }
        });

        lists.forEach((s, modelRenderers) -> {
            for(int i = 0; i < modelRenderers.size(); i++)
            {
                createPartFor(s + "_" + i, modelRenderers.get(i), done, project);
            }
        });

        fields.forEach(((s, modelRenderer) -> {
            if(done.containsKey(modelRenderer))
            {
                done.get(modelRenderer).name = s;
            }
        }));

        project.partCountProjectLife = done.size();

        return project;
    }

    private static void createPartFor(String name, ModelRenderer renderer, HashMap<ModelRenderer, Identifiable<?>> done, Identifiable<?> parent)
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

        ObjectList<ModelRenderer> childModels = renderer.childModels;
        for(int i = 0; i < childModels.size(); i++)
        {
            ModelRenderer childModel = childModels.get(i);
            createPartFor(name + "_" + i, childModel, done, part);
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
}
