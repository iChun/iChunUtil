package ichun.common.core.util;

import java.awt.image.BufferedImage;

public class IOUtil
{
    public static boolean areBufferedImagesEqual(BufferedImage img1, BufferedImage img2)
    {
        if(img1 == null && img2 == null)
        {
            return true;
        }
        else if(img1 == null || img2 == null)
        {
            return false;
        }
        else if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight())
        {
            for (int x = 0; x < img1.getWidth(); x++)
            {
                for (int y = 0; y < img1.getHeight(); y++)
                {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
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
}
