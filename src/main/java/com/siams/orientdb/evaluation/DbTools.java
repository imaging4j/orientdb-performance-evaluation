package com.siams.orientdb.evaluation;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    static int getMaxTileId(ODatabaseDocument db) {
        int result;
        final List<ODocument> maxTileIdQuery = db.query(new OSQLSynchQuery<ODocument>("select max(id) from Tile"));
        if (maxTileIdQuery.size() == 1) {
            result = maxTileIdQuery.get(0).field("max");
        } else {
            result = -1;
        }
        return result;
    }
}
