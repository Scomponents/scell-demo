package org.example.scelldemo.controls.helper;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;

public class FsXlsxWatcher {
    public static void watchDirectory(Path path, Consumer<Path> newFileHandler) {
        // Sanity check - Check if path is a folder
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path, "basic:isDirectory", LinkOption.NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path + " is not a folder");
            }
        } catch (IOException ioe) {
            // Folder does not exists
            ioe.printStackTrace();
        }

        System.out.println("Watching path: " + path);

        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem();

        // We create the new WatchService using the new try() block
        try (WatchService service = fs.newWatchService()) {

            // We register the path to the service
            // We watch for creation events
            path.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

            // Start the infinite polling loop
            WatchKey key = null;
            while (true) {
                key = service.take();

                // Dequeueing events
                WatchEvent.Kind<?> kind = null;
                List<WatchEvent<?>> events = key.pollEvents();
                System.out.println("NUMBER OF EVENTS: " + events.size());
                for (WatchEvent<?> watchEvent : events) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    if (StandardWatchEventKinds.OVERFLOW == kind) {
                        continue; // loop
                    } else if (StandardWatchEventKinds.ENTRY_CREATE == kind) {
                        // A new Path was created
                        Path newPath = path.resolve(((WatchEvent<Path>) watchEvent).context());
                        // Output
                        System.out.println("New path created: " + newPath);
                        if (newPath.toFile().getPath().endsWith(".xlsx")) {
                            System.out.println("RUN " + newPath);
                            newFileHandler.accept(newPath);
                        }
                    } else if (StandardWatchEventKinds.ENTRY_MODIFY == kind) {
                        // modified
                        Path newPath = path.resolve(((WatchEvent<Path>) watchEvent).context());
                        // Output
                        System.out.println("New path modified: " + newPath);
                        if (newPath.toFile().getPath().endsWith(".xlsx")) {
                            System.out.println("RUN " + newPath);
                            newFileHandler.accept(newPath);
                        }
                    }
                }

                if (!key.reset()) {
                    break; // loop
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }
}
