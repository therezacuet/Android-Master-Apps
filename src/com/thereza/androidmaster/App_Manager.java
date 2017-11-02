package com.thereza.androidmaster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class App_Manager extends ListActivity implements
		 OnItemClickListener,OnItemLongClickListener {
	static PackageManager packagemanager;
	final String knox = "com.sec.knox.containeragent.USE_CONTAINERAGENT";
	final String knoxapp = "com.sec.knox.containeragent.USE_KNOX_UI";
	ListView apkList;
	List<PackageInfo> packageList;
	List<PackageInfo> packageList1;
	static List<PackageInfo> selected;
	public static int NoOfApps = 0;
	List<Boolean> selection;
	String path = Environment.getExternalStorageDirectory().toString()
			+ "/AppAndApks";   
	Adapter adapter;
	//private static int expand = -1;
	ActionMode action;
	static boolean exit, response;
	boolean actionModeEnabled = false;
	GroupPackage pack;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		exit = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app__manager);
		ActionBar bar = getActionBar();
  
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3983C0")));
		bar.setTitle(Html.fromHtml("<b>ANDROID MASTER</b>"));
		selection = new ArrayList<Boolean>();
	 
		packagemanager = getPackageManager();
		packageList = packagemanager
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		packageList1 = new ArrayList<PackageInfo>();
		for (PackageInfo pi : packageList) {
			boolean b = isSystemPackage(pi);

			@SuppressWarnings("unused")
			String[] permission = (pi.requestedPermissions);
			if (!b) {
				try {
					packageList1.add(pi);
					selection.add(false);
					NoOfApps++;
				} catch (Exception e) {
				}
			}
		}
		sort(packageList1);
		adapter = new Adapter(this, packageList1, packagemanager, selection);
		apkList = (ListView) findViewById(android.R.id.list);
		apkList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		apkList.setAdapter(adapter);
		apkList.setOnItemClickListener(this);
		//apkList.setMultiChoiceModeListener(this);
		apkList.setOnItemLongClickListener(this);

	}

	private boolean isSystemPackage(PackageInfo pi) {
		// TODO Auto-generated method stub
		return ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.apk, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.genapk:
			MakeWarning();
			// if (response)new Gen().execute(packageList1);
			break;
		case R.id.genfile:
			WriteData wd = new WriteData();
			Log.v("onmenu reached", "true");
			wd.makeFile(packageList1);
			Toast.makeText(
					getBaseContext(),
					"Details of All Installed Apps stored in "
							+ Environment.getExternalStorageDirectory()
									.toString() + "/AppAndApkList",
					Toast.LENGTH_LONG).show();
			break;

		}

		return super.onMenuItemSelected(featureId, item);
	}

	private boolean MakeWarning() {
		// TODO Auto-generated method stub
		response = false;
		AlertDialog.Builder builder = new AlertDialog.Builder(App_Manager.this);
		builder.setTitle("Warning");
		builder.setMessage("Do you really want to extract all the Apps????");
		builder.setPositiveButton("Confirm",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						response = true;
						arg0.cancel();
						new Gen().execute(packageList1);
					}

				});
		builder.setNegativeButton("Nope",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						response = false;
						dialog.cancel();
					}

				});
		builder.show();
		return response;
	}

	@SuppressWarnings("rawtypes")
	public class Gen extends AsyncTask<List, String, String> {

		ProgressDialog dialog;
		final String paths = Environment.getExternalStorageDirectory()
				.toString() + "/AppAndApks";

		protected void onPreExecute() {
			// example of setting up something
			dialog = new ProgressDialog(App_Manager.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setTitle("Generating Apk");
			dialog.setMessage("apks");
			dialog.show();

		}

		@Override
		protected String doInBackground(List... arg) {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			final Intent main = new Intent(Intent.ACTION_MAIN, null);
			main.addCategory(Intent.CATEGORY_LAUNCHER);

			List packagelist = getPackageManager().queryIntentActivities(main,
					0);
			dialog.setMax(App_Manager.NoOfApps);

			// Pair a =new Pair(1,((ResolveInfo)
			// object).loadLabel(getPackageManager()).toString());
			for (final Object object : packagelist) {
				String[] progress = {
						"1",
						((ResolveInfo) object).loadLabel(getPackageManager())
								.toString() };
				publishProgress(progress);
				Thread app = new Thread(new Runnable() {
					public void run() {
						ResolveInfo rs = (ResolveInfo) object;
						if ((rs.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
							File f1 = new File(
									rs.activityInfo.applicationInfo.publicSourceDir);
							Log.v("file--",
									" " + f1.getName().toString() + "----"
											+ rs.loadLabel(getPackageManager()));
							try {
								String filename = rs.loadLabel(
										getPackageManager()).toString();
								Log.d("file_name--", "" + filename);
								File f2;
								String info = Environment
										.getExternalStorageState();
								if (info.equals(Environment.MEDIA_MOUNTED)) {
									f2 = new File(Environment
											.getExternalStorageDirectory()
											.toString()
											+ "/AppAndApks");

								} else {
									f2 = getCacheDir();
								}
								if (!f2.exists())
									f2.mkdirs();
								f2 = new File(f2.getPath() + "/" + filename
										+ ".apk");
								path = f2.getPath();
								f2.createNewFile();
								InputStream in = new FileInputStream(f1);
								OutputStream out = new FileOutputStream(f2);
								byte[] bf = new byte[1024];
								int len;
								while ((len = in.read(bf)) > 0) {
									out.write(bf, 0, len);
								}
								in.close();
								out.close();

							} catch (FileNotFoundException ex) {
								System.out.println(ex.getMessage()
										+ " in the specified directory.");
							} catch (IOException e) {
								System.out.println(e.getMessage());
							}
						}

					};
				});

				app.start();
			}

			dialog.dismiss();
			return null;
		}

		protected void onProgressUpdate(String... progress) {
			dialog.incrementProgressBy(Integer.parseInt(progress[0]));
			dialog.setMessage(progress[1]);
		}

		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(),
					"BackUp of all the Apk is made at Location" + paths,
					Toast.LENGTH_LONG).show();
		}

	}

	/*
	 * public void onActivityResult(int requestCode, int resultCode, final
	 * Intent data) { Toast.makeText(getApplicationContext(),
	 * "onActivityResultCalled", Toast.LENGTH_LONG).show(); final int
	 * requestcode = requestCode; if (requestCode != -1) { final Handler handler
	 * = new Handler(); handler.postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { // Do something after 5s = 5000ms
	 * PackageInfo p = packageList1.get(requestcode); String loc =
	 * Environment.getExternalStorageDirectory() .toString() + "/AppAndApks" +
	 * "/" + p.packageName.toString() + ".apk"; File file = new File(loc);
	 * file.delete(); Toast.makeText(getApplicationContext(), "file deleted",
	 * Toast.LENGTH_LONG).show(); if (file.exists()) { try {
	 * file.getCanonicalFile().delete(); Toast.makeText(getApplicationContext(),
	 * "file deleted", Toast.LENGTH_LONG).show(); } catch (IOException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } if (file.exists())
	 * { getApplicationContext().deleteFile(file.getName());
	 * Toast.makeText(getApplicationContext(), "file deleted",
	 * Toast.LENGTH_LONG).show(); }
	 * 
	 * } } }, 10000); }
	 * 
	 * }
	 */

	private void sort(List<PackageInfo> list) {
		if (list.size() > 0) {
			Collections.sort(list, new Comparator<PackageInfo>() {
				@Override
				public int compare(final PackageInfo object1,
						final PackageInfo object2) {
					return packagemanager
							.getApplicationLabel(object1.applicationInfo)
							.toString()
							.compareTo(
									packagemanager.getApplicationLabel(
											object2.applicationInfo).toString());
				}
			});
		}
	}

	@SuppressWarnings("unused")
	private void showInstalledAppDetails(ResolveInfo paramResolveInfo) {
		String str1 = paramResolveInfo.activityInfo.packageName;
		Intent localIntent = new Intent();
		int i = Build.VERSION.SDK_INT;
		if (i >= 9) {
			localIntent
					.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			localIntent.setData(Uri.fromParts("package", str1, null));
			startActivity(localIntent);
			return;
		}
		if (i == 8) {
		}
		for (String str2 = "pkg";; str2 = "com.android.settings.ApplicationPkgName") {
			localIntent.setAction("android.intent.action.VIEW");
			localIntent.setClassName("com.android.settings",
					"com.android.settings.InstalledAppDetails");
			localIntent.putExtra(str2, str1);
			break;
		}
	}

	public String extractapk(PackageInfo i) {
		File f = new File(i.applicationInfo.publicSourceDir);

		String filename = i.packageName.toString();
		Log.d("file_name--", "" + filename);
		File f2;
		String path1 = Environment.getExternalStorageDirectory().toString()
				+ "/AppAndApks";
		try {
			String info = Environment.getExternalStorageState();
			if (info.equals(Environment.MEDIA_MOUNTED)) {
				f2 = new File(Environment.getExternalStorageDirectory()
						.toString() + "/AppAndApks");

			} else {
				f2 = getCacheDir();
			}
			if (!f2.exists())
				f2.mkdirs();
			f2 = new File(f2.getPath() + "/" + filename + ".apk");
			if (!f2.exists()) {
				f2.createNewFile();
				InputStream in = new FileInputStream(f);
				OutputStream out = new FileOutputStream(f2);
				byte[] bf = new byte[1024];
				int len;
				while ((len = in.read(bf)) > 0) {
					out.write(bf, 0, len);
				}
				in.close();
				out.close();
				System.out.println("File Copied");

			}
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		return path1;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final App_Manager acti = this;
		AlertDialog.Builder a = new AlertDialog.Builder(App_Manager.this);
		a.setTitle("Exit");
		a.setMessage("Want to Exit?");
		a.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				exit = true;
				acti.finish();
				dialog.cancel();
			}
		});
		a.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				exit = false;
				dialog.cancel();
			}

		});
		a.show();
	}
