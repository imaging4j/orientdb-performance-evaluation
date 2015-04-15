package com.siams.orientdb.evaluation.geom.utils;

import com.siams.orientdb.evaluation.geom.objects.Shape;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/15/2015.
 */
public interface ShapesResultListener {
    boolean result(Shape shape);
}
