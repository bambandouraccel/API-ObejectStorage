package accel_tech.net.objectstorage_api.serviceImpl;

import accel_tech.net.objectstorage_api.dto.PlatformDto;
import accel_tech.net.objectstorage_api.dto.UpdatePlatformRequestDto;
import accel_tech.net.objectstorage_api.entity.Platform;
import accel_tech.net.objectstorage_api.enumation.Kind;
import accel_tech.net.objectstorage_api.exception.BadRequestException;
import accel_tech.net.objectstorage_api.exception.ResourceNotFoundException;
import accel_tech.net.objectstorage_api.repository.PlatformRepository;
import accel_tech.net.objectstorage_api.service.PlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component("platformService")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlatformServiceImpl implements PlatformService {

    private final PlatformRepository platformRepository;
    private final KubernetesApiValidator kubernetesApiValidator;

    @Override
    public List<PlatformDto> findAllPlatforms() {
        List<PlatformDto>  platformDtos = new ArrayList<>();
        List<Platform> list = platformRepository.findAll();
        list.stream()
                .forEach(platform -> {
                    PlatformDto platformDto = mapEntityToDto(platform);
                    platformDtos.add(platformDto);
                });
        return platformDtos;
    }

    @Override
    public PlatformDto addPlatform(PlatformDto dto) {
        if (platformRepository.existsPlatformByName(dto.getName())) {
            throw new BadRequestException("Platform with name '" + dto.getName() + "' already exists");
        }
        kubernetesApiValidator.validateKubernetesAccess(dto.getApiUrl(), dto.getApiToken());

        Platform platform = new Platform();
        mapDtoToEntity(dto, platform);
        Platform addPlatform = platformRepository.save(platform);
        return mapEntityToDto(addPlatform);
    }

    @Override
    public PlatformDto partialUpdatePlatform(String _id, UpdatePlatformRequestDto updateDto) {
        Platform existingPlatform = platformRepository.findById(_id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform not found with id: " + _id));

        if (updateDto.getIsActive() != null) {
            existingPlatform.setIsActive(updateDto.getIsActive());
        }
        if (updateDto.getApiToken() != null) {
            existingPlatform.setApiToken(updateDto.getApiToken());
        }
        if (updateDto.getApiUrl() != null) {
            existingPlatform.setApiUrl(updateDto.getApiUrl());
        }
        if (updateDto.getStorageClassName() != null) {
            existingPlatform.setStorageClassName(updateDto.getStorageClassName());
        }
        if (updateDto.getGlobalEndpoint() != null) {
            existingPlatform.setGlobalEndpoint(updateDto.getGlobalEndpoint());
        }
        Platform updatedPlatform = platformRepository.save(existingPlatform);
        return mapEntityToDto(updatedPlatform);
    }

    @Override
    public String deletePlatformById(String _id) {
        Platform existingPlatform = platformRepository.findById(_id).
                orElseThrow(() -> new ResourceNotFoundException("Platform not found with id:"+_id));
        platformRepository.delete(existingPlatform);
        return _id;
    }

    @Override
    public PlatformDto findPlatformById(String _id) {
        Platform findingPlatform = platformRepository.findById(_id).
                orElseThrow(() -> new ResourceNotFoundException("Platform not found with id:"+_id));
        return mapEntityToDto(findingPlatform);
    }

    private PlatformDto mapEntityToDto(Platform platform){
        PlatformDto platformDto = new PlatformDto();
        platformDto.set_id(platform.get_id());
        platformDto.setName(platform.getName());
        platformDto.setKind(platform.getKind());
        platformDto.setApiUrl(platform.getApiUrl());
        platformDto.setIsActive(platform.getIsActive());
        platformDto.setStorageClassName(platform.getStorageClassName());
        platformDto.setGlobalEndpoint(platform.getGlobalEndpoint());
        platformDto.setRegion(platform.getRegion());
        return platformDto;
    }

    private void mapDtoToEntity(PlatformDto platformDto, Platform platform){
        platform.set_id(platformDto.get_id());
        platform.setName(platformDto.getName());
        platform.setKind(String.valueOf(Kind.Kubernetes));
        platform.setApiUrl(platformDto.getApiUrl());
        platform.setApiToken(platformDto.getApiToken());
        platform.setIsActive(platformDto.getIsActive() != null ? platformDto.getIsActive() : true);
        platform.setStorageClassName(platformDto.getStorageClassName());
        platform.setGlobalEndpoint(platformDto.getGlobalEndpoint());
        platform.setRegion(platformDto.getRegion());

    }
}
