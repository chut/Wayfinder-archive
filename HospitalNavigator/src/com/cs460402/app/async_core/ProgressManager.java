package com.cs460402.app.async_core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ProgressBar;

public class ProgressManager implements Runnable {

	private final Activity mActivity;
	private final ProgressBar mProgressBar;
	private final ProgressDialog mProgressDialog;
	private final int tasktype;
	private final int value;
	private final String message;

	private boolean indeterminateBar;

	// constructor for progress bar
	public ProgressManager(ProgressBar mProgressBar, Activity mActivity,
			int tasktype, int value, String message) {
		this.mActivity = mActivity;
		this.mProgressBar = mProgressBar;
		this.mProgressDialog = null;
		this.tasktype = tasktype;
		this.value = value;
		this.message = message;

		this.indeterminateBar = true; // by default, we show indeterminate
										// progress bar
	}

	// constructor for progress dialog
	public ProgressManager(ProgressDialog mProgressDialog, int tasktype,
			String message) {
		this.mActivity = null;
		this.mProgressBar = null;
		this.mProgressDialog = mProgressDialog;
		this.tasktype = tasktype;
		this.value = -1;
		this.message = message;

		this.indeterminateBar = true; // by default, we show indeterminate
										// progress bar
	}

	public void setIndeterminateBar(boolean b) {
		this.indeterminateBar = b;
	}

	public void run() {
		switch (tasktype) {
		case AsyncConstants.PROGRESS_SHOW_BAR:
			showProgressBar();
			break;

		case AsyncConstants.PROGRESS_SHOW_DIALOG:
			showProgressDialog();
			break;

		case AsyncConstants.PROGRESS_UPDATE_BAR:
			updateProgressBar();
			break;

		case AsyncConstants.PROGRESS_UPDATE_DIALOG:
			updateProgressDialog();
			break;

		case AsyncConstants.PROGRESS_CLOSE_BAR:
			closeProgressBar();
			break;

		case AsyncConstants.PROGRESS_CLOSE_DIALOG:
			closeProgressDialog();
			break;

		default:
			break;
		}

	}

	private void showProgressBar() {
		if (indeterminateBar) {
			// Request for the progress bar to be shown in the title
			// this statement has to be made before
			// "setContentView(R.layout.xxxx)"
			// activity.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

			// show bar
			Log.i("TAG", "ProgressManager - showProgressBar");
			mActivity.setProgressBarIndeterminateVisibility(true);

		} else {
			// we are not using horizontal bars in this app, so I wont bother
			// coding this
			// if (titleBar)
			// activity.requestWindowFeature(Window.FEATURE_PROGRESS);

			// mProgressBar.setProgress(somenumber);
			// setProgress(mProgressBar.getProgress() * 100);

		}
	}

	private void showProgressDialog() {
		if (mProgressDialog != null)
			mProgressDialog.show();
	}

	private void updateProgressBar() {
		if (indeterminateBar) {
			// do nothing

		} else {
			// we are not using horizontal bars in this app, so I wont bother
			// coding this
			// if (titleBar)
			// activity.requestWindowFeature(Window.FEATURE_PROGRESS);

			// mProgressBar.setProgress(somenumber);
			// setProgress(mProgressBar.getProgress() * 100);

		}
	}

	private void updateProgressDialog() {
		mProgressDialog.setMessage(message);
	}

	private void closeProgressBar() {
		if (indeterminateBar) {
			// hide bar
			mActivity.setProgressBarIndeterminateVisibility(false);

		} else {
			// we are not using horizontal bars in this app, so I wont bother
			// coding this

		}
	}

	private void closeProgressDialog() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}
}