/*
	@Override
	public boolean onCreateActionMode(ActionMode actionMode,
			android.view.Menu menu) {
		actionModeEnabled = true;
		actionMode.setTitle("Select Items");
		MenuInflater inflater = actionMode.getMenuInflater();
		inflater.inflate(R.menu.actionmode, menu);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		adapter.mCheckStates.clear();
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode,
			android.view.Menu menu) {

		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

		switch (menuItem.getItemId()) {
		case R.id.extractmulti: {
			if (checkedcount() != 0) {
				selected = new ArrayList<PackageInfo>();
				getList(selected);
				if (actionMode.getTitle().equals("Select Items")) {
					Thread extract = new Thread(new Runnable() {
						public void run() {
							for (PackageInfo info : selected) {
								extractapk(info);
							}
						};
					});
					extract.start();
					uncheck();
					Toast.makeText(getBaseContext(), "Apk generated",
							Toast.LENGTH_SHORT).show();

				}

			}
		}
			break;
		case R.id.multishare: {
			if (checkedcount() != 0) {
				selected = new ArrayList<PackageInfo>();
				getList(selected);
				List<String> pathofFile = new ArrayList<String>();
				if (actionMode.getTitle().equals("Select Items")) {
					MimeTypeMap type = MimeTypeMap.getSingleton();
					String typeofdata = (type
							.getMimeTypeFromExtension(MimeTypeMap
									.getFileExtensionFromUrl(selected.get(0).applicationInfo.publicSourceDir)));
					// TODO Auto-generated method stub
					for (PackageInfo p : selected) {
						pathofFile.add(p.applicationInfo.publicSourceDir);
					}
					MultiShare(pathofFile, typeofdata);
				}
				uncheck();
			}
		}
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		actionMode.finish();
		return true;
	}*/

	private void MultiShare(List<String> filestoshare, String datatype) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND_MULTIPLE);
		intent.setType("application");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Files to Share");
		intent.setType(datatype); /* This example is sharing jpeg images. */

		ArrayList<Uri> files = new ArrayList<Uri>();

		for (String path : filestoshare /* List of the files you want to send */) {
			File file = new File(path);
			file.setReadable(true, false);
			Uri uri = Uri.fromFile(file);
			files.add(uri);
		}

		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
		startActivity(Intent.createChooser(intent, "Share using"));
	}

	private void getList(List<PackageInfo> selec) {
		// TODO Auto-generated method stub
		for (int i = 0; i < packageList1.size(); i++) {
			if (adapter.mCheckStates.valueAt(i)) {
				selec.add(packageList1.get(i));
			}
		}
		return;
	}

	/*@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		actionModeEnabled = false;
		uncheck();
		setVisibility(false);
		actionMode.invalidate();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}*/

	public void setVisibility(Boolean visible) {
		List<CheckBox> checkboxs = adapter.getCheckbox();
		if (visible) {
			for (CheckBox a : checkboxs) {
				a.setVisibility(View.VISIBLE);
			}
		} else {
			for (CheckBox a : checkboxs) {
				a.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void uncheck() {
		List<CheckBox> checkboxs = adapter.getCheckbox();
		for (CheckBox a : checkboxs) {
			a.setChecked(false);
		}
	}

	public int checkedcount() {
		int checked = 0;
		for (int i = 0; i < packageList1.size(); i++) {
			if (adapter.mCheckStates.valueAt(i)) {
				checked++;
			}
		}
		return checked;
	}

/*	@Override
	public void onItemCheckedStateChanged(ActionMode actionmode, int position,
			long id, boolean checked) {
		// TODO Auto-generated method stub
		pack = (GroupPackage) adapter.getGroupPackage();
		if (apkList.getCheckedItemCount() == 1) {
			actionmode.setSubtitle("0 item selected");
			int index = position;
			@SuppressWarnings("unused")
			int location = pack.packagelist.indexOf((PackageInfo) apkList
					.getItemAtPosition(index));
			setVisibility(true);
			adapter.setChecked((int) index, true);
			action = actionmode;
			adapter.notifyDataSetChanged();
		} else {
			actionmode.setSubtitle(checkedcount() + " items selected");
		}

	}
*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(actionModeEnabled){}else{
		switch (arg1.getId()) {
		case (R.id.select):
			if(action!=null){
				action.setSubtitle(checkedcount()+" item selected");
			}
			break;
		case (R.id.overflow_in_li):
			if (arg2 % 100 == 0) {
				int position = (arg2 - 0) / 100;
				PackageInfo p = packageList1.get(position);
				String pathsingle = extractapk(p);
				Toast.makeText(getBaseContext(),
						"Apk extracted in location " + pathsingle,
						Toast.LENGTH_SHORT).show();
			} else if (arg2 % 100 == 1) {
				int position = (arg2 - 1) / 100;
				PackageInfo p = packageList1.get(position);
				String name=p.packageName.toString();
            	Intent intent = new Intent(Intent.ACTION_DELETE);
            	intent.setData(Uri.parse("package:"+name));
            	if (null != intent) {
    				startActivity(intent);
    			}   
		 /*
				int position = (arg2 - 1) / 100;
				PackageInfo p = packageList1.get(position);
				AppData appdata = (AppData) getApplicationContext();
				appdata.setPackageInfo(p);

				Intent appInfo = new Intent(getApplicationContext(),
						ApkInfo.class);
				startActivity(appInfo);
		 */
			} else if (arg2 % 100 == 2) {
				int position = (arg2 - 2) / 100;
				try {
					PackageInfo p = packageList1.get(position);
					Intent i = packagemanager
							.getLaunchIntentForPackage(p.packageName);
					if (i == null)
						throw new PackageManager.NameNotFoundException();
					i.addCategory(Intent.CATEGORY_LAUNCHER);
					startActivity(i);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getBaseContext(),
							"Its a secured app couldnot be opened",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}

			} else if (arg2 % 100 == 3) {
				int position = (arg2 - 3) / 100;
				try {
					PackageInfo p = packageList1.get(position);
					String location = p.applicationInfo.publicSourceDir;
					// String location=
					// Environment.getExternalStorageDirectory().toString()+"/AppAndApks/"+p.packageName.toString()+".apk";
					Intent sharingIntent = new Intent(Intent.ACTION_SEND);
					sharingIntent.setType("application");
					MimeTypeMap type = MimeTypeMap.getSingleton();
					sharingIntent.setType(type
							.getMimeTypeFromExtension(MimeTypeMap
									.getFileExtensionFromUrl(location)));
					String path = extractapk(p);
					path = path + "/" + p.packageName.toString() + ".apk";
					Bundle extra = new Bundle();
					extra.putString("1", path);
					File file = new File(path);
					file.setReadable(true, false);
					Uri uri = Uri.fromFile(file);
					sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
					sharingIntent.putExtra(Intent.EXTRA_TEXT,
							p.packageName.toString());
					startActivityForResult(
							Intent.createChooser(sharingIntent, "Share using"),
							position, null);
				} catch (Exception e) {
				}
			}
			break;
		}}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "long click called", Toast.LENGTH_LONG).show();
		pack = (GroupPackage) adapter.getGroupPackage();
		int index = arg2;
		@SuppressWarnings("unused")
		int location = pack.packagelist.indexOf((PackageInfo) apkList
				.getItemAtPosition(index));
		setVisibility(true);
		adapter.setChecked((int) index, true);
		adapter.notifyDataSetChanged();
		if(action!=null){
			return false;
		}
		action = this.startActionMode(mActionModeCallback); 
             
        return true; 
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){
		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
			actionModeEnabled = false;
			uncheck();
			action=null;
			setVisibility(false);
			actionMode.invalidate();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
		@Override
		public boolean onCreateActionMode(ActionMode actionMode,
				android.view.Menu menu) {
			actionModeEnabled = true;
			actionMode.setTitle("Select Items");
			actionMode.setSubtitle("1 item selected");
			MenuInflater inflater = actionMode.getMenuInflater();
			inflater.inflate(R.menu.actionmode, menu);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			adapter.mCheckStates.clear();
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode actionMode,
				android.view.Menu menu) {

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

			switch (menuItem.getItemId()) {
			case R.id.extractmulti: {
				if (checkedcount() != 0) {
					selected = new ArrayList<PackageInfo>();
					getList(selected);
					if (actionMode.getTitle().equals("Select Items")) {
						Thread extract = new Thread(new Runnable() {
							public void run() {
								for (PackageInfo info : selected) {
									extractapk(info);
								}
							};
						});
						extract.start();
						uncheck();
						Toast.makeText(getBaseContext(), "Apk generated",
								Toast.LENGTH_SHORT).show();

					}

				}
			}
				break;
			case R.id.multishare: {
				if (checkedcount() != 0) {
					selected = new ArrayList<PackageInfo>();
					getList(selected);
					List<String> pathofFile = new ArrayList<String>();
					if (actionMode.getTitle().equals("Select Items")) {
						MimeTypeMap type = MimeTypeMap.getSingleton();
						String typeofdata = (type
								.getMimeTypeFromExtension(MimeTypeMap
										.getFileExtensionFromUrl(selected.get(0).applicationInfo.publicSourceDir)));
						// TODO Auto-generated method stub
						for (PackageInfo p : selected) {
							pathofFile.add(p.applicationInfo.publicSourceDir);
						}
						MultiShare(pathofFile, typeofdata);
					}
					uncheck();
				}
			}
			}
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			actionMode.finish();
			return true;
		}
	};

}