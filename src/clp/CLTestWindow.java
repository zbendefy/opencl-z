package clp;

import static org.jocl.CL.*;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

public class CLTestWindow {

	private ImagePanel p;
	private String separator = System.getProperty("line.separator");
	JFrame frame;

	CLTestWindow(int p_id, int d_id) {
		int width = 790;
		int height = 470;

		frame = new JFrame("Running Test - "
				+ CLUtils.GetCLStringProperty(p_id, d_id, CL_DEVICE_NAME)
						.trim());
		SpringLayout layout = new SpringLayout();
		frame.setLayout(layout);
		Container contentPane = frame.getContentPane();
		frame.setVisible(true);
		frame.setMinimumSize(new Dimension(width, height + 110));
		frame.setResizable(false);
		frame.setSize(width + 5, height + 5);

		JTextArea l = new JTextArea("Running mandelbrot test on "
				+ CLUtils.GetCLStringProperty(p_id, d_id, CL_DEVICE_NAME)
						.trim() + "..." + separator);
		l.setEditable(false);
		JScrollPane scrollpane = new JScrollPane(l);
		frame.add(scrollpane);
		layout.putConstraint(SpringLayout.NORTH, scrollpane, height + 5,
				SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, scrollpane, height + 105,
				SpringLayout.SOUTH, contentPane);
		layout.putConstraint(SpringLayout.EAST, scrollpane, 0,
				SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.WEST, scrollpane, 0,
				SpringLayout.WEST, contentPane);

		p = new ImagePanel();
		frame.add(p);

		layout.putConstraint(SpringLayout.NORTH, p, 0, SpringLayout.NORTH,
				contentPane);
		layout.putConstraint(SpringLayout.WEST, p, 0, SpringLayout.WEST,
				contentPane);
		layout.putConstraint(SpringLayout.SOUTH, p, height, SpringLayout.NORTH,
				contentPane);
		layout.putConstraint(SpringLayout.EAST, p, width, SpringLayout.WEST,
				contentPane);

		frame.pack();

		try {
			long init = System.nanoTime();
			int n = width * height;
			int srcArrayA[] = new int[3];
			float srcArrayB[] = new float[4];
			int dstArray[] = new int[n];

			srcArrayA[0] = 3000; // 50 iterations
			srcArrayA[1] = width; // resx
			srcArrayA[2] = height; // resy
			srcArrayB[0] = -1.47855716128f; // posx
			srcArrayB[1] = -0.00268103788434f; // posy
			srcArrayB[2] = 0.00121958864f; // zoom
			srcArrayB[3] = (float)height / (float)width; // aspect ratio

			Pointer srcA = Pointer.to(srcArrayA);
			Pointer srcB = Pointer.to(srcArrayB);
			Pointer dst = Pointer.to(dstArray);

			// The platform, device type and device number
			// that will be used
			final int platformIndex = p_id;
			final long deviceType = CL_DEVICE_TYPE_ALL;
			final int deviceIndex = d_id;

			//CL.setExceptionsEnabled(true);

			int numPlatformsArray[] = new int[1];
			clGetPlatformIDs(0, null, numPlatformsArray);
			int numPlatforms = numPlatformsArray[0];

			cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
			clGetPlatformIDs(platforms.length, platforms, null);
			cl_platform_id platform = platforms[platformIndex];

			cl_context_properties contextProperties = new cl_context_properties();
			contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

			int numDevicesArray[] = new int[1];
			clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
			int numDevices = numDevicesArray[0];

			cl_device_id devices[] = new cl_device_id[numDevices];
			clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
			cl_device_id device = devices[deviceIndex];

			cl_context context = clCreateContext(contextProperties, 1,
					new cl_device_id[] { device }, null, null, null);

			cl_command_queue commandQueue = clCreateCommandQueue(context,
					device, 0, null);

			// Allocate the memory objects for the input- and output data
			cl_mem memObjects[] = new cl_mem[3];
			memObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY
					| CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * 3, srcA, null);
			memObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY
					| CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * 4, srcB, null);
			memObjects[2] = clCreateBuffer(context, CL_MEM_WRITE_ONLY,
					Sizeof.cl_int * n, null, null);

			int[] errcode = new int[1];
			
			// Create the program from the source code
			cl_program program = clCreateProgramWithSource(context, 1,
					new String[] { CLSources.getMandelbrotSource() }, null,
					errcode);
			
			if (errcode[0] != CL_SUCCESS) {
				throw new Exception(
						"Failed to create cl program from source! Error code: "
								+ errcode[0]);
			}

			cl_device_id[] devs = new cl_device_id[] {device};
			int buildResult = clBuildProgram(program, 1, devs, null, null, null);
			
			if (buildResult != CL_SUCCESS) {
				long[] logSize = new long[1];
				clGetProgramBuildInfo(program, device,
						CL.CL_PROGRAM_BUILD_LOG, 0, null, logSize);

				byte logData[] = new byte[(int) logSize[0]];
				clGetProgramBuildInfo(program, device,
						CL.CL_PROGRAM_BUILD_LOG, logSize[0], Pointer.to(logData),
						null);
				throw new Exception("Failed to build cl program! Error code: "
						+ buildResult + " Build log: "
						+ System.getProperty("line.separator")
						+ new String(logData));
			}
			
			cl_kernel kernel = clCreateKernel(program, "mandelbrot", errcode);

			if (errcode[0] != CL_SUCCESS) {
				throw new Exception("Failed to create cl kernel! Error code: "
						+ errcode[0]);
			}
			
			// Set the arguments for the kernel
			clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
			clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(memObjects[1]));
			clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(memObjects[2]));

			// Set the work-item dimensions
			final int work_dimensions = 2;
			long global_work_size[] = new long[] { width, height };

			long before = System.nanoTime();

			// Execute the kernel
			clEnqueueNDRangeKernel(commandQueue, kernel, work_dimensions, null,
					global_work_size, null, 0, null, null);

			// Read the output data
			clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0, n
					* Sizeof.cl_int, dst, 0, null, null);
			long afterCopy = System.nanoTime();

			float millisecondsinit = ((float) (before - init)) / 1000000.0f;
			float milliseconds = ((float) (afterCopy - before)) / 1000000.0f;

			// Release kernel, program, and memory objects
			clReleaseMemObject(memObjects[0]);
			clReleaseMemObject(memObjects[1]);
			clReleaseMemObject(memObjects[2]);
			clReleaseKernel(kernel);
			clReleaseProgram(program);
			clReleaseCommandQueue(commandQueue);
			clReleaseContext(context);
			clReleaseDevice(device);

			p.image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			p.image.setRGB(0, 0, width, height, dstArray, 0, width);
			frame.setTitle("Test complete - "
					+ CLUtils.GetCLStringProperty(p_id, d_id, CL_DEVICE_NAME)
							.trim());
			l.setText(l.getText() + "Test Completed!" + separator
					+ "OpenCL compilation time: " + millisecondsinit + "ms"
					+ separator + "Calculation time: " + milliseconds + "ms");

		} catch (Exception e) {
			frame.remove(p);
			l.setText(l.getText()
					+ "Error running OpenCL code on "
					+ CLUtils.GetCLStringProperty(p_id, d_id, CL_DEVICE_NAME)
							.trim() + System.getProperty("line.separator")
					+ System.getProperty("line.separator") + e.toString());
			frame.setTitle("Error! - "
					+ CLUtils.GetCLStringProperty(p_id, d_id, CL_DEVICE_NAME)
							.trim());
		}

	}

}
