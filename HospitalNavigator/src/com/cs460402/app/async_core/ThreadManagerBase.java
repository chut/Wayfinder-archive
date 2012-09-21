package com.cs460402.app.async_core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadManagerBase {

	protected final ExecutorService executorService;

	public ThreadManagerBase(int threadPoolSize) {
		this.executorService = Executors.newFixedThreadPool(threadPoolSize);
	}

}
