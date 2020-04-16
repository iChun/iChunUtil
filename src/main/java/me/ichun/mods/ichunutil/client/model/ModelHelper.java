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

        HashSet<ModelRenderer> done = new HashSet<>();

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

        project.partCountProjectLife = done.size();

        return project;
    }

    private static void createPartFor(String name, ModelRenderer renderer, HashSet<ModelRenderer> done, Identifiable<?> parent)
    {
        if(done.contains(renderer))
        {
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

        for(ModelRenderer.ModelBox modelBox : renderer.cubeList)
        {
            Project.Part.Box box = new Project.Part.Box(part);
            box.posX = modelBox.posX1;
            box.posY = modelBox.posY1;
            box.posZ = modelBox.posZ1;

            box.dimX = Math.abs(modelBox.posX2 - modelBox.posX1);
            box.dimY = Math.abs(modelBox.posY2 - modelBox.posY1);
            box.dimZ = Math.abs(modelBox.posZ2 - modelBox.posZ1);

            if(modelBox.quads != null) //TODO WHY DOES THE HORSE LOOK WEIRD
            {
                ModelRenderer.PositionTextureVertex[] vertices = modelBox.quads[1].vertexPositions;// left Quad, txOffsetX, txOffsetY + sizeZ

                part.mirror = (((vertices[part.mirror ? 1 : 2].position.getY() - vertices[part.mirror ? 3 : 0].position.getY()) - box.dimY) / 2 < 0.0D);//silly techne check to see if the model is really mirrored or not

                part.texOffX = (int)(vertices[part.mirror ? 2 : 1].textureU * part.texWidth);
                part.texOffY = (int)(vertices[part.mirror ? 2 : 1].textureV * part.texHeight - box.dimZ);

                if(vertices[part.mirror ? 2 : 1].textureV > vertices[part.mirror ? 1 : 2].textureV) //Check to correct the texture offset on the y axis to fix some minecraft models
                {
                    part.mirror = !part.mirror;

                    part.texOffX = (int)(vertices[part.mirror ? 2 : 1].textureU * part.texWidth);
                    part.texOffY = (int)(vertices[part.mirror ? 2 : 1].textureV * part.texHeight - box.dimZ);
                }

                float delta = ((vertices[part.mirror ? 1 : 2].position.getY() - vertices[part.mirror ? 3 : 0].position.getY()) - box.dimY) / 2;
                box.expandX = box.expandY = box.expandZ = delta;
            }

            part.boxes.add(box);
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
        done.add(renderer);
    }
}
