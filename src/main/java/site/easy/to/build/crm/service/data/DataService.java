package site.easy.to.build.crm.service.data;

import org.springframework.web.multipart.MultipartFile;

public interface DataService {
    public void reset() throws Exception;
    public void restore(MultipartFile file) throws Exception;
}
