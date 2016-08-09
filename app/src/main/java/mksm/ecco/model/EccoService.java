package mksm.ecco.model;

import android.content.Context;
import android.database.Observable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mksm on 09.08.2016.
 */
public class EccoService {

	private final static String TAG = "EccoServiceTag";
	private final static String IS_OFFLINE = "isOffline";
	private final static String DEFAULT = "default";
	private final static String REGION = "Москва";


	private DownloadObservable observable;
	private DownloadTask mSignInTask;
	private boolean mIsWorking;
	private List<EccoShop> cachedShops;
	private final ShopLoader loader;
	private final ConnectivityManager cm;


	public EccoService(Context applicationContext) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://jsonplaceholder.typicode.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		loader = retrofit.create(ShopLoader.class);
		cm = (ConnectivityManager) applicationContext.getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);

	}

	public List<EccoShop> getShops(boolean fromNetwork) {
		if (!fromNetwork) {
			if (cachedShops == null || cachedShops.isEmpty()) {

			}
		}
	}

	public interface Observer {
		void onDownloadStarted();

		void onDownloadSucceeded();

		void onDownloadFailed(String reason);
	}

	private class DownloadObservable extends Observable<Observer> {
		public void notifyStarted() {
			for (final Observer observer : mObservers) {
				observer.onDownloadStarted();
			}
		}

		public void notifySucceeded() {
			for (final Observer observer : mObservers) {
				observer.onDownloadSucceeded();
			}
		}

		public void notifyFailed(String reason) {
			for (final Observer observer : mObservers) {
				observer.onDownloadFailed(reason);
			}
		}
	}

	private class DownloadTask extends AsyncTask<Void, Void, List<EccoShop>> {

		private final String region;
		private boolean isOffline = false;
		private boolean failed = false;



		public DownloadTask(String region) {
			this.region = region;
		}

		@Override
		protected List<EccoShop> doInBackground(final Void... params) {
			List<EccoShop> result = null;
			if (isOnline()) {
				try {
					result = loader.getShops(new ShopLoader.Region(region)).execute().body();
				} catch (IOException ex) {
					failed = true;
					Log.e(TAG, ex.getMessage());
				}
			} else {
				isOffline = true;
			}
			if (result == null || result.isEmpty()) {
				failed = true;

			}
			return result;
		}

		@Override
		protected void onPostExecute(final List<EccoShop> shops) {
			EccoService.this.mIsWorking = false;

			if (shops != null && !shops.isEmpty()) {
				EccoService.this.cachedShops = shops;
				observable.notifySucceeded();
			} else {
				if (isOffline) {
					observable.notifyFailed(IS_OFFLINE);
				} else {
					observable.notifyFailed(DEFAULT);
				}
			}
		}
	}

	private boolean isOnline() {
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
