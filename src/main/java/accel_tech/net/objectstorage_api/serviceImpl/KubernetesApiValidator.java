package accel_tech.net.objectstorage_api.serviceImpl;

import accel_tech.net.objectstorage_api.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KubernetesApiValidator {

    private final RestTemplate restTemplate;

    public void validateKubernetesAccess(String apiUrl, String apiToken) {
        if (!isValidUrl(apiUrl)) {
            throw new BadRequestException("Invalid Kubernetes API URL format");
        }
        checkApiAvailability(apiUrl);
        validateApiAuthentication(apiUrl, apiToken);
    }

    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url).toURI();
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }

    private void checkApiAvailability(String apiUrl) {
        try {
            String healthUrl = apiUrl + (apiUrl.endsWith("/") ? "" : "/") + "livez?verbose";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BadRequestException("Kubernetes API is not healthy");
            }
        } catch (ResourceAccessException e) {
            throw new BadRequestException("Kubernetes API is not reachable: " + e.getMessage());
        } catch (RestClientException e) {
            throw new BadRequestException("Failed to connect to Kubernetes API: " + e.getMessage());
        }
    }

    private void validateApiAuthentication(String apiUrl, String apiToken) {
        try {
            String authUrl = apiUrl + (apiUrl.endsWith("/") ? "" : "/");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new BadRequestException("API token has insufficient permissions");
            }

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BadRequestException("Failed to authenticate with Kubernetes API");
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BadRequestException("Invalid Kubernetes API token");
        } catch (RestClientException e) {
            throw new BadRequestException("Authentication validation failed: " + e.getMessage());
        }
    }
}
