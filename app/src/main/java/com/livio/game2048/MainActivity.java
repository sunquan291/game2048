package com.livio.game2048;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.livio.InternetOper.DownLoadImage;
import com.livio.pic.StackBlurManager;

import java.io.*;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.livio.game2048.R.*;

public class MainActivity extends Activity {

    private TextView txScore;
    private ImageView beauty;
    private Button btnshare;
    /**
     * 处理图片模糊
     */
    private StackBlurManager stackBlurManager;

    private TextView tvTime;

    private boolean unlock = false;

    private Bitmap girls;

    private static final String url = "http://i.pbase.com/o6/92/229792/1/80199697.uAs58yHk.50pxCross_of_the_Knights_Templar_svg.png";
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_test/";
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            activityHandler.sendEmptyMessage(1);
        }
    };
    private Handler activityHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    time++;
                    tvTime.setText(time + "");
                    break;
                case 2:
                    blurBitmap(radius);
                    break;
                default:break;

            }
            super.handleMessage(msg);
        }
    };

    private static final int upScore = 1000;// 最高分，即图片解锁时所需要的分数
    private int radius = upScore / 100;

    private int oldRadius = radius;
    private int score = 0;
    private static MainActivity mainActivity;

    // 系统提供的图片数量
    public static final int picNums = 14;

    // 时间
    private int time = 0;
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
//    private Timer timer = new Timer();

    public static int indexOfPic;

    // 音效 播放器引用
    private MediaPlayer musicPlay = null;

    public StackBlurManager getStackBlurManager() {
        return stackBlurManager;
    }

    public MainActivity() {
        super();
        mainActivity = this;
        indexOfPic = (int) (Math.random() * picNums + 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        txScore = (TextView) findViewById(id.tvScore);
        beauty = (ImageView) findViewById(id.beauty);
        tvTime = (TextView) findViewById(id.tvTime);
        btnshare = (Button) findViewById(id.btnShare);
        btnshare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(v.getContext(), "dd",
                // Toast.LENGTH_LONG).show();
                share();
            }
        });
        // musicPlay = MediaPlayer.create(this, R.raw.gamepast);
    }

    /**
     * 分享
     */
    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Game2048");
        intent.putExtra(Intent.EXTRA_TEXT, "在约定291天后的2048游戏中,我获得了" + score + "分,接下来看你的了.http://yunpan.cn/cKLMedns2XVCt  提取码 8662");
        try {
            /** 获得截图，并保存至本地 */
            saveBitmapToFile(getScreenShot(), ALBUM_PATH + "b.png");
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /** 开始选择分享 */
        Uri uri = Uri.fromFile(new File(ALBUM_PATH + "b.png"));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }

    public Bitmap getScreenShot() {
        // 获取windows中最顶层的view
        View view = mainActivity.getWindow().getDecorView();
        view.buildDrawingCache();
        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = mainActivity.getWindowManager().getDefaultDisplay();
        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();
        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeights, widths, heights - statusBarHeights);
        // 销毁缓存信息
        view.destroyDrawingCache();
        return bmp;

    }

    private void saveToSD(Bitmap bmp, String dirName, String fileName) throws IOException {
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(dirName);
            // 判断文件夹是否存在，不存在则创建
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dirName + fileName);
            // 判断文件是否存在，不存在则创建
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                if (fos != null) {
                    // 第一参数是图片格式，第二个是图片质量，第三个是输出流
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    // 用完关闭
                    fos.flush();
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveBitmapToFile(Bitmap bitmap, String _file) throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
            // String _filePath_file.replace(File.separatorChar +
            // file.getName(), "");
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        /** 获取图片 */
        Bitmap bm = getBitmapFromAsset(getApplicationContext(), indexOfPic);
        stackBlurManager = new StackBlurManager(bm);
        blurBitmap(radius);
        // 开始计时器
        timer.scheduleWithFixedDelay(task, 2000, 1000, TimeUnit.MILLISECONDS);
//        timer.schedule(task, 2000, 1000);
        super.onPostCreate(savedInstanceState);
    }

    private void blurBitmap(int score) {
        int t_height = getWindowManager().getDefaultDisplay().getHeight();
        int t_width = getWindowManager().getDefaultDisplay().getWidth();
        beauty.setImageBitmap(stackBlurManager.process(score, t_width, t_height));
    }

    /**
     * 网络上下载图片
     *
     * @param context 上下文
     * @param url     图片路径
     * @return
     */
    private Bitmap getBitmapFromInternet(Context context, String url) {
        InputStream is = getResources().openRawResource(drawable.beautify0);
        DownLoadImage downloadFromInternet = new DownLoadImage(getApplicationContext(), beauty);
        downloadFromInternet.execute(url);
        return BitmapFactory.decodeStream(is);

    }

    /***
     * 在本地获取图片
     *
     * @param context
     * @param i
     * @return
     */
    public Bitmap getBitmapFromLocal(Context context, int i) {
        InputStream is;
        switch (i) {
            case 1:
                is = getResources().openRawResource(drawable.beautify1);
                break;
            case 2:
                is = getResources().openRawResource(drawable.beautify2);
                break;
            case 3:
                is = getResources().openRawResource(drawable.beautify3);
                break;
            case 4:
                is = getResources().openRawResource(drawable.beautify4);
                break;
            case 5:
                is = getResources().openRawResource(drawable.beautify5);
                break;
            case 6:
                is = getResources().openRawResource(drawable.beautify6);
                break;
            case 7:
                is = getResources().openRawResource(drawable.beautify7);
                break;
            case 8:
                is = getResources().openRawResource(drawable.beautify8);
                break;
            case 9:
                is = getResources().openRawResource(drawable.beautify9);
                break;
            case 10:
                is = getResources().openRawResource(drawable.beautify10);
                break;
            case 11:
                is = getResources().openRawResource(drawable.beautify11);
                break;
            case 12:
                is = getResources().openRawResource(drawable.beautify12);
                break;
            case 13:
                is = getResources().openRawResource(drawable.beautify13);
                break;
            case 14:
                is = getResources().openRawResource(drawable.beautify14);
                break;
            default:
                is = getResources().openRawResource(drawable.beautify0);
        }
        return BitmapFactory.decodeStream(is);
    }

    /***
     * 判断设备是否连网
     *
     * @return
     */
    private boolean isConnectingToInternet(Context context) {
        boolean bm = false;
        return bm;
    }

    private boolean isWiFiActive(Context inContext) {
        WifiManager mWifiManager = (WifiManager) inContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            System.out.println("**** WIFI is on");
            return true;
        } else {
            System.out.println("**** WIFI is off");
            return false;
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            System.out.println("**** newwork is off");
            return false;
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info == null) {
                System.out.println("**** newwork is off");
                return false;
            } else {
                if (info.isAvailable()) {
                    System.out.println("**** newwork is on");
                    return true;
                }

            }
        }
        System.out.println("**** newwork is off");
        return false;
    }

    /***
     * 获得图片
     *
     * @param context
     * @param i
     * @return
     */
    private Bitmap getBitmapFromAsset(Context context, int i) {

        Bitmap mBitmap = null;
        // if (isConnectingToInternet(getApplicationContext())) {
        // mBitmap = getBitmapFromInternet(context, url);
        // } else {
        mBitmap = getBitmapFromLocal(context, i);
        // }
        // InputStream is =
        // getResources().openRawResource(R.drawable.ic_launcher);
        // mBitmap=BitmapFactory.decodeStream(is);
        return mBitmap;
    }

    public void clearScore() {
        score = 0;
        time = 0;
        showScore(score);
        unlock = false;
        radius = upScore / 100;
        activityHandler.sendEmptyMessage(2);
    }

    /**
     * 增加分数
     */
    public void addScore(int num) {
        score += num;
        showScore(score);
        updateGrils();
    }

    private void updateGrils() {
        int tempRadius = oldRadius - score / 100;
        if (tempRadius <= 0 && !unlock) {
            beauty.setImageBitmap(stackBlurManager.getImage());
            Toast.makeText(this, "Congratulations, photos have unlocked.", Toast.LENGTH_LONG).show();
            unlock = true;
            radius = 0;
        } else if (tempRadius < radius) {
            radius = tempRadius;
            activityHandler.sendEmptyMessage(2);

        }
    }

    private void showScore(int score) {
        txScore.setText(score + "");
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public void gameOver() {
        musicPlay = MediaPlayer.create(this, raw.gameover);
        musicPlay.setVolume(0.2f, 0.2f);
        musicPlay.start();
    }

}
