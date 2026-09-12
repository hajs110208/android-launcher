package com.customRobTop

import android.opengl.EGLConfig
import android.opengl.EGLDisplay
import android.opengl.EGLExt
import android.opengl.GLSurfaceView
import android.os.Build
import javax.microedition.khronos.egl.EGL10

/**
 * Custom EGL config chooser that enables HDR rendering on Android 14+.
 * Requests a 16-bit floating point color buffer with BT2020-HLG color space.
 */
class HDREGLConfigChooser : GLSurfaceView.EGLConfigChooser {
    override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig {
        val attribList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // HDR configuration for Android 14+
            intArrayOf(
                EGLExt.EGL_COLOR_COMPONENT_TYPE_EXT, EGLExt.EGL_COLOR_COMPONENT_TYPE_FLOAT_EXT,
                EGLExt.EGL_GL_COLORSPACE_KHR, EGLExt.EGL_GL_COLORSPACE_BT2020_HLG_KHR,
                EGL10.EGL_RED_SIZE, 16,
                EGL10.EGL_GREEN_SIZE, 16,
                EGL10.EGL_BLUE_SIZE, 16,
                EGL10.EGL_ALPHA_SIZE, 16,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_RENDERABLE_TYPE, EGLExt.EGL_OPENGL_ES3_BIT_KHR,
                EGL10.EGL_NONE
            )
        } else {
            // Standard SDR configuration for older devices
            intArrayOf(
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_NONE
            )
        }

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)

        if (!egl.eglChooseConfig(display, attribList, configs, 1, numConfigs)) {
            throw IllegalArgumentException("eglChooseConfig failed")
        }

        if (numConfigs[0] == 0) {
            throw IllegalArgumentException("No EGL configs available")
        }

        return configs[0] ?: throw IllegalArgumentException("No suitable EGL config found")
    }
}
