package me.ichun.mods.ichunutil.common.util;

import me.ichun.mods.ichunutil.common.iChunUtil;

import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

//Modified from https://stackoverflow.com/questions/16251273/can-i-watch-for-single-file-change-with-watchservice-not-the-whole-directory
public class WatchServiceThread extends Thread
{
    private static final Map<Path, WatchServiceThread> WATCH_SERVICES = Collections.synchronizedMap(new HashMap<>());

    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(WatchServiceThread::terminateWatchServices));
    }

    private final AtomicBoolean stop = new AtomicBoolean(false);

    private final Path watchDir;

    private final Map<String, Consumer<String>> toWatch = Collections.synchronizedMap(new HashMap<>());

    private long sleepTime = 1000L; //initially 50... but do we really need to watch the item that often?

    public WatchServiceThread(Path path)
    {
        this.setName("iChunUtil File Watcher Service");
        this.setDaemon(true);
        this.watchDir = path;
    }

    public WatchServiceThread setSleepTime(long l)
    {
        sleepTime = Math.min(l, sleepTime);
        return this;
    }

    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    public void addFileToWatch(String s, Consumer<String> onChange)
    {
        toWatch.put(s, onChange);
    }

    public void removeFileToWatch(String s)
    {
        toWatch.remove(s);
        if(toWatch.isEmpty())
        {
            stopThread();
            WATCH_SERVICES.remove(watchDir);
        }
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            watchDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                try { key = watcher.poll(25, TimeUnit.MILLISECONDS); }
                catch (InterruptedException e) { return; }
                if (key == null) { Thread.sleep(100); continue; }

                Thread.sleep(100); //done to prevent double trigger of "file changed"

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        Thread.yield();
                        continue;
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY
                        && toWatch.containsKey(filename.toString())) {
                        toWatch.get(filename.toString()).accept(filename.toString());
                    }
                    boolean valid = key.reset();
                    if (!valid) { break; }
                }
                Thread.sleep(sleepTime);
            }
        } catch (Throwable e) {
            iChunUtil.LOGGER.error("Error with watch service", e);
        }
    }

    public static WatchServiceThread watchFile(Path file, Consumer<String> onChanged)
    {
        WatchServiceThread watchServiceThread = WATCH_SERVICES.computeIfAbsent(file.getParent(), k -> {
            WatchServiceThread thread = new WatchServiceThread(file.getParent());
            thread.start();
            return thread;
        });
        watchServiceThread.addFileToWatch(file.getFileName().toString(), onChanged);
        return watchServiceThread;
    }

    public static void stopWatchFile(Path file)
    {
        if(WATCH_SERVICES.containsKey(file.getParent()))
        {
            WatchServiceThread thread = WATCH_SERVICES.get(file.getParent());
            thread.removeFileToWatch(file.getFileName().toString());
        }
    }

    public static void stopWatchFolder(Path folder)
    {
        WatchServiceThread watchServiceThread = WATCH_SERVICES.get(folder);
        if(watchServiceThread != null)
        {
            watchServiceThread.stopThread();
            WATCH_SERVICES.remove(folder);
        }
    }

    private static void terminateWatchServices()
    {
        WATCH_SERVICES.forEach((k, v) -> v.stopThread());
        WATCH_SERVICES.clear();
    }
}
