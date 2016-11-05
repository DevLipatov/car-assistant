package com.main.carassistant.threads;

public interface OnResultCallback<Result, Progress> extends ProgressCallback<Progress> {

    void onSuccess(Result result);

    void onError(Exception e);
}
