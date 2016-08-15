package mksm.ecco.network;

import java.util.List;

import mksm.ecco.model.EccoShop;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by mksm on 09.08.2016.
 */
public interface ShopLoader {

	@POST("/shops/list")
	Call<Resource> getResource(@Header("Content-Type") String requestType, @Body Region region);

	public static class Region {
		public String region;

		public Region(String region) {
			this.region = region;
		}
	}

}
