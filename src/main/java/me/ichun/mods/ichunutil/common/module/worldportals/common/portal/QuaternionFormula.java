package me.ichun.mods.ichunutil.common.module.worldportals.common.portal;

import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Quaternion;

import java.util.HashMap;

public class QuaternionFormula
{
    //made from http://math.stackexchange.com/a/535223 and http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/
    public static final Quaternion NO_ROTATION_QUAT = new Quaternion(0, 0, 0, 1);
    public static final QuaternionFormula NO_ROTATION = new QuaternionFormula(NO_ROTATION_QUAT, NO_ROTATION_QUAT);
    public static final float[] NO_ROTATION_FLOAT = new float[] { 0F, 0F, 0F };

    public static final HashMap<Float, Quaternion> horizontalQuats = new HashMap<>();

    public static Quaternion getHorizontalQuaternion(EnumFacing in)
    {
        float horiAngle = in.getHorizontalAngle() - 0.0125F; // please tell me you won't notice a 0.0125 difference in the yaw. -.-
        Quaternion quat = horizontalQuats.get(horiAngle);
        if(quat == null)
        {
            quat = createQuaternionFromZXYEuler(0, horiAngle, 0); // x = pitch, y = yaw, z = roll
            horizontalQuats.put(horiAngle, quat);
        }
        return quat;
    }

    public static final HashMap<Float, Quaternion> verticalQuats = new HashMap<>();

    public static Quaternion getVerticalQuaternion(EnumFacing in)
    {
        float vertAngle = in.getFrontOffsetY() * 90F * 0.99F;
        Quaternion quat = verticalQuats.get(vertAngle);
        if(quat == null)
        {
            quat = createQuaternionFromZXYEuler(vertAngle, 0, 0); // x = pitch, y = yaw, z = roll
            verticalQuats.put(vertAngle, quat);
        }
        return quat;
    }


    public Quaternion qHI;
    public Quaternion conjQHI;
    public Quaternion qHO;
    public Quaternion conjQHO;
    public Quaternion qVI;
    public Quaternion conjQVI;
    public Quaternion qVO;
    public Quaternion conjQVO;

    public boolean noRotation;

    public QuaternionFormula(Quaternion inH, Quaternion outH) //horizontal in and out
    {
        qHI = inH;
        conjQHI = qHI.negate(null);
        qHO = outH;
        conjQHO = qHO.negate(null);
        qVI = qVO = NO_ROTATION_QUAT;
        conjQVI = conjQVO = NO_ROTATION_QUAT.negate(null);

        noRotation = QuaternionFormula.quaternionsIdentical(qHI, qHO);
    }

    public QuaternionFormula(Quaternion inH, Quaternion outH, Quaternion inV, Quaternion outV) //vertical and horizontal
    {
        this(inH, outH);
        qVI = inV;
        conjQVI = qVI.negate(null);
        qVO = outV;
        conjQVO = qVO.negate(null);

        noRotation = QuaternionFormula.quaternionsIdentical(qVI, qVO) && noRotation;
    }

    public static QuaternionFormula createFromFaces(EnumFacing in, EnumFacing inUp, EnumFacing out, EnumFacing outUp)
    {
        if(in.getAxis().isVertical() || out.getAxis().isVertical()) //Y Axis Involvement or ceiling and floor portals
        {
            return new QuaternionFormula(QuaternionFormula.getHorizontalQuaternion(in.getAxis().isVertical() ? inUp : in), QuaternionFormula.getHorizontalQuaternion(out.getAxis().isVertical() ? outUp : out), QuaternionFormula.getVerticalQuaternion(in), QuaternionFormula.getVerticalQuaternion(out));
        }
        return new QuaternionFormula(QuaternionFormula.getHorizontalQuaternion(in), QuaternionFormula.getHorizontalQuaternion(out));
    }

    public static QuaternionFormula createFromPlanes(EnumFacing in, EnumFacing inUp, EnumFacing out, EnumFacing outUp)
    {
        return createFromFaces(in.getOpposite(), in == EnumFacing.UP ? inUp : inUp.getOpposite(), out, out == EnumFacing.DOWN ? outUp : outUp.getOpposite());
    }

    public float[] applyPositionalRotation(float[] ori) //takes the offset from origin, rotates and returns the new position.
    {
        if(noRotation)
        {
            return ori;
        }
        float[] applied = new float[3];

        Quaternion qPos = new Quaternion(ori[0], ori[1], ori[2], 0);
        qPos = Quaternion.mul(Quaternion.mul(qHI, qPos, null), conjQHI, null); //apply the out
        qPos = Quaternion.mul(Quaternion.mul(qVI, qPos, null), conjQVI, null); //apply the out
        qPos = Quaternion.mul(Quaternion.mul(conjQVO, qPos, null), qVO, null); //reset the in
        qPos = Quaternion.mul(Quaternion.mul(conjQHO, qPos, null), qHO, null); //reset the in
        applied[0] = qPos.getX();
        applied[1] = qPos.getY();
        applied[2] = qPos.getZ();

        return applied;
    }

