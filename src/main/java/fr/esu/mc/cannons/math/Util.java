package fr.esu.mc.cannons.math;

import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.ejml.simple.SimpleMatrix;

public class Util {
    // returns the rotation matrix aligning a onto b (https://math.stackexchange.com/a/476311)
    public static SimpleMatrix getRotationMatrix(Vector a, Vector b){
        if(a.clone().subtract(b).length() < 0.0001)
            return SimpleMatrix.identity(3);
        Vector v = a.getCrossProduct(b);
        double c = a.dot(b); // cosine of angle
        if(c + 1 < 0.0001)
            return SimpleMatrix.identity(3).scale(-1);
        SimpleMatrix vSkewSymmetricCrossProductMatrix = new SimpleMatrix(3, 3);
        vSkewSymmetricCrossProductMatrix.set(0, 1, -v.getZ());
        vSkewSymmetricCrossProductMatrix.set(0, 2, v.getY());
        vSkewSymmetricCrossProductMatrix.set(1, 0, v.getZ());
        vSkewSymmetricCrossProductMatrix.set(1, 2, -v.getX());
        vSkewSymmetricCrossProductMatrix.set(2, 0, -v.getY());
        vSkewSymmetricCrossProductMatrix.set(2, 1, v.getX());
        return SimpleMatrix.identity(3)
                .plus(vSkewSymmetricCrossProductMatrix)
                .plus(vSkewSymmetricCrossProductMatrix
                        .mult(vSkewSymmetricCrossProductMatrix)
                        .scale(1 / (1 + c))
                );
    }
    public static Vector applyRotationMatrix(SimpleMatrix rotationMatrix, Vector x){
        return convertEJMLSimpleMatrixToBukkitVector(rotationMatrix.mult(convertBukkitVectorToEJMLSimpleMatrix(x)));
    }
    public static SimpleMatrix convertBukkitVectorToEJMLSimpleMatrix(Vector a){
        SimpleMatrix simpleMatrix = new SimpleMatrix(3, 1);
        simpleMatrix.set(0, 0, a.getX());
        simpleMatrix.set(1, 0, a.getY());
        simpleMatrix.set(2, 0, a.getZ());
        return simpleMatrix;
    }
    public static Vector convertEJMLSimpleMatrixToBukkitVector(SimpleMatrix simpleMatrix){
        return new Vector(simpleMatrix.get(0, 0), simpleMatrix.get(1, 0), simpleMatrix.get(2, 0));
    }
    public static EulerAngle directionToEuler(Vector dir) {
        double xzLength = Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ());
        double pitch = Math.atan2(xzLength, dir.getY()) - Math.PI / 2;
        double yaw = -Math.atan2(dir.getX(), dir.getZ());
        return new EulerAngle(pitch, yaw, 0);
    }
}