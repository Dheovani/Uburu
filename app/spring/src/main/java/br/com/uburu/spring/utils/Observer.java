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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.uburu.spring.service.IndexService;
import jakarta.annotation.PostConstruct;

import static java.nio.file.WatchEvent.Kind;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public final class Observer {

    private static volatile Observer observer;

    @Autowired
    private Indexer indexer;

    @Autowired
    private IndexService indexService;

    private Map<WatchKey, Path> keyMap;

    private Observer() {
        keyMap = new HashMap<>();
    }

    @PostConstruct
    private void initialize() {
        indexService.findAll().forEach(e -> {
            add(e.getPath());
        });
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
                        case "ENTRY_DELETE":
                            indexer.deleteIndex(absolutePath);
                            break;

                        case "ENTRY_CREATE":
                        case "ENTRY_MODIFY":
                            indexer.index(absolutePath);
                            break;
                    }
                }
            } while (watchKey.reset());
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
    public static Observer getInstance() {
        if (observer == null) {
            observer = new Observer();
        }

        return observer;
    }
    
}
