package example.myapplication19;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2019/9/15 0015.
 */

public class Permissions
{
    static  final int PEMISSION_REQ_CODE = 1;
    public static  void check(Activity activity)
    {
        final String[] permission = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA
        };

        if(ContextCompat.checkSelfPermission(activity, permission[0])!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity, permission, PEMISSION_REQ_CODE);
        }
    }
    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
//    {
//
//        if(requestCode == PERMISSION_REQ_CODE)
//        {
//            for(int i=0; i<permissions.length;i++)
//            {
//                if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
//                {
//                    // 惨,用户没给我们授权...这意味着有此功能就不能用了
//                }
//            }
//        }
//    }
}
