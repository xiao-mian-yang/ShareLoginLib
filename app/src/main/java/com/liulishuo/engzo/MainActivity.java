package com.liulishuo.engzo;

import com.liulishuo.demo.R;
import com.liulishuo.share.LoginManager;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.ShareManager;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentPic;
import com.liulishuo.share.content.ShareContentText;
import com.liulishuo.share.content.ShareContentWebPage;
import com.liulishuo.share.type.LoginType;
import com.liulishuo.share.type.ShareType;
import com.squareup.picasso.Picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 步骤：
 * 1.添加混淆参数
 * 2.在包中放入微信必须的activity
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String URL = "https://www.zhihu.com/question/22913650";

    public static final String TITLE = "这里是标题";

    public static final String MSG = "这是描述信息";

    private ShareContent mShareContent;

    private ImageView tempPicIv;

    private TextView userInfoTv;

    private ImageView userPicIv;

    private TextView resultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempPicIv = (ImageView) findViewById(R.id.temp_pic_iv);
        RadioGroup shareTypeRg = (RadioGroup) findViewById(R.id.share_type_rg);
        userInfoTv = (TextView) findViewById(R.id.user_info_tv);
        userPicIv = (ImageView) findViewById(R.id.user_pic_iv);
        resultTv = (TextView) findViewById(R.id.result);

        assert getResources().getDrawable(R.drawable.kale) != null;
        final Bitmap imageBmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.kale)).getBitmap();

        loadPicFromTempFile();
        String imagePic = "http://aliimg.changba.com/cache/photo/177593234_200_200.jpg";

//        imagePic = "http://ww1.sinaimg.cn/mw690/854f1e58gw1f8ao1y3xd2j215o15ogrv.jpg";

//        imagePic = "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white_fe6da1ec.png";

        final String imageUrl = imagePic; // 仅仅qq分享的sdk支持url，但是竟然不支持https的图片！！！

        mShareContent = new ShareContentWebPage(TITLE, MSG, URL, imageBmp, imagePic);

        shareTypeRg.check(R.id.rich_text);
        shareTypeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rich_text) {
                    mShareContent = new ShareContentWebPage(TITLE, MSG, URL, imageBmp, imageUrl);
                } else if (checkedId == R.id.only_image) {
                    mShareContent = new ShareContentPic(imageBmp, imageUrl);
                } else if (checkedId == R.id.only_text) {
                    mShareContent = new ShareContentText("share text");
                }
            }
        });

        Toast.makeText(MainActivity.this, getPackageName(), Toast.LENGTH_SHORT).show();
    }

    private void loadPicFromTempFile() {
        try {
            String path = ShareBlock.Config.pathTemp + "sharePic_temp";
            File file = new File(path);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                tempPicIv.setImageBitmap(BitmapFactory.decodeStream(fis));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        ShareManager.ShareStateListener mShareListener = new ShareListener(this);

        int i = v.getId();
        switch (i) {
            case R.id.QQ登录:
                LoginManager.login(this, LoginType.QQ, new LoginListener(this, LoginType.QQ));
                break;
            case R.id.微博登录:
                LoginManager.login(this, LoginType.WEIBO, new LoginListener(this, LoginType.WEIBO));
                break;
            case R.id.微信登录:
                LoginManager.login(this, LoginType.WEIXIN, new LoginListener(this, LoginType.WEIXIN));
                break;
            case R.id.分享给QQ好友:
                ShareManager.share(this, ShareType.QQ_FRIEND, mShareContent, mShareListener);
                break;
            case R.id.分享到QQ空间:
                ShareManager.share(this, ShareType.QQ_ZONE, mShareContent, mShareListener);
                break;
            case R.id.分享到微博:
                ShareManager.share(this, ShareType.WEIBO_TIME_LINE, mShareContent, mShareListener);
                break;
            case R.id.分享给微信好友:
                ShareManager.share(this, ShareType.WEIXIN_FRIEND, mShareContent, mShareListener);
                break;
            case R.id.分享到微信朋友圈:
                ShareManager.share(this, ShareType.WEIXIN_FRIEND_ZONE, mShareContent, mShareListener);
                break;
        }
        userInfoTv.setText("");
        userPicIv.setImageResource(0);
        resultTv.setText("");
    }

    public void onGotUserInfo(@Nullable String text, @Nullable String imageUrl) {
        userInfoTv.setText(text);
        Picasso.with(this).load(imageUrl).into(userPicIv);
    }

    public void handResult(String result) {
        resultTv.setText(result);
    }
}
