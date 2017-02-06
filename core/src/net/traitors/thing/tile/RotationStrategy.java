package net.traitors.thing.tile;

public class RotationStrategy {

    private final float rotationAngle;

    public RotationStrategy(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    /**
     * Get the closest legal rotation to the requested rotation
     *
     * @param userRotation requested user rotation
     * @param thisRotation rotation half way through the rotation angle
     * @return the closest valid rotation
     */
    public float getRotation(float userRotation, float thisRotation) {
        float lower = (float) ((thisRotation - rotationAngle / 2 + Math.PI * 2) % (Math.PI * 2));
        float upper = (float) ((thisRotation + rotationAngle / 2 + Math.PI * 2) % (Math.PI * 2));
        if ((lower < upper && userRotation > lower && userRotation < upper)
                || (lower > upper && (userRotation > lower || userRotation < upper))) {
            return userRotation;
        } else if (lower - userRotation > 0 && lower - userRotation < Math.PI - rotationAngle / 2) {
            return lower;
        } else {
            return upper;
        }
    }

}
