package com.siams.orientdb.evaluation.geom.utils;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.siams.orientdb.evaluation.geom.objects.Cell;
import com.siams.orientdb.evaluation.geom.objects.Shape;
import javafx.geometry.Bounds;

import java.util.HashSet;
import java.util.Set;

import static com.siams.orientdb.evaluation.geom.utils.CellIndex.toIndex;
import static com.siams.orientdb.evaluation.geom.utils.CellIndex.toZ;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/15/2015.
 */
public class SpatialIndex {

    private static final String CELL_CLASS_NAME = Cell.class.getSimpleName();
    private final int cellDim = 256;

    public int insert(OObjectDatabaseTx db, Shape shape, int label) {
        int result = 0;
        final Bounds bounds = shape.toBounds();
        final int z = toZ(this.cellDim, Math.max(bounds.getWidth(), bounds.getHeight()));
        assert z >= 16;

        final long cellDim = this.cellDim * (1l << z);
        final long x1 = Math.round(bounds.getMinX() / cellDim);
        final long y1 = Math.round(bounds.getMinY() / cellDim);
        final long x2 = Math.round(bounds.getMaxX() / cellDim);
        final long y2 = Math.round(bounds.getMaxY() / cellDim);

        assert x1 <= 0xffff;
        assert x2 <= 0xffff;
        assert y1 <= 0xffff;
        assert y2 <= 0xffff;

        final ODatabaseDocumentInternal underlying = db.getUnderlying();
        final ODocument oShape = shape.toODocument(underlying);
        final OIndex<?> oCellIndex = underlying.getMetadata().getIndexManager().getIndex(CELL_CLASS_NAME + ".index");
        for (int y = (int) y1; y <= y2; y++) {
            for (int x = (int) x1; x <= x2; x++) {
                final long index = toIndex(x, y, z, label);
                final ODocument oCell = openCell(underlying, oCellIndex, index);
                final Set<Object> shapes = oCell.field("shapes", OType.LINKSET);
                shapes.add(oShape);
                oCell.save();
                result++;
            }
        }
        return result;
    }

    private ODocument openCell(ODatabaseDocumentInternal db, OIndex<?> oCellIndex, Long index) {
        ODocument result = null;
        final ORecordId rid = (ORecordId) oCellIndex.get(index);
        if (rid != null) {
            result = db.getRecord(rid);
        }
        if (result == null) {
            result = db.newInstance(CELL_CLASS_NAME);
            result.field("index", index);
            result.field("shapes", new HashSet<>(0), OType.LINKSET);
        }
        return result;
    }

    public void select(ODatabaseDocumentTx db, Bounds bounds, ShapesResultListener listener) {

    }

}
