package com.easymusic.mappers;

import com.easymusic.entity.po.PromptOptimizeRecord;
import org.apache.ibatis.annotations.Param;

public interface PromptOptimizeRecordMapper {

    Integer insert(@Param("bean") PromptOptimizeRecord bean);

    PromptOptimizeRecord selectByRecordId(@Param("recordId") String recordId);
}
