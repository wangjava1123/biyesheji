package com.easymusic.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.easymusic.entity.config.AppConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Component
@Slf4j
public class OSSUtils {

    @Resource
    private AppConfig appConfig;

    private OSS createClient() {
        return new OSSClientBuilder().build(
                appConfig.getOssEndpoint(),
                appConfig.getOssAccessKeyId(),
                appConfig.getOssAccessKeySecret()
        );
    }

    /**
     * 上传文件到 OSS
     *
     * @param inputStream 文件输入流
     * @param ossKey      OSS 对象键 (如 file/202508/xxx.mp3)
     * @return 完整的 OSS 访问 URL
     */
    public String uploadFile(InputStream inputStream, String ossKey) {
        OSS ossClient = createClient();
        try {
            ossClient.putObject(appConfig.getOssBucket(), ossKey, inputStream);
            return appConfig.getOssDomain() + "/" + ossKey;
        } catch (Exception e) {
            log.error("OSS 上传失败, key: {}", ossKey, e);
            throw new RuntimeException("OSS 上传失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 上传本地文件到 OSS
     *
     * @param file   本地文件
     * @param ossKey OSS 对象键
     * @return 完整的 OSS 访问 URL
     */
    public String uploadFile(File file, String ossKey) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return uploadFile(fis, ossKey);
        } catch (Exception e) {
            log.error("OSS 上传本地文件失败, key: {}", ossKey, e);
            throw new RuntimeException("OSS 上传失败", e);
        }
    }
}
