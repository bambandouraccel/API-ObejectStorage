package accel_tech.net.objectstorage_api.service;

import accel_tech.net.objectstorage_api.dto.PlatformDto;
import accel_tech.net.objectstorage_api.dto.UpdatePlatformRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlatformService {
    public List<PlatformDto> findAllPlatforms();
    public PlatformDto addPlatform(PlatformDto dto);
    public PlatformDto partialUpdatePlatform(String _id, UpdatePlatformRequestDto updateDto);
    public String deletePlatformById(String _id);
    public PlatformDto findPlatformById(String _id);

}
