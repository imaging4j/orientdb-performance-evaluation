package com.siams.orientdb.evaluation.geom.objects;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import javafx.geometry.Bounds;

import javax.persistence.Version;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/14/2015.
 */
public abstract class Shape {
    public abstract Bounds toBounds();

    public abstract ODocument toODocument(ODatabaseDocumentInternal db);
}
