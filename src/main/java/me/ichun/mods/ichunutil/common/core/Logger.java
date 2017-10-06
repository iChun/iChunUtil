package me.ichun.mods.ichunutil.common.core;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class Logger
{
    public final String modName;
    public final org.apache.logging.log4j.Logger logger;

    private Logger(String id)
    {
        this.modName = id;
        this.logger = LogManager.getLogger(modName);
    }

    public void log(Level logLevel, String format, Object... msg)
    {
        logger.log(logLevel, String.format(format, msg));
    }

    public void log(Level logLevel, Object msg)
    {
        log(logLevel, "%s", msg);
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
