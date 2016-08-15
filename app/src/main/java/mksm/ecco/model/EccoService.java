package mksm.ecco.model;

import android.content.Context;
import android.database.Observable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import mksm.ecco.model.database.CacheTable;
import mksm.ecco.network.Resource;
import mksm.ecco.network.ShopLoader;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mksm on 09.08.2016.
 */
public class EccoService {

	private final static String TAG = "EccoServiceTag";
	public final static String IS_OFFLINE = "isOffline";
	public final static String DEFAULT = "default";
	private final static String REGION = "Москва";
	public final static double MOSCOW_LONGTITUDE = 37.609218;
	public final static double MOSCOW_LATITUDE = 55.753559;         //https://tech.yandex.ru/maps/doc/jsapi/1.x/dg/concepts/map-parameters-docpage/?ncrnd=6379


	private DownloadObservable observable;
	private boolean mIsWorking;
	private List<EccoShop> cachedShops;
	private final ShopLoader loader;
	private final ConnectivityManager cm;
	private final CacheTable cacheTable;

	private static EccoService sInstance;

	public static synchronized EccoService getInstance(Context applicationContext) {

		if (sInstance == null) {
			sInstance = new EccoService(applicationContext);
		}
		return sInstance;
	}


	public EccoService(Context applicationContext) {
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);
		httpClient.networkInterceptors().add(new Interceptor() {

			@Override
			public Response intercept(Chain chain) throws IOException {
				Request original = chain.request();

				Request.Builder requestBuilder = original.newBuilder().header("Content-Type", "");

				Request request = requestBuilder.build();
				return chain.proceed(request);
			}
		});

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://app.ecco-shoes.ru/")
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient.build())
				.build();

		loader = retrofit.create(ShopLoader.class);
		cm = (ConnectivityManager) applicationContext.getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		cacheTable = CacheTable.getInstance(applicationContext);
		observable = new DownloadObservable();

	}

	public void startShopsLoading(boolean fromNetwork) {
		if (mIsWorking)
			return;
		observable.notifyStarted();
		if (!fromNetwork && cachedShops != null && !cachedShops.isEmpty()) {
			observable.notifySucceeded(cachedShops);
		} else {
			DownloadTask task = new DownloadTask(REGION);
			task.execute();
		}
	}

	public void registerObserver(final Observer observer) {
		observable.registerObserver(observer);
		if (mIsWorking) {
			observer.onDownloadStarted();
		}
	}

	public void unregisterObserver(final Observer observer) {
		observable.unregisterObserver(observer);
	}

	public interface Observer {
		void onDownloadStarted();

		void onDownloadSucceeded(List<EccoShop> shops);

		void onDownloadFailed(String reason, List<EccoShop> cachedShops);
	}

	private class DownloadObservable extends Observable<Observer> {
		public void notifyStarted() {
			mIsWorking = true;
			for (final Observer observer : mObservers) {
				observer.onDownloadStarted();
			}
		}

		public void notifySucceeded(List<EccoShop> shops) {
			mIsWorking = false;
			for (final Observer observer : mObservers) {
				observer.onDownloadSucceeded(shops);
			}
		}

		public void notifyFailed(String reason, List<EccoShop> cachedShops) {
			mIsWorking = false;
			for (final Observer observer : mObservers) {
				observer.onDownloadFailed(reason, cachedShops);
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
					Resource resource = loader.getResource(null, new ShopLoader.Region(region)).execute().body();
					if (resource.isResponseRignt()) {
						result = resource.getShops();
						cacheTable.replaceAllNotes(result); //пропишем в кэш новые значения
					} else {
						Log.e(TAG, resource.getErrorMessage());
						failed = true;
					}
				} catch (IOException ex) {
					failed = true;
					Log.e(TAG, ex.getMessage());
				}
			} else {
				isOffline = true;
			}
			if (result == null || result.isEmpty()) {
				failed = true;
				result = cacheTable.getAllShops();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final List<EccoShop> shops) {

			if (shops != null && !shops.isEmpty()) {
				EccoService.this.cachedShops = shops;
			}

			if (!failed) {
				observable.notifySucceeded(shops);
			} else {
				if (isOffline) {
					observable.notifyFailed(IS_OFFLINE, cachedShops);
				} else {
					observable.notifyFailed(DEFAULT, cachedShops);
				}
			}
		}
	}

	private boolean isOnline() {
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
