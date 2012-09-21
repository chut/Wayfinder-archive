package com.cs460402.app;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.cs460402.app.async_core.TaskBase;
import com.cs460402.app.async_core.UIHandler;

public class RouteTask extends TaskBase<Integer> {

	private final Context context;
	private final Route route;
	private final TextView element; // element to be updated - i.e. ArrayList,
									// TextView, etc...

	/* UI Thread */
	public RouteTask(Context context, UIHandler handlerUI, Route route,
			TextView element) {
		super(handlerUI);
		this.context = context;

		this.route = route;
		this.element = element;
	}

	/* Separate Thread */
	public Integer call() throws Exception {
		toggleProgress();
		Log.i("ROUTE", "start task");
		final int result = route.initialize().calculateRoute();
		Log.i("ROUTE", "done with calc - displaying comes next");

		// for testing - make this task last a while
		// Thread.sleep(5000);

		// for testing, display results in text view
		if (this.future == null || !this.future.isCancelled()) {
			Log.i("ROUTE", "posting results");
			this.handlerUI.post(new Runnable() {
				public void run() {
					element.append("runResult = " + result + "\n");
					element.append(route.toString() + "\n");
					element.append(route.getMyMetrics().toString() + "\n");
				}
			});
		}

		// this.handlerUI.post(new Runnable() {
		// public void run() {
		// Toast.makeText(context, "Task completed", Toast.LENGTH_SHORT).show();
		// }
		// });

		toggleProgress();
		return result;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (this.future != null)
			this.future.cancel(true);
		Toast.makeText(context, "Task canceled!", Toast.LENGTH_SHORT).show();
	}

}
