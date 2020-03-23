package com.ice.crackflash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ShowAllFile extends AppCompatActivity implements View.OnClickListener{
    private ListView listView;
    private String[] filePathAndName,dataName;
    private ImageView back_iv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition transition = new Fade().setDuration(1000);
            getWindow().setEnterTransition(transition);
            getWindow().setExitTransition(transition);
        }
        getSupportActionBar().hide();
        setContentView(R.layout.showallfile);

        listView = findViewById(R.id.lv);
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(this);
        String Path = Environment.getExternalStorageDirectory() + "/"+"IceCrackFlash";
        filePathAndName = getFilesAllName(Path).toArray(new String[0]);//路径和文件名
        dataName = getFilesAllOnlyName(Path).toArray(new String[0]);//只有文件名

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),filePathAndName[i],Toast.LENGTH_SHORT).show();

                openFile(ShowAllFile.this,new File(filePathAndName[i]));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder delDialog = new AlertDialog.Builder(ShowAllFile.this);
                delDialog.setTitle("提示");
                delDialog.setMessage("确定永久删除文件吗？");
                delDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(getApplicationContext(),filePathAndName[position],Toast.LENGTH_SHORT).show();
                        File delFile = new File(filePathAndName[position]);
                        if (delFile.exists()&&delFile.isFile()){
                            if(delFile.delete()){
                                Toast.makeText(getApplicationContext(),"删除文件成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(),"删除文件失败",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"删除失败，文件不存在",Toast.LENGTH_SHORT).show();
                        }
//                        List<String> dataNameList = new ArrayList<>(dataName.length);
//                        for (String s:dataName){
//                            dataNameList.add(s);
//                        }
//                        dataNameList.remove(position);
//                        dataName = dataNameList.toArray(new String[0]);

                    }
                });
                delDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                delDialog.show();
                return true;
            }
        });
    }

    public static List<String> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){
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
        return s;
    }
    public static List<String> getFilesAllOnlyName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){Log.e("error","空目录");return null;}
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
            s.add(files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("/")+1));
        }
        return s;
    }

    /*
     * 使用自定义方法打开文件,记得修改fileprovider的包名
     * */
    public static void openFile(Activity activityFrom, File file) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //  此处注意替换包名，
            Uri contentUri = FileProvider.getUriForFile(activityFrom, "com.ice.crackflash.fileprovider", file);
            //Log.e("file_open", " uri   " + contentUri.getPath());
            intent.setDataAndType(contentUri, "image/jpeg");
//            intent.setDataAndType(contentUri, "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "image/jpeg");//也可使用 Uri.parse("file://"+file.getAbsolutePath());
        }

        //以下设置都不是必须的
        intent.setAction(Intent.ACTION_VIEW);// 系统根据不同的Data类型，通过已注册的对应Application显示匹配的结果。
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//系统会检查当前所有已创建的Task中是否有该要启动的Activity的Task
        //若有，则在该Task上创建Activity；若没有则新建具有该Activity属性的Task，并在该新建的Task上创建Activity。
        intent.addCategory(Intent.CATEGORY_DEFAULT);//按照普通Activity的执行方式执行
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activityFrom.startActivity(intent);
    }

    /**
     * 使用自定义方法获得文件的MIME类型
     */
//    public static String getMimeTypeFromFile(File file) {
//        String type = "*/*";
//        String fName = file.getName();
//        //获取后缀名前的分隔符"."在fName中的位置。
//        int dotIndex = fName.lastIndexOf(".");
//        if (dotIndex > 0) {
//            //获取文件的后缀名
//            String end = fName.substring(dotIndex, fName.length()).toLowerCase(Locale.getDefault());
//            //在MIME和文件类型的匹配表中找到对应的MIME类型。
//            HashMap<String, String> map = MyMimeMap.getMimeMap();//对比文件类型，看是否能匹配上
//            if (!TextUtils.isEmpty(end) && map.keySet().contains(end)) {
//                type = map.get(end);
//            }
//        }
//        //Log.i("bqt", "我定义的MIME类型为：" + type);
//        return type;
//    }


    @Override
    public void onClick(View view) {
        finish();
    }

}
