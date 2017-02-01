package net.traitors.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Overlapper {

    /**
     * Puts stuff in trees
     *
     * @param stuff all the things we're dealing with, sorted by stacking
     *              precedence (earlier in the list ends up on bottom of stacks)
     * @return Set of trees of things where the root is at the bottom
     */
    public static Set<TreeNode> getOverlaps(List<net.traitors.thing.Thing> stuff) {
        Set<TreeNode> thingTrees = new HashSet<TreeNode>();

        //Turn the things into unassembled tree nodes, but reverse the order.
        List<TreeNode> nodes = new ArrayList<TreeNode>(stuff.size());
        for (int i = stuff.size() - 1; i >= 0; i--) {
            nodes.add(new TreeNode(new RotRec(stuff.get(i))));
        }

        //Instead, go through stuff in reverse and add each thing to the smallest platform that it overlaps with.

        for (int i = 0; i < nodes.size(); i++) {
            TreeNode node = nodes.get(i);
            boolean placed = false;
            for (int j = i + 1; j < nodes.size(); j++) {
                if (nodes.get(j).getRotRet().contains(node.getRotRet().point)) {
                    nodes.get(j).addChild(node);
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                thingTrees.add(node);
            }
        }

        return thingTrees;
    }

    static class RotRec {

        Point point;
        float width;
        float height;
        float rotation;
        net.traitors.thing.Thing thing;

        //As always, rotation is in radians
        RotRec(net.traitors.thing.Thing thing) {
            this.point = thing.getWorldPoint();
            this.width = thing.getWidth();
            this.height = thing.getHeight();
            this.rotation = thing.getWorldRotation();
            this.thing = thing;
        }

        boolean contains(Point p) {
            //Rotate p to be easier to compare to this rectangle
            p = p.subtract(point).rotate(-rotation);
            return Math.abs(p.x) <= width / 2 && Math.abs(p.y) <= height / 2;
        }
    }

}
