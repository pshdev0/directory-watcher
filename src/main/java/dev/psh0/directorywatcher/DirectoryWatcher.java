package dev.psh0.directorywatcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

public class DirectoryWatcher {

    boolean poll;
    WatchService watchService;
    Thread scanThread;

    public DirectoryWatcher(Path path,
                            Consumer<String> createFunc,
                            Consumer<String> modifyFunc,
                            Consumer<String> deleteFunc,
                            Runnable defaultFunc) throws IOException {
        poll = true;
        watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

        Runnable run = () -> {
            boolean poll = true;

            try {
                while (poll) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {

                        String fileName = event.context().toString();

                        if (createFunc != null && event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                            createFunc.accept(fileName);
                        }
                        else if(modifyFunc != null && event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                            modifyFunc.accept(fileName);
                        }
                        else if (deleteFunc != null && event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                            deleteFunc.accept(fileName);
                        }
                        else if(deleteFunc != null) {
                            defaultFunc.run();
                        }
                    }
                    poll = key.reset();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        scanThread = new Thread(run);
        scanThread.start();
    }
    
    public void stop() {
        poll = false;
    }
}
