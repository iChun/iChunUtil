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
    private transient BufferedImage image;
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

        public void createNewModelArray(int size)
        {
            Models = new Model[size];
            for(int i = 0; i < Models.length; i++)
            {
                Models[i] = new Model();
            }
        }
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

        public void createNewShapeArray(int size)
        {
            Shape = new Shape[size];
        }
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

        info.Techne.createNewModelArray(list.getLength());

        int id = 1;

        for(int i = 0; i < list.getLength(); i++)
        {
            Model model = info.Techne.Models[i];

            Node node = list.item(i);

            for(int j = 0; j < node.getAttributes().getLength(); j++)
            {
                Node attribute = node.getAttributes().item(j);

                if(attribute.getNodeName().equals("texture"))
                {
                    model.Model.texture = attribute.getNodeValue();
                }
            }

            int shapeCount = 0;
            for(int k = 0; k < node.getChildNodes().getLength(); k++)
            {
                Node child = node.getChildNodes().item(k);
                if(child.getNodeName().equals("GlScale"))
                {
                    model.Model.GlScale = child.getTextContent();
                }
                else if(child.getNodeName().equals("Name"))
                {
                    model.Model.Name = child.getTextContent();
                }
                else if(child.getNodeName().equals("TextureSize"))
                {
                    model.Model.TextureSize = child.getTextContent();
                }
                else if(child.getNodeName().equals("BaseClass"))
                {
                    model.Model.BaseClass = child.getTextContent();
                }
                else if(child.getNodeName().equals("Shape"))
                {
                    for(int j = 0; j < child.getAttributes().getLength(); j++)
                    {
                        Node attribute = node.getAttributes().item(j);
                        if(attribute.getNodeName().equalsIgnoreCase("type") && (attribute.getNodeValue().equalsIgnoreCase("d9e621f7-957f-4b77-b1ae-20dcd0da7751") || attribute.getNodeValue().equalsIgnoreCase("de81aa14-bd60-4228-8d8d-5238bcd3caaa")))
                        {
                            shapeCount++;
                        }
                    }
                }
            }

            model.Model.Geometry.createNewShapeArray(shapeCount);

            int shapeNum = 0;
            for(int k = 0; k < node.getChildNodes().getLength(); k++)
            {
                Node child = node.getChildNodes().item(k);
                if(child.getNodeName().equals("Shape"))
                {
                    for(int j = 0; j < child.getAttributes().getLength(); j++)
                    {
                        Node attribute = node.getAttributes().item(j);
                        if(attribute.getNodeName().equalsIgnoreCase("type") && (attribute.getNodeValue().equalsIgnoreCase("d9e621f7-957f-4b77-b1ae-20dcd0da7751") || attribute.getNodeValue().equalsIgnoreCase("de81aa14-bd60-4228-8d8d-5238bcd3caaa")) && shapeNum < shapeCount)
                        {
                            Shape shape = model.Model.Geometry.Shape[shapeNum];
                            shape.Id = id++;

                            for(int jj = 0; jj < child.getAttributes().getLength(); jj++)
                            {
                                Node attribute1 = node.getAttributes().item(jj);
                                if(attribute1.getNodeName().equalsIgnoreCase("name"))
                                {
                                    shape.Name = attribute1.getNodeValue();
                                    break;
                                }
                            }

                            for(int kk = 0; kk < node.getChildNodes().getLength(); kk++)
                            {
                                Node child1 = node.getChildNodes().item(kk);
                                if(child1.getNodeName().equals("IsDecorative"))
                                {
                                    shape.IsDecorative = child1.getTextContent();
                                }
                                else if(child1.getNodeName().equals("IsFixed"))
                                {
                                    shape.IsFixed = child1.getTextContent();
                                }
                                else if(child1.getNodeName().equals("IsMirrored"))
                                {
                                    shape.IsMirrored = child1.getTextContent();
                                }
                                else if(child1.getNodeName().equals("Position"))
                                {
                                    shape.Position = child1.getTextContent();
                                }
                                else if(child1.getNodeName().equals("Rotation"))
                                {
                                    shape.Rotation = child1.getTextContent();
                                }
                                else if(child1.getNodeName().equals("Size"))
                                {
                                    shape.Size = child1.getTextContent();
                                }
                                else if(child1.getNodeName().equals("TextureOffset"))
                                {
                                    shape.TextureOffset = child1.getTextContent();
                                }
                            }
                            shapeNum++;
                        }
                    }
                }
            }
        }

        info.image = ImageIO.read(png);

        xml.close();
        png.close();

        return info;
    }
}
