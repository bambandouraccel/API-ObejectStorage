package accel_tech.net.objectstorage_api.repository;

import accel_tech.net.objectstorage_api.entity.Platform;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends MongoRepository<Platform, String> {
    boolean existsPlatformByName(String name);
}
