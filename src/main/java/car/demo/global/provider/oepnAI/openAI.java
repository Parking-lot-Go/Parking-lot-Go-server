package car.demo.global.provider.oepnAI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Component
public class openAI {

  @Value("${open-api.api-key}")
  private String API_KEY;

  public String transcribe(MultipartFile file) throws Exception {

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(API_KEY);
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    body.add("file", new MultipartInputStreamFileResource(
        file.getInputStream(),
        file.getOriginalFilename()
    ));
    body.add("model", "gpt-4o-mini-transcribe");

    HttpEntity<MultiValueMap<String, Object>> request =
        new HttpEntity<>(body, headers);

    String url = "https://api.openai.com/v1/audio/transcriptions";

    ResponseEntity<String> response =
        restTemplate.postForEntity(url, request, String.class);

    return response.getBody();
  }

}
