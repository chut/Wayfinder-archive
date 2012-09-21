package com.cs460402.app.io.extdb;

public final class ExtDbConstants {
	// database connection providers
	public static final int PROVIDER_HTTP_APACHE = 1;
	public static final int PROVIDER_SOCKET = 2;
	public static final int DATABASE_PROVIDER = PROVIDER_HTTP_APACHE; // this is where we set which database provide we will use

	public static final String BASE_URL = "http://wayfinder.mapsdb.com/cs460spr2012/DBservlet";
	public static final int BUFFERSIZE = 1000;

	// query types
	public static final int QUERY_NEIGHBORS = 1;
	public static final int QUERY_ALLDATA = 2;
	public static final int QUERY_DEBUGMAPDATA = 3;

}
