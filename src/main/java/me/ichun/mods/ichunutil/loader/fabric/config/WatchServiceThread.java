package me.ichun.mods.ichunutil.loader.fabric.config;

import me.ichun.mods.ichunutil.common.iChunUtil;

import java.nio.file.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

//Modified from https://stackoverflow.com/questions/16251273/can-i-watch-for-single-file-change-with-watchservice-not-the-whole-directory
public class WatchServiceThread extends Thread
{
    private final AtomicBoolean stop = new AtomicBoolean(false);

    private final Path watchDir;
    private final Consumer<String> onChange;

    private final Set<String> toWatch = Collections.synchronizedSet(new HashSet<>());

    public WatchServiceThread(Path path, Consumer<String> onChange)
    {
        this.setName("iChunUtil Config File Watcher Service");
        this.setDaemon(true);
        this.watchDir = path;
        this.onChange = onChange;
    }

    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    public void addFileToWatch(String s)
    {
        synchronized(toWatch)
        {
            toWatch.add(s);
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
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
                            && toWatch.contains(filename.toString())) {
                        onChange.accept(filename.toString());
                    }
                    boolean valid = key.reset();
                    if (!valid) { break; }
                }
                Thread.sleep(50);
            }
        } catch (Throwable e) {
            iChunUtil.LOGGER.error("Error with watch service", e);
        }
    }

}
