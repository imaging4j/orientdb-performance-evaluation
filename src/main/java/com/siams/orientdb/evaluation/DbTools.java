package com.siams.orientdb.evaluation;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by alexei.vylegzhanin@gmail.com on 3/12/2015.
 */
public class DbTools {

    static String getLocalDbPath(String name) {
        try {
            final Path dbPath = Paths.get(".").toRealPath().resolve("test-db-root").resolve(name);
            return "plocal:" + dbPath.toUri().getPath();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    static String getLocalDbPath(String[] args) {
        return getLocalDbPath(args.length > 0 ? args[0] : "test-db");
    }
}
