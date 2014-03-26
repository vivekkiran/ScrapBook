package el.solde.scrapbook.activity;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import el.solder.scrapbook.opengl.Config2D888MSAA;
import el.solder.scrapbook.opengl.GLRender;

public class CombineScrap extends Fragment {

	private GLSurfaceView glSurfaceView;
	private GLRender render;
	private Config2D888MSAA ConfigChooser;
	private Handler mHandler = new Handler();
	private Boolean RPause = false; // pause flag
	private int FPS = 30;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.combine_image, container, false);
		glSurfaceView = (GLSurfaceView) view
				.findViewById(R.id.graphics_glsurfaceview);

		glSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
		glSurfaceView.setEGLContextClientVersion(2);
		glSurfaceView
				.setEGLConfigChooser(ConfigChooser = new Config2D888MSAA()); // используем
																				// свою
																				// реализацию
																				// EGLConfigChooser

		render = new GLRender(getActivity()); // инициализируем свою реализацию
												// Renderer

		glSurfaceView.setRenderer(render);

		glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // устанавливаем
																			// смену
																			// кадров
																			// по
																			// вызову

		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	void reqRend() {
		mHandler.removeCallbacks(mDrawRa);
		if (!RPause) {
			mHandler.postDelayed(mDrawRa, 1000 / FPS); // отложенный вызов
														// mDrawRa
			glSurfaceView.requestRender();
		}
	}

	private final Runnable mDrawRa = new Runnable() {
		public void run() {
			reqRend();
		}
	};

}
