package mksm.ecco.network;

import java.util.List;

import mksm.ecco.model.EccoShop;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mksm on 09.08.2016.
 */
public interface ShopLoader {

	@POST
	Call<Resource> getResource(@Body Region region);

	public static class Region {
		protected String region;

		public Region(String region) {
			this.region = region;
		}
	}

}
