package clp;

import static org.jocl.CL.CL_DEVICE_ADDRESS_BITS;
import static org.jocl.CL.CL_DEVICE_AVAILABLE;
import static org.jocl.CL.CL_DEVICE_COMPILER_AVAILABLE;
import static org.jocl.CL.CL_DEVICE_EXTENSIONS;
import static org.jocl.CL.CL_DEVICE_GLOBAL_MEM_CACHE_SIZE;
import static org.jocl.CL.CL_DEVICE_GLOBAL_MEM_SIZE;
import static org.jocl.CL.CL_DEVICE_LOCAL_MEM_SIZE;
import static org.jocl.CL.CL_DEVICE_MAX_CLOCK_FREQUENCY;
import static org.jocl.CL.CL_DEVICE_MAX_COMPUTE_UNITS;
import static org.jocl.CL.CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE;
import static org.jocl.CL.CL_DEVICE_MAX_WORK_GROUP_SIZE;
import static org.jocl.CL.CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS;
import static org.jocl.CL.CL_DEVICE_MAX_WORK_ITEM_SIZES;
import static org.jocl.CL.CL_DEVICE_NAME;
import static org.jocl.CL.CL_DEVICE_TYPE;
import static org.jocl.CL.CL_DEVICE_TYPE_ACCELERATOR;
import static org.jocl.CL.CL_DEVICE_TYPE_CPU;
import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.CL_DEVICE_VENDOR;
import static org.jocl.CL.CL_DEVICE_VERSION;
import static org.jocl.CL.CL_DRIVER_VERSION;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import org.jocl.CL;

public class Window extends javax.swing.JFrame implements ActionListener {
	JFrame frame;

	private JLabel platformLbl;
	private JLabel deviceLbl;
	private JComboBox<String> platformlist;
	private JComboBox<String> devlist;
	private JTable propertyList;
	private TableModel tableModel;
	private JButton testBtn;

	private static final String version = "1.1b";

