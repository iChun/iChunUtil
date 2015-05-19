package us.ichun.mods.ichunutil.common.core;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class Logger
{
    public final String modId;

    private Logger(String id)
    {
        this.modId = id;
    }

    public void log(Level logLevel, Object msg)
    {
        FMLLog.log(modId, logLevel, String.valueOf(msg));
    }

    public void warn(Object msg)
    {
        log(Level.WARN, msg);
    }

    public void info(Object msg)
    {
        log(Level.INFO, msg);
    }

    public void error(Object msg)
    {
        log(Level.ERROR, msg);
    }

    public static Logger createLogger(String modId)
    {
        return new Logger(modId);
    }
}
