package com.easymusic.api;

import com.easymusic.entity.dto.ImageCreateResultDTO;

public interface ImageCreateApi {

    ImageCreateResultDTO generate(String prompt, String style, String title);
}
