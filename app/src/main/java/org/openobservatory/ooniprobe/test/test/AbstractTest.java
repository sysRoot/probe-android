package org.openobservatory.ooniprobe.test.test;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Network;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import java.io.File;
import java.io.FileOutputStream;

public abstract class AbstractTest {
	private String name;
	private int labelResId;
	private int iconResId;

	public AbstractTest(String name, int labelResId, int iconResId) {
		this.name = name;
		this.labelResId = labelResId;
		this.iconResId = iconResId;
	}

	public abstract void run(AbstractActivity activity, Result result, int index, TestCallback testCallback);

	protected void run(AbstractActivity activity, BaseTest test, Result result, int index, TestCallback testCallback) {
		test.use_logcat();
		test.set_error_filepath(new File(activity.getFilesDir(), result.id + "-" + name + ".log").getPath());
		test.set_verbosity(BuildConfig.DEBUG ? LogSeverity.LOG_DEBUG2 : LogSeverity.LOG_INFO);
		test.set_option("geoip_country_path", activity.getFilesDir() + "/GeoIP.dat");
		test.set_option("geoip_asn_path", activity.getFilesDir() + "/GeoIPASNum.dat");
		test.set_option("save_real_probe_ip", activity.getPreferenceManager().getIncludeIp());
		test.set_option("save_real_probe_asn", activity.getPreferenceManager().getIncludeAsn());
		test.set_option("save_real_probe_cc", activity.getPreferenceManager().getIncludeCc());
		test.set_option("no_collector", activity.getPreferenceManager().getNoUploadResults());
		test.set_option("software_name", "ooniprobe-android");
		test.set_option("software_version", VersionUtils.get_software_version());
		testCallback.onStart(activity.getString(labelResId));
		testCallback.onProgress(Double.valueOf(index * 100).intValue());
		test.on_progress((v, s) -> testCallback.onProgress(Double.valueOf((index + v) * 100).intValue()));
		test.on_log((l, s) -> testCallback.onLog(s));
		test.on_entry(entry -> {
			Log.d("entry", entry);
			Measurement measurement = new Measurement(result, name);
			JsonResult jr = activity.getGson().fromJson(entry, JsonResult.class);
			if (jr == null)
				measurement.is_failed = true;
			else
				onEntry(activity.getPreferenceManager(), jr, measurement);
			measurement.save();
			try {
				FileOutputStream outputStream = activity.openFileOutput(measurement.id + "_" + measurement.test_name, Context.MODE_PRIVATE);
				outputStream.write(entry.getBytes());
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		test.run();
	}

	@CallSuper public void onEntry(PreferenceManager preferenceManager, @NonNull JsonResult json, Measurement measurement) {
		if (json.test_start_time != null)
			measurement.result.start_time = json.test_start_time;
		if (json.measurement_start_time != null)
			measurement.start_time = json.measurement_start_time;
		if (json.test_runtime != null) {
			measurement.runtime = json.test_runtime;
			measurement.result.addDuration(json.test_runtime);
		}
		measurement.network = new Network();
		if (json.probe_asn != null && preferenceManager.isIncludeAsn()) {
			measurement.network.asn = json.probe_asn; //TODO-SBS asn name
			measurement.network.network_name = "Vodafone";
		}
		if (json.probe_cc != null && preferenceManager.isIncludeCc())
			measurement.network.country_code = json.probe_cc;
		if (json.probe_ip != null && preferenceManager.isIncludeIp())
			measurement.network.ip = json.probe_ip;
		if (json.report_id != null)
			measurement.report_id = json.report_id;
		measurement.setTestKeys(json.test_keys);
	}

	public int getLabelResId() {
		return labelResId;
	}

	public int getIconResId() {
		return iconResId;
	}

	public interface TestCallback {
		void onStart(String name);

		void onProgress(int progress);

		void onLog(String log);
	}
}
