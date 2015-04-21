package clp;

import static org.jocl.CL.*;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

public class CLUtils {

	public static String[] GetCLPlatformNames() {
		String[] ret = new String[0];
		try {
			int numPlatformsArray[] = new int[1];

			clGetPlatformIDs(0, null, numPlatformsArray);

			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			int deviceCount = numPlatformsArray[0];

			ret = new String[deviceCount];

			for (int i = 0; i < deviceCount; i++) {
				ret[i] = String.valueOf(i) + ": "
						+ getString(platforms[i], CL_PLATFORM_NAME).trim();
			}

		} catch (Throwable e) {
			ret = new String[0];
		}

		return ret;
	}

	public static String[] GetCLDeviceNames(int p_id) {
		String[] ret = new String[0];
		try {
			int numDevices[] = new int[1];

			int numPlatformsArray[] = new int[1];
			clGetPlatformIDs(0, null, numPlatformsArray);
			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, 0, null,
					numDevices);

			cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, numDevices[0],
					devicesArray, null);

			int deviceCount = numDevices[0];

			ret = new String[deviceCount];

			for (int i = 0; i < deviceCount; i++) {
				ret[i] = getString(devicesArray[i], CL_DEVICE_NAME).trim();
			}

		} catch (Throwable e) {
			ret = new String[0];
		}

		return ret;
	}

	public static String[] GetCLDeviceDetails(int p_id) {
		String[] ret = new String[0];
		try {
			int numDevices[] = new int[1];

			int numPlatformsArray[] = new int[1];

			clGetPlatformIDs(0, null, numPlatformsArray);
			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, 0, null,
					numDevices);

			cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, numDevices[0],
					devicesArray, null);

			int deviceCount = numDevices[0];

			ret = new String[deviceCount];

			for (int i = 0; i < deviceCount; i++) {
				String type = "Unknown type";
				long t = getLong(devicesArray[i], CL_DEVICE_TYPE);
				if ((t & CL_DEVICE_TYPE_GPU) != 0) {
					type = "GPU";
				}
				if ((t & CL_DEVICE_TYPE_CPU) != 0) {
					type = "CPU";
				}
				if ((t & CL_DEVICE_TYPE_ACCELERATOR) != 0) {
					type = "ACC";
				}
				ret[i] = String.valueOf(i) + ": [" + type + "] "
						+ getString(devicesArray[i], CL_DEVICE_NAME).trim();

			}

		} catch (Throwable e) {
			ret = new String[0];
		}

		return ret;
	}

	public static long GetCLLongPropertyNoCast(int p_id, int d_id, int property) {
		try {
			int numDevices[] = new int[1];

			int numPlatformsArray[] = new int[1];
			clGetPlatformIDs(0, null, numPlatformsArray);
			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, 0, null,
					numDevices);

			cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, numDevices[0],
					devicesArray, null);

			long ret = getLong(devicesArray[d_id], property);

			return ret;
		} catch (Throwable e) {
			return 0;
		}
	}

	public static long[] GetCLLongsProperty(int p_id, int d_id, int property,
			int numvalues) {
		try {
			int numDevices[] = new int[1];

			int numPlatformsArray[] = new int[1];
			clGetPlatformIDs(0, null, numPlatformsArray);
			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, 0, null,
					numDevices);

			cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, numDevices[0],
					devicesArray, null);

			long[] ret = getLongs(devicesArray[d_id], property, numvalues);
			return ret;
		} catch (Throwable e) {
			return new long[0];
		}
	}

	public static String GetCLLongProperty(int p_id, int d_id, int property) {
		try {
			int numDevices[] = new int[1];

			int numPlatformsArray[] = new int[1];
			clGetPlatformIDs(0, null, numPlatformsArray);
			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, 0, null,
					numDevices);

			cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, numDevices[0],
					devicesArray, null);

			long ret = getLong(devicesArray[d_id], property);

			return String.valueOf(ret);
		} catch (Throwable e) {
			return "";
		}
	}

	public static String GetCLStringProperty(int p_id, int d_id, int property) {
		try {
			int numDevices[] = new int[1];

			int numPlatformsArray[] = new int[1];
			clGetPlatformIDs(0, null, numPlatformsArray);
			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, 0, null,
					numDevices);

			cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, numDevices[0],
					devicesArray, null);

			return getString(devicesArray[d_id], property).replace("  ", " ");
		} catch (Throwable e) {
			return "";
		}
	}

	/**
	 * Returns the value of the device info parameter with the given name
	 * 
	 * @param device
	 *            The device
	 * @param paramName
	 *            The parameter name
	 * @return The value
	 */
	private static String getString(cl_device_id device, int paramName) {
		try {
			// Obtain the length of the string that will be queried
			long size[] = new long[1];
			clGetDeviceInfo(device, paramName, 0, null, size);

			// Create a buffer of the appropriate size and fill it with the info
			byte buffer[] = new byte[(int) size[0]];
			clGetDeviceInfo(device, paramName, buffer.length,
					Pointer.to(buffer), null);

			// Create a string from the buffer (excluding the trailing \0 byte)
			return new String(buffer, 0, buffer.length - 1);
		} catch (Throwable e) {
			return "";
		}
	}

	/**
	 * Returns the value of the platform info parameter with the given name
	 * 
	 * @param platform
	 *            The platform
	 * @param paramName
	 *            The parameter name
	 * @return The value
	 */
	private static String getString(cl_platform_id platform, int paramName) {
		try {
			// Obtain the length of the string that will be queried
			long size[] = new long[1];
			clGetPlatformInfo(platform, paramName, 0, null, size);

			// Create a buffer of the appropriate size and fill it with the info
			byte buffer[] = new byte[(int) size[0]];
			clGetPlatformInfo(platform, paramName, buffer.length,
					Pointer.to(buffer), null);

			// Create a string from the buffer (excluding the trailing \0 byte)
			return new String(buffer, 0, buffer.length - 1);
		} catch (Throwable e) {
			return "";
		}
	}

	private static long getLong(cl_device_id device, int paramName) {
		return getLongs(device, paramName, 1)[0];
	}

	/**
	 * Returns the values of the device info parameter with the given name
	 * 
	 * @param device
	 *            The device
	 * @param paramName
	 *            The parameter name
	 * @param numValues
	 *            The number of values
	 * @return The value
	 */
	private static long[] getLongs(cl_device_id device, int paramName,
			int numValues) {
		try {
			long values[] = new long[numValues];
			clGetDeviceInfo(device, paramName, Sizeof.cl_long * numValues,
					Pointer.to(values), null);
			return values;
		} catch (Throwable e) {
			return new long[0];
		}
	}

}
