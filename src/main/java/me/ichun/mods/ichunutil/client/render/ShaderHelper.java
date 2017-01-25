package me.ichun.mods.ichunutil.client.render;

import gnu.trove.map.hash.TIntFloatHashMap;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

/**
 * This class was made by the CoFH team for CoFH Core but has been adapted for use in PortalGun.<br>
 * You can find the original source at http://github.com/CoFH/CoFHCore
 */
public final class ShaderHelper {

    private static final int VERT = ARBVertexShader.GL_VERTEX_SHADER_ARB;
    private static final int FRAG = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;

    private static TIntFloatHashMap prevTime = new TIntFloatHashMap();

    public static void useShader(int shader, IShaderCallback callback) {

        if (!supportsShaders())
            return;

        ARBShaderObjects.glUseProgramObjectARB(shader);

        if (shader != 0) {
            float frameTime = iChunUtil.eventHandlerClient.ticks + iChunUtil.eventHandlerClient.renderTick;
            boolean newFrame = frameTime != prevTime.get(shader);

            if (newFrame) {
                int time = ARBShaderObjects.glGetUniformLocationARB(shader, "time");
                ARBShaderObjects.glUniform1fARB(time, frameTime);
                prevTime.put(shader, frameTime);
            }

            if (callback != null)
                callback.call(shader, newFrame);
        }
    }

    public static void useShader(int shader) {

        useShader(shader, null);
    }

    public static void releaseShader() {

        useShader(0);
    }

    public static boolean supportsShaders() {

        return OpenGlHelper.shadersSupported;
    }

    // Most of the code taken from the LWJGL wiki
    // http://lwjgl.org/wiki/index.php?title=GLSL_Shaders_with_LWJGL
    public static int createProgram(String vert, String frag) {

        if (!supportsShaders())
            return -1;

        int vertId = 0, fragId = 0, program = 0;
        if (vert != null)
            vertId = createShader(vert, VERT);
        if (frag != null)
            fragId = createShader(frag, FRAG);

        program = ARBShaderObjects.glCreateProgramObjectARB();
        if (program == 0)
            return 0;

        if (vert != null)
            ARBShaderObjects.glAttachObjectARB(program, vertId);
        if (frag != null)
            ARBShaderObjects.glAttachObjectARB(program, fragId);

        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            iChunUtil.LOGGER.error(getLogInfo(program));
            ARBShaderObjects.glDeleteObjectARB(program);
            return 0;
        }

        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            iChunUtil.LOGGER.error(getLogInfo(program));
            ARBShaderObjects.glDeleteObjectARB(program);
            return 0;
        }

        prevTime.put(program, -1);

        return program;
    }

    private static int createShader(String shaderString, int shaderType) {

        int shader = 0;

        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if (shader == 0)
                return 0;

            ARBShaderObjects.glShaderSourceARB(shader, shaderString);
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

            return shader;
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            e.printStackTrace();
            return -1;
        }
    }

    private static String getLogInfo(int obj) {

        return ARBShaderObjects.glGetInfoLogARB(obj,
                ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    public static interface IShaderCallback {

        public void call(int shader, boolean newFrame);
    }

}