package com.main.carassistant.threads;

public interface ProgressCallback<Progress> {

    void onProgressChanged(Progress progress);

}
