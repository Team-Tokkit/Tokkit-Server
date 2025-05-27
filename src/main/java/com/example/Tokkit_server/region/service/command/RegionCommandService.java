package com.example.Tokkit_server.region.service.command;

import com.example.Tokkit_server.region.dto.response.SidoResponse;
import com.example.Tokkit_server.region.dto.response.SigunguResponse;

import java.util.List;

public interface RegionCommandService {
    public List<SigunguResponse> getSigunguBySido(String sidoName);

    public List<SidoResponse> getAllSidoNames();
}
