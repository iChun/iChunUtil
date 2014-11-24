package us.ichun.module.tabula.common.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.techne.TC1Json;
import ichun.common.core.techne.TC2Info;
import ichun.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.resources.IResource;
import org.apache.commons.io.IOUtils;
import us.ichun.module.tabula.client.model.ModelBaseDummy;
import us.ichun.module.tabula.client.model.ModelInfo;
import us.ichun.module.tabula.common.project.components.CubeGroup;
import us.ichun.module.tabula.common.project.components.CubeInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ProjectInfo
{
    public static final int IDENTIFIER_LENGTH = 20;

    public transient String identifier;
    public transient File saveFile;
    public transient String saveFileMd5;
    public transient boolean saved;

    public transient int lastAutosave;
    public transient boolean autosaved;

    public transient float cameraFov = 30F;
    public transient float cameraZoom = 1.0F;
    public transient float cameraYaw;
    public transient float cameraPitch;
    public transient float cameraOffsetX;
    public transient float cameraOffsetY;

    public transient boolean ignoreNextImage;
    public transient File textureFile;
    public transient String textureFileMd5;

    public transient long lastEdit;

    public transient BufferedImage bufferedTexture;

    public transient boolean destroyed;

    public transient ArrayList<String> states;
    public transient int lastState;
    public transient int switchState;

    @SideOnly(Side.CLIENT)
    public transient ModelBaseDummy model;

    public String modelName;
    public String authorName;
    public int projVersion; //TODO if projVersion < current version, do file repairs and resave.

    public int textureWidth = 64;
    public int textureHeight = 32;

    public ArrayList<CubeGroup> cubeGroups;
    public ArrayList<CubeInfo> cubes;

    public int cubeCount;

    public ProjectInfo()
    {
        modelName = "";
        authorName = "";
        cameraFov = 30F;
        cameraZoom = 1.0F;
        cubeGroups = new ArrayList<CubeGroup>();
        cubes = new ArrayList<CubeInfo>();
        states = new ArrayList<String>();
        switchState = -1;
    }

    public ProjectInfo(String name, String author)
    {
        this();
        modelName = name;
        authorName = author;

        cubeGroups = new ArrayList<CubeGroup>();
        cubes = new ArrayList<CubeInfo>();
    }

    public String getAsJson()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //        Gson gson = new Gson();

        return gson.toJson(this);
    }

    @SideOnly(Side.CLIENT)
    public void initClient()
    {
        model = new ModelBaseDummy(this);
        model.textureWidth = textureWidth;
        model.textureHeight = textureHeight;
        for(int i = 0; i < cubeGroups.size(); i++)
        {
            createGroupCubes(cubeGroups.get(i));
        }
        for(int i = 0 ; i < cubes.size(); i++)
        {
            //TODO remember to support child models.
            model.createModelFromCubeInfo(cubes.get(i));
        }
    }

    @SideOnly(Side.CLIENT)
    public void destroy()
    {
        if(model != null && !destroyed)
        {
            destroyed = true;
            for(int i = model.cubes.size() - 1; i >= 0; i--)
            {
                model.removeCubeInfo(model.cubes.get(i));
            }
            model.rotationPoint.destroy();
        }
    }

    public ArrayList<CubeInfo> getAllCubes()
    {
        ArrayList<CubeInfo> cubes = new ArrayList<CubeInfo>();
        addAllCubes(cubes, this.cubes);
        addAllCubesFromGroups(cubes, this.cubeGroups);
        return cubes;
    }

    public void addAllCubes(ArrayList<CubeInfo> list, ArrayList<CubeInfo> cubes)
    {
        list.addAll(cubes);
        for(CubeInfo cube : cubes)
        {
            addAllCubes(list, cube.getChildren());
        }
    }

    public void addAllCubesFromGroups(ArrayList<CubeInfo> list, ArrayList<CubeGroup> groups)
    {
        for(CubeGroup group : groups)
        {
            addAllCubes(list, group.cubes);
            addAllCubesFromGroups(list, group.cubeGroups);
        }
    }

    public void createNewGroup()
    {
        cubeGroups.add(new CubeGroup("Group"));
    }

    public void createNewCube()
    {
        cubeCount++;
        cubes.add(new CubeInfo("shape" + Integer.toString(cubeCount)));
    }

    private void createGroupCubes(CubeGroup group)
    {
        for(int i = 0; i < group.cubeGroups.size(); i++)
        {
            createGroupCubes(group.cubeGroups.get(i));
        }
        for(int i = 0; i < group.cubes.size(); i++)
        {
            //TODO remember to support child models.
            model.createModelFromCubeInfo(group.cubes.get(i));
        }
    }

    public Object getObjectByIdent(String ident)
    {
        Object obj = null;
        for(CubeInfo inf : cubes)
        {
            if(obj == null)
            {
                obj = findObjectInCube(ident, inf);
            }
        }
        for(CubeGroup group1 : cubeGroups)
        {
            if(obj == null)
            {
                obj = findObjectInGroup(ident, group1);
            }
        }
        return obj;
    }

    public Object findObjectInCube(String ident, CubeInfo cube)
    {
        if(cube.identifier.equals(ident))
        {
            return cube;
        }
        Object obj = null;
        for(CubeInfo inf : cube.getChildren())
        {
            if(obj == null)
            {
                obj = findObjectInCube(ident, inf);
            }
        }
        return obj;
    }

    public Object findObjectInGroup(String ident, CubeGroup group)
    {
        if(group.identifier.equals(ident))
        {
            return group;
        }
        Object obj = null;
        for(CubeInfo inf : group.cubes)
        {
            if(obj == null)
            {
                obj = findObjectInCube(ident, inf);
            }
        }
        for(CubeGroup group1 : group.cubeGroups)
        {
            if(obj == null)
            {
                obj = findObjectInGroup(ident, group1);
            }
        }
        return obj;
    }

    @SideOnly(Side.CLIENT)
    public boolean importModel(ModelInfo model, boolean texture)
    {
        for(Map.Entry<String, ModelRenderer> e : model.modelList.entrySet())
        {
            ModelRenderer rend = e.getValue();

            CubeInfo firstCreated = null;
            for(int j = 0; j < rend.cubeList.size(); j++)
            {
                CubeInfo info = new CubeInfo(e.getKey() + (rend.cubeList.size() == 1 ? "" : (" - " + j)));
                ModelBox box = (ModelBox)rend.cubeList.get(j);

                info.dimensions[0] = (int)Math.abs(box.posX2 - box.posX1);
                info.dimensions[1] = (int)Math.abs(box.posY2 - box.posY1);
                info.dimensions[2] = (int)Math.abs(box.posZ2 - box.posZ1);

                info.position[0] = rend.rotationPointX;
                info.position[1] = rend.rotationPointY;
                info.position[2] = rend.rotationPointZ;

                info.offset[0] = box.posX1;
                info.offset[1] = box.posY1;
                info.offset[2] = box.posZ1;

                info.rotation[0] = Math.toDegrees(rend.rotateAngleX);
                info.rotation[1] = Math.toDegrees(rend.rotateAngleY);
                info.rotation[2] = Math.toDegrees(rend.rotateAngleZ);

                info.scale[0] = info.scale[1] = info.scale[2] = 1.0F;

                info.txMirror = rend.mirror;

                PositionTextureVertex[] vertices = box.quadList[1].vertexPositions;// left Quad, txOffsetX, txOffsetY + sizeZ
                info.txOffset[0] = (int)(vertices[info.txMirror ? 2 : 1].texturePositionX * rend.textureWidth);
                info.txOffset[1] = (int)(vertices[info.txMirror ? 2 : 1].texturePositionY * rend.textureHeight) - info.dimensions[2];

                info.mcScale = ((vertices[info.txMirror ? 1 : 2].vector3D.yCoord - vertices[info.txMirror ? 3 : 0].vector3D.yCoord) - info.dimensions[1]) / 2;

                cubeCount++;
                cubes.add(info);
                if(firstCreated == null)
                {
                    firstCreated = info;
                }
            }
            if(firstCreated != null)
            {
                createChildren(e.getKey(), rend, firstCreated);
            }
        }
        if(texture)
        {
            if(model.texture != null)
            {
                InputStream inputstream = null;
                try
                {
                    IResource iresource = Minecraft.getMinecraft().mcResourceManager.getResource(model.texture);
                    inputstream = iresource.getInputStream();
                    bufferedTexture = ImageIO.read(inputstream);

                    if(bufferedTexture != null)
                    {
                        for(Map.Entry<String, ModelRenderer> e : model.modelList.entrySet())
                        {
                            ModelRenderer rend = e.getValue();

                            textureHeight = (int)rend.textureHeight;
                            textureWidth = (int)rend.textureWidth;

                            break;
                        }
                        return true;
                    }
                }
                catch(Exception e)
                {

                }
            }
            else
            {
                bufferedTexture = null;
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void createChildren(String name, ModelRenderer rend, CubeInfo info)
    {
        if(rend.childModels == null)
        {
            return;
        }
        for(int i = 0; i < rend.childModels.size(); i++)
        {
            CubeInfo firstCreated = null;
            ModelRenderer rend1 = (ModelRenderer)rend.childModels.get(i);
            for(int j = 0; j < rend1.cubeList.size(); j++)
            {
                CubeInfo info1 = new CubeInfo(name + (rend1.cubeList.size() == 1 ? "Child" : ("Child - " + j)));
                ModelBox box = (ModelBox)rend1.cubeList.get(j);

                info1.dimensions[0] = (int)Math.abs(box.posX2 - box.posX1);
                info1.dimensions[1] = (int)Math.abs(box.posY2 - box.posY1);
                info1.dimensions[2] = (int)Math.abs(box.posZ2 - box.posZ1);

                info1.position[0] = rend1.rotationPointX;
                info1.position[1] = rend1.rotationPointY;
                info1.position[2] = rend1.rotationPointZ;

                info1.offset[0] = box.posX1;
                info1.offset[1] = box.posY1;
                info1.offset[2] = box.posZ1;

                info1.rotation[0] = Math.toDegrees(rend1.rotateAngleX);
                info1.rotation[1] = Math.toDegrees(rend1.rotateAngleY);
                info1.rotation[2] = Math.toDegrees(rend1.rotateAngleZ);

                info1.scale[0] = info1.scale[1] = info1.scale[2] = 1.0F;

                info1.txOffset[0] = rend1.textureOffsetX;
                info1.txOffset[1] = rend1.textureOffsetY;

                info1.txMirror = rend1.mirror;

                cubeCount++;
                info.addChild(info1);
                if(firstCreated == null)
                {
                    firstCreated = info1;
                }
            }
            if(firstCreated != null)
            {
                createChildren(name + "Child", rend1, firstCreated);
            }
        }
    }

    public void cloneFrom(ProjectInfo info)
    {
        this.saveFile = info.saveFile;
        this.saveFileMd5 = info.saveFileMd5;
        this.textureFile = info.textureFile;
        this.ignoreNextImage = info.ignoreNextImage;
        this.bufferedTexture = info.bufferedTexture;
        this.cameraFov = info.cameraFov;
        this.cameraZoom = info.cameraZoom;
        this.cameraYaw = info.cameraYaw;
        this.cameraPitch = info.cameraPitch;
        this.cameraOffsetX = info.cameraOffsetX;
        this.cameraOffsetY = info.cameraOffsetY;
        this.lastAutosave = info.lastAutosave;
    }

    public void inherit(ProjectInfo info)//for use of mainframe
    {
        this.cloneFrom(info);
        this.identifier = info.identifier;
        this.textureFileMd5 = info.textureFileMd5;
        this.states = info.states;
        this.lastState = info.lastState;
        this.projVersion = info.projVersion;
        this.textureWidth = info.textureWidth;
        this.textureHeight = info.textureHeight;
        this.switchState = info.switchState;
    }

    public static ProjectInfo openProject(File file)
    {
        try
        {
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();

            ZipEntry modelInfo = null;
            HashMap<String, InputStream> images = new HashMap<String, InputStream>();

            boolean tampered = false;

            while(entries.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                if(!entry.isDirectory())
                {
                    if(entry.getName().endsWith(".png") && entry.getCrc() != Long.decode("0xf970c898"))
                    {
                        images.put(entry.getName(), zipFile.getInputStream(entry));
                    }
                    if(entry.getName().endsWith(".xml") || entry.getName().endsWith(".json"))
                    {
                        modelInfo = entry;
                    }
                    if(!entry.getName().endsWith(".png") && !entry.getName().endsWith(".xml") && !entry.getName().endsWith(".json"))
                    {
                        tampered = true;
                    }
                }
            }

            ProjectInfo info = null;
            if(modelInfo != null)
            {
                if(modelInfo.getName().endsWith(".xml"))
                {
                    info = convertTechneFile(TC2Info.convertTechneFile(zipFile.getInputStream(modelInfo), images), file.getName());
                }
                else
                {
                    info = readModelFile(zipFile.getInputStream(modelInfo), images, file.getName());
                }
            }

            zipFile.close();

            if(tampered)
            {
                iChunUtil.console(file.getName() + " is a tampered model file.", true);
            }

            return info;
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            return null;
        }
    }

    public static ProjectInfo convertTechneFile(TC2Info tc2Info, String fileName)
    {
        if(tc2Info == null)
        {
            return null;
        }

        ProjectInfo project = new ProjectInfo(fileName, "TechneToTabulaImporter");

        project.projVersion = 1;
        if(!tc2Info.Techne.Author.equals("ZeuX") && !tc2Info.Techne.Author.equals("NotZeux"))
        {
            project.authorName = tc2Info.Techne.Author;
        }

        float scaleX = 1.0F;
        float scaleY = 1.0F;
        float scaleZ = 1.0F;

        for(TC2Info.Model model : tc2Info.Techne.Models)
        {
            try
            {
                String[] textureSize = model.Model.TextureSize.split(",");
                project.textureWidth = Integer.parseInt(textureSize[0]);
                project.textureHeight = Integer.parseInt(textureSize[1]);

                if(project.bufferedTexture == null)
                {
                    project.bufferedTexture = model.Model.image;
                }

                String[] scale = model.Model.GlScale.split(",");
                scaleX = Float.parseFloat(scale[0]);
                scaleY = Float.parseFloat(scale[1]);
                scaleZ = Float.parseFloat(scale[2]);

                boolean degrees = determineAngleType(model.Model.Geometry);
                project.cubeCount += exploreTC2Info(project.cubes, project.cubeGroups, model.Model.Geometry, 0, 0, 0, 0, 0, 0, scaleX, scaleY, scaleZ, degrees);
                //TODO support groups
            }
            catch(NumberFormatException e)
            {
                iChunUtil.console("Error parsing Techne 2 model for Tabula: Invalid number", true);
                e.printStackTrace();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                iChunUtil.console("Error parsing Techne 2 model for Tabula: Array too short", true);
            }
        }

        return project;
    }

    private static boolean determineAngleType(TC2Info.Group geometry)
    {
        boolean degrees = false;
        if(geometry.Shape != null)
        {
            for(TC2Info.Shape shape : geometry.Shape)
            {
                String[] rot = shape.Rotation.split(",");
                double rX = Float.parseFloat(rot[0]);
                double rY = Float.parseFloat(rot[1]);
                double rZ = Float.parseFloat(rot[2]);

                if(rX < -Math.PI || rX > Math.PI || rY < -Math.PI || rY > Math.PI || rZ < -Math.PI || rZ > Math.PI)
                {
                    degrees = true;
                    break;
                }
            }

        }
        if(geometry.Null != null && !degrees)
        {
            for(TC2Info.Null nul : geometry.Null)
            {
                boolean degInner = determineAngleType(nul.Children);
                if(degInner)
                {
                    degrees = degInner;
                    break;
                }
            }
        }
        return degrees;
    }

    private static int exploreTC2Info(ArrayList<CubeInfo> cubes, ArrayList<CubeGroup> groups, TC2Info.Group geometry, double posX, double posY, double posZ, double rotX, double rotY, double rotZ, float scaleX, float scaleY, float scaleZ, boolean isDegrees)
    {
        int cubeCount = 0;
        if(geometry.Shape != null)
        {
            for(TC2Info.Shape shape : geometry.Shape)
            {
                CubeInfo info = new CubeInfo(shape.Name == null ? "shape" + Integer.toString(cubeCount + 1) : shape.Name);

                String[] textureOffset = shape.TextureOffset.split(",");
                info.txOffset = new int[] { Integer.parseInt(textureOffset[0]), Integer.parseInt(textureOffset[1]) };
                info.txMirror = !shape.IsMirrored.equals("False");

                String[] pos = shape.Position.split(",");
                String[] rot = shape.Rotation.split(",");
                String[] size = shape.Size.split(",");
                String[] offset = new String[] { "0", "0", "0" }; //Techne 2 files by default lack the Offset field.
                if(shape.Offset != null)
                {
                    offset = shape.Offset.split(",");
                }
                info.position = new double[] { Float.parseFloat(pos[0]) + posX, Float.parseFloat(pos[1]) + posY, Float.parseFloat(pos[2]) + posZ };
                info.offset = new double[] { Float.parseFloat(offset[0]), Float.parseFloat(offset[1]), Float.parseFloat(offset[2]) };
                info.dimensions = new int[] { Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]) };

                double rX = Float.parseFloat(rot[0]) + rotX;
                double rY = Float.parseFloat(rot[1]) + rotY;
                double rZ = Float.parseFloat(rot[2]) + rotZ;

                TechneConverter.Rotation rotation;
                if(isDegrees)
                {
                    rotation = TechneConverter.fromTechne(TechneConverter.Rotation.createFromDegrees(rX, rY, rZ));
                }
                else
                {
                    rotation = TechneConverter.fromTechne(TechneConverter.Rotation.createFromRadians(rX, rY, rZ));
                }
                info.rotation = new double[] { rotation.getDegreesX(), rotation.getDegreesY(), rotation.getDegreesZ() };
                info.scale = new double[] { scaleX, scaleY, scaleZ };

                cubes.add(info);

                cubeCount++;
            }
        }
        if(geometry.Null != null)
        {
            for(TC2Info.Null nul : geometry.Null)
            {
                CubeGroup group = new CubeGroup(nul.Name);
                groups.add(group);
                String[] pos = nul.Position.split(",");
                String[] rot = nul.Rotation.split(",");
                cubeCount += exploreTC2Info(group.cubes, group.cubeGroups, nul.Children, Float.parseFloat(pos[0]), Float.parseFloat(pos[1]), Float.parseFloat(pos[2]), Float.parseFloat(rot[0]), Float.parseFloat(rot[1]), Float.parseFloat(rot[2]), scaleX, scaleY, scaleZ, isDegrees);
            }
        }
        return cubeCount;
    }

    public static ProjectInfo readModelFile(InputStream json, HashMap<String, InputStream> images, String fileName) throws IOException
    {
        if(json == null || images == null)
        {
            return null;
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(json, writer);
        String jsonString = writer.toString();

        ProjectInfo project = null;

        if(jsonString.contains("Techne") || jsonString.contains("techne"))
        {
            TC2Info info;

            try
            {
                info = (new Gson()).fromJson(jsonString, TC2Info.class);
            }
            catch(JsonSyntaxException e1)
            {
                info = (new Gson()).fromJson(jsonString.replaceAll("\u0000", ""), TC1Json.class).toTC2Info();
            }
            if(info != null)
            {
                for(TC2Info.Model model : info.Techne.Models)
                {
                    InputStream stream = images.get(model.Model.texture);
                    if(stream != null)
                    {
                        model.Model.image = ImageIO.read(stream);
                        stream.close();
                    }
                }

                project = convertTechneFile(info, fileName);
            }
        }
        else
        {
            project = (new Gson()).fromJson(jsonString, ProjectInfo.class);

            InputStream stream = images.get("texture.png");
            if(stream != null)
            {
                project.bufferedTexture = ImageIO.read(stream);
                stream.close();
            }
        }

        json.close();

        for(Map.Entry<String, InputStream> img : images.entrySet())
        {
            img.getValue().close();
        }
        return project;
    }

    public static boolean saveProject(ProjectInfo info, File file)
    {
        try
        {
            file.getParentFile().mkdirs();

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            out.setLevel(9);
            out.putNextEntry(new ZipEntry("model.json"));

            byte[] data = (new Gson()).toJson(info).getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            if(info.bufferedTexture != null)
            {
                out.putNextEntry(new ZipEntry("texture.png"));
                ImageIO.write(info.bufferedTexture, "png", out);
            }
            out.closeEntry();

            out.close();

            info.saved = true;
            return true;
        }
        catch(Exception e)
        {
            iChunUtil.console("Failed to save model: " + info.modelName, true);
            e.printStackTrace();
            return false;
        }
    }
}
