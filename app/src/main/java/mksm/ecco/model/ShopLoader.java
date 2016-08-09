package mksm.ecco.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mksm on 09.08.2016.
 */
public interface ShopLoader {

	@POST
	Call<List<EccoShop>> getShops(@Body Region region);

	public static class Region {
		protected String region;

		public Region(String region) {
			this.region = region;
		}
	}

}
