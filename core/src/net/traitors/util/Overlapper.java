package net.traitors.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Overlapper {

    private static float OVERLAP_BUFFER = .1f;

    /**
     * Pairs every thing with the other things it overlaps with
     *
     * @param stuff        all the things we're dealing with
     * @param lastOverlaps previous overlaps will have a little buffer in the calculation
     * @return Mapping of every thing in stuff to every thing it's touching.
     */
    public static Map<Thing, Set<Thing>> getOverlaps(List<Thing> stuff, Map<Thing, Set<Thing>> lastOverlaps) {
        List<RotRec> recs = new ArrayList<RotRec>(stuff.size());
        Map<Thing, Set<Thing>> overlaps = new HashMap<Thing, Set<Thing>>();
        for (Thing thing : stuff) {
            recs.add(new RotRec(thing.getWorldPoint(), thing.getWidth(), thing.getHeight(), thing.getWorldRotation(), thing));
            overlaps.put(thing, new HashSet<Thing>());
        }

        //"Premature optimization is the root of all evil" - Donald Knuth
        //TODO: Optimize this
        for (RotRec r1 : recs) {
            for (RotRec r2 : recs) {
                if (r1 != r2 && r1.contains(r2.point, lastOverlaps.containsKey(r1.thing) && lastOverlaps.get(r1.thing).contains(r2.thing))) {
                    overlaps.get(r1.thing).add(r2.thing);
                }
            }
        }

        return overlaps;
    }

    private static class RotRec {

        Point point;
        float width;
        float height;
        float rotation;
        Thing thing;

        //As always, rotation is in radians
        RotRec(Point point, float width, float height, float rotation, Thing thing) {
            this.point = point;
            this.width = width;
            this.height = height;
            this.rotation = rotation;
            this.thing = thing;
        }

        boolean contains(Point p, boolean useBuffer) {
            //Rotate p to be easier to compare to this rectangle
            float buffer = (useBuffer) ? OVERLAP_BUFFER : 0;
            if(useBuffer) System.out.println("Using buffer!");
            p = p.subtract(point).rotate(-rotation);
            return Math.abs(p.x) <= width / 2 + buffer && Math.abs(p.y) <= height / 2 + buffer;
        }
    }

}
