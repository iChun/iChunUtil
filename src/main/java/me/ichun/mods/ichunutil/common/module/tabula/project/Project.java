package me.ichun.mods.ichunutil.common.module.tabula.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.ichun.mods.ichunutil.client.model.tabula.ModelTabula;
import me.ichun.mods.ichunutil.client.render.NativeImageTexture;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private transient byte[] textureBytes;
    @OnlyIn(Dist.CLIENT)
    public transient NativeImageTexture nativeImageTexture;

    //Client Model
    @OnlyIn(Dist.CLIENT)
    private transient ModelTabula model;

    //defaults
    public String author = "";
    public int projVersion = PROJ_VERSION;
    public ArrayList<String> notes = new ArrayList<>();

    public float scaleX = 1F;
    public float scaleY = 1F;
    public float scaleZ = 1F;

    public int texWidth = 64;
    public int texHeight = 32;

    public String textureFile = null;
    public String textureFileMd5 = null;

    public ArrayList<Part> parts = new ArrayList<>();

    public int partCountProjectLife = 0;

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
    public void adoptChildren()
    {
        for(Part part : parts)
        {
            part.parent = this;
            part.adoptChildren();
        }
    }

    @Override
    public void disown(Identifiable<?> child)
    {
        if(!parts.remove(child))
        {
            for(Part part : parts)
            {
                part.disown(child);
            }
        }
        else
        {
            child.parent = null;
        }
    }

    @Override
    public void adopt(Identifiable<?> child)
    {
        if(child instanceof Part)
        {
            parts.add((Part)child);
            child.parent = this;
        }
    }

    @Override
    public boolean rearrange(Identifiable<?> before, Identifiable<?> child)
    {
        boolean arranged = false;
        while(before.parent != null && before.parent != this)
        {
            if(before.parent.rearrange(before, child))
            {
                arranged = true;
                break;
            }
            before = before.parent;
        }
        if(before.rearrange(before, child))
        {
            arranged = true;
        }
        if(before == child)
        {
            return true;
        }
        if(!arranged)
        {
            if(child instanceof Part && parts.contains(child) && parts.remove(child))
            {
                if(before == this)
                {
                    parts.add(0, (Part)child);
                }
                else
                {
                    for(int i = 0; i < parts.size(); i++)
                    {
                        if(parts.get(i) == before)
                        {
                            parts.add(i + 1, (Part)child);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void witnessProtectionProgramme() //change the identity of the part
    {
        identifier = RandomStringUtils.randomAscii(Project.IDENTIFIER_LENGTH);
        parts.forEach(Identifiable::witnessProtectionProgramme);
    }

    @Override
    public Project clone()
    {
        Project project = SIMPLE_GSON.fromJson(SIMPLE_GSON.toJson(this), Project.class);
        project.adoptChildren();
        return project;
    }

    //DO NOT CALL DESTROY
    public void transferTransients(Project project) //not all transients
    {
        //force dirty
        project.isDirty = true;

        project.saveFile = this.saveFile;
        project.tampered = this.tampered;
        project.isOldTabula = this.isOldTabula;

        project.textureBytes = this.textureBytes;
        project.nativeImageTexture = this.nativeImageTexture;
    }

    public boolean save(@Nonnull File saveFile) //file to save as
    {
        return saveProject(this, saveFile);
    }

    @Override
    public Project getProject()
    {
        return this;
    }

    @Override
    public Project markDirty() //Should mostly be called by the PROJECT INFO.
    {
        isDirty = true;

        if(FMLEnvironment.dist.isClient())
        {
            updateModel();
        }

        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public void destroy()
    {
        //destroy the texture
        setImageBytes(null);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateModel()
    {
        if(model != null)
        {
            model.isDirty = true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public ModelTabula getModel()
    {
        if(model == null)
        {
            model = new ModelTabula(this);
        }
        return model;
    }

    public void importProject(@Nonnull Project project, boolean texture)
    {
        if(texture && project.getTextureBytes() != null)
        {
            setImageBytes(project.getTextureBytes());
        }
        parts.addAll(project.parts);
        adoptChildren();
    }


    @OnlyIn(Dist.CLIENT)
    public void setImageBytes(byte[] bytes)
    {
        if(nativeImageTexture != null)
        {
            Minecraft.getInstance().getTextureManager().deleteTexture(nativeImageTexture.getResourceLocation());

            nativeImageTexture = null;
        }
        this.textureBytes = bytes;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getNativeImageResourceLocation()
    {
        if(textureBytes != null)
        {
            if(nativeImageTexture == null)
            {
                try (NativeImage image = NativeImage.read(new ByteArrayInputStream(textureBytes)))
                {
                    nativeImageTexture = new NativeImageTexture(image);
                    Minecraft.getInstance().getTextureManager().loadTexture(nativeImageTexture.getResourceLocation(), nativeImageTexture);
                }
                catch(IOException e)
                {
                    iChunUtil.LOGGER.error("Failed to read NativeImage for project: " + name);
                    e.printStackTrace();
                }
            }

            return nativeImageTexture.getResourceLocation();
        }
        return null;
    }

    public byte[] getTextureBytes()
    {
        return textureBytes;
    }

    public ArrayList<Part.Box> getAllBoxes()
    {
        ArrayList<Part.Box> boxes = new ArrayList<>();

        for(Part part : parts)
        {
            part.addAllBoxes(boxes);
        }

        return boxes;
    }

    public ArrayList<Part> getAllParts()
    {
        ArrayList<Part> parts = new ArrayList<>(this.parts);

        for(Part part : this.parts)
        {
            part.addAllParts(parts);
        }

        return parts;
    }

    public void load()
    {
        repair(); //repair first.

        adoptChildren();
    }

    public void repair()
    {
        while(projVersion < PROJ_VERSION)
        {
            //OldTabula is version 4. Nothing to repair.
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

            if(project.textureBytes != null)
            {
                out.putNextEntry(new ZipEntry("texture.png"));
                out.write(project.textureBytes, 0, project.textureBytes.length);
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
        public boolean showModel = true;

        public ArrayList<Box> boxes = new ArrayList<>();
        public ArrayList<Part> children = new ArrayList<>();

        public Part(Identifiable<?> parent, int count)
        {
            this.parent = parent;
            this.boxes.add(new Box(this)); //there is code that rely on parts always having 1 box
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
        public void adoptChildren()
        {
            for(Box box : boxes)
            {
                box.parent = this;
                box.adoptChildren();
            }

            for(Part part : children)
            {
                part.parent = this;
                part.adoptChildren();
            }
        }

        @Override
        public void disown(Identifiable<?> child)
        {
            if(!(boxes.remove(child) | children.remove(child)))
            {
                for(Part part : children)
                {
                    part.disown(child);
                }
            }
            else
            {
                child.parent = null;
            }
        }

        @Override
        public void adopt(Identifiable<?> child)
        {
            if(child instanceof Part)
            {
                children.add((Part)child);
                child.parent = this;
            }
            else if(child instanceof Box)
            {
                boxes.add((Box)child);
                child.parent = this;
            }
        }

        @Override
        public boolean rearrange(Identifiable<?> before, Identifiable<?> child)
        {
            if(before == child)
            {
                return true;
            }
            if(child instanceof Part && children.contains(child) && children.remove(child))
            {
                if(before == this)
                {
                    children.add(0, (Part)child);
                }
                else
                {
                    for(int i = 0; i < children.size(); i++)
                    {
                        if(children.get(i) == before)
                        {
                            children.add(i + 1, (Part)child);
                        }
                    }
                }
                return true;
            }
            if(child instanceof Box && boxes.contains(child) && boxes.remove(child))
            {
                if(before == this)
                {
                    boxes.add(0, (Box)child);
                }
                else
                {
                    for(int i = 0; i < boxes.size(); i++)
                    {
                        if(boxes.get(i) == before)
                        {
                            boxes.add(i + 1, (Box)child);
                        }
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public void witnessProtectionProgramme()
        {
            identifier = RandomStringUtils.randomAscii(Project.IDENTIFIER_LENGTH);
            boxes.forEach(Identifiable::witnessProtectionProgramme);
            children.forEach(Identifiable::witnessProtectionProgramme);
        }

        @Override
        public Part clone()
        {
            Part part = SIMPLE_GSON.fromJson(SIMPLE_GSON.toJson(this), Part.class);
            part.adoptChildren();
            return part;
        }

        public int[] getProjectTextureDims()
        {
            if(parent instanceof Part)
            {
                return ((Part)parent).getProjectTextureDims();
            }
            else if(parent instanceof Project)
            {
                return new int[] { ((Project)parent).texWidth, ((Project)parent).texHeight };
            }
            iChunUtil.LOGGER.error("We can't find out parent's texture dimensions, we have an orphaned Part. Uh oh! Their name is {} and their identifier is {}", name, identifier);
            return new int[] { texWidth, texHeight };
        }

        public void addAllBoxes(ArrayList<Box> boxes)
        {
            boxes.addAll(this.boxes);
            for(Part child : children)
            {
                child.addAllBoxes(boxes);
            }
        }

        public void addAllParts(ArrayList<Part> parts)
        {
            parts.addAll(this.children);
            for(Part child : children)
            {
                child.addAllParts(parts);
            }
        }

        public void resetPositions()
        {
            this.rotPX = this.rotPY = this.rotPZ = 0F;
            children.forEach(Part::resetPositions);
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

            public int texOffX;
            public int texOffY;

            public Box(Identifiable<?> parent)
            {
                this.parent = parent;
                this.name = "Box";
            }

            @Override
            public Identifiable<?> getById(String id)
            {
                return identifier.equals(id) ? this : null;
            }

            @Override
            public String getJsonWithoutChildren()
            {
                return SIMPLE_GSON.toJson(this);
            }

            @Override
            public void adoptChildren(){} //boxes are infertile

            @Override
            public void disown(Identifiable<?> child){}

            @Override
            public void adopt(Identifiable<?> child){}

            @Override
            public boolean rearrange(Identifiable<?> before, Identifiable<?> child){ return false; }

            @Override
            public void witnessProtectionProgramme()
            {
                identifier = RandomStringUtils.randomAscii(Project.IDENTIFIER_LENGTH);
            }

            @Override
            public Box clone()
            {
                Box box = SIMPLE_GSON.fromJson(SIMPLE_GSON.toJson(this), Box.class);
                box.adoptChildren();
                return box;
            }
        }
    }

    public void addPart(Identifiable<?> parent, Part part)
    {
        if(parent instanceof Part) //Parts can have children
        {
            part.parent = parent;

            ((Part)parent).children.add(part);
        }
        else if(parent instanceof Part.Box)
        {
            addPart(parent.parent, part);
        }
        else
        {
            part.parent = this;
            parts.add(part);
        }
    }

    public Part addBox(Identifiable<?> parent, Part.Box box)
    {
        if(parent instanceof Part) //Parts can have children
        {
            box.parent = parent;
            ((Part)parent).boxes.add(box);
        }
        else if(parent instanceof Part.Box)
        {
            addBox(parent.parent, box);
        }
        else
        {
            Part part = new Part(this, ++partCountProjectLife);
            part.boxes.clear();
            parts.add(part);
            addBox(part, box);

            return part;
        }
        return null;
    }

    public void delete(Identifiable<?> child)
    {
        Identifiable<?> parent = child.parent;
        if(parent instanceof Project) //lets orphan this mofo
        {
            ((Project)parent).parts.remove(child);
        }
        else if(parent instanceof Part)
        {
            ((Part)parent).boxes.remove(child);
            ((Part)parent).children.remove(child);
        }
    }
}
