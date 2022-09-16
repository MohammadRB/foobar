/*
Instead of reflecting rays to test for collision we can reflect the whole world and keep rays straight.
 ___________
|  /\       |
| /  \      |
|     *     |
 -----------
 Is equal to below collision
 ___________
|     *     |
|    /      |
|___/_______|
|  /        |
| /         |
|           |
 -----------
In each iteration we create all possible reflections and test for collision lengths.
We continue until no collision could be found within given distance.

Reflections are created recursively from current position in below order to cover all area.
current -> go left -> go top -> go right -> go bottom -> got left
*/

import java.util.HashMap;
import java.util.stream.Stream;

class Vector {
    public int X;
    public int Y;

    public Vector(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public Vector multiply(int value) {
        return new Vector(X * value, Y * value);
    }

    public Vector sum(Vector other) {
        return new Vector(X + other.X, Y + other.Y);
    }

    public Vector minus(Vector other) {
        return new Vector(X - other.X, Y - other.Y);
    }

    public int dot(Vector other) {
        return X * other.X + Y * other.Y;
    }

    public float cosTheta(Vector other) {
        // A.B = |A| * |B| * cos(&)
        // cos(&) = A.B / |A| * |B|
        int dot = dot(other);
        int length = length_sqr();
        int otherLength = other.length_sqr();
        float cosTheta = dot / (float)Math.sqrt(length * otherLength);
        return cosTheta;
    }

    public float atan2() {
        return (float)Math.atan2(X, Y);
    }

    public int length_sqr() {
        return dot(this);
    }

    public float length() {
        return (float)Math.sqrt(length_sqr());
    }

    public Vector clone() {
        return new Vector(X, Y);
    }
}

class FightGround {
    public int XDimension;
    public int YDimension;
    public Vector PlayerPosition;
    public Vector TrainerPosition;
    private Vector LeftBottom;
    private Vector LeftTop;
    private Vector RightTop;
    private Vector RightBottom;

    public FightGround(int xDimension, int yDimension, Vector playerPosition, Vector trainerPosition) {
        XDimension = xDimension;
        YDimension = yDimension;
        PlayerPosition = playerPosition;
        TrainerPosition = trainerPosition;

        LeftBottom = new Vector(0,0);
        LeftTop = new Vector(LeftBottom.X, LeftBottom.Y + YDimension);
        RightTop = new Vector(LeftTop.X + XDimension, LeftTop.Y);
        RightBottom = new Vector(RightTop.X, RightTop.Y - YDimension);
    }

    public void reflectLeft() {
        PlayerPosition.X -= ((PlayerPosition.X - LeftBottom.X) * 2);
        TrainerPosition.X -= ((TrainerPosition.X - LeftBottom.X) * 2);
        RightBottom.X -= (XDimension * 2);
        RightTop.X -= (XDimension * 2);
        swapLeftRight();
    }

    public void reflectRight() {
        PlayerPosition.X += ((RightBottom.X - PlayerPosition.X) * 2);
        TrainerPosition.X += ((RightBottom.X - TrainerPosition.X) * 2);
        LeftBottom.X += (XDimension * 2);
        LeftTop.X += (XDimension * 2);
        swapLeftRight();
    }

    public void reflectTop() {
        PlayerPosition.Y += ((LeftTop.Y - PlayerPosition.Y) * 2);
        TrainerPosition.Y += ((LeftTop.Y - TrainerPosition.Y) * 2);
        LeftBottom.Y += (YDimension * 2);
        RightBottom.Y += (YDimension * 2);
        swapBottomTop();
    }

    public void reflectBottom() {
        PlayerPosition.Y -= ((PlayerPosition.Y - LeftBottom.Y) * 2);
        TrainerPosition.Y -= ((TrainerPosition.Y - LeftBottom.Y) * 2);
        LeftTop.Y -= (YDimension * 2);
        RightTop.Y -= (YDimension * 2);
        swapBottomTop();
    }

    private void swapLeftRight() {
        Vector temp = LeftBottom;
        LeftBottom = RightBottom;
        RightBottom = temp;

        temp = LeftTop;
        LeftTop = RightTop;
        RightTop = temp;
    }

