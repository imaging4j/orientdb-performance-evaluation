package com.siams.orientdb.evaluation.geom.objects;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/17/2015.
 */
public class Scene {
    private Map<String, Phase> phaseMap = new HashMap<>(0);

    public Map<String, Phase> getPhaseMap() {
        return phaseMap;
    }
}
