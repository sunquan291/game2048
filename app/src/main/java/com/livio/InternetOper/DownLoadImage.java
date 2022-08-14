package com.livio.InternetOper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.livio.game2048.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownLoadImage extends AsyncTask<String, Void, Bitmap> {

	private ImageView iv;
	private Context context;

	public DownLoadImage() {
		super();
	}

	public DownLoadImage(Context context,ImageView iv) {
		super();
		this.iv = iv;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		URL url;
		Bitmap bm = null;
		try {
			url = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6 * 1000);
			if (conn.getResponseCode() != 200)
				throw new RuntimeException("请求url失败");
			InputStream is = conn.getInputStream();
			bm = BitmapFactory.decodeStream(is);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bm;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (result != null) {
			iv.setImageBitmap(result);
			MainActivity.getMainActivity().getStackBlurManager()
					.setImage(result);
		} else {
			Bitmap bm=MainActivity.getMainActivity().getBitmapFromLocal(context, MainActivity.indexOfPic);
			iv.setImageBitmap(result);
			MainActivity.getMainActivity().getStackBlurManager().setImage(bm);
		}
		super.onPostExecute(result);
	}

}
