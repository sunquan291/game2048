/**
 * StackBlur v1.0 for Android
 *
 * @Author: Enrique López Mañas <eenriquelopez@gmail.com>
 * http://www.neo-tech.es
 *
 * Author of the original algorithm: Mario Klingemann <mario.quasimondo.com>
 *
 * This is a compromise between Gaussian Blur and Box blur
 * It creates much better looking blurs than Box Blur, but is
 * 7x faster than my Gaussian Blur implementation.
 *
 * I called it Stack Blur because this describes best how this
 * filter works internally: it creates a kind of moving stack
 * of colors whilst scanning through the image. Thereby it
 * just has to add one new block of color to the right side
 * of the stack and remove the leftmost color. The remaining
 * colors on the topmost layer of the stack are either added on
 * or reduced by one, depending on if they are on the right or
 * on the left side of the stack.
 *
 * @copyright: Enrique López Mañas
 * @license: Apache License 2.0
 */

package com.livio.pic;

import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.RSRuntimeException;

public class StackBlurManager {
	/**
	 * Original image
	 */    
	private Bitmap _image;

	/**
	 * Most recent result of blurring
	 */
	private Bitmap _result;

	/**
	 * Method of blurring
	 */
	private final BlurProcess _blurProcess;


	/**
	 * Constructor method (basic initialization and construction of the pixel
	 * array)
	 * 
	 * @param image
	 *            The image that will be analyed
	 */
	public StackBlurManager(Bitmap image) {
		_image = image;
		_blurProcess = new JavaBlurProcess();
//		_blurProcess = new NativeBlurProcess();
	
	}
	
	public void setImage(Bitmap image){
		_image=image;
		
	}

	/**
	 * Process the image on the given radius. Radius must be at least 1
	 * 
	 * @param radius
	 */
	public Bitmap process(int radius, int t_width, int t_height) {
		_result = _blurProcess.blur(_image, radius, t_width, t_height);
		return _result;
	}

	/**
	 * Returns the blurred image as a bitmap
	 * 
	 * @return blurred image
	 */
	public Bitmap returnBlurredImage() {
		return _result;
	}

	/**
	 * Save the image into the file system
	 * 
	 * @param path
	 *            The path where to save the image
	 */
	public void saveIntoFile(String path) {
		try {
			FileOutputStream out = new FileOutputStream(path);
			_result.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the original image as a bitmap
	 * 
	 * @return the original bitmap image
	 */
	public Bitmap getImage() {
		return this._image;
	}

	/**
	 * Process the image using a native library
	 */
	public Bitmap processNatively(int radius, int t_width, int t_height) {
		NativeBlurProcess blur = new NativeBlurProcess();
		_result = blur.blur(_image, radius, t_width, t_height);
		return _result;
	}

	/**
	 * Process the image using renderscript if possible Fall back to native if
	 * renderscript is not available
	 * 
	 * @param context
	 *            renderscript requires an android context
	 * @param radius
	 */
	// public Bitmap processRenderScript(Context context, float radius) {
	// BlurProcess blurProcess;
	// // The renderscript support library doesn't have .so files for ARMv6.
	// // If the
	// try {
	// blurProcess = new RSBlurProcess(context);
	// } catch (RSRuntimeException e) {
	// blurProcess = new NativeBlurProcess();
	// }
	// _result = blurProcess.blur(_image, radius);
	// return _result;
	// }
}
