package ichun.common.core.techne;

import com.google.gson.annotations.SerializedName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Deserialized version of Techne 2's JSON save files.
 */
public class TC2Info
{
    private BufferedImage image;
    public Techne Techne = new Techne();

    private class Techne
    {
        @SerializedName("@Version")
        String Version = "2.2";
        String Author = "NotZeuX";
        String Name = "";
        String PreviewImage = "";
        String ProjectName = "";
        String ProjectType = "";
        String Description = "";
        String DateCreated = "";
        Model[] Models = new Model[] { };
    }

    public class Model
    {
        ModelInfo Model = new ModelInfo();
    }

    public class ModelInfo
    {
        String GlScale = "1,1,1";
        String Name = "";
        String TextureSize = "64,32";
        @SerializedName("@texture")
        String texture = "texture.png";
        String BaseClass = "ModelBase";
        Group Geometry = new Group();
    }

    public class Group
    {
        Circular[] Circular = new Circular[] {};
        Shape[] Shape = new Shape[] {};
        Linear[] Linear = new Linear[] {};
        Null[] Null = new Null[] {};
    }

    public class Circular
    {
        @SerializedName("@Type")
        String Type = "16932820-ef7c-4b4b-bf05-b72063b3d23c";
        @SerializedName("@Name")
        String Name = "Circular Array";
        String Position = "0,0,0";
        String Rotation = "0,0,0";
        Group Children = new Group();
        int Count = 5;
        int Radius = 16;
    }

    public class Shape
    {
        int Id = 1; //is a variable
        @SerializedName("@Type")
        String Type = "d9e621f7-957f-4b77-b1ae-20dcd0da7751";
        @SerializedName("@Name")
        String Name = "new cube";
        String IsDecorative = "False";
        String IsFixed = "False";
        String IsMirrored = "False";
        String Position = "0,0,0";
        String Rotation = "0,0,0"; //TODO is in radians. Be sure to convert accordingly
        String Size = "1,1,1";
        String TextureOffset = "0,0";
    }

    public class Linear
    {
        @SerializedName("@Type")
        String Type = "fc4f63c9-8296-4c97-abd8-414f20e49bd5";
        @SerializedName("@Name")
        String Name = "Linear Array";
        String Position = "0,0,0";
        String Rotation = "0,0,0";
        Group Children = new Group();
        String Count = "0,0,0";
        String Spacing = "0,0,0";
    }

    public class Null
    {
        @SerializedName("@Type")
        String Type = "3b3bb6e5-2f8b-4bbd-8dbb-478b67762fd0";
        @SerializedName("@Name")
        String Name = "null element";
        String Position = "0,0,0";
        String Rotation = "0,0,0";
        Group Children = new Group();
    }

    public static TC2Info convertTechneFile(File file)
    {
        try
        {
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();

            ZipEntry xml = null;
            ZipEntry png = null;

            while(entries.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                if(!entry.isDirectory())
                {
                    if(entry.getName().endsWith(".png"))
                    {
                        png = entry;
                    }
                    if(entry.getName().endsWith(".xml"))
                    {
                        xml = entry;
                    }
                }
            }

            zipFile.close();

            return convertTechneFile(zipFile.getInputStream(xml), zipFile.getInputStream(png));
        }
        catch (Exception e1)
        {
            return null;
        }
    }

    public static TC2Info convertTechneFile(ZipInputStream stream)
    {
        try
        {
            ZipInputStream cloneXML = new ZipInputStream(stream);
            ZipInputStream clonePNG = new ZipInputStream(stream);
            stream.close();

            ZipEntry entry = null;

            boolean hasXML = false;
            while((entry = cloneXML.getNextEntry()) != null)
            {
                if(!entry.isDirectory() && entry.getName().endsWith(".xml"))
                {
                    hasXML = true;
                }
            }

            entry = null;

            boolean hasPNG = false;
            while((entry = clonePNG.getNextEntry()) != null)
            {
                if(!entry.isDirectory() && entry.getName().endsWith(".png"))
                {
                    hasPNG = true;
                }
            }

            if(hasXML && hasPNG)
            {
                return convertTechneFile(cloneXML, clonePNG);
            }
            return null;
        }
        catch(Exception e1)
        {
            return null;
        }
    }

    public static TC2Info convertTechneFile(InputStream xml, InputStream png) throws IOException, ParserConfigurationException, SAXException
    {
        if(xml == null || png == null)
        {
            return null;
        }

        TC2Info info = new TC2Info();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document doc = builder.parse(xml);

        info.Techne.Version     = doc.getElementsByTagName("Techne").item(0).getAttributes().item(0).getNodeValue();
        info.Techne.Author      = doc.getElementsByTagName("Techne").item(0).getChildNodes().item(0).getNodeValue().equals("ZeuX") ? "NotZeux" : doc.getElementsByTagName("Techne").item(0).getChildNodes().item(0).getNodeValue();
        info.Techne.Name        = doc.getElementsByTagName("Name").item(0).getChildNodes().item(0).getNodeValue();
        info.Techne.PreviewImage= doc.getElementsByTagName("PreviewImage").item(0).getChildNodes().item(0).getNodeValue();
        info.Techne.ProjectName = doc.getElementsByTagName("ProjectName").item(0).getChildNodes().item(0).getNodeValue();
        info.Techne.ProjectType = doc.getElementsByTagName("ProjectType").item(0).getChildNodes().item(0).getNodeValue();
        info.Techne.Description = doc.getElementsByTagName("Description").item(0).getChildNodes().item(0).getNodeValue();
        info.Techne.DateCreated = doc.getElementsByTagName("DateCreated").item(0).getChildNodes().item(0).getNodeValue();

        NodeList list = doc.getElementsByTagName("Model");

        info.Techne.Models = new Model[list.getLength()];

        for(int i = 0; i < list.getLength(); i++)
        {
            info.Techne.Models[i] = new Model();
            Node node = list.item(i);

            for(int j = 0; j < node.getAttributes().getLength(); j++)
            {
                Node attribute = node.getAttributes().item(j);

                if(attribute.getNodeName().equalsIgnoreCase("texture") && (attribute.getNodeValue().equalsIgnoreCase("d9e621f7-957f-4b77-b1ae-20dcd0da7751") || attribute.getNodeValue().equalsIgnoreCase("de81aa14-bd60-4228-8d8d-5238bcd3caaa")))
                {
                }
            }
        }


                    info.image = ImageIO.read(png);

        xml.close();
        png.close();

        return info;
    }
}
