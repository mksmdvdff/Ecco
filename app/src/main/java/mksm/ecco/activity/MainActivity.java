package mksm.ecco.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mksm.ecco.R;
import mksm.ecco.model.EccoService;
import mksm.ecco.model.EccoShop;

public class MainActivity extends AppCompatActivity implements EccoService.Observer, SwipeRefreshLayout.OnRefreshListener {

	private static final String MAIN_FRAGMENT = "mainFragment";
	private EccoService eccoSerive;
	private SwipeRefreshLayout refreshLayout;
	private ListView listView;
	private List<EccoShop> shops; //очень не хотел хранить их здесь, но сортировка потребовала

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swiperefresh);
		refreshLayout.setOnRefreshListener(this);
		listView = (ListView) findViewById(R.id.main_list);

		eccoSerive = EccoService.getInstance(this.getApplicationContext());
		eccoSerive.registerObserver(this);
		eccoSerive.startShopsLoading(false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		eccoSerive.unregisterObserver(this);
	}

	@Override
	public void onDownloadStarted() {
		refreshLayout.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(true);
			}
		});

	}

	@Override
	public void onDownloadSucceeded(List<EccoShop> shops) {
		refreshLayout.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(false);
			}
		});
		this.shops = shops;
		setAdapterForListView(shops);
	}

	@Override
	public void onDownloadFailed(String reason, List<EccoShop> cachedShops) {
		refreshLayout.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(false);
			}
		});
		shops = cachedShops;
		setAdapterForListView(cachedShops);
		switch (reason) {
			case EccoService.IS_OFFLINE:
				Toast.makeText(this, R.string.offline_problem, Toast.LENGTH_LONG).show();
				break;
			case EccoService.DEFAULT:
			default:
				Toast.makeText(this, R.string.default_problem, Toast.LENGTH_LONG).show();
				break;
		}

	}

	@Override
	public void onRefresh() {
		eccoSerive.startShopsLoading(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sort_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menu_default_sort:
				setAdapterForListView(shops);
				return true;
			case R.id.menu_distance_sort:
				List<EccoShop> sortedShops = new ArrayList<>(shops);
				Collections.sort(sortedShops, new Comparator<EccoShop>() {
					@Override
					public int compare(EccoShop lhs, EccoShop rhs) {
						return (int) ((lhs.getFromCenter() - rhs.getFromCenter()) * 1000);
					}
				});
				setAdapterForListView(sortedShops);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void setAdapterForListView(List<EccoShop> eccoShops) {
		ListAdapter adapter = new ListAdapter(this, eccoShops);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
}
