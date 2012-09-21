package com.metrics;

import java.util.ArrayList;

public class MetricGroup {

	private ArrayList<Metric> metrics;
	private String dbaseProvider;

	// constructor
	public MetricGroup() {
		this.metrics = new ArrayList<Metric>();
		this.dbaseProvider = "";
	}

	public void setDbaseProvider(String dbaseProvider) {
		this.dbaseProvider = dbaseProvider;
	}

	public String getDbaseProvider() {
		return dbaseProvider;
	}

	public ArrayList<Metric> getMetrics() {
		return metrics;
	}

	public Metric getMetricsByID(String metricID) {
		if ((metrics == null) || (metrics.isEmpty())) {
			return null;
		}

		for (Metric m1 : metrics) {
			if (m1.getID().equals(metricID))
				return m1;
		}

		return null;
	}

	public Metric addMetric(String metricID) {
		Metric m1 = new Metric(metricID);

		metrics.add(m1);
		return m1;
	}

	private long calcAverage(long[] myList) {
		long runningTotal = 0;

		for (long l : myList) {
			runningTotal = runningTotal + l;
		}

		return (runningTotal / myList.length);
	}

	private long calcMedian(long[] myList) {
		long temp;
		long median;

		for (int i = 1; i < myList.length; i++) {
			for (int j = 0; j < myList.length - i; j++) {
				if (myList[j] > myList[j + 1]) {
					temp = myList[j];
					myList[j] = myList[j + 1];
					myList[j + 1] = temp;
				}
			}
		}

		if ((myList.length % 2) == 1) {
			// for an odd count
			int index = (int) myList.length / 2;
			median = myList[index];
		} else {
			// for an even count
			int index1 = (int) myList.length / 2;
			int index2 = index1 - 1;
			median = ((myList[index1] + myList[index2]) / 2);
		}

		return median;
	}

	public long medianMetricTime() {
		long myList[] = new long[metrics.size()];

		for (int i = 0; i < metrics.size(); i++) {
			myList[i] = metrics.get(i).getTime();
		}

		return calcMedian(myList);
	}

	public long medianMetricNanoTime() {
		long myList[] = new long[metrics.size()];

		for (int i = 0; i < metrics.size(); i++) {
			myList[i] = metrics.get(i).getNanoTime();
		}

		return calcMedian(myList);
	}

	@Override
	public String toString() {
		String output = "";

		for (Metric m1 : metrics) {
			output = output + m1.getID() + " --------------" + "\n";
			if (dbaseProvider.length() != 0)
				output = output + "  " + dbaseProvider + "\n";
			output = output + "  algorithm looped " + m1.getAlgorithmLoops()
					+ " times" + "\n";
			if (m1.getAlgorithmLoops() != 0)
				output = output + "    average milliseconds per loop: "
						+ (m1.time / m1.getAlgorithmLoops()) + "\n";
			if (m1.getAlgorithmLoops() != 0)
				output = output + "    average nanoseconds per loop: "
						+ (m1.nanoTime / m1.getAlgorithmLoops()) + "\n";
			output = output + "  algorithm loaded " + m1.getLoadedNodes()
					+ " Nodes" + "\n";
			output = output + "  database calls: "
					+ m1.getDatabaseCalls().size() + "\n";
			if (m1.getDatabaseCalls().size() != 0) {
				output = output + "    total database call time: "
						+ m1.totalDatabaseCallTime() + " milliseconds, "
						+ m1.totalDatabaseCallNanoTime() + " nanoseconds"
						+ "\n";
				output = output + "    median database call time: "
						+ m1.medianDatabaseCallTime() + " milliseconds, "
						+ m1.medianDatabaseNanoCallTime() + " nanoseconds"
						+ "\n";
				output = output + "    average database call time: "
						+ m1.averageDatabaseCallTime() + " milliseconds, "
						+ m1.averageDatabaseNanoCallTime() + " nanoseconds"
						+ "\n";
				output = output + m1.listDatabaseCalls();
			}
			if (m1.getTime() != 0 && m1.getEndNanoTime() != 0)
				output = output + "  " + m1.getID() + ": total time took "
						+ m1.time + " milliseconds, " + m1.nanoTime
						+ " nanoseconds" + "\n";
			if (m1.totalDatabaseCallTime() != 0)
				output = output + "    total datbase time: "
						+ m1.totalDatabaseCallTime()
						+ " milliseconds, total algorithm time: "
						+ (m1.time - m1.totalDatabaseCallTime())
						+ " milliseconds" + "\n";
			if (m1.totalDatabaseCallNanoTime() != 0)
				output = output + "    total datbase time: "
						+ m1.totalDatabaseCallNanoTime()
						+ " nanoseconds, total algorithm time: "
						+ (m1.nanoTime - m1.totalDatabaseCallNanoTime())
						+ " nanoseconds" + "\n";

		}
		output = output + "-------------------------" + "\n";
		return output;
	}

	public class Metric {

