package com.easymusic.utils;

import com.easymusic.entity.config.AppConfig;
import com.easymusic.entity.constants.Constants;
import com.easymusic.entity.enums.DateTimePatternEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class FileUtils {
    @Resource
    private AppConfig appConfig;

    @Resource
    private OSSUtils ossUtils;

    private boolean useOss() {
        return !StringTools.isEmpty(appConfig.getOssEndpoint())
                && !StringTools.isEmpty(appConfig.getOssBucket());
    }

    public String uploadFile(MultipartFile file, String folderName, String fileName) {
        if (StringTools.isEmpty(folderName)) {
            folderName = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern()) + "/";
        }
        if (StringTools.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + StringTools.getFileSuffix(file.getOriginalFilename());
        }

        if (useOss()) {
            try {
                String ossKey = Constants.FILE_FOLDER_FILE + folderName + fileName;
                return ossUtils.uploadFile(file.getInputStream(), ossKey);
            } catch (IOException e) {
                log.error("OSS 上传失败", e);
                throw new RuntimeException("文件上传失败", e);
            }
        }

        // 本地存储 fallback
        String localFolderPath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + folderName;
        File folder = new File(localFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            file.transferTo(new File(localFolderPath + fileName));
        } catch (IOException e) {
            log.error("图片上传失败", e);
        }
        return folderName + fileName;
    }

    public String copyAvatar(String userId) {
        try {
            int randomNumber = (int) (Math.random() * 20) + 1;
            ClassPathResource classPathResource = new ClassPathResource(String.format(Constants.DEFAULT_AVATAR_PATH, randomNumber));
            String avatarPath = Constants.FILE_FOLDER_AVATAR_NAME + userId + Constants.AVATAR_SUFIX;

            if (useOss()) {
                String ossKey = Constants.FILE_FOLDER_FILE + avatarPath;
                return ossUtils.uploadFile(classPathResource.getInputStream(), ossKey);
            }

            // 本地存储 fallback
            String avatarFolderPath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
            File folder = new File(avatarFolderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File avatarFile = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + avatarPath);
            org.apache.commons.io.FileUtils.copyToFile(classPathResource.getInputStream(), avatarFile);
            return avatarPath;
        } catch (Exception e) {
            log.error("拷贝头像失败", e);
        }
        return null;
    }

    public String downloadFile(String url, String suffix) {
        String folderName = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
        String fileName = StringTools.getRandomString(Constants.LENGTH_30) + suffix;

        if (useOss()) {
            // 先下载到临时文件，再上传到 OSS
            String tempPath = System.getProperty("java.io.tmpdir") + "/" + fileName;
            try {
                OKHttpUtils.download(url, tempPath);
                String ossKey = Constants.FILE_FOLDER_FILE + folderName + "/" + fileName;
                String ossUrl = ossUtils.uploadFile(new File(tempPath), ossKey);
                return ossUrl;
            } finally {
                new File(tempPath).delete();
            }
        }

        // 本地存储 fallback
        String localFolderPath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + folderName + "/";
        File folder = new File(localFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String filePath = localFolderPath + fileName;
        OKHttpUtils.download(url, filePath);
        return folderName + "/" + fileName;
    }
}
