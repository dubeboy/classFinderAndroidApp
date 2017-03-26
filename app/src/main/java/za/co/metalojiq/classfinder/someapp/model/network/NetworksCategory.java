package za.co.metalojiq.classfinder.someapp.model.network;

import java.io.Serializable;

/**
 * Created by divine on 3/19/17.
 */
public class NetworksCategory  implements Serializable{
    private String name;

    public NetworksCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
