package accel_tech.net.objectstorage_api.controller;

import accel_tech.net.objectstorage_api.dto.ApiResponse;
import accel_tech.net.objectstorage_api.dto.DeletePlatformResponseDto;
import accel_tech.net.objectstorage_api.dto.PlatformDto;
import accel_tech.net.objectstorage_api.dto.UpdatePlatformRequestDto;
import accel_tech.net.objectstorage_api.exception.BadRequestException;
import accel_tech.net.objectstorage_api.service.PlatformService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping(path = "")
    public ResponseEntity<?> findAllPlatforms(){
        List<PlatformDto> list = platformService.findAllPlatforms();
        list = list.stream()
                .sorted(Comparator.comparing(PlatformDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, list));
    }

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addNewPlatform(@Valid @RequestBody PlatformDto platformDto){
        PlatformDto createdPlatform = platformService.addPlatform(platformDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, createdPlatform));
    }

    @PatchMapping(value = "/{_id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<ApiResponse<PlatformDto>> partialUpdatePlatform(@PathVariable String _id, @Valid @RequestBody String rawBody) {
        // Convertir manuellement le texte en JSON
        ObjectMapper mapper = new ObjectMapper();
        UpdatePlatformRequestDto updateDto;
        try {
            updateDto = mapper.readValue(rawBody, UpdatePlatformRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid JSON format");
        }

        PlatformDto updatedPlatform = platformService.partialUpdatePlatform(_id, updateDto);
        return ResponseEntity.ok(new ApiResponse<>(true, updatedPlatform));
    }

    @DeleteMapping(path = "/{_id}")
    public ResponseEntity<?> deletePlatformById(@PathVariable("_id") String _id){
        String deletedId = platformService.deletePlatformById(_id);
        DeletePlatformResponseDto response = new DeletePlatformResponseDto();
        response.set_id(deletedId);
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @GetMapping(path = "/{_id}")
    public ResponseEntity<?> findPlatformById(@PathVariable String _id){
        PlatformDto platform = platformService.findPlatformById(_id);
        return ResponseEntity.ok(new ApiResponse<>(true, platform));
    }

}
