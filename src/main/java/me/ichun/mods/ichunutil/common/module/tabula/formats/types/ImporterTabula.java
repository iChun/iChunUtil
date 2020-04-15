package me.ichun.mods.ichunutil.common.module.tabula.formats.types;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.legacy.importer.ImportTabulaLegacy;
import me.ichun.mods.ichunutil.common.module.tabula.legacy.project.ProjectInfo;
import me.ichun.mods.ichunutil.common.module.tabula.project.Identifiable;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ImporterTabula
        implements Importer
{
    @Override
    public Project createProject(File file)
    {
        try
        {
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();

            ZipEntry modelInfo = null;
            InputStream image = null;

            boolean tampered = false;

            while(entries.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                if(!entry.isDirectory())
                {
                    if(entry.getName().endsWith(".png") && entry.getCrc() != Long.decode("0xf970c898")) //is this Techne legacy? 1x1 white pixel was it?
                    {
                        image = zipFile.getInputStream(entry);
                    }
                    if(entry.getName().endsWith(".json"))
                    {
                        modelInfo = entry;
                    }
                    if(!(entry.getName().endsWith(".png") || entry.getName().endsWith(".json")))
                    {
                        tampered = true;
                    }
                }
            }

            Project info = null;
            if(modelInfo != null)
            {
                info = readModelFile(zipFile.getInputStream(modelInfo), image);
            }

            zipFile.close();

            if(info == null || info.projVersion <= 4) //somehow our file is still null. Let's try piping it into our Legacy loader.
            {
                info = convertProjectInfo(ImportTabulaLegacy.createProjectInfo(file));
            }
            else if(tampered)
            {
                info.tampered = true;
                iChunUtil.LOGGER.warn("{} is a tampered Tabula model file.", file.getName());
            }
            return info;
        }
        catch (Exception e1)
        {
            iChunUtil.LOGGER.warn("Something went wrong loading Tabula file: {}", file.getName());
            e1.printStackTrace();
            return null;
        }
    }

    @Override
    public int getProjectVersion()
    {
        return Project.PROJ_VERSION;
    }

    public static Project readModelFile(InputStream json, InputStream image) throws IOException
    {
        if(json == null)
        {
            return null;
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(json, writer, StandardCharsets.UTF_8);
        json.close();

        String jsonString = writer.toString();
        Project project = Project.SIMPLE_GSON.fromJson(jsonString, Project.class);

        if(image != null)
        {
            project.setBufferedTexture(ImageIO.read(image));
            image.close();
        }

        return project;
    }

    //TODO announce to user if they load this kind of file
    public static Project convertProjectInfo(ProjectInfo old)
    {
        if(old == null)
        {
            return null;
        }

        old.repair(); //put us at the latest version, 4.

        Project project = new Project();
        project.isOldTabula = true; //TODO warn.
        project.name = old.modelName;
        project.author = old.authorName;
        project.notes.addAll(old.metadata);
        project.texWidth = old.textureWidth;
        project.texHeight = old.textureHeight;
        //TODO scale is not supported

        //TODO handle cube Groups

        addParts(old.cubes, project.parts, project);

        //TODO handle Anims

        project.partCountProjectLife = old.cubeCount;
        project.setBufferedTexture(old.bufferedTexture);

        return project;
    }

    public static void addParts(ArrayList<ProjectInfo.CubeInfo> cubes, ArrayList<Project.Part> parts, Identifiable<?> parent)
    {
        cubes.forEach(cube -> {
            Project.Part part = new Project.Part(parent, 0);
            part.name = cube.name;
            Project.Part.Box box = part.boxes.get(0);
            box.dimX = cube.dimensions[0];
            box.dimY = cube.dimensions[1];
            box.dimZ = cube.dimensions[2];

            part.rotPX = (float)cube.position[0];
            part.rotPY = (float)cube.position[1];
            part.rotPZ = (float)cube.position[2];

            box.posX = (float)cube.offset[0];
            box.posY = (float)cube.offset[1];
            box.posZ = (float)cube.offset[2];

            part.rotAX = (float)cube.rotation[0];
            part.rotAY = (float)cube.rotation[1];
            part.rotAZ = (float)cube.rotation[2];

            //TODO scale

            part.texOffX = cube.txOffset[0];
            part.texOffY = cube.txOffset[1];

            part.mirror = cube.txMirror;
            box.expandX = box.expandY = box.expandZ = (float)cube.mcScale;

            //TODO opacity

            part.showModel = !cube.hidden;

            part.notes.addAll(cube.metadata);

            addParts(cube.children, part.children, part);

            part.identifier = cube.identifier;

            parts.add(part);
        });
    }
}
