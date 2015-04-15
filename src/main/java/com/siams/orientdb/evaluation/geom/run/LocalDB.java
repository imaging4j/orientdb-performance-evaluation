package com.siams.orientdb.evaluation.geom.run;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/16/2015.
 */
class LocalDB {
    public static String toURI() {
        return toURI("geom-db");
    }

    public static String toURI(String name) {
        try {
            final Path dbPath = Paths.get("..").toRealPath()
                    .resolve("orientdb-performance-evaluation-db-root")
                    .resolve(name);
            return "plocal:" + dbPath.toUri().getPath();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
