import com.actuate.aces.idapi.*;
import com.actuate.aces.idapi.actions.VolumeDownload;
import com.actuate.aces.idapi.actions.VolumeMigrateSimple;
import com.actuate.aces.idapi.actions.VolumeUpload;
import com.actuate.aces.idapi.actions.VolumeUploadSimple;
import com.actuate.aces.idapi.actions.model.VolumeUploadModel;
import com.actuate.aces.idapi.control.ActuateException;
import com.actuate.aces.idapi.system.VolumeAdmin;
import com.actuate.schemas.ExecuteReportStatus;
import com.actuate.schemas.File;
import com.actuate.schemas.JobProperties;
import com.actuate.schemas.ParameterDefinition;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class Tester {


	public static void main(String[] args) throws IOException, ActuateException, ServiceException {

		//executeReportAndSaveToDisk();
		//exportReportAndSaveToDisk();
		//downloadFile();
		//executeReportTest(5, true);
		//listAllFiles();
		//downloadEntireFolder();
		//uploadEntireFolder();
		//migrateFolder();
		//volumeUpload();
		//volumeOfflineOnline("actuate", "ActuateOne");
		//getJobsForReport("/Resources/Classic Models.datadesign");
		//getReportParameters("/Ad-Hoc Mashup.rptdesign");
		scheduleJob();

		System.exit(0);
	}

	private static void scheduleJob() throws MalformedURLException, ActuateException, ServiceException {
		String host = "http://vm-nitrous:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		String executableName = "/Public/BIRT and BIRT Studio Examples/Customer Order History.rptdesign";
		String outputName = "/My Output.rptdocument";
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("Customer", "Alpha Cognac");

		JobScheduler jobScheduler = new JobScheduler(host, username, password, volume);
		String jobId = jobScheduler.scheduleJob("TEST JOB", executableName, outputName, null, parameters);
		System.out.println("jobId = " + jobId);
	}

	private static void getReportParameters(String reportName) throws MalformedURLException, ActuateException, ServiceException {
		String host = "http://vm-nitrous:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		ReportParameterProvider test = new ReportParameterProvider(host, username, password, volume);
		ArrayList<ParameterDefinition> params = test.getParameters("/Ad-Hoc Mashup.rptdesign;1");
		for (ParameterDefinition param : params) {
			System.out.println(param.getName());
		}
	}


	private static void getJobsForReport(String reportName) throws MalformedURLException, ActuateException, ServiceException {
		String host = "http://vm-nitrous:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		JobSearcher jobSearcher = new JobSearcher(host, username, password, volume);
		ArrayList<JobProperties> jobPropertiesList = jobSearcher.getJobs(reportName);
		for (JobProperties jobProperties : jobPropertiesList) {
			System.out.println(jobProperties.getActualOutputFileName());
		}
	}

	private static void volumeOfflineOnline(String systemPassword, String volumeName) throws ServiceException, ActuateException, MalformedURLException {
		String host = "http://localhost:8000";

		// Initialize com.actuate.aces.idapi.system.VolumeAdmin class with iServer SOAP Endpoint
		VolumeAdmin volumeAdmin = new VolumeAdmin(host);

		// Do a System login with system administrator password
		volumeAdmin.systemLogin(systemPassword);

		// Put a volume Offline (volumeName)
		volumeAdmin.putOffline(volumeName);

		// Take a volume online (volumeName);
		volumeAdmin.takeOnline(volumeName);
	}


	private static void volumeUpload() throws IOException, ActuateException, ServiceException {
		String host = "http://localhost:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuteOne";

		try {

			JAXBContext context = JAXBContext.newInstance("com.actuate.aces.idapi.actions.model");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			java.io.File file = new java.io.File("C:\\Work\\Skunkworks\\IDAPI\\idapi-wrapper\\VolumeUploadModel.xml");
			VolumeUploadModel volumeUploadModel = (VolumeUploadModel) unmarshaller.unmarshal(file);

			VolumeUpload volumeUpload = new VolumeUpload(host, username, password, volume);
			volumeUpload.upload("C:/DownloadTest", "/", volumeUploadModel);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private static void migrateFolder() throws IOException, ActuateException, ServiceException {
		String sourceHost = "http://localhost:8000";
		String sourceUsername = "Administrator";
		String sourcePassword = "";
		String sourceVolume = "ActuateOne";

		String targetHost = "http://localhost:8000";
		String targetUsername = "Administrator";
		String targetPassword = "";
		String targetVolume = "Test";

		VolumeMigrateSimple volumeMigrate = new VolumeMigrateSimple(sourceHost, sourceUsername, sourcePassword, sourceVolume, targetHost, targetUsername, targetPassword, targetVolume);
		volumeMigrate.migrate("/", "/", true, true);
	}

	private static void uploadEntireFolder() throws IOException, ActuateException, ServiceException {
		String host = "http://localhost:8000";
		String username = "Administrator";
		String password = "";
		String volume = "Test";

		VolumeUploadSimple volumeUpload = new VolumeUploadSimple(host, username, password, volume);
		volumeUpload.upload("C:/DownloadTest", "/", true);
	}

	private static void downloadEntireFolder() throws IOException, ActuateException, ServiceException {
		String host = "http://localhost:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		VolumeDownload volumeDownload = new VolumeDownload(host, username, password, volume);
		volumeDownload.download("/", "C:/DownloadTest", true);

	}

	private static void listAllFiles() throws MalformedURLException, ActuateException, ServiceException {
		String host = "http://localhost:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		FileLister fileLister = new FileLister(host, username, password, volume);
		doListFiles(fileLister, "/home/Administrator", ">  \"2012-07-11T00:44:00\"");
	}

	private static void doListFiles(FileLister fileLister, String folder, String pattern) {
		ArrayList<File> files = fileLister.getFileList(folder, pattern);
		for (File file : files) {
			if (file.getFileType().equalsIgnoreCase("directory")) {
				if (folder.equals("/"))
					doListFiles(fileLister, folder + file.getName(), pattern);
				else
					doListFiles(fileLister, folder + "/" + file.getName(), pattern);
			} else {
				if (folder.equals("/"))
					System.out.println(folder + file.getName());
				else
					System.out.println(folder + "/" + file.getName());
			}
		}
	}

	private static void executeReportAndSaveToDisk() throws IOException, ActuateException, ServiceException {

		String host = "http://localhost:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		ReportExecuter reportExecuter = new ReportExecuter(host, username, password, volume);
		String objId = reportExecuter.executeReport("/Public/BIRT and BIRT Studio Examples/Sales by Employee.rptdesign");

		JavaReportViewer javaReportViewer = new JavaReportViewer(reportExecuter);
		javaReportViewer.viewToFile("/$$$Transient/" + objId + ".rptdocument", "PDF", "C:\\TestReport.pdf");
	}

	private static void exportReportAndSaveToDisk() throws IOException, ActuateException, ServiceException {

		String host = "http://localhost:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		JavaReportViewer javaReportViewer = new JavaReportViewer(host, username, password, volume);
		javaReportViewer.viewToFile("/home/Administrator/Customer Dashboard.rptdocument", "PDF", "/Users/pierretessier/TestReport.pdf");
	}

	private static void executeReportTest(int mode, boolean open) throws MalformedURLException, ActuateException, ServiceException, UnsupportedEncodingException {

		String host = "http://localhost:8001";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		String viewURL = "http://localhost:8700/iportal/";

		ReportExecuter reportExecuter = new ReportExecuter(host, username, password, volume);

		// mode
		// 1 = On demand, no saving output
		// 2 = Run and Save
		// 3 = On demand with Parameters, no saving output
		// 4 = Run and Save with Parameters
		// 5 = Progressive, no saving
		// 6 = Progressive, with saving

		//int mode = 3;
		if (mode == 1) {
			// On demand, no saving output
			String objId = reportExecuter.executeReport("/Public/BIRT and BIRT Studio Examples/Customers List by Country.rptdesign");
			viewURL += "iv?__report=/$$$Transient/" + objId + ".rptdocument&connectionHandle=" + URLEncoder.encode(reportExecuter.getConnectionHandle(), "UTF-8");

		} else if (mode == 2) {
			// Run and Save
			reportExecuter.executeReport("/Public/BIRT and BIRT Studio Examples/Customers List by Country.rptdesign", "/Test Output.rptdocument");
			viewURL += "iv?__report=/Test Output.rptdocument";

		} else if (mode == 3) {
			// On demand with Parameters, no saving output
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("Territory", "EMEA");
			String objId = reportExecuter.executeReport("/Public/BIRT and BIRT Studio Examples/Sales by Territory.rptdesign", null, parameters);
			viewURL += "iv?__report=/$$$Transient/" + objId + ".rptdocument&connectionHandle=" + URLEncoder.encode(reportExecuter.getConnectionHandle(), "UTF-8");

		} else if (mode == 4) {
			// Run and Save	with Parameters
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("Territory", "APAC");
			reportExecuter.executeReport("/Public/BIRT and BIRT Studio Examples/Customers List by Country.rptdesign", "/Test Output.rptdocument", parameters);
			viewURL += "iv?__report=/Test Output.rptdocument";
		} else if (mode == 5) {
			// progressive viewing, no saving output
			String objId = reportExecuter.executeReport("Progressive Viewing.rptdesign", null, null, ExecuteReportStatus.FirstPage);
			viewURL += "iv?__report=/$$$Transient/" + objId + ".rptdocument&connectionHandle=" + URLEncoder.encode(reportExecuter.getConnectionHandle(), "UTF-8");
		} else if (mode == 6) {
			// progressive viewing saved output
			String objId = reportExecuter.executeReport("Progressive Viewing.rptdesign", "/Progressive Viewing.rptdocument", null, ExecuteReportStatus.FirstPage);
			viewURL += "iv?__report=/Progressive Viewing.rptdocument&connectionHandle=" + URLEncoder.encode(reportExecuter.getConnectionHandle(), "UTF-8");
		}

		System.out.println("viewURL = " + viewURL);
		if (open) {
			viewURL += "&userid=" + username;
			viewURL += "&password=" + password;
			viewURL += "&volume=" + volume;
			openURL(viewURL);
		}
	}

	private static void downloadFile() throws IOException, ActuateException, ServiceException {

		String host = "http://localhost:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		Downloader downloader = new Downloader(host, username, password, volume);
		downloader.downloadToFile("/Customer Dashboard.pdf", "C:\\Test Report.pdf");
	}

	private static void setPermissionsRecursively() throws MalformedURLException, ActuateException, ServiceException {

		String host = "http://localhost:8000";
		String username = "Administrator";
		String password = "";
		String volume = "ActuateOne";

		PermissionSetter permissionSetter = new PermissionSetter(host, username, password, volume);
		permissionSetter.addPermission(null, "TestRole", "VR");
		permissionSetter.setRecursivePermissions("/Temp1", true);

	}

	private static void openURL(String url) {
		String os = System.getProperty("os.name");
		Runtime runtime = Runtime.getRuntime();
		try {
			if (os.startsWith("Windows")) {
				// Block for Windows Platform
				String cmd = "rundll32 url.dll,FileProtocolHandler " + url;
				Runtime.getRuntime().exec(cmd);
			} else if (os.startsWith("Mac OS")) {
				//Block for Mac OS
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
				openURL.invoke(null, url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
