package el.solder.scrapbook.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLRender implements GLSurfaceView.Renderer {

	private Context cnx;

	public GLRender(Context context) {
		this.cnx = context;
	}

	public void onDrawFrame(GL10 glUnused) {
		// render.

	}

	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height); // size of GL viewport
	}

	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

	}

	public void onTouchEvent(final MotionEvent event) {

	}
}
