package com.lexu.mobileacademy3.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lexu.mobileacademy3.InfoActivity;
import com.lexu.mobileacademy3.ServiceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lexu.mobileacademy3.services.CustomService.ARTICLES_EXTRA;
import static com.lexu.mobileacademy3.services.CustomService.STATUS_CODE;

public class GetInstalledAppsService extends Service {

    private static final String TAG = GetInstalledAppsService.class.getSimpleName();

    private List<String> result = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startBackgroundProcessing();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startBackgroundProcessing() {
        final PackageManager pm = getPackageManager();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<ApplicationInfo> data = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                result = new ArrayList<String>();
                if(data != null) {
                    for (ApplicationInfo info : data) {
                        result.add((String) info.loadLabel(pm));
                    }
                }

                if(result.size() != 0) {
                    Collections.sort(result);
                }

                GetInstalledAppsService.this.sendUpdateNotification();
            }
        };

        new Thread(runnable).start();
    }

    private void sendUpdateNotification() {
        Intent intent = new Intent();
        intent.setAction(InfoActivity.GET_INSTALLED_APPS_ACTION);

        ServiceUtils.Wrapper<String> wrapper = new ServiceUtils.Wrapper<String>();
        wrapper.data = result;

        intent.putExtra(STATUS_CODE, 2);
        intent.putExtra(ARTICLES_EXTRA, wrapper);
        sendBroadcast(intent);

        GetInstalledAppsService.this.stopSelf();
    }
}