    public float[] applyRotationalRotation(float[] ori) //takes a look vector, returns the expected rotationYawPitchRoll
    {
        if(noRotation)
        {
            return NO_ROTATION_FLOAT;
        }

        //yaw rotates on Y axis
        //pitch rotates on X axis
        //q2 * q1 = absolute frame of reference
        //q1 * q2 = frame of reference of rotating object
        Quaternion qRot = createQuaternionFromZXYEuler(ori[1], ori[0], ori[2]); //x = pitch, y = yaw, z = roll.
        if(QuaternionFormula.quaternionsIdentical(qVI, NO_ROTATION_QUAT)) //Horizontal IN only
        {
            qRot = Quaternion.mul(qRot, conjQHI, null);
        }
        else
        {
            qRot = Quaternion.mul(qRot, conjQHI, null);
            qRot = Quaternion.mul(qRot, qVI, null);
        }
        if(QuaternionFormula.quaternionsIdentical(qVO, NO_ROTATION_QUAT)) //Horizontal OUT only
        {
            qRot = Quaternion.mul(qRot, qHO, null);
        }
        else
        {
            qRot = Quaternion.mul(qRot, conjQVO, null);
            qRot = Quaternion.mul(qRot, qHO, null);
        }

        float[] angles = createZXYEulerFromQuaternion(qRot);
        return new float[] { angles[1] - ori[0], angles[0] - ori[1], angles[2] - ori[2] }; //x = pitch, y = yaw, z = roll; 0 = yaw, 1 = pitch, 2 = roll
    }

    public static Quaternion createQuaternionFromZXYEuler(float x, float y, float z)
    {
        //x = pitch
        //y = yaw
        //z = roll
        double radX = Math.toRadians(x);
        double radY = Math.toRadians(y);
        double radZ = Math.toRadians(z);

        float cx = (float)Math.cos(radX);
        float sx = (float)Math.sin(radX);
        float cy = (float)Math.cos(radY);
        float sy = (float)Math.sin(radY);
        float cz = (float)Math.cos(radZ);
        float sz = (float)Math.sin(radZ);

        return Quaternion.setFromMatrix(new Matrix4f(new float[] { cy * cz - sx * sy * sz, -cx * sz, cz * sy + cy * sx * sz, 0, cz * sx * sy + cy * sz, cx * cz, -cy * cz * sx + sy * sz, 0, -cx * sy, sx, cx * cy, 0, 0, 0, 0, 0 }), new Quaternion());
    }

    public static float[] createZXYEulerFromQuaternion(Quaternion q) // https://www.geometrictools.com/Documentation/EulerAngles.pdf
    {
        final float xx = q.x * q.x;
        final float xy = q.x * q.y;
        final float xz = q.x * q.z;
        final float xw = q.x * q.w;
        final float yy = q.y * q.y;
        final float yz = q.y * q.z;
        final float yw = q.y * q.w;
        final float zz = q.z * q.z;
        final float zw = q.z * q.w;

        Matrix4f matrix = new Matrix4f(new float[] { 1 - 2 * (yy + zz), 2 * (xy - zw), 2 * (xz + yw), 0, 2 * (xy + zw), 1 - 2 * (xx + zz), 2 * (yz - xw), 0, 2 * (xz - yw), 2 * (yz + xw), 1 - 2 * (xx + yy), 0, 0, 0, 0, 1 });

        double thetaX, thetaY, thetaZ;
        if(matrix.m21 < 1)
        {
            if(matrix.m21 > -1)
            {
                thetaX = Math.asin(matrix.m21);
                thetaZ = Math.atan2(-matrix.m01, matrix.m11);
                thetaY = Math.atan2(-matrix.m20, matrix.m22);
            }
            else // == -1
            {
                thetaX = -Math.PI / 2;
                thetaZ = -Math.atan2(matrix.m02, matrix.m00);
                thetaY = 0;
            }
        }
        else // = 1
        {
            thetaX = Math.PI / 2;
            thetaZ = Math.atan2(matrix.m02, matrix.m00);
            thetaY = 0;
        }

        float[] angles = new float[3];

        angles[0] = (float)Math.toDegrees(thetaX);
        angles[1] = (float)Math.toDegrees(thetaY);
        angles[2] = (float)Math.toDegrees(thetaZ);

        //pitch on x, yaw on y, roll on z
        return angles;
    }

    public static boolean quaternionsIdentical(Quaternion a, Quaternion b)
    {
        return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ() && a.getW() == b.getW();
    }
}
