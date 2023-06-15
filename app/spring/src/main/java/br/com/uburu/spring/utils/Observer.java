/**
 *  @file Observer.java
 *  @author Dheovani Xavier da Cruz
 *
 *  Copyright 2023, Dheovani Xavier da Cruz.  All rights reserved.
 *  https://github.com/Dheovani/Uburu
 *  Use of this source code is governed by a MIT license
 *  that can be found in the License file.
 *
 *  Uburu
 */

package br.com.uburu.spring.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.WatchEvent.Kind;

public final class Observer {

    private Observer observer;
    private Indexer indexer;
    private Map<WatchKey, Path> keyMap;
    private final String ENTRY_CREATE = "ENTRY_CREATE";
    private final String ENTRY_DELETE = "ENTRY_DELETE";
    private final String ENTRY_MODIFY = "ENTRY_MODIFY";
    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    private Observer() {
        indexer = new Indexer();
        keyMap = new HashMap<>();
    }

    /**
     * Função que observa os diretórios e reindexa eles caso necessário
     */
    public void watch() {
        try (WatchService service = FileSystems.getDefault().newWatchService()) {
            WatchKey watchKey;

            do {
                watchKey = service.take();
                Path eventDir = keyMap.get(watchKey);

                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    Kind<?> kind = event.kind();
                    final String absolutePath = eventDir.toFile().getAbsolutePath();

                    switch (kind.toString()) {
                        case ENTRY_DELETE:
                            indexer.deleteIndex(absolutePath);
                            break;

                        case ENTRY_CREATE:
                        case ENTRY_MODIFY:
                            indexer.index(absolutePath);
                            break;

                    }
                }
            } while (watchKey.reset());
        } catch (final IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Adiciona um path ao mapa
     * @param String pathToFile
     */
    public void add(String pathToFile) {
        try (WatchService service = FileSystems.getDefault().newWatchService()) {
            final Path path = Paths.get(pathToFile);
            final WatchKey key = path.register(
                service,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
            );

            keyMap.put(key, path);
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Adiciona um path ao mapa
     * @param WatchKey key
     * @param Path path
     */
    public void add(final WatchKey key, final Path path) {
        keyMap.put(key, path);
    }

    /**
     * Retorna instância do observer
     * @return Observer
     */
    public Observer getInstance() {
        if (observer == null) {
            observer = new Observer();
        }

        return observer;
    }
    
}
