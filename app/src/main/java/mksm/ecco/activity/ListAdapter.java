package mksm.ecco.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;

import mksm.ecco.R;
import mksm.ecco.model.EccoShop;

/**
 * Created by mskm on 14.08.2016.
 */
public class ListAdapter extends BaseSwipeAdapter {

    private final static int layout = R.layout.main_list_item;
    private static ListAdapter sInstance;
    private Context context;
    private List<EccoShop> shops;

    public ListAdapter(Context context, List<EccoShop> shops) {
        this.context = context;
        this.shops = shops;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_item;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.main_list_item, parent, false);
    }

    @Override
    public void fillValues(final int position, View convertView) {
        final EccoShop shop = shops.get(position);
        //пришлось добавить эту строку из-за проблем с Content-Type
        String town = (shop.getTown() == null || shop.getTown().isEmpty()) ? "" : ", " + shop.getTown();
        ((TextView) convertView.findViewById(R.id.list_item_address)).setText(shop.getAddress());
        ((TextView) convertView.findViewById(R.id.list_item_city)).setText(shop.getRegion() + town);
        ((TextView) convertView.findViewById(R.id.list_item_phone)).setText(shop.getPhone());
        ((TextView) convertView.findViewById(R.id.list_item_metro)).setText(getCorrectMetroString(shop.getMetro()));

        final SwipeLayout swipeLayout = (SwipeLayout)convertView.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        convertView.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shops.remove(position);
                swipeLayout.close(false);
                ListAdapter.this.notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getCount() {
        return shops.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String getCorrectMetroString(String metroUnparsed) {
        if (metroUnparsed == null || metroUnparsed.trim().isEmpty()) return "";
        StringBuilder metroResult = new StringBuilder();
        for (String metroStation : metroUnparsed.split(",")) {
            metroStation.trim();
            if (!metroStation.isEmpty())
                metroResult.append(metroStation.substring(0, 1).toUpperCase()).append(metroStation.substring(1)).append(", ");
        }
        metroResult.setLength(metroResult.length() - 2);
        return metroResult.toString();
    }
}
