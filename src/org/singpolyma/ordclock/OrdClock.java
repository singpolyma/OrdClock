package org.singpolyma.ordclock;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.util.Locale;

public class OrdClock extends AppWidgetProvider
{
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
		int[] appWidgetIds) {
		// To prevent any ANR timeouts, we perform the update in a service
		context.startService(new Intent(context, UpdateService.class));
	}

	public static class UpdateService extends Service {
		@Override
		public void onStart(Intent intent, int startId) {
			// Build the widget update for today
			RemoteViews updateViews = buildUpdate(this);

			// Push update for this widget to the home screen
			ComponentName thisWidget = new ComponentName(this, OrdClock.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(thisWidget, updateViews);
		}

		public String fracTime(Time t) {
			double time = (t.hour*60*60.0 + t.minute*60.0 + t.second) / 86400.0;
			return new DecimalFormat(".000").format(time);
		}

		/**
		 * Build a widget update to show the current Wiktionary
		 * "Word of the day." Will block until the online API returns.
		 */
		public RemoteViews buildUpdate(Context context) {
			Resources res = context.getResources();
			RemoteViews updateViews = null;

			// Find current month and day
			Time today = new Time();
			today.setToNow();

			Time utc = new Time("UTC");
			utc.setToNow();

			updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_ordclock);
			updateViews.setTextViewText(R.id.day, today.format("%Y-%j"));

			updateViews.setTextViewText(R.id.time, fracTime(today));
			updateViews.setTextViewText(R.id.utc, fracTime(utc)+" Z");

			return updateViews;
		}

		@Override
		public IBinder onBind(Intent intent) {
			// We don't need to bind to this service
			return null;
		}
	}
}
