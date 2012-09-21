package com.cs460402.app.async_core;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import android.widget.ProgressBar;

public abstract class TaskBase<V> implements Callable<V>, OnCancelListener {

	protected final UIHandler handlerUI;
	protected Activity mActivity;
	protected ProgressBar mProgressBar;
	protected boolean bHasIndeterminateProgressBar;
	protected ProgressDialog mProgressDialog;
	protected Future<V> future;
	private boolean bToggleProgress;

	public TaskBase(UIHandler handlerUI) {
		this.handlerUI = handlerUI; // used to post runnables to the UI
		this.mActivity = null; // needed for indeterminate progress bar
		this.mProgressBar = null;
		this.bHasIndeterminateProgressBar = false;
		this.mProgressDialog = null;
		this.future = null; // used for canceling task
		this.bToggleProgress = false;
	}

	public final void addProgressDialog(Context context, String title,
			String message) {
		mProgressDialog = new ProgressDialog(context);

		if (title != null)
			mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(message);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
	}

	public final void initOnCancelListener(Future<V> future) {
		this.future = future;
		if (mProgressDialog != null) {
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(this);
		}
	}

	public final void addProgressBar(ProgressBar mProgressBar) {
		this.mProgressBar = mProgressBar;
	}

	public final void addIndeterminateProgressBar(Activity mActivity) {
		this.mActivity = mActivity;
		this.bHasIndeterminateProgressBar = true;
		Log.i("TAG", "addIndeterminateProgressBar");
	}

	public final void toggleProgress() {
		// dont do anything if progress has not been enabled
		if (mProgressDialog != null || bHasIndeterminateProgressBar
				|| mProgressBar != null) {
			if (bToggleProgress) {
				// progress is currently ON

				// turn progress off
				bToggleProgress = !bToggleProgress;

				if (mProgressDialog != null)
					this.handlerUI.post(new ProgressManager(mProgressDialog,
							AsyncConstants.PROGRESS_CLOSE_DIALOG, null));
				if (bHasIndeterminateProgressBar || mProgressBar != null) {
					Log.i("TAG", "toggleProgress - off");
					this.handlerUI.post(new ProgressManager(mProgressBar,
							mActivity, AsyncConstants.PROGRESS_CLOSE_BAR, 0,
							null));
				}

			} else {
				// progress is currently OFF

				// turn progress on
				bToggleProgress = !bToggleProgress;

				if (mProgressDialog != null)
					this.handlerUI.post(new ProgressManager(mProgressDialog,
							AsyncConstants.PROGRESS_SHOW_DIALOG, null));
				if (bHasIndeterminateProgressBar || mProgressBar != null) {
					Log.i("TAG", "toggleProgress - on");
					this.handlerUI.post(new ProgressManager(mProgressBar,
							mActivity, AsyncConstants.PROGRESS_SHOW_BAR, 0,
							null));
				}
			}
		}
	}

	public abstract void onCancel(DialogInterface dialog);

	public abstract V call() throws Exception;
}