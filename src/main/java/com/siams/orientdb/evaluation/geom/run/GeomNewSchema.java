package com.siams.orientdb.evaluation.geom.run;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.siams.orientdb.evaluation.geom.objects.Cell;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/16/2015.
 */
public class GeomNewSchema {
    public static void main(String[] args) {
        try (final OObjectDatabaseTx db = new OObjectDatabaseTx(LocalDB.toURI()).create()) {
            System.out.println("Schema generating: " + db.getURL());
            db.getMetadata().getSchema().generateSchema(Cell.class.getPackage().getName());
            db.getEntityManager().registerEntityClasses(Cell.class.getPackage().getName());

            addIndex(db, Cell.class);
        }
        System.out.println("done");
    }

    private static void addIndex(OObjectDatabaseTx db, Class<Cell> iClass) {
        db.getMetadata().getSchema().getClass(iClass)
                .createIndex(iClass.getSimpleName() + ".index", OClass.INDEX_TYPE.UNIQUE_HASH_INDEX, "index");
    }
}
