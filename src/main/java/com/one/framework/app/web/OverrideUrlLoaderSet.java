package com.one.framework.app.web;

import android.webkit.WebView;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author xianchaohua
 */

public class OverrideUrlLoaderSet implements OverrideUrlLoader {

    private Set<OverrideUrlLoader> overrideUrlLoaders = new LinkedHashSet();

    public OverrideUrlLoaderSet() {
    }

    public void addOverrideUrlLoader(OverrideUrlLoader loader) {
        if(loader != null) {
            this.overrideUrlLoaders.add(loader);
        }
    }

    public boolean removeOverrideUrlLoader(OverrideUrlLoader loader) {
        return this.overrideUrlLoaders.remove(loader);
    }

    public void clearAllOverrideUrlLoaders() {
        this.overrideUrlLoaders.clear();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        Iterator<OverrideUrlLoader> iterator = overrideUrlLoaders.iterator();

        OverrideUrlLoader loader;
        while(iterator.hasNext()) {
            loader = iterator.next();
            boolean override = loader.shouldOverrideUrlLoading(view, url);
            if (override) {
                return true;
            }
        }
        return false;
    }
}
