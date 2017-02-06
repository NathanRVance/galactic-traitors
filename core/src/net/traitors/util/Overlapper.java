package net.traitors.util;

import net.traitors.thing.Thing;
import net.traitors.thing.platform.Platform;

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
    public static Set<TreeNode<Thing>> getOverlaps(List<Thing> stuff) {
        Set<TreeNode<Thing>> thingTrees = new HashSet<>();

        //Turn the things into unassembled tree nodes, but reverse the order.
        List<TreeNode<Thing>> nodes = new ArrayList<>(stuff.size());
        for (int i = stuff.size() - 1; i >= 0; i--) {
            nodes.add(new TreeNode<>(stuff.get(i)));
        }

        //Instead, go through stuff in reverse and add each thing to the smallest platform that it overlaps with.

        for (int i = 0; i < nodes.size(); i++) {
            TreeNode<Thing> node = nodes.get(i);
            boolean placed = false;
            for (int j = i + 1; j < nodes.size(); j++) {
                if (nodes.get(j).getPayload().contains(node.getPayload().getWorldPoint()) && nodes.get(j).getPayload() instanceof Platform) {
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

}
