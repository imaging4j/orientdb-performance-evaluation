package com.siams.orientdb.evaluation.geom.objects;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/16/2015.
 */
public class BoxShape extends Shape {
    private double minX;
    private double minY;
    private double width;
    private double height;

    public BoxShape(double minX, double minY, double width, double height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public Bounds toBounds() {
        return new BoundingBox(minX, minY, width, height);
    }

    @Override
    public ODocument toODocument(ODatabaseDocumentInternal db) {
        final ODocument result = db.newInstance(BoxShape.class.getSimpleName());
        result.field("minX", getMinX(), OType.DOUBLE);
        result.field("minY", getMinY(), OType.DOUBLE);
        result.field("width", getWidth(), OType.DOUBLE);
        result.field("height", getHeight(), OType.DOUBLE);
        return result;
    }
}
