package me.ichun.mods.ichunutil.common.module.tabula.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ichun.mods.ichunutil.common.iChunUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Project extends Identifiable<Project> //Model
{
    public static final int IDENTIFIER_LENGTH = 20;
    public static final int PROJ_VERSION = 5;

    public static final Gson SIMPLE_GSON = new GsonBuilder().disableHtmlEscaping().create();

    //Save file stuff
    @Nullable
    public transient File saveFile;
    public transient boolean isDirty;
    public transient boolean tampered; //file may be tampered?
    public transient boolean isOldTabula; //*GASP*

    //Project texture Stuffs
    private transient BufferedImage bufferedTexture;
    public transient int bufferedTextureId = -1;

    //defaults
    public String author = "";
    public int projVersion = PROJ_VERSION; //TODO detect if version is old (< 5). Support? Should we support Techne?
    public ArrayList<String> notes = new ArrayList<>();

    public int texWidth = 64;
    public int texHeight = 32;

    public ArrayList<Part> parts = new ArrayList<>();

    public int partCountProjectLife = 0;

    //TODO should we check for tampered files?

    @Override
    public Identifiable<?> getById(String id)
    {
        for(Part part : parts)
        {
            Identifiable<?> ident = part.getById(id);
            if(ident != null)
            {
                return ident;
            }
        }
        return identifier.equals(id) ? this : null;
    }

    @Override
    public String getJsonWithoutChildren()
    {
        ArrayList<Part> parts = this.parts;
        this.parts = null;
        String json = SIMPLE_GSON.toJson(this);
        this.parts = parts;
        return json;
    }

    @Override
    public void transferChildren(Project clone)
    {
        clone.parts = parts;
    }

    public boolean save(@Nonnull File saveFile) //file to save as
    {
        return saveProject(this, saveFile);
    }

    public void markDirty()
    {
        isDirty = true;
    }

    public void importProject(@Nonnull Project project, boolean texture)
    {
        if(texture && project.getBufferedTexture() != null)
        {
            loadBufferedTexture(project.getBufferedTexture(), project.bufferedTextureId);
        }
        parts.addAll(project.parts);
    }

    public void loadBufferedTexture(BufferedImage texture, int newId)
    {
        if(bufferedTextureId != -1)
        {
            //TODO delete the old texture
        }
        this.bufferedTexture = texture;
        this.bufferedTextureId = newId;
    }

    public BufferedImage getBufferedTexture()
    {
        return bufferedTexture;
    }

    public void repair()
    {
        while(projVersion < PROJ_VERSION)
        {
            if(projVersion <= 4) //TODO UHHH is this necessary?
            {

            }
            projVersion++;
        }
    }

    public static boolean saveProject(Project project, File file)
    {
        try
        {
            file.getParentFile().mkdirs();

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            out.setLevel(9);
            out.putNextEntry(new ZipEntry("model.json"));

            byte[] data = SIMPLE_GSON.toJson(project).getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            if(project.bufferedTexture != null)
            {
                out.putNextEntry(new ZipEntry("texture.png"));
                ImageIO.write(project.bufferedTexture, "png", out);
                out.closeEntry();
            }

            out.close();

            //save and mark no longer dirty
            //set our save file as the saveFile.
            project.saveFile = file;
            project.isDirty = false;

            return true;
        }
        catch(Exception e)
        {
            iChunUtil.LOGGER.error("Failed to save model: {}", project.name + " - " + project.author);
            e.printStackTrace();
        }
        return false;
    }

    public static class Part extends Identifiable<Part> //ModelRenderer
    {
        public ArrayList<String> notes = new ArrayList<>();

        public int texWidth = 64;
        public int texHeight = 32;
        public boolean matchProject = true;

        public int texOffX;
        public int texOffY;

        //position
        public float rotPX;
        public float rotPY;
        public float rotPZ;

        //angles
        public float rotAX;
        public float rotAY;
        public float rotAZ;

        public boolean mirror;

        public ArrayList<Box> boxes = new ArrayList<>();
        public ArrayList<Part> children = new ArrayList<>();

        public Part(String parentIdent, int count)
        {
            this.parentIdent = parentIdent;
            this.boxes.add(new Box(identifier)); //there is code that rely on parts always having 1 box
            this.name = "part" + count;
        }

        @Override
        public Identifiable<?> getById(String id)
        {
            for(Box part : boxes)
            {
                Identifiable<?> ident = part.getById(id);
                if(ident != null)
                {
                    return ident;
                }
            }

            for(Part part : children)
            {
                Identifiable<?> ident = part.getById(id);
                if(ident != null)
                {
                    return ident;
                }
            }
            return identifier.equals(id) ? this : null;
        }

        @Override
        public String getJsonWithoutChildren()
        {
            ArrayList<Box> boxes = this.boxes;
            ArrayList<Part> children = this.children;
            this.boxes = null;
            this.children = null;
            String json = SIMPLE_GSON.toJson(this);
            this.boxes = boxes;
            this.children = children;
            return json;
        }

        @Override
        public void transferChildren(Part clone)
        {
            clone.boxes = boxes;
            clone.children = children;
        }


        public static class Box extends Identifiable<Box> //ModelBox
        {
            //the old offsets.
            public float posX;
            public float posY;
            public float posZ;

            public float dimX = 1F;
            public float dimY = 1F;
            public float dimZ = 1F;

            public float expandX;
            public float expandY;
            public float expandZ;

            public Box(String identifier)
            {
                this.parentIdent = identifier;
                this.name = "Box";
            }

            @Override
            public Identifiable getById(String id)
            {
                return identifier.equals(id) ? this : null;
            }

            @Override
            public String getJsonWithoutChildren()
            {
                return SIMPLE_GSON.toJson(this);
            }

            @Override
            public void transferChildren(Box clone){}
        }
    }

    public Part addPart(Identifiable<?> parent)
    {
        markDirty();

        Part part = new Part(this.identifier, ++partCountProjectLife);
        if(parent instanceof Part) //Parts can have children
        {
            part.parentIdent = parent.identifier;

            ((Part)parent).children.add(part);
        }
        else if(parent instanceof Part.Box)
        {
            return addPart(getById(parent.parentIdent));
        }
        else
        {
            parts.add(part);
        }

        return part;
    }

    public Part.Box addBox(Identifiable<?> parent)
    {
        markDirty();

        if(parent instanceof Part) //Parts can have children
        {
            Part.Box box = new Part.Box(parent.identifier);

            ((Part)parent).boxes.add(box);
            return box;
        }
        else if(parent instanceof Part.Box)
        {
            return addBox(getById(parent.parentIdent));
        }
        else
        {
            Part part = new Part(this.identifier, ++partCountProjectLife);
            parts.add(part);
            return part.boxes.get(0);
        }
    }

    public void delete(Identifiable<?> child)
    {
        Identifiable<?> parent = getById(child.parentIdent);
        if(parent instanceof Project) //lets orphan this mofo
        {
            ((Project)parent).parts.remove(child);
        }
        else if(parent instanceof Part)
        {
            ((Part)parent).boxes.remove(child);
            ((Part)parent).children.remove(child);
        }
        markDirty();
    }
}
