package com.oe.general;

public class LogSystem
{
	public static void log(Object caller, String message) {
		System.out.println("["+caller.getClass().getSimpleName()+"] "+message);
	}
	public static void warning(Object caller, String message) {
		System.out.println("["+caller.getClass().getSimpleName()+"] Warning: "+message);
	}
	public static void error(Object caller, String message) {
		System.out.println("["+caller.getClass().getSimpleName()+"] Error: "+message);
	}
}
