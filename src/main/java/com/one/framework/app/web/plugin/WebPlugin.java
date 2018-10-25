package com.one.framework.app.web.plugin;

import android.content.Intent;
import android.os.Bundle;
import com.one.framework.app.web.plugin.model.WebActivityParamsModel;

/**
 * Created by huangqichan on 2015/9/23.
 */
public interface WebPlugin {

    void onCreate(WebActivityParamsModel webActivityParamsModel);

    void onStart();

    void onReStart();

    void onResume();

    void onSaveInstanceState(Bundle outState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onPause();

    void onStop();

    void onDestroy();
}