	Window() {

		frame = new JFrame("OpenCL-Z " + version);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout layout = new SpringLayout();
		frame.setMinimumSize(new Dimension(310, 220));
		frame.setLayout(layout);
		Container contentPane = frame.getContentPane();

		int LabelWidth = 90;

		String[] pllist = CLUtils.GetCLPlatformNames();
		platformlist = new JComboBox<String>(pllist);
		platformlist.addActionListener(this);
		frame.add(platformlist);
		layout.putConstraint(SpringLayout.NORTH, platformlist, 10,
				SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.WEST, platformlist, LabelWidth,
				SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, platformlist, -10,
				SpringLayout.EAST, contentPane);

		devlist = new JComboBox<String>();
		devlist.addActionListener(this);
		layout.putConstraint(SpringLayout.NORTH, devlist, 10,
				SpringLayout.SOUTH, platformlist);
		layout.putConstraint(SpringLayout.WEST, devlist, LabelWidth,
				SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, devlist, -10,
				SpringLayout.EAST, contentPane);
		frame.add(devlist);
		refreshClControls();

		platformLbl = new JLabel("Platforms:");
		platformLbl.setToolTipText("Platforms");
		frame.add(platformLbl);
		layout.putConstraint(SpringLayout.NORTH, platformLbl, 3,
				SpringLayout.NORTH, platformlist);
		layout.putConstraint(SpringLayout.WEST, platformLbl, 10,
				SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, platformLbl, -5,
				SpringLayout.WEST, platformlist);

		deviceLbl = new JLabel("Devices:");
		deviceLbl.setToolTipText("Devices");
		frame.add(deviceLbl);
		layout.putConstraint(SpringLayout.NORTH, deviceLbl, 3,
				SpringLayout.NORTH, devlist);
		layout.putConstraint(SpringLayout.WEST, deviceLbl, 10,
				SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, deviceLbl, -5,
				SpringLayout.WEST, devlist);

		testBtn = new JButton("Test device");
		testBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int platform_selected = platformlist.getSelectedIndex();
				int device_selected = devlist.getSelectedIndex();

				if (platform_selected != -1 && device_selected != -1) {
					CLTestWindow w = new CLTestWindow(platform_selected,
							device_selected);
				}
			}
		});
		frame.add(testBtn);
		layout.putConstraint(SpringLayout.EAST, testBtn, -10,
				SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, testBtn, -5,
				SpringLayout.SOUTH, contentPane);

		tableModel = new TableModel();
		propertyList = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(propertyList);
		propertyList.setColumnSelectionAllowed(false);
		propertyList.setCellSelectionEnabled(false);
		propertyList.setRowSelectionAllowed(true);
		propertyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layout.putConstraint(SpringLayout.NORTH, scrollPane, 20,
				SpringLayout.SOUTH, devlist);
		layout.putConstraint(SpringLayout.WEST, scrollPane, 10,
				SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, scrollPane, -10,
				SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, scrollPane, -5,
				SpringLayout.NORTH, testBtn);
		frame.add(scrollPane);

		refreshClInfoTable();

		frame.pack();
		frame.setVisible(true);
		frame.setSize(500, 400);
	}

	public void setImage(String file) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(platformlist)) {
			refreshClControls();
			refreshClInfoTable();
		}
		if (e.getSource().equals(devlist)) {
			refreshClInfoTable();
		}
	}

	public void refreshClControls() {
		DefaultComboBoxModel<String> m = new DefaultComboBoxModel<String>();
		int selected = platformlist.getSelectedIndex();
		if (selected != -1) {
			String[] devices = CLUtils.GetCLDeviceDetails(selected);
			for (int i = 0; i < devices.length; i++) {
				m.addElement(devices[i].trim());
			}
			devlist.setModel(m);
		}
	}

	public void refreshClInfoTable() {
		int platform_selected = platformlist.getSelectedIndex();
		int device_selected = devlist.getSelectedIndex();
		tableModel.clear();

		try {

			if (platform_selected != -1 && device_selected != -1) {
				String DeviceType = "";
				long DeviceTypeRaw = CLUtils.GetCLLongPropertyNoCast(
						platform_selected, device_selected, CL_DEVICE_TYPE);
				if ((DeviceTypeRaw & CL_DEVICE_TYPE_GPU) != 0) {
					if (DeviceType.length() != 0) {
						DeviceType += ", ";
					}
					DeviceType += "GPU";
				}
				if ((DeviceTypeRaw & CL_DEVICE_TYPE_CPU) != 0) {
					if (DeviceType.length() != 0) {
						DeviceType += ", ";
					}
					DeviceType += "CPU";
				}
				if ((DeviceTypeRaw & CL_DEVICE_TYPE_ACCELERATOR) != 0) {
					if (DeviceType.length() != 0) {
						DeviceType += ", ";
					}
					DeviceType += "Accelerator";
				}

				int workDimensions = (int) CLUtils.GetCLLongPropertyNoCast(
						platform_selected, device_selected,
						CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
				long[] sizes = CLUtils.GetCLLongsProperty(platform_selected,
						device_selected, CL_DEVICE_MAX_WORK_ITEM_SIZES,
						workDimensions);
				String workitemsizes = "(";
				for (int i = 0; i < sizes.length; i++) {
					workitemsizes += String.valueOf(sizes[i]);
					if (i != workDimensions - 1) {
						workitemsizes += " x ";
					}
				}
				workitemsizes += ")";

				long compilerAvaliable = CLUtils.GetCLLongPropertyNoCast(
						platform_selected, device_selected,
						CL_DEVICE_COMPILER_AVAILABLE);
				String StrCompilerAvaliable = "Unknown";
				if (compilerAvaliable == 0)
					StrCompilerAvaliable = "No";
				if (compilerAvaliable == 1)
					StrCompilerAvaliable = "Yes";

				long ImagesSupported = CLUtils.GetCLLongPropertyNoCast(
						platform_selected, device_selected,
						CL.CL_DEVICE_IMAGE_SUPPORT);
				String StrImageSupportAvaliable = "Unknown";
				if (ImagesSupported == 0)
					StrImageSupportAvaliable = "No";
				if (ImagesSupported == 1)
					StrImageSupportAvaliable = "Yes";

				long deviceAvaliable = CLUtils
						.GetCLLongPropertyNoCast(platform_selected,
								device_selected, CL_DEVICE_AVAILABLE);
				String StrDeviceAvaliable = "Unknown";
				if (compilerAvaliable == 0)
					StrDeviceAvaliable = "No";
				if (compilerAvaliable == 1)
					StrDeviceAvaliable = "Yes";

				long GlobalMemory = CLUtils.GetCLLongPropertyNoCast(
						platform_selected, device_selected,
						CL_DEVICE_GLOBAL_MEM_SIZE);
				long LocalMemory = CLUtils.GetCLLongPropertyNoCast(
						platform_selected, device_selected,
						CL_DEVICE_LOCAL_MEM_SIZE);
				long ConstantMemory = CLUtils.GetCLLongPropertyNoCast(
						platform_selected, device_selected,
						CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE);
				long GlobalCache = CLUtils.GetCLLongPropertyNoCast(
						platform_selected, device_selected,
						CL_DEVICE_GLOBAL_MEM_CACHE_SIZE);

				String rawExtensions = CLUtils.GetCLStringProperty(
						platform_selected, device_selected,
						CL_DEVICE_EXTENSIONS);
				String rawExtL = rawExtensions.toLowerCase();
				String[] ext = rawExtensions.split(" ");

				String Str64BitAvailable = "No";
				if (rawExtL.contains("cl_khr_fp64")) {
					Str64BitAvailable = "Yes";
				} else if (rawExtL.contains("cl_amd_fp64")) {
					Str64BitAvailable = "Yes (Only AMD specific)";
				}

				String StrOpenglsharing = "No";
				if (rawExtL.contains("cl_khr_gl_sharing")) {
					StrOpenglsharing = "Yes";
				}

				String StrD3D9Sharing = "No";
				if (rawExtL.contains("cl_khr_dx9_media_sharing")) {
					StrD3D9Sharing = "Yes";
				} else if (rawExtL.contains("cl_intel_dx9_media_sharing")) {
					StrD3D9Sharing = "Yes (Only Intel specific)";
				} else if (rawExtL.contains("cl_nv_d3d9_sharing")) {
					StrD3D9Sharing = "Yes (Only Nvidia specific)";
				} else if (rawExtL.contains("cl_amd_d3d9_sharing")) {
					StrD3D9Sharing = "Yes (Only AMD specific)";
				}

				String StrD3D10Sharing = "No";
				if (rawExtL.contains("cl_khr_d3d10_sharing")) {
					StrD3D10Sharing = "Yes";
				} else if (rawExtL.contains("cl_nv_d3d10_sharing")) {
					StrD3D10Sharing = "Yes (Only Nvidia specific)";
				}

				String StrD3D11Sharing = "No";
				if (rawExtL.contains("cl_khr_d3d11_sharing")) {
					StrD3D11Sharing = "Yes";
				} else if (rawExtL.contains("cl_nv_d3d11_sharing")) {
					StrD3D11Sharing = "Yes (Only Nvidia specific)";
				}

				// tableModel.add(new
				// TableRow(String.valueOf(platform_selected),
				// String.valueOf(device_selected)));
				tableModel.add(new TableRow("MAIN PROPERTIES", ""));
				tableModel.add(new TableRow("Vendor:", CLUtils
						.GetCLStringProperty(platform_selected,
								device_selected, CL_DEVICE_VENDOR).trim()));
				tableModel.add(new TableRow("Device name:", CLUtils
						.GetCLStringProperty(platform_selected,
								device_selected, CL_DEVICE_NAME).trim()));
				tableModel.add(new TableRow("Device type:", DeviceType));
				tableModel.add(new TableRow("OpenCL Version:", CLUtils
						.GetCLStringProperty(platform_selected,
								device_selected, CL_DEVICE_VERSION)));
				tableModel.add(new TableRow("Driver Version:", CLUtils
						.GetCLStringProperty(platform_selected,
								device_selected, CL_DRIVER_VERSION)));
				tableModel.add(new TableRow("Device avaliable:",
						StrDeviceAvaliable));
				tableModel.add(new TableRow("Compiler avaliable:",
						StrCompilerAvaliable));
				tableModel.add(new TableRow("", ""));
				tableModel.add(new TableRow("COMPUTE UNITS", ""));
				tableModel.add(new TableRow("Compute units:", CLUtils
						.GetCLLongProperty(platform_selected, device_selected,
								CL_DEVICE_MAX_COMPUTE_UNITS)));
				tableModel.add(new TableRow("Compute unit speed:", CLUtils
						.GetCLLongProperty(platform_selected, device_selected,
								CL_DEVICE_MAX_CLOCK_FREQUENCY)
						+ " Mhz"));
				tableModel.add(new TableRow("Address width:", CLUtils
						.GetCLLongProperty(platform_selected, device_selected,
								CL_DEVICE_ADDRESS_BITS)
						+ " bit"));
				tableModel.add(new TableRow("", ""));
				tableModel.add(new TableRow("WORK GROUPS", ""));
				tableModel.add(new TableRow("Max work group size:", CLUtils
						.GetCLLongProperty(platform_selected, device_selected,
								CL_DEVICE_MAX_WORK_GROUP_SIZE)));
				tableModel.add(new TableRow("Work item dimensions:",
						workitemsizes));
				tableModel.add(new TableRow("", ""));
				tableModel.add(new TableRow("MEMORY AND CACHE", ""));
				tableModel.add(new TableRow("Global memory size:", String
						.valueOf(GlobalMemory / (1024 * 1024)) + " MB"));
				tableModel.add(new TableRow("Local memory size:", String
						.valueOf(LocalMemory / 1024) + " KB"));
				tableModel.add(new TableRow("Constant buffer size:", String
						.valueOf(ConstantMemory / 1024) + " KB"));
				tableModel.add(new TableRow("Global cache size:", String
						.valueOf(GlobalCache / 1024) + " KB"));
				tableModel.add(new TableRow("", ""));
				tableModel.add(new TableRow("IMAGING", ""));
				tableModel.add(new TableRow("Image support",
						StrImageSupportAvaliable));
				tableModel.add(new TableRow("Max 2D Image size:", CLUtils
						.GetCLLongProperty(platform_selected, device_selected,
								CL.CL_DEVICE_IMAGE2D_MAX_HEIGHT)
						+ " x "
						+ CLUtils.GetCLLongProperty(platform_selected,
								device_selected,
								CL.CL_DEVICE_IMAGE2D_MAX_HEIGHT)));
				tableModel.add(new TableRow("Max 3D Image size:", CLUtils
						.GetCLLongProperty(platform_selected, device_selected,
								CL.CL_DEVICE_IMAGE3D_MAX_HEIGHT)
						+ " x "
						+ CLUtils.GetCLLongProperty(platform_selected,
								device_selected,
								CL.CL_DEVICE_IMAGE3D_MAX_HEIGHT)
						+ " x "
						+ CLUtils
								.GetCLLongProperty(platform_selected,
										device_selected,
										CL.CL_DEVICE_IMAGE3D_MAX_DEPTH)));
				tableModel.add(new TableRow("Samplers:", CLUtils
						.GetCLLongProperty(platform_selected, device_selected,
								CL.CL_DEVICE_MAX_SAMPLERS)));
				tableModel.add(new TableRow("", ""));
				tableModel.add(new TableRow("OPENCL EXTENSIONS", ""));
				tableModel.add(new TableRow("64-bit floating point support:",
						Str64BitAvailable));
				tableModel
						.add(new TableRow("OpenGL Sharing:", StrOpenglsharing));
				tableModel.add(new TableRow("Direct3D 9 Sharing:",
						StrD3D9Sharing));
				tableModel.add(new TableRow("Direct3D 10 Sharing:",
						StrD3D10Sharing));
				tableModel.add(new TableRow("Direct3D 11 Sharing:",
						StrD3D11Sharing));
				tableModel.add(new TableRow("Byte addressable store:", rawExtL
						.contains("cl_khr_byte_addressable_store") ? "Yes"
						: "No"));
				tableModel.add(new TableRow("", ""));
				tableModel.add(new TableRow("ALL EXTENSIONS", ""));
				for (int j = 0; j < ext.length; j++) {
					tableModel.add(new TableRow("", ext[j]));
				}

			} else {
				tableModel.add(new TableRow("No OpenCL Devices found!", ""));
			}

		} catch (Exception e) {
			System.err.println(e.toString());
			tableModel.clear();
			tableModel.add(new TableRow("No OpenCL Devices found!", ""));
		}
	}

}
