package burptesting;

import burp.api.montoya.MontoyaApi;

public interface ApiAware {
    void setApi(MontoyaApi api);
}
