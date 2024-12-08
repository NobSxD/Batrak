package org.example.service.impl;

import org.example.service.Rebut;

public class RebutImpl implements Rebut {
    public void restartApplication() {
        try {
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
