package com.cs460402.app.io;

import java.util.ArrayList;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.cs460402.app.Route;
import com.cs460402.app.RouteTask;
import com.cs460402.app.async_core.ThreadManagerBase;
import com.cs460402.app.async_core.UIHandler;
import com.cs460402.app.io.sqlite.SQLiteConstants;

public class AppIO extends ThreadManagerBase {

	private final UIHandler handlerUI;

	public AppIO(int threadPoolSize) {
		super(threadPoolSize);
		this.handlerUI = new UIHandler();
	}

	// calculate route
	public int calculateRoute(Context context, Route route, TextView textview) {
		// create task
		RouteTask routeTask = new RouteTask(context, handlerUI, route, textview);

		// attach progress dialog
		routeTask.addProgressDialog(context, null, "Working, please wait..."); // TODO refactor this to use @String

		// submit task
		Future<Integer> future = this.executorService.submit(routeTask);

		// enable task canceling
		routeTask.initOnCancelListener(future);

		return 0;
	}

	// external database
	// public void extDb_async_getData(boolean bProgressDialog, int queryType,
	// String ... params) {
	// Log.i("ROUTE", "extDb_async_getData");
	// setupAsyncTask(new ExtDatabaseTask(activity.getResources(), queryType,
	// params), bProgressDialog);
	// }

	// public ArrayList<String> extDb_getData(int queryType, String ... params)
	// {
	// return ((ExtDatabaseTask) setupTask(new
	// ExtDatabaseTask(activity.getResources(), queryType,
	// params))).doInThisThread();
	// //onComplete();
	// }

	// SQLite
	public <E1, E2> Future<ArrayList<?>> sqlite_async_getData(int querytype, int progress, E1 element1, E2 element2, Activity mActivity, String... params) {
		// create task
		SQLiteIOTask<E1, E2> sqliteTask = new SQLiteIOTask<E1, E2>(mActivity, handlerUI, querytype, params, element1, element2);

		// attach progress bar/dialog
		switch (progress) {
		case SQLiteConstants.PROGRESS_BAR:
			// app is not using progress bars
			break;
		case SQLiteConstants.PROGRESS_BAR_INDETERMINATE:
			sqliteTask.addIndeterminateProgressBar(mActivity);
			break;
		case SQLiteConstants.PROGRESS_DIALOG:
			sqliteTask.addProgressDialog(mActivity, null,
					"Working, please wait...");
			break;

		default:
			// do nothing - no progress bar/dialog
			break;
		}

		// submit task
		Future<ArrayList<?>> future = this.executorService.submit(sqliteTask);

		// enable task canceling
		sqliteTask.initOnCancelListener(future);
		
		return future;

	}

	// public void sqlite_getData(int queryType, String ... params) {
	// ((SQLiteTask) setupTask(new SQLiteTask(activity, queryType,
	// params))).doInThisThread();
	// }

}
