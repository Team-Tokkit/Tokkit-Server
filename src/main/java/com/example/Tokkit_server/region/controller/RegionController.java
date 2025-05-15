package com.example.Tokkit_server.region.controller;

import com.example.Tokkit_server.global.apiPayload.ApiResponse;
import com.example.Tokkit_server.region.dto.response.SidoResponse;
import com.example.Tokkit_server.region.dto.response.SigunguResponse;
import com.example.Tokkit_server.region.service.command.RegionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionCommandService regionService;

    @GetMapping("/sido")
    public ApiResponse<List<SidoResponse>> getSidoNames() {
        return ApiResponse.onSuccess(regionService.getAllSidoNames());
    }

    // 2. 특정 시/도의 시군구 목록 조회
    @GetMapping("/sigungu")
    public ApiResponse<List<SigunguResponse>> getSigunguNames(@RequestParam String sido) {
        return ApiResponse.onSuccess(regionService.getSigunguBySido(sido));
    }
}