    private void swapBottomTop() {
        Vector temp = LeftBottom;
        LeftBottom = LeftTop;
        LeftTop = temp;

        temp = RightBottom;
        RightBottom = RightTop;
        RightTop = temp;
    }
}

enum ColliderType {
    Player,
    Trainer
}

class ColliderData {
    public ColliderType Type;
    public int DistanceSqr;

    public ColliderData(ColliderType type, int distanceSq) {
        Type = type;
        DistanceSqr = distanceSq;
    }
}

public class bringing_a_gun_to_a_trainer_fight {

    public static int solution(int[] dimensions, int[] your_position, int[] trainer_position, int distance) {
        Vector playerPosition = new Vector(your_position[0], your_position[1]);
        FightGround fightGround = new FightGround
        (
            dimensions[0],
            dimensions[1],
            playerPosition.clone(),
            new Vector(trainer_position[0], trainer_position[1])
        );

        HashMap<Float, ColliderData> colliders = new HashMap<>();
        Vector currentFightGroundPosition = new Vector(0, 0);
        int maxDistanceSqr = distance * distance;
        int reflectIteration = 1;
        int distanceDiscovered = 0;

        testCollision(playerPosition, fightGround.TrainerPosition, maxDistanceSqr, ColliderType.Trainer, colliders);

        while (distanceDiscovered <= distance) {
            while (currentFightGroundPosition.X > reflectIteration * -1) {
                --currentFightGroundPosition.X;
                fightGround.reflectLeft();

                testCollisions(playerPosition, fightGround, maxDistanceSqr, colliders);
            }
            while (currentFightGroundPosition.Y < reflectIteration) {
                ++currentFightGroundPosition.Y;
                fightGround.reflectTop();

                testCollisions(playerPosition, fightGround, maxDistanceSqr, colliders);
            }
            while (currentFightGroundPosition.X < reflectIteration) {
                ++currentFightGroundPosition.X;
                fightGround.reflectRight();

                testCollisions(playerPosition, fightGround, maxDistanceSqr, colliders);
            }
            while (currentFightGroundPosition.Y > reflectIteration * -1) {
                --currentFightGroundPosition.Y;
                fightGround.reflectBottom();

                testCollisions(playerPosition, fightGround, maxDistanceSqr, colliders);
            }
            while (currentFightGroundPosition.X > reflectIteration * -1) {
                --currentFightGroundPosition.X;
                fightGround.reflectLeft();

                testCollisions(playerPosition, fightGround, maxDistanceSqr, colliders);
            }

            distanceDiscovered += Math.min(fightGround.XDimension, fightGround.YDimension);
            ++reflectIteration;
        }

        Stream<ColliderData> trainerColliders = colliders.values().stream().filter(c -> c.Type == ColliderType.Trainer);
        return (int)trainerColliders.count();
    }

    private static void testCollisions(Vector playerPosition,
                                       FightGround fightGround,
                                       int maxDistanceSq,
                                       HashMap<Float, ColliderData> colliders) {
        testCollision(playerPosition, fightGround.TrainerPosition, maxDistanceSq, ColliderType.Trainer, colliders);
        testCollision(playerPosition, fightGround.PlayerPosition, maxDistanceSq, ColliderType.Player, colliders);
    }

    private static boolean testCollision(Vector playerPosition,
                                         Vector colliderPosition,
                                         int maxDistanceSqr,
                                         ColliderType colliderType,
                                         HashMap<Float, ColliderData> colliders) {
        Vector ray = colliderPosition.minus(playerPosition);
        int distanceSqr = ray.length_sqr();

        if (distanceSqr <= maxDistanceSqr) {
            // compute the angle between ray and x-axis
            float rayXAngle = ray.atan2();

            ColliderData collider = colliders.get(rayXAngle);
            if (collider == null) {
                collider = new ColliderData(colliderType, distanceSqr);
                colliders.put(rayXAngle, collider);
            }

            if (distanceSqr < collider.DistanceSqr) {
                collider.Type = colliderType;
                collider.DistanceSqr = distanceSqr;
            }

            return true;
        }

        return false;
    }
}
