package com.cs460402.app.async_core;

import java.util.ArrayList;

import android.util.Log;
import android.widget.TextView;

public abstract class UpdateElementBase<E1, E2> implements Runnable {

	protected final E1 element1;
	protected final E2 element2;

	public UpdateElementBase(E1 element1, E2 element2) {
		this.element1 = element1;
		this.element2 = element2; // secondary element, e.g. array adapter for
									// an arrayList
	}

	public void run() {
		Log.i("TASK", "TextView: " + TextView.class.isAssignableFrom(element1.getClass()));
		Log.i("TASK", "ArrayList: " + ArrayList.class.isAssignableFrom(element1.getClass()));
		Log.i("TASK", "StringArray: " + String[].class.isAssignableFrom(element1.getClass()));
		Log.i("TASK", "Runnable: " + Runnable.class.isAssignableFrom(element1.getClass()));

		if (TextView.class.isAssignableFrom(element1.getClass())) {
			updateTextView();
		} else if (ArrayList.class.isAssignableFrom(element1.getClass())) {
			updateArrayList();
		} else if (String[].class.isAssignableFrom(element1.getClass())) {
			updateStringArray();
		} else if (Runnable.class.isAssignableFrom(element1.getClass())) {
			updateRunnable();
		}

	}

	public abstract void updateTextView();

	public abstract void updateArrayList();

	public abstract void updateStringArray();
	
	public abstract void updateRunnable();
}
