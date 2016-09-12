# RuntimePermission
We know that before Android 6.0, the system granted all the permissions declared in AndroidManifest.xml, once our app is installed. 
But after Android 6.0, developers need to request specific permissions at runtime. To request a permission in a Activity is easy. We
request the permission, then deal the result in onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults).
But how about we do it in the background, in a Service for example. The Service does not have the method onRequestPermissionsResult like an Activity. 

Here, we got a way.

First, we start an Activity with transparent theme from the background. 
Then we request permissions and handle the result in the transparent Activity. 
Finally, the result will be delivered via a BroadcastReceriver.

the code like below:
```java
public class MyService extends Service implements OnPermissionRequestResult {

    private PermissionRequestResultReceiver mPermissionRequestReceiver;

    private final static String ACTION_REQUEST_PERMISSION = "com.ivan.runtimepermission.intent.action.ACTION_REQUEST_PERMISSION";
    private static final int REQUEST_CODE = 0x3000;

    @Override
    public void onCreate() {
        super.onCreate();
        mPermissionRequestReceiver = new PermissionRequestResultReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PermissionRequestResultReceiver.ACTION_REQUEST_PERMISSION_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mPermissionRequestReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = new Bundle();
        b.putString("data", "this_is_data");
        PermissionActivity.startActivity(MyService.this, b, REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPermissionRequestReceiver);
    }
    @Override
    public void onPermissionGranted(int requestCode, Bundle data) {
        Toast.makeText(MyService.this, "permission granted " + data.get("data"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(int requestCode, Bundle data) {
        Toast.makeText(MyService.this, "permission denied " + data.get("data"), Toast.LENGTH_SHORT).show();
    }
}
```
