package mksm.ecco.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import mksm.ecco.R;
import mksm.ecco.model.EccoService;
import mksm.ecco.model.EccoShop;

public class MainActivity extends AppCompatActivity implements EccoService.Observer {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onDownloadStarted() {

	}

	@Override
	public void onDownloadSucceeded(List<EccoShop> shops) {

	}

	@Override
	public void onDownloadFailed(String reason) {

	}
}
