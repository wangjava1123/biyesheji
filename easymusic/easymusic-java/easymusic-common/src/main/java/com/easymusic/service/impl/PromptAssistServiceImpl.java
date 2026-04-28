package com.easymusic.service.impl;

import com.easymusic.api.PromptGenerateApi;
import com.easymusic.entity.constants.Constants;
import com.easymusic.entity.dto.PromptAssistResultDTO;
import com.easymusic.entity.po.PromptOptimizeRecord;
import com.easymusic.mappers.PromptOptimizeRecordMapper;
import com.easymusic.service.PromptAssistService;
import com.easymusic.utils.JsonUtils;
import com.easymusic.utils.StringTools;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("promptAssistService")
public class PromptAssistServiceImpl implements PromptAssistService {

    private static final Integer STATUS_SUCCESS = 1;

    private static final Integer STATUS_FAIL = 2;

    @Resource
    private PromptGenerateApi promptGenerateApi;

    @Resource
    private PromptOptimizeRecordMapper promptOptimizeRecordMapper;

    @Override
    public PromptAssistResultDTO generatePrompt(String userId, String rawPrompt, String bizType) {
        String recordId = StringTools.getRandomString(Constants.LENGTH_15);
        Date currentTime = new Date();
        try {
            PromptAssistResultDTO result = promptGenerateApi.generate(rawPrompt);
            PromptOptimizeRecord record = new PromptOptimizeRecord();
            record.setRecordId(recordId);
            record.setUserId(userId);
            record.setBizType(bizType);
            record.setRawPrompt(rawPrompt);
            record.setOptimizedPrompt(result.getMusicPrompt());
            record.setStructuredResult(JsonUtils.convertObj2Json(result));
            record.setProvider(result.getProvider());
            record.setModel(result.getModel());
            record.setIntegral(0);
            record.setStatus(STATUS_SUCCESS);
            record.setCreateTime(currentTime);
            record.setFinishTime(new Date());
            promptOptimizeRecordMapper.insert(record);
            result.setRecordId(recordId);
            return result;
        } catch (RuntimeException e) {
            PromptOptimizeRecord record = new PromptOptimizeRecord();
            record.setRecordId(recordId);
            record.setUserId(userId);
            record.setBizType(bizType);
            record.setRawPrompt(rawPrompt);
            record.setIntegral(0);
            record.setStatus(STATUS_FAIL);
            record.setFailReason(cutMessage(e.getMessage()));
            record.setCreateTime(currentTime);
            record.setFinishTime(new Date());
            promptOptimizeRecordMapper.insert(record);
            throw e;
        }
    }

    private String cutMessage(String message) {
        if (StringTools.isEmpty(message)) {
            return "prompt assist failed";
        }
        return message.length() > 500 ? message.substring(0, 500) : message;
    }
}
