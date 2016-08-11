package mksm.ecco.activity;

import android.app.Fragment;
import android.os.Bundle;

import mksm.ecco.model.EccoService;

/**
 * Created by mskm on 11.08.2016.
 */
public class MainRetainFragment extends Fragment {

    private final EccoService eccoService;

    public MainRetainFragment() {
        eccoService = new EccoService(this.getActivity().getApplicationContext());
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public EccoService getSignInModel() {
        return eccoService;
    }

}
