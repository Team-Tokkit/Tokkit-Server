package com.example.Tokkit_server.store.service.command;

import java.util.List;
public interface StoreCommandService {
    public List<StoreResponse> getStoresByRadius(double lat, double lng, double radius);
}
