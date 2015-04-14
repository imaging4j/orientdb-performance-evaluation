package com.siams.orientdb.evaluation.geom.objects;

import javafx.geometry.Bounds;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/14/2015.
 */
public class Cell {
    private long index;
    private Set<Shape> shapes = new HashSet<>(0);

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public Set<Shape> getShapes() {
        return shapes;
    }

    public Set<Shape> getShapes(Bounds bounds) {
        final LinkedHashSet<Shape> result = new LinkedHashSet<>();
        final Set<Shape> shapes = getShapes();
        for (Shape shape : shapes) {
            final Bounds bounds1 = shape.toBounds();
            if (bounds.intersects(bounds1)) {
                result.add(shape);
            }
        }
        return result;
    }
}
