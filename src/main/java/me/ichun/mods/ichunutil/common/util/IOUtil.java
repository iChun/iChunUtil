package me.ichun.mods.ichunutil.common.util;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.texture.NativeImage;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IOUtil
{
    public static final int IDENTIFIER_LENGTH = 20; //Typical string length for an identifier.

    public static boolean areNativeImagesEqual(NativeImage img1, NativeImage img2)
    {
        if(img1 == null && img2 == null)
        {
            return true;
        }
        else if(img1 == null || img2 == null)
        {
            return false;
        }
        else if(img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight())
        {
            for(int x = 0; x < img1.getWidth(); x++)
            {
                for(int y = 0; y < img1.getHeight(); y++)
                {
                    if(img1.getPixelRGBA(x, y) != img2.getPixelRGBA(x, y))
                    {
                        return false;
                    }
                }
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    public static void renameFilesToLowerCaseInDir(File dir)
    {
        File[] files = dir.listFiles();
        for(File file : files)
        {
            if(file.isDirectory())
            {
                renameFilesToLowerCaseInDir(file);
            }
            else if(!file.getName().equals(file.getName().toLowerCase(Locale.ROOT)))
            {
                String name = file.getName().toLowerCase();
                File newFile = new File(dir, name);
                if(file.renameTo(newFile))
                {
                    iChunUtil.LOGGER.info("Renaming {} to {}", file.getAbsolutePath(), newFile.getAbsolutePath());
                }
                else
                {
                    iChunUtil.LOGGER.error("Failed to rename {} to {}", file.getAbsolutePath(), newFile.getAbsolutePath());
                }
            }
        }
    }

    public static int extractFiles(@Nonnull Path dir, @Nonnull InputStream inputStream, boolean overwrite) throws IOException
    {
        int i = 0;
        try(ZipInputStream zipStream = new ZipInputStream(inputStream))
        {
            ZipEntry entry;

            while((entry = zipStream.getNextEntry()) != null)
            {
                Path path = dir.resolve(entry.getName());
                if(!overwrite && Files.exists(path) && Files.size(path) > 3L) //check if there are at least some bytes written so we know the file isn't empty
                {
                    continue;
                }

                if(entry.isDirectory())
                {
                    if(!Files.exists(path))
                    {
                        Files.createDirectories(path);
                    }
                }
                else
                {
                    try(OutputStream out = Files.newOutputStream(path))
                    {
                        byte[] buffer = new byte[8192];
                        int len;
                        while((len = zipStream.read(buffer)) != -1)
                        {
                            out.write(buffer, 0, len);
                        }

                        i++;
                    }
                }
            }
        }
        return i;
    }

    //function return true if the file is processed.
    public static int scourDirectoryForFiles(Path path, Function<Path, Boolean> fileFunction) throws IOException
    {
        if(!Files.exists(path))
        {
            Files.createDirectories(path);
        }

        int count = 0;
        List<Path> files = Files.list(path).collect(Collectors.toList());

        for(Path p : files)
        {
            if(Files.isDirectory(p))
            {
                count += scourDirectoryForFiles(p, fileFunction);
            }
            else if(fileFunction.apply(p))
            {
                count++;
            }
        }
        return count;
    }

    /*
     * Taken from http://www.rgagnon.com/javadetails/java-0416.html
     * and http://www.rgagnon.com/javadetails/java-0596.html
     * Modified for use. Thanks!
     */
    static final byte[] HEX_CHAR_TABLE = { (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6', (byte)'7', (byte)'8', (byte)'9', (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f' };

    private static byte[] createChecksum(File filename) throws Exception
    {
        InputStream fis = new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do
        {
            numRead = fis.read(buffer);
            if(numRead > 0)
            {
                complete.update(buffer, 0, numRead);
            }
        }
        while(numRead != -1);
        fis.close();
        return complete.digest();
    }

    public static String getMD5Checksum(File file)
    {
        try
        {
            byte[] b = createChecksum(file);
            String hex = getHexString(b);
            return hex;
        }
        catch(Exception e)
        {
            iChunUtil.LOGGER.warn("Failed to generate MD5 checksum for {}", file.getName());
            return null;
        }
    }

    private static String getHexString(byte[] raw) throws UnsupportedEncodingException
    {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for(byte b : raw)
        {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "ASCII");
    }

    public static String readableFileSize(long size)
    {
        if(size <= 0)
        {
            return "0";
        }
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int)(Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    //compresses a string using gzip
    public static byte[] compress(String string) throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    //decompressed a byte array to a string using gzip
    public static String decompress(byte[] compressed) throws IOException
    {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while((bytesRead = gis.read(data)) != -1)
        {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }
}
