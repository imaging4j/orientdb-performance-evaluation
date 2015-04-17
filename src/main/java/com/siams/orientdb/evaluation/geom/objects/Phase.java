package com.siams.orientdb.evaluation.geom.objects;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/17/2015.
 */
public class Phase {
    private String name;
    private Set<Shape> shapes = new HashSet<>(0);

    public Phase() {

    }

    public Phase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Shape> getShapes() {
        return shapes;
    }

}
