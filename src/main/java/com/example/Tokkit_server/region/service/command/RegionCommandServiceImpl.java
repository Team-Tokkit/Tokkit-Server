package com.example.Tokkit_server.region.service.command;


import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.region.dto.response.SidoResponse;
import com.example.Tokkit_server.region.dto.response.SigunguResponse;
import com.example.Tokkit_server.region.entity.Region;
import com.example.Tokkit_server.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionCommandServiceImpl implements RegionCommandService {

    private final RegionRepository regionRepository;

    public List<SidoResponse> getAllSidoNames() {
        return regionRepository.findDistinctBySidoNameNotNull()
                .stream()
                .map(Region::getSidoName)
                .distinct()
                .map(SidoResponse::from)
                .collect(Collectors.toList());
    }

    public List<SigunguResponse> getSigunguBySido(String sidoName) {
        List<SigunguResponse> result = regionRepository.findBySidoName(sidoName)
                .stream()
                .map(Region::getSigunguName)
                .distinct()
                .map(SigunguResponse::from)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_SIDO);
        }

        return result;
    }
}
