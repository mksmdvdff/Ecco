package mksm.ecco.network;

import java.util.ArrayList;
import java.util.List;

import mksm.ecco.model.EccoShop;

/**
 * Created by mskm on 11.08.2016.
 */
public class Resource {

    private final Meta meta;
    private List<EccoShop> data;

    public Resource(Meta meta, List<EccoShop> data) {
        this.meta = meta;
        this.data = data;
    }

    public boolean isResponseRignt(){
        return "".equals(this.meta.error_message);
    }

    public String getErrorMessage(){
        return this.meta.error_message;
    }

    public List<EccoShop> getShops() {
        if (this.data == null) {
            this.data = new ArrayList<>(); //избежим NPE
        }
        return data;
    }

    private class Meta{
        private int code;
        private String error_message;
        private int rows_count;

        public Meta(int code, String error_message, int rows_count) {
            this.code = code;
            this.error_message = error_message;
            this.rows_count = rows_count;
        }
    }
}
