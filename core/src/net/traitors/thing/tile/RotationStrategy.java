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
        float pi2 = (float) Math.PI * 2;
        userRotation = (userRotation + pi2) % pi2;
        float lower = (thisRotation - rotationAngle / 2 + pi2) % pi2;
        float upper = (thisRotation + rotationAngle / 2 + pi2) % pi2;
        if ((lower < upper && userRotation > lower && userRotation < upper)
                || (lower > upper && (userRotation > lower || userRotation < upper))) {
            return userRotation;
        }
        float distToLower = Math.min(Math.abs(lower - userRotation), pi2 - Math.abs(lower - userRotation));
        float distToUpper = Math.min(Math.abs(upper - userRotation), pi2 - Math.abs(upper - userRotation));
        return (distToLower < distToUpper) ? lower : upper;
    }

}
