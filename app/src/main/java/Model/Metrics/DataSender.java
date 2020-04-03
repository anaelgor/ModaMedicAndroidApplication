package Model.Metrics;

import Model.Utils.HttpRequests;

public interface DataSender {

    void sendDataToServer(HttpRequests httpRequests);
}
