package com.amplifiers.pathfinder.entity.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository repository;

    public Image saveImage(MultipartFile file) throws Exception {
        Dotenv dotenv = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        cloudinary.config.secure = true;

        Map params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", true,
                "overwrite", true,
                "folder", "pathFinder"
        );
        Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), params1);
        String publicId = (String) uploadedFile.get("public_id");
        String displayName = (String) uploadedFile.get("display_name") ;
        String url = cloudinary.url().secure(true).generate(publicId);

        Image image = Image.builder().filename(displayName).format((String) uploadedFile.get("format")).url(url).build();

        return repository.save(image);
    }

    public List<Image> findAll() {
        return repository.findAll();
    }

    public ResponseEntity<?> getImage(String filename) {
        Image image = repository.findByFilename(filename);

        RestTemplate restTemplate = new RestTemplate();
        String url = image.getUrl();

        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.IMAGE_JPEG);


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<?> result =
                restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        return result;
    }
}
