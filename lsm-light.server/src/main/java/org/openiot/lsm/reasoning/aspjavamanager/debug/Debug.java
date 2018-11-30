/**This software is distributed under terms of the BSD license. See the LICENSE file for details.**/
/*
 * for Printing
 */
package org.openiot.lsm.reasoning.aspjavamanager.debug;

public class Debug {
	public static boolean debugMode = false;

	public static void echo(Object ob, Object... message) {
		if (debugMode) {
			String result = "";
			for (final Object element : message) {
				result += "  " + element;
			}
			String classname = "";
			if (ob == null) {
				classname = " main class";
			} else {
				classname = ob.getClass().getName();
			}

			System.out.println("Debug: In: " + classname + " Message: "
					+ result);
		}
	}

	public static void echo(String result) {
		if (debugMode) {
			System.out.println("Debug Message: " + result);
		}
	}
}
