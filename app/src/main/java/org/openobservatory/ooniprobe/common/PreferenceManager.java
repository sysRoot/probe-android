package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.utils.NotificationService;

import java.util.ArrayList;

public class PreferenceManager {
	public static final String GEO_VER = "geo_ver";
	public static final String TOKEN = "token";
	private static final String SHOW_ONBOARDING = "first_run";
	private SharedPreferences sp;
	private Resources r;
	private ConnectivityManager connectivityManager;

	PreferenceManager(Context context) {
		androidx.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_global, true);
		androidx.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_instant_messaging, true);
		androidx.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_middleboxes, true);
		androidx.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_performance, true);
		androidx.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_websites, true);
		sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
		r = context.getResources();
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public String getNetworkType() {
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI)
			return NotificationService.WIFI;
		else if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE)
			return NotificationService.MOBILE;
		else
			return NotificationService.NO_INTERNET;
	}

	public String getToken() {
		return sp.getString(TOKEN, null);
	}

	public void setToken(String token) {
		sp.edit().putString(TOKEN, token).apply();
	}

	public int getGeoVer() {
		return sp.getInt(GEO_VER, 0);
	}

	public void setGeoVer(int geoVer) {
		sp.edit().putInt(GEO_VER, geoVer).apply();
	}

	public Integer getMaxRuntime() {
		try {
			return sp.getInt(r.getString(R.string.max_runtime), 90);
		} catch (Exception e) {
			return 90;
		}
	}

	public boolean isSendCrash() {
		return sp.getBoolean(r.getString(R.string.send_crash), true);
	}

	public boolean isShowOnboarding() {
		return sp.getBoolean(SHOW_ONBOARDING, true);
	}

	public void setShowOnboarding(boolean showIntro) {
		sp.edit().putBoolean(SHOW_ONBOARDING, showIntro).apply();
	}

	public boolean isNotifications() {
		return sp.getBoolean(r.getString(R.string.notifications_enabled), true);
	}

	public boolean isNotificationsCompletion() {
		return isNotifications() && sp.getBoolean(r.getString(R.string.notifications_completion), true);
	}

	public boolean isNotificationsNews() {
		return isNotifications() && sp.getBoolean(r.getString(R.string.notifications_news), true);
	}

	public boolean isUploadResults() {
		return sp.getBoolean(r.getString(R.string.upload_results), true);
	}

	public boolean isIncludeIp() {
		return sp.getBoolean(r.getString(R.string.include_ip), false);
	}

	public boolean isIncludeAsn() {
		return sp.getBoolean(r.getString(R.string.include_asn), true);
	}

	public boolean isDebugLogs() {
		return sp.getBoolean(r.getString(R.string.debugLogs), false);
	}

	public boolean isIncludeCc() {
		return sp.getBoolean(r.getString(R.string.include_cc), true);
	}

	public boolean isTestWhatsapp() {
		return sp.getBoolean(r.getString(R.string.test_whatsapp), true);
	}

	public boolean isTestWhatsappExtensive() {
		return sp.getBoolean(r.getString(R.string.test_whatsapp_extensive), true);
	}

	public boolean isTestTelegram() {
		return sp.getBoolean(r.getString(R.string.test_telegram), true);
	}

	public boolean isTestFacebookMessenger() {
		return sp.getBoolean(r.getString(R.string.test_facebook_messenger), true);
	}

	public boolean isRunHttpInvalidRequestLine() {
		return sp.getBoolean(r.getString(R.string.run_http_invalid_request_line), true);
	}

	public boolean isRunHttpHeaderFieldManipulation() {
		return sp.getBoolean(r.getString(R.string.run_http_header_field_manipulation), true);
	}

	public boolean isRunNdt() {
		return sp.getBoolean(r.getString(R.string.run_ndt), true);
	}

	public boolean isRunDash() {
		return sp.getBoolean(r.getString(R.string.run_dash), true);
	}

	public boolean isAllCategoryEnabled() {
		boolean out = true;
		for (String key : r.getStringArray(R.array.CategoryCodes))
			out = out && sp.getBoolean(key, true);
		return out;
	}

	public String getEnabledCategory() {
		ArrayList<String> list = new ArrayList<>(31);
		for (String key : r.getStringArray(R.array.CategoryCodes))
			if (sp.getBoolean(key, true))
				list.add(key);
		return TextUtils.join(",", list);
	}

	public Integer countEnabledCategory() {
		int count = 0;
		for (String key : r.getStringArray(R.array.CategoryCodes))
			if (sp.getBoolean(key, true))
				count++;
		return count;
	}
}