		private String id;
		private long startTime;
		private long endTime;
		private long startNanoTime;
		private long endNanoTime;
		private long time;
		private long nanoTime;
		private ArrayList<DatabaseCall> databaseCalls;
		private int algorithmLoops;
		private int loadedNodes;

		public Metric(String metricID) {
			this.id = metricID;
			this.startTime = 0;
			this.endTime = 0;
			this.time = 0;
			this.databaseCalls = new ArrayList<DatabaseCall>();
			this.algorithmLoops = 0;
			this.loadedNodes = 0;
		}

		public void setID(String id) {
			this.id = id;
		}

		public String getID() {
			return id;
		}

		public void setStartTime() {
			this.startNanoTime = System.nanoTime();
			this.startTime = System.currentTimeMillis();
		}

		public long getStartTime() {
			return startTime;
		}

		public long getStartNanoTime() {
			return startNanoTime;
		}

		public void setEndTime() {
			this.endNanoTime = System.nanoTime();
			this.endTime = System.currentTimeMillis();
			this.nanoTime = endNanoTime - startNanoTime;
			this.time = endTime - startTime;
		}

		public long getEndTime() {
			return endTime;
		}

		public long getEndNanoTime() {
			return endNanoTime;
		}

		public long getTime() {
			return time;
		}

		public long getNanoTime() {
			return nanoTime;
		}

		public ArrayList<DatabaseCall> getDatabaseCalls() {
			return databaseCalls;
		}

		public int addDatabaseCall() {
			DatabaseCall d1 = new DatabaseCall();
			databaseCalls.add(d1);
			return databaseCalls.indexOf(d1);
		}

		public void setAlgorithmLoops(int number) {
			this.algorithmLoops = number;
		}

		public int getAlgorithmLoops() {
			return algorithmLoops;
		}

		public void addAlgorithmLoop() {
			algorithmLoops++;
		}

		public void setLoadedNodes(int number) {
			this.loadedNodes = number;
		}

		public int getLoadedNodes() {
			return loadedNodes;
		}

		public int totalDatabaseCalls() {
			return databaseCalls.size();
		}

		public long medianDatabaseCallTime() {
			long myList[] = new long[databaseCalls.size()];

			for (int i = 0; i < databaseCalls.size(); i++) {
				myList[i] = databaseCalls.get(i).getTime();
			}

			return calcMedian(myList);
		}

		public long medianDatabaseNanoCallTime() {
			long myList[] = new long[databaseCalls.size()];

			for (int i = 0; i < databaseCalls.size(); i++) {
				myList[i] = databaseCalls.get(i).getNanoTime();
			}

			return calcMedian(myList);
		}

		public long averageDatabaseCallTime() {
			long myList[] = new long[databaseCalls.size()];

			for (int i = 0; i < databaseCalls.size(); i++) {
				myList[i] = databaseCalls.get(i).getTime();
			}

			return calcAverage(myList);
		}

		public long averageDatabaseNanoCallTime() {
			long myList[] = new long[databaseCalls.size()];

			for (int i = 0; i < databaseCalls.size(); i++) {
				myList[i] = databaseCalls.get(i).getNanoTime();
			}

			return calcAverage(myList);
		}

		public String listDatabaseCalls() {
			String output = "";

			for (int i = 0; i < databaseCalls.size(); i++) {
				output = output + "    call " + (i + 1) + ": "
						+ databaseCalls.get(i).getTime() + " milliseconds, "
						+ databaseCalls.get(i).getNanoTime() + " nanoseconds"
						+ "\n";
			}

			return output;
		}

		public long totalDatabaseCallTime() {
			long total = 0;
			for (int i = 0; i < databaseCalls.size(); i++) {
				total = total + databaseCalls.get(i).getTime();
			}

			return total;
		}

		public long totalDatabaseCallNanoTime() {
			long total = 0;
			for (int i = 0; i < databaseCalls.size(); i++) {
				total = total + databaseCalls.get(i).getNanoTime();
			}

			return total;
		}
	}

	public class DatabaseCall {
		private long startTime;
		private long endTime;
		private long startNanoTime;
		private long endNanoTime;
		private long time;
		private long nanoTime;
		private String sql;

		public DatabaseCall() {
			this.startTime = 0;
			this.endTime = 0;
			this.time = 0;
			this.sql = "";
		}

		public void setStartTime() {
			this.startNanoTime = System.nanoTime();
			this.startTime = System.currentTimeMillis();
		}

		public long getStartTime() {
			return startTime;
		}

		public long getStartNanoTime() {
			return startNanoTime;
		}

		public void setEndTime() {
			this.endNanoTime = System.nanoTime();
			this.endTime = System.currentTimeMillis();
			this.nanoTime = endNanoTime - startNanoTime;
			this.time = endTime - startTime;
		}

		public long getEndTime() {
			return endTime;
		}

		public long getEndNanoTime() {
			return endNanoTime;
		}

		public void setSQL(String sql) {
			this.sql = sql;
		}

		public String getSQL() {
			return sql;
		}

		public long getTime() {
			return time;
		}

		public long getNanoTime() {
			return nanoTime;
		}
	}
}
