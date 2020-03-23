package com.ice.crackflash;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button getFile_btn,saveFile_btn,showFile_btn;
    private ImageView iv,showUsage_iv;
    private DrawerLayout drawerLayout;
    private Context context;
    private String[] fileNameAndPath;
    private boolean isCanSave = false;
    private String newPathDir="";
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        drawerLayout = findViewById(R.id.drawerLayout);
        iv = findViewById(R.id.iv);
        showUsage_iv = findViewById(R.id.showUsage_iv);
        showUsage_iv.setOnClickListener(this);
        getFile_btn=findViewById(R.id.getFile_btn);
        getFile_btn.setOnClickListener(this);
        saveFile_btn = findViewById(R.id.saveFile_btn);
        saveFile_btn.setOnClickListener(this);
        showFile_btn = findViewById(R.id.showFile_btn);
        showFile_btn.setOnClickListener(this);

        checkPermission();
        newPathDir = Environment.getExternalStorageDirectory()+"/"+"IceCrackFlash";
        try{
            File dir = new File(newPathDir);
            if(!dir.exists()){
                dir.mkdir();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //获取文件列表
    public static List<String> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files.length == 0){
            Log.e("error","空目录");return null;}

            //对文件列表进行排序操作
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    long diff = f1.lastModified()-f2.lastModified();
                    if(diff>0)
                        return -1;
                    else if(diff==0)
                        return 0;
                    else return 1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
                }
        });
        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            s.add(files[i].getAbsolutePath());
        }
        //Log.e("test", "getFilesAllName: "+s);
        return s;
    }

    /**
     *  请求读写权限
     */
    private void checkPermission() {
        //检查当前权限（若没有该权限，值为-1；若有该权限，值为0）
        //授权读取权限，READ_EXTERNAL_STORAGE

        int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if(hasReadExternalStoragePermission== PackageManager.PERMISSION_GRANTED){
            // 处理自己的逻辑
            //Toast.makeText(getApplicationContext(),"有权限",Toast.LENGTH_SHORT).show();
        }else{
            //若没有授权，会弹出一个对话框（这个对话框是系统的，开发者不能自己定制），用户选择是否授权应用使用系统权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            //Toast.makeText(getApplicationContext(),"请求权限",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     *  用户选择是否同意授权后，会回调这个方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1){
            if(permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //用户同意授权，执行读取文件的代码
            }else{
                //若用户不同意授权，直接暴力退出应用。
                // 当然，这里也可以有比较温柔的操作。
                Toast.makeText(getApplicationContext(),"您拒绝读写权限，无法保存文件",Toast.LENGTH_LONG).show();
                //finish();
            }

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.getFile_btn:

                String Path = Environment.getExternalStorageDirectory() + "/"+"tencent/MobileQQ/diskcache";  //8.2.8之前版本的存储目录
                String Path2 = Environment.getExternalStorageDirectory() + "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/chatimg";  //8.2.8及之后版本的存储目录
                File diskCacheDir = new File(Path);
                File diskCacheDir2 = new File(Path2);
                if (!diskCacheDir.exists()){
                    diskCacheDir.mkdir();
                }
                if (!diskCacheDir2.exists()){
                    diskCacheDir2.mkdir();
                }

                //8.2.8及之后版本的存储目录
                if(getFilesAllName(Path2)!=null){
                    List<String> AllFileList = getFilesAllName(Path2);
                    String latestDir = AllFileList.get(0);
                    fileNameAndPath = getFilesAllName(latestDir).toArray(new String[0]);
                    //Log.e("新版本所有文件", "onClick: "+fileNameAndPath[0]);
                    Bitmap bm = BitmapFactory.decodeFile(fileNameAndPath[0]);
                    iv.setImageBitmap(bm);
                    isCanSave = true;
                }

                //8.2.8之前版本的存储目录
                else if(getFilesAllName(Path)!=null)    //不要使用!getFilesAllName(Path).isEmpty()判断，否则可能会空指针异常
                {
                    fileNameAndPath = getFilesAllName(Path).toArray(new String[0]);
                    Bitmap bm = BitmapFactory.decodeFile(fileNameAndPath[0]);
                    iv.setImageBitmap(bm);
                    isCanSave = true;

                }


                else {
                    Toast.makeText(getApplicationContext(),"获取闪照失败，目录为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.saveFile_btn:
                //Toast.makeText(getApplicationContext(),"点击2",Toast.LENGTH_SHORT).show();
                if(isCanSave){
                    //Toast.makeText(getApplicationContext(),"允许保存",Toast.LENGTH_SHORT).show();
                    File dir = new File(newPathDir);
                    if(!dir.exists()){
                        dir.mkdir();
                    }

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
                    Date date = new Date(System.currentTimeMillis());
                    String newPath = newPathDir+"/"+simpleDateFormat.format(date)+".jpg";
                    if(copyFile(fileNameAndPath[0],newPath)){
                        Toast.makeText(getApplicationContext(),"保存成功:"+newPath,Toast.LENGTH_SHORT).show();
                    }
                    isCanSave = false;
                }else {
                    Toast.makeText(getApplicationContext(),"请先查看闪照",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.showFile_btn:
                //Toast.makeText(getApplicationContext(),"点击3",Toast.LENGTH_SHORT).show();
                File dir = new File(newPathDir);
                if(!dir.exists()){
                    dir.mkdir();
                }
                Intent intent = new Intent(MainActivity.this,ShowAllFile.class);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                }else {
                    startActivity(intent);
                }

                break;
            case R.id.showUsage_iv:
                drawerLayout.openDrawer(Gravity.END);
                break;
        }

    }

    private boolean copyFile(String oldPathFile,String newPathFile){
        try{
            File oldFile = new File(oldPathFile);
            if(!oldFile.exists()||!oldFile.isFile()||!oldFile.canRead()){
                return  false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPathFile);
            FileOutputStream fileOutputStream = new FileOutputStream(newPathFile);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();

            return true;
        }catch (Exception e){
            Log.e("copyFile异常",e.toString());
            return false;
        }
    }

    //再按一次退出
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            startTime = currentTime;
        } else {
            finish();
        }
    }
}

