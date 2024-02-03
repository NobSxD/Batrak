package ches.filter;

import java.util.List;

public interface Change {
    String infoAccount(String publicKey, String secretKey, String userName);
    void startTread(String pair);
    void stopTread();

    List<String> pair();

}
