package tech.chillo.msgestioncv.profile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.util.Set;

@Service
public class ProfileService {

    public Set<Profile> readFromFiles() {
        try {

            final Resource resource = new ClassPathResource("cv/candidates.json");
            final InputStream inputStream = resource.getInputStream();
            final byte[] fileData = FileCopyUtils.copyToByteArray(inputStream);
            final String outputString = new String(fileData);
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(outputString, new TypeReference<Set<Profile>>() {
            });
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
