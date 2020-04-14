package me.ichun.mods.ichunutil.common.module.tabula.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Project extends Identifiable<Project> //Model
{
    public static final int IDENTIFIER_LENGTH = 20;
    public static final int PROJ_VERSION = 5;

    public static final Gson SIMPLE_GSON = new GsonBuilder().disableHtmlEscaping().create();

    //defaults
    public String modelName = "";
    public String author = "";
    public int projVersion = PROJ_VERSION; //TODO detect if version is old (< 5). Support? Should we support Techne?
    public ArrayList<String> notes = new ArrayList<>();

    public int texWidth = 64;
    public int texHeight = 32;

    public ArrayList<Part> parts = new ArrayList<>();

    @Override
    public Identifiable getById(String id)
    {
        for(Part part : parts)
        {
            Identifiable ident = part.getById(id);
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

    public static class Part extends Identifiable<Part> //ModelRenderer
    {
        public String name = "Part";
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

        public Part(String parentIdent)
        {
            this.parentIdent = parentIdent;
            this.boxes.add(new Box(identifier));
        }

        @Override
        public Identifiable getById(String id)
        {
            for(Box part : boxes)
            {
                Identifiable ident = part.getById(id);
                if(ident != null)
                {
                    return ident;
                }
            }

            for(Part part : children)
            {
                Identifiable ident = part.getById(id);
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
            public String name = "Box";

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

    public Part addPart()
    {
        Part part = new Part(this.identifier);
        parts.add(part);
        return part;
    }
}
